package com.enessaidokur.dontsmoke.ui.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enessaidokur.dontsmoke.ui.network.RetrofitInstance
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class YatirimUrunu(
    val isim: String,
    val miktar: Double,
    val birim: String,
    val anlikFiyat: Double,
    @DrawableRes val ikonResId: Int
)

data class CuzdanUiState(
    val bakiye: Double = 100000.0,
    val urunler: List<YatirimUrunu> = emptyList(),
    val anlikFiyatlar: Map<String, Double> = emptyMap(),
    val isFiyatlarLoading: Boolean = false,
    val fiyatHataMesaji: String? = null
)

class CuzdanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CuzdanUiState())
    val uiState: StateFlow<CuzdanUiState> = _uiState.asStateFlow()

    init {
        startPriceUpdates()
    }

    private fun startPriceUpdates() {
        viewModelScope.launch {
            fetchAnlikFiyatlar(showLoading = true)
            while (true) {
                // Güncelleme süresi 15 dakikaya çıkarıldı (API limitini korumak için)
                delay(18000000L) // 15 dakikada bir güncelle (900000 milisaniye)
                fetchAnlikFiyatlar(showLoading = false)
            }
        }
    }

    fun fetchAnlikFiyatlar(showLoading: Boolean = true) {
        if (showLoading) {
            _uiState.update { it.copy(isFiyatlarLoading = true, fiyatHataMesaji = null) }
        }

        viewModelScope.launch {
            try {
                coroutineScope {
                    val goldRequest = async { RetrofitInstance.api.getGoldPrices() }
                    val silverRequest = async { RetrofitInstance.api.getSilverPrice() }
                    val currencyRequest = async { RetrofitInstance.api.getAllCurrencies() }
                    val cryptoRequest = async { RetrofitInstance.api.getCryptoPrices() }

                    val goldResponse = goldRequest.await()
                    val silverResponse = silverRequest.await()
                    val currencyResponse = currencyRequest.await()
                    val cryptoResponse = cryptoRequest.await()

                    val newPrices = mutableMapOf<String, Double>()
                    val errorMessages = mutableListOf<String>()
                    var usdTryRate = 0.0

                    // Döviz Kurları
                    currencyResponse.result.find { it.name.contains("USD", ignoreCase = true) }?.let {
                        val rate = it.selling?.replace(",", ".")?.toDoubleOrNull()
                        if (rate != null && rate > 0) {
                            newPrices["USD"] = rate
                            usdTryRate = rate
                        } else errorMessages.add("USD Fiyatı Okunamadı")
                    } ?: errorMessages.add("USD Bulunamadı")

                    currencyResponse.result.find { it.name.contains("EUR", ignoreCase = true) }?.let {
                        val rate = it.selling?.replace(",", ".")?.toDoubleOrNull()
                        if (rate != null && rate > 0) newPrices["EUR"] = rate else errorMessages.add("EUR Fiyatı Okunamadı")
                    } ?: errorMessages.add("EUR Bulunamadı")

                    // Altın Fiyatı (Yeni modele göre güncellendi)
                    val gramGold = goldResponse.result.find { it.name.contains("Gram", ignoreCase = true) }
                    if (gramGold != null) {
                        // Artık doğrudan Double okuyoruz, metin işlemine gerek yok.
                        val price = gramGold.buying
                        if (price != null && price > 0) {
                            newPrices["ALTIN"] = price
                        } else {
                            errorMessages.add("Gram Altın Fiyatı Geçersiz")
                        }
                    } else {
                        errorMessages.add("Gram Altın Bulunamadı")
                    }

                    // Gümüş Fiyatı
                    newPrices["GUMUS"] = silverResponse.result.selling

                    // Bitcoin Fiyatı
                    val bitcoin = cryptoResponse.result.find { it.code.equals("BTC", ignoreCase = true) }
                    if (bitcoin != null) {
                        if (usdTryRate > 0) {
                            newPrices["BTC"] = bitcoin.price * usdTryRate
                        } else errorMessages.add("BTC için USD kuru alınamadı")
                    } else errorMessages.add("BTC Bulunamadı")

                    // Sonuçları Değerlendir
                    if (errorMessages.isNotEmpty()) {
                        _uiState.update { it.copy(isFiyatlarLoading = false, fiyatHataMesaji = errorMessages.joinToString(", ")) }
                    } else {
                        _uiState.update { it.copy(isFiyatlarLoading = false, anlikFiyatlar = newPrices, fiyatHataMesaji = null) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isFiyatlarLoading = false, fiyatHataMesaji = "Fiyatlar yüklenemedi: ${e.message}") }
            }
        }
    }

    fun satinAl(isim: String, alinanMiktar: Double, harcananTutar: Double, birim: String, anlikFiyat: Double, @DrawableRes ikonResId: Int) {
        _uiState.update { currentState ->
            if (currentState.bakiye < harcananTutar) return@update currentState

            val yeniBakiye = currentState.bakiye - harcananTutar
            val guncelUrunler = currentState.urunler.toMutableList()
            val mevcutUrunIndex = guncelUrunler.indexOfFirst { it.isim == isim }

            if (mevcutUrunIndex != -1) {
                val mevcutUrun = guncelUrunler[mevcutUrunIndex]
                val yeniMiktar = mevcutUrun.miktar + alinanMiktar
                guncelUrunler[mevcutUrunIndex] = mevcutUrun.copy(miktar = yeniMiktar, anlikFiyat = anlikFiyat)
            } else {
                guncelUrunler.add(YatirimUrunu(isim = isim, miktar = alinanMiktar, birim = birim, anlikFiyat = anlikFiyat, ikonResId = ikonResId))
            }

            currentState.copy(bakiye = yeniBakiye, urunler = guncelUrunler)
        }
    }
}
