package com.enessaidokur.dontsmoke.ui.screens.yatirim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.enessaidokur.dontsmoke.data.KullaniciVeriRepository
import com.enessaidokur.dontsmoke.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// 1. Yatırım ekranının ihtiyacı olan tüm veriyi tutacak UiState
data class YatirimUiState(
    // Bakiye Bilgileri
    val birikenPara: Double = 0.0,
    val birikenParaFormatli: String = "0,00 ₺",

    // API Verileri
    val anlikFiyatlar: Map<String, Double> = emptyMap(),
    val isFiyatlarLoading: Boolean = false,
    val fiyatHataMesaji: String? = null
)

// 2. ViewModel Sınıfı
class YatirimViewModel(private val repository: KullaniciVeriRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(YatirimUiState())
    val uiState: StateFlow<YatirimUiState> = _uiState.asStateFlow()

    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    private var paraGuncellemeJob: Job? = null

    init {
        observeKullaniciVerileri()
        startPriceUpdates()
    }

    private fun observeKullaniciVerileri() {
        viewModelScope.launch(Dispatchers.Default) {
            combine(
                repository.birakmaTarihi,
                repository.gundeKacPaket,
                repository.paketFiyati
            ) { birakmaTarihi, gundeKacPaket, paketFiyati ->
                Triple(birakmaTarihi, gundeKacPaket, paketFiyati)
            }.collect { (birakmaTarihi, gundeKacPaket, paketFiyati) ->
                paraGuncellemeJob?.cancel()
                paraGuncellemeJob = viewModelScope.launch(Dispatchers.Default) {
                    val gunlukMaliyet : Float= gundeKacPaket * paketFiyati
                    val saniyelikMaliyet: Float = gunlukMaliyet / (24 * 60 * 60)

                    while (true) {
                        if (birakmaTarihi > 0) {
                            val gecenSureSaniye  = (System.currentTimeMillis() - birakmaTarihi) / 1000
                            val anlikBirikenPara= gecenSureSaniye * saniyelikMaliyet
                            _uiState.update {
                                it.copy(
                                    birikenPara = anlikBirikenPara.toDouble(),
                                    birikenParaFormatli = currencyFormatter.format(anlikBirikenPara)
                                )
                            }
                        }
                        delay(1000)
                    }
                }
            }
        }
    }

    private fun startPriceUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchAnlikFiyatlar(showLoading = true)
            while (true) {
                delay(18000000L) // 5 Saatte bir güncelle
                fetchAnlikFiyatlar(showLoading = false)
            }
        }
    }

    fun fetchAnlikFiyatlar(showLoading: Boolean) {
        if (showLoading) {
            _uiState.update { it.copy(isFiyatlarLoading = true, fiyatHataMesaji = null) }
        }
        viewModelScope.launch(Dispatchers.IO) {
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
                    var usdTryRate = 0.0

                    currencyResponse.result.find { it.name.contains("USD", ignoreCase = true) }?.let {
                        val rate = it.selling?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
                        newPrices["USD"] = rate
                        usdTryRate = rate
                    }
                    currencyResponse.result.find { it.name.contains("EUR", ignoreCase = true) }?.let {
                        newPrices["EUR"] = it.selling?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
                    }
                    goldResponse.result.find { it.name.contains("Gram", ignoreCase = true) }?.let {
                        newPrices["ALTIN"] = it.buying ?: 0.0
                    }
                    newPrices["GUMUS"] = silverResponse.result.selling

                    cryptoResponse.result.find { it.code.equals("BTC", ignoreCase = true) }?.let { btcData ->
                        if (usdTryRate > 0) newPrices["BTC"] = btcData.price * usdTryRate
                    }

                    _uiState.update { it.copy(isFiyatlarLoading = false, anlikFiyatlar = newPrices, fiyatHataMesaji = null) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isFiyatlarLoading = false, fiyatHataMesaji = "Fiyatlar yüklenemedi.") }
            }
        }
    }

    fun satinAl(harcananTutar: Double) {
        // Bu fonksiyonun iç mantığı, biriken paranın nasıl yönetileceğine göre doldurulacak.
    }

    companion object {
        fun Factory(repository: KullaniciVeriRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(YatirimViewModel::class.java)) {
                        return YatirimViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
