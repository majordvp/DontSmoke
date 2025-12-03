package com.enessaidokur.dontsmoke.ui.screens.yatirim

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enessaidokur.dontsmoke.R
import com.enessaidokur.dontsmoke.network.GoldPriceResult
import com.enessaidokur.dontsmoke.network.RetrofitInstance
import com.enessaidokur.dontsmoke.ui.components.acikGriArkaPlan
import com.enessaidokur.dontsmoke.ui.components.acikYesil
import com.enessaidokur.dontsmoke.ui.components.anaYesil
import com.enessaidokur.dontsmoke.ui.components.koyuMetin
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YatirimEkrani() {

    // --- VERİLER VE DURUMLAR ---
    // Henüz ViewModel olmadığı için her şey burada, Composable içinde yönetiliyor.
    var dovizVerileri by remember { mutableStateOf<List<GoldPriceResult>>(emptyList()) }
    var yukleniyor by remember { mutableStateOf(true) }
    var hata by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val apiService = RetrofitInstance.api

    // --- SİSTEM ÇUBUĞU RENKLERİ ---
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = acikYesil, darkIcons = false)
        systemUiController.setNavigationBarColor(color = Color.White, darkIcons = true)
    }

    // --- VERİLERİ ÇEKME İŞLEMİ ---
    // Bu 'LaunchedEffect', ekran ilk açıldığında SADECE BİR KERE çalışır.
    LaunchedEffect(key1 = true) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                // API'ye istek atıp cevabı alıyoruz.
                val response = apiService.getGoldPrices()
                // Gelen veriyi state'e atıyoruz, bu da arayüzü güncelliyor.
                dovizVerileri = response.result
                yukleniyor = false // Yüklenme bitti.
            } catch (e: Exception) {
                // Hata olursa, hata mesajını state'e atıyoruz.
                hata = "Veriler yüklenemedi: ${e.localizedMessage}"
                yukleniyor = false // Yüklenme bitti (başarısız).
            }
        }
    }

    // --- EKRANIN ANA YAPISI ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yatırım") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = acikYesil,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = acikGriArkaPlan
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Yüklenme, Hata ve Başarı durumlarına göre doğru arayüzü göster.
            when {
                yukleniyor -> CircularProgressIndicator(color = anaYesil)
                hata != null -> Text(text = hata!!, color = Color.Red, textAlign = TextAlign.Center)
                else -> YatirimIcerigi(dovizVerileri = dovizVerileri) // Veriler geldiyse içeriği göster.
            }
        }
    }
}

@Composable
fun YatirimIcerigi(dovizVerileri: List<GoldPriceResult>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bakiye Kartı (Henüz hesaplama olmadığı için statik bir değer gösteriyor)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = anaYesil),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Kullanılabilir Bakiye", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
                    // Bu değer henüz dinamik değil, sadece bir yer tutucu.
                    Text("0,00 ₺", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // İnternetten gelen her bir döviz verisi için bir kart oluşturur.
        items(dovizVerileri) { doviz ->
            // API'den gelen "Gram Altın", "Gümüş/Ons" gibi isimleri kendi istediğimiz formata çeviriyoruz.
            val (isim, ikon, birim) = when {
                doviz.name.contains("Gram Altın", ignoreCase = true) -> Triple("Altın", R.drawable.altin, "gr")
                doviz.name.contains("Gümüş", ignoreCase = true) -> Triple("Gümüş", R.drawable.gumus, "gr")
                doviz.name.contains("Dolar", ignoreCase = true) -> Triple("Dolar", R.drawable.dolar, "$")
                else -> null // Euro, Sterlin gibi diğer verileri şimdilik atlıyoruz.
            } ?: return@items // Eğer istediğimiz bir döviz değilse, bu döngü adımını atla.

            YatirimAraciKarti(
                isim = isim,
                guncelFiyat = doviz.selling ?: 0.0,
                ikonResId = ikon,
                birim = birim
            )
        }
    }
}

@Composable
fun YatirimAraciKarti(
    isim: String,
    guncelFiyat: Double,
    @DrawableRes ikonResId: Int,
    birim: String
) {
    var girilenTutar by remember { mutableStateOf("") }
    val girilenTutarDouble = girilenTutar.toDoubleOrNull() ?: 0.0
    // Girilen tutara göre ne kadar alınabileceğini anlık olarak hesapla.
    val hesaplananMiktar = if (guncelFiyat > 0) girilenTutarDouble / guncelFiyat else 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Kartın üst kısmı: İkon, İsim ve Güncel Fiyat
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = ikonResId),
                    contentDescription = isim,
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(acikGriArkaPlan).padding(4.dp)
                )
                Text(text = isim, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = koyuMetin)
                Text(
                    text = String.format(Locale.getDefault(), "%.2f ₺", guncelFiyat),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = acikGriArkaPlan, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))
            // Kartın alt kısmı: Tutar giriş alanı ve "Al" butonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = girilenTutar,
                        onValueChange = { girilenTutar = it },
                        label = { Text("Tutar (₺)") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = anaYesil,
                            unfocusedIndicatorColor = Color.LightGray,
                            focusedContainerColor = acikGriArkaPlan,
                            unfocusedContainerColor = acikGriArkaPlan,
                            cursorColor = anaYesil
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "Yaklaşık: %.4f %s", hesaplananMiktar, birim),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = anaYesil),
                    onClick = { /* Buton henüz bir işlevselliğe sahip değil */ },
                    modifier = Modifier.height(56.dp),
                    // Buton sadece pozitif bir tutar girildiğinde aktif olur.
                    enabled = (girilenTutarDouble > 0)
                ) {
                    Text(text = "Al")
                }
            }
        }
    }
}
