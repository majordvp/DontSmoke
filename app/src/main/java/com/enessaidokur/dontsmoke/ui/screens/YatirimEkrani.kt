package com.enessaidokur.dontsmoke.ui.screens


import androidx.compose.runtime.getValue
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enessaidokur.dontsmoke.ui.components.acikGriArkaPlan
import com.enessaidokur.dontsmoke.ui.components.acikYesil
import com.enessaidokur.dontsmoke.ui.components.koyuMetin
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import com.enessaidokur.dontsmoke.R
import com.enessaidokur.dontsmoke.ui.components.anaYesil

@Composable
 fun YatirimEkrani(){
    var toplamAltinGram by remember { mutableStateOf(0.0) }
    var toplamGumusGram by remember { mutableStateOf(0.0) }
    var toplamDolarMiktari by remember { mutableStateOf(0.0) }
    var toplamEuroMiktari by remember { mutableStateOf(0.0) }
    var toplamBitcoinMiktari by remember { mutableStateOf(0.0)}

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = acikYesil, darkIcons = false)
        systemUiController.setNavigationBarColor(color = Color.Transparent, darkIcons = true)
    }
    Scaffold(
        // --- TOP BAR ---
        topBar = {
           KalpRitmiAppBar()
        },

        containerColor = acikGriArkaPlan

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            YatirimAraciKarti(
                isim = "Altın",
                guncelFiyat = 5042.0,
                ikonResId = R.drawable.altin,
                butonAl = { _, alinanMiktar -> toplamAltinGram += alinanMiktar }
            )
            YatirimAraciKarti(
                isim = "Gümüş",
                guncelFiyat = 66.0,
                ikonResId = R.drawable.gumus,
                butonAl = { _, alinanMiktar -> toplamGumusGram += alinanMiktar }
            )
            YatirimAraciKarti(
                birim = "$",
                isim = "Dolar",
                guncelFiyat = 41.84,
                ikonResId = R.drawable.dolar,
                butonAl = { _, alinanMiktar -> toplamDolarMiktari += alinanMiktar }
            )
            YatirimAraciKarti(
                birim = "€",
                isim = "Euro",
                guncelFiyat = 48.45,
                ikonResId = R.drawable.euro,
                butonAl = { _, alinanMiktar -> toplamEuroMiktari += alinanMiktar }
            )
            YatirimAraciKarti(
                birim = "BTC",
                isim = "Bitcoin",
                guncelFiyat =4524920.73,
                ikonResId = R.drawable.bitcoin,
                butonAl = { _, alinanMiktar -> toplamBitcoinMiktari += alinanMiktar }
            )
        }
}
 }


// --- YENİ FONKSİYON 2: Yatırım Aracı Kartı (Altın, Gümüş vb.) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YatirimAraciKarti(
    isim: String,
    guncelFiyat: Double, // örn: 5042.0
    @DrawableRes ikonResId: Int, // R.drawable.ic_gold_ingot
    butonAl: (Double, Double) -> Unit, // (TL, Gram)
    birim: String = "gr"
) {

    var girilenTutar by remember { mutableStateOf("") }

    // Girilen tutara göre anlık gram hesabı
    val hesaplananGram = (girilenTutar.toDoubleOrNull() ?: 0.0) / guncelFiyat

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            //  Üst Bilgi Satırı (İkon, İsim, Fiyat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Aralarını aç
            ) {

                Image(
                    painter = painterResource(id = ikonResId),
                    contentDescription = isim,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(acikGriArkaPlan)
                        .padding(4.dp)
                )

                Text(
                    text = isim,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = koyuMetin
                )

                Text(
                    // Fiyatı 2 ondalıklı göster
                    text = "%.2f ₺".format(guncelFiyat),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
            }

            // Araya bir ayırıcı çizgi
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = acikGriArkaPlan, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            //  Alt Eylem Satırı (Giriş Alanları ve Buton)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically // Her şeyi dikeyde ortala
            ) {
                // Sol Taraf: Giriş Alanları
                Column(
                    modifier = Modifier.weight(1f), // Ağırlık vererek alanı doldur
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Elemanların arasını aç
                ) {

                    OutlinedTextField(
                        value = girilenTutar,
                        onValueChange = { girilenTutar = it },
                        label = { Text("Tutar (₺)") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = acikYesil,      // Odaklanınca çizgi YEŞİL olsun
                            unfocusedIndicatorColor = Color.LightGray,
                            focusedTextColor = koyuMetin,
                            unfocusedTextColor = koyuMetin,
                            focusedContainerColor = acikGriArkaPlan,
                            unfocusedContainerColor = acikGriArkaPlan
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(200.dp)
                    )
                    // Hesaplanan Gramaj
                    Text(
                        // Gramajı 4 ondalıklı göster
                        text = "Yaklaşık: %.4f %s".format(hesaplananGram,birim),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Sağ Taraf: "Al" Butonu
                // Bu buton, Column'un dikey olarak tam ortasına denk gelir
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = anaYesil
                    ),
                    onClick = {
                        val alinanTutar = girilenTutar.toDoubleOrNull() ?: 0.0
                        if (alinanTutar > 0 && hesaplananGram > 0) {
                            butonAl(alinanTutar, hesaplananGram)
                            girilenTutar = "" // İşlem sonrası alanı temizle
                        }
                    },
                    modifier = Modifier
                        .height(56.dp), // TextField ile aynı yükseklikte
                    // Bakiye yeterli değilse veya 0 girildiyse butonu pasif yap
                    enabled = hesaplananGram > 0
                ) {
                    Text(text = "Al")
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

