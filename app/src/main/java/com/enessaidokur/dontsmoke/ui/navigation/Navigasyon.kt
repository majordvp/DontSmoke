package com.enessaidokur.dontsmoke.ui.navigation

// --- Gerekli Tüm Importlar ---
import BottomNavigationBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation // navigation grafiği için
import androidx.navigation.compose.rememberNavController
import com.enessaidokur.dontsmoke.ui.screens.AnaSayfaEkrani
import com.enessaidokur.dontsmoke.ui.screens.CüzdanEkrani
import com.enessaidokur.dontsmoke.ui.screens.HosgeldinizEkrani
import com.enessaidokur.dontsmoke.ui.screens.SaglikEkrani
import com.enessaidokur.dontsmoke.ui.screens.SigaraBilgileriEkrani
import com.enessaidokur.dontsmoke.ui.screens.TebrikEkrani
import com.enessaidokur.dontsmoke.ui.screens.YatirimEkrani


// Rota (Ekran) isimleri ve Grafik isimleri
object Rotalar {
    const val ONBOARDING_GRAPH = "onboarding_graph" // Giriş Dünyası
    const val MAIN_GRAPH = "main_graph"         // Ana Uygulama Dünyası

    // Giriş Ekranları
    const val HOSGELDINIZ = "hosgeldin"
    const val SIGARA_BILGILERI = "sigara_bilgileri"
    const val TEBRIK = "tebrikler"

    // Ana Uygulama Ekranları
    const val ANA_SAYFA = "ana_sayfa"
    const val YATIRIM = "yatirim"
    const val SAGLIK = "saglik"

    const val CUZDAN = "cuzdan"
}


// --- ANA NAVİGASYON FONKSİYONU ---
@Composable
fun Navigasyon() {
    val anaNavController = rememberNavController()

    // 2. Üst Seviye NavHost: İki ana dünyamız (Grafik) arasında geçişi yönetecek.
    NavHost(
        navController = anaNavController,
        startDestination = Rotalar.ONBOARDING_GRAPH // Uygulama Giriş Dünyası ile başlar
        // Modifier.padding YOK! Bu NavHost tüm ekranı kaplar.
    ) {
        // --- GRAFİK 1: GİRİŞ DÜNYASI (Bottom Bar YOK) ---
        navigation(
            startDestination = Rotalar.HOSGELDINIZ, // Bu dünyanın başlangıç ekranı
            route = Rotalar.ONBOARDING_GRAPH      // Bu dünyanın (grafiğin) adı
        ) {
            composable(Rotalar.HOSGELDINIZ) {
                HosgeldinizEkrani(
                    onIleriClicked = { anaNavController.navigate(Rotalar.SIGARA_BILGILERI) }
                )
            }
            composable(Rotalar.SIGARA_BILGILERI) {
                SigaraBilgileriEkrani(
                    // onIleriClicked artık 3 parametre alıyor (daha önceki düzeltmemiz)
                    onIleriClicked = { _, _, _ ->
                        // TODO: Bilgileri kaydet
                        // Sigara bilgilerinden sonra TEBRIK ekranına gitmeli
                        anaNavController.navigate(Rotalar.TEBRIK)
                    }
                )
            }
            composable(Rotalar.TEBRIK) {
                TebrikEkrani(
                    onHadiBaslayalimClicked = {
                        // Tebrik'ten sonra ANA UYGULAMA DÜNYASINA geçiyoruz
                        anaNavController.navigate(Rotalar.MAIN_GRAPH) {
                            // Giriş dünyasını geri yığınından SİLİYORUZ
                            // ki geri tuşuyla buraya dönülmesin.
                            popUpTo(Rotalar.ONBOARDING_GRAPH) { inclusive = true }
                        }
                    }
                )
            }
        } // Giriş Dünyası (navigation) Bitti

        // --- GRAFİK 2: ANA UYGULAMA DÜNYASI (Bottom Bar VAR) ---
        // Bu, üst seviye NavHost'un ikinci rotasıdır.
        composable(Rotalar.MAIN_GRAPH) {
            // Ana Uygulama Dünyasını yönetecek AYRI bir Composable çağırıyoruz
            // ve ona ANA NavController'ı VERİYORUZ.
            AnaUygulamaIcerigi()
        }

    } // Üst Seviye NavHost Bitti
}


// --- ANA UYGULAMA İÇERİĞİNİ YÖNETEN AYRI FONKSİYON ---
// Bu fonksiyon, Scaffold'u ve BottomBar'ı içerir.
@Composable
fun AnaUygulamaIcerigi() { // ANA NavController'ı parametre olarak alır
    val icNavController = rememberNavController()
    // Mevcut rotayı dinle (BottomBar'ın hangi ikonun seçili olduğunu bilmesi için)
    val navBackStackEntry by icNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Bottom bar'ı burada, merkezi bir şekilde yönetiyoruz
            BottomNavigationBar(
                navController = icNavController,
                currentRoute = currentRoute // Hangi ikonun seçili olacağını söyle
            )
        }
    ) { innerPadding -> // Scaffold'un verdiği boşluk

        // İÇ İÇE NAVHOST: Sadece Ana Uygulama Ekranları Arasında Gezer
        NavHost(
            navController = icNavController,
            startDestination = Rotalar.ANA_SAYFA, // Ana dünyanın başlangıç ekranı
            modifier = Modifier.padding(innerPadding) // Scaffold boşluğunu uygula
            // route = Rotalar.MAIN_GRAPH // İsteğe bağlı: Bu iç NavHost'a da bir rota adı verilebilir
        ) {
            // Ana Uygulama Ekranları burada tanımlanır
            composable(Rotalar.ANA_SAYFA) { AnaSayfaEkrani() } // NavController'ı ilettik
            composable(Rotalar.YATIRIM) { YatirimEkrani()}
            composable(Rotalar.SAGLIK) { SaglikEkrani() }
            composable(Rotalar.CUZDAN) { CüzdanEkrani() }
        }

        }
    }


