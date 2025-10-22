import androidx.compose.material3.Typography // Doğru import
import androidx.compose.material3.* // Material3 bileşenlerini dahil etmek
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkPurple = Color(0xFF8E44AD)
val LightBlue = Color(0xFF3498DB)

val DarkThemeColors = darkColorScheme(
    primary = DarkPurple,
    secondary = LightBlue,
    background = DarkPurple,
    surface = DarkPurple,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)


// Varsayılan Typography kullanımı
@Composable
fun DontSmokeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkThemeColors, // Material3'te colorScheme kullanılır
        content = content
    )
}

