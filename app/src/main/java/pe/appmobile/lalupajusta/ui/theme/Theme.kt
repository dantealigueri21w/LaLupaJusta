package pe.appmobile.lalupajusta.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = PurpuraMedianoche,
    onPrimary = Color.White,
    secondary = DoradoRevelacion,
    onSecondary = PurpuraOscuro,
    tertiary = VerdeMenta,
    onTertiary = PurpuraOscuro,
    background = BlancoLavanda,
    onBackground = PurpuraOscuro,
    surface = BlancoLavanda,
    onSurface = PurpuraOscuro,
    error = ErrorClaro,
    onError = Color.White,
)

private val EsquemaOscuro = darkColorScheme(
    primary = PurpuraMedianoche,
    onPrimary = Color.White,
    secondary = DoradoRevelacion,
    onSecondary = PurpuraOscuro,
    tertiary = VerdeMenta,
    onTertiary = PurpuraOscuro,
    background = PurpuraOscuro,
    onBackground = BlancoLavanda,
    surface = PurpuraOscuro,
    onSurface = BlancoLavanda,
    error = ErrorOscuro,
    onError = PurpuraOscuro,
)

@Composable
fun LaLupaJustaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = LaLupaJustaTypography,
        content = content,
    )
}
