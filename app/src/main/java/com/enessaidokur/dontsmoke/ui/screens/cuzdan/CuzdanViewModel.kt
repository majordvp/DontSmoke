package com.enessaidokur.dontsmoke.ui.screens.cuzdan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.enessaidokur.dontsmoke.data.KullaniciVeriRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// 1. Cüzdan ekranının ihtiyacı olan tüm veriyi tutacak olan UiState sınıfı
data class CuzdanUiState(
    val birikenPara: Double = 0.0,
    val birikenParaFormatli: String = "0,00 ₺"
)

// 2. ViewModel Sınıfı
class CuzdanViewModel(private val repository: KullaniciVeriRepository) : ViewModel() {

    // Arayüzün dinleyeceği canlı veri (StateFlow)
    private val _uiState = MutableStateFlow(CuzdanUiState())
    val uiState: StateFlow<CuzdanUiState> = _uiState.asStateFlow()

    // Para formatlayıcı
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))

    init {
        // ViewModel oluşturulunca, Repository'den gerekli verileri dinlemeye başla
        viewModelScope.launch(Dispatchers.Default) {
            // combine: Birden fazla Flow'u birleştirir. Biri değiştiğinde hepsi tetiklenir.
            combine(
                repository.birakmaTarihi,
                repository.gundeKacPaket,
                repository.paketFiyati
            ) { birakmaTarihi, gundeKacPaket, paketFiyati ->
                // AnaSayfaViewModel'deki hesaplamanın aynısı
                val gunlukMaliyet = gundeKacPaket * paketFiyati
                val saniyelikMaliyet = gunlukMaliyet / (24 * 60 * 60)

                // Sürekli güncellenecek sayaç döngüsü
                while (true) {
                    if (birakmaTarihi > 0) {
                        val gecenSureSaniye = (System.currentTimeMillis() - birakmaTarihi) / 1000
                        val anlikBirikenPara = gecenSureSaniye * saniyelikMaliyet

                        // --- ÇÖZÜM BURADA ---
                        // Önce Double'a çevir, sonra bu yeni değeri her iki yerde de kullan
                        val anlikBirikenParaDouble = anlikBirikenPara.toDouble()

                        // Hesaplanan yeni verilerle UiState'i güncelle
                        _uiState.value = CuzdanUiState(
                            birikenPara = anlikBirikenParaDouble,
                            birikenParaFormatli = currencyFormatter.format(anlikBirikenParaDouble)
                        )
                    }
                    delay(1000) // Her saniye döngüyü tekrarla
                }
            }.collect {} // combine akışını başlatmak için collect gereklidir.
        }
    }



    // 3. Factory: Bu ViewModel'in nasıl oluşturulacağını Android'e öğreten kılavuz
    companion object {
        fun Factory(repository: KullaniciVeriRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(CuzdanViewModel::class.java)) {
                        return CuzdanViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
