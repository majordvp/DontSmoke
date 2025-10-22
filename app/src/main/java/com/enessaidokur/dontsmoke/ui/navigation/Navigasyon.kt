package com.enessaidokur.dontsmoke.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.enessaidokur.dontsmoke.ui.screens.AnaSayfaEkrani
import com.enessaidokur.dontsmoke.ui.screens.HosgeldinizEkrani
import com.enessaidokur.dontsmoke.ui.screens.SigaraBilgileriEkrani
import com.enessaidokur.dontsmoke.ui.screens.TebrikEkrani

@Composable
fun Navigasyon() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "hosgeldiniz") {
        composable("hosgeldiniz") {
            HosgeldinizEkrani(onIleriClicked = {
                navController.navigate("sigara_bilgileri")
            })
        }
        composable("sigara_bilgileri") {
            SigaraBilgileriEkrani(onIleriClicked = { kacYildir, gundeKac ->
                navController.navigate("tebrik")
            })
        }
        composable("tebrik") {
            TebrikEkrani(onHadiBaslayalimClicked = {
                navController.navigate("ana_sayfa")
            })
        }
        composable("ana_sayfa") {
            AnaSayfaEkrani()
        }
    }
}
