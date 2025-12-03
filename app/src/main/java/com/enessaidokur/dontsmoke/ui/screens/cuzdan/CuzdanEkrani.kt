package com.enessaidokur.dontsmoke.ui.screens.cuzdan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enessaidokur.dontsmoke.ui.components.acikGriArkaPlan
import com.enessaidokur.dontsmoke.ui.components.acikYesil
import com.enessaidokur.dontsmoke.ui.components.anaYesil
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuzdanEkrani(viewModel: CuzdanViewModel) {

    // 2. ViewModel'den gelen canlı veriyi (UiState) dinliyoruz
    val uiState by viewModel.uiState.collectAsState()

    // --- Sistem çubuğu ayarları (Aynı) ---
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = acikYesil, darkIcons = false)
        systemUiController.setNavigationBarColor(color = Color.Transparent, darkIcons = true)
    }

    Scaffold(
        topBar = {
            // --- Basit bir TopAppBar ---
            TopAppBar(
                title = {
                    Text(
                        text = "Cüzdanım",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = acikYesil
                )
            )
        },
        containerColor = acikGriArkaPlan
    ) { innerPadding ->
        // 3. EKRANIN GÖVDESİ
        // Tüm içeriği ortalamak için Column kullanıyoruz
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp), // Ekstra padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // İçeriği dikeyde de ortala
        ) {
            Text(
                text = "Sigara İçmeyerek Biriktirdiğiniz Para",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Ana Para Gösterge Kartı ---
            Card(
                modifier = Modifier, // fillMaxWidth'ı kaldırdık ki kart içeriğe göre büyüsün
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = anaYesil),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                // 4. VERİYİ GÖSTERME
                // ViewModel'den gelen hazır ve formatlı veriyi direkt Text'e veriyoruz
                Text(
                    text = uiState.birikenParaFormatli, // <-- İŞTE BAĞLANTI BURADA!
                    fontSize = 40.sp, // Daha büyük ve dikkat çekici
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp)
                )
            }
        }
    }
}
