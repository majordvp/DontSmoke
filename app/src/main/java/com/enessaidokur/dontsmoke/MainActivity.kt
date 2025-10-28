// MainActivity.kt DOSYASININ TAMAMI

package com.enessaidokur.dontsmoke

import DontSmokeTheme // Theme.kt dosyan
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraph
import androidx.navigation.compose.rememberNavController
// KENDİ NAVGRAPH DOSYANI BURADAN IMPORT EDECEKSİN
 // << DOĞRUSU BU // << DİKKAT! BU SATIR EN ÖNEMLİSİ
import com.enessaidokur.dontsmoke.ui.navigation.Navigasyon
import com.enessaidokur.dontsmoke.ui.screens.SaglikEkrani

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DontSmokeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigasyon()
                }
            }
        }
    }
}
