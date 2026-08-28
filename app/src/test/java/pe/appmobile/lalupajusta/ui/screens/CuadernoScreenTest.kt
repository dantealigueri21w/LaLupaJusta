package pe.appmobile.lalupajusta.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pe.appmobile.lalupajusta.ui.theme.LaLupaJustaTheme
import pe.appmobile.lalupajusta.ui.viewmodel.CuadernoUiState
import pe.appmobile.lalupajusta.ui.viewmodel.EntradaCuaderno

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CuadernoScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `el cuaderno vacio no revienta y muestra el mensaje de vacio`() {
        compose.setContent {
            LaLupaJustaTheme {
                CuadernoScreen(uiState = CuadernoUiState(entradas = emptyList(), cargando = false))
            }
        }
        compose.onNodeWithText("Todavía no resolviste ningún caso. Vuelve al mapa y toca un lugar del pueblo.")
            .assertExists()
    }

    @Test
    fun `el cuaderno con resultados reales no revienta y muestra cada entrada`() {
        val entradas = listOf(
            EntradaCuaderno(
                nombreCaso = "El Juego Favorito",
                prediccion = "Fútbol",
                valorReal = "Fútbol",
                acerto = true,
                tipoSesgo = null,
            ),
            EntradaCuaderno(
                nombreCaso = "El Delegado del Salón",
                prediccion = "Ana",
                valorReal = "Luis",
                acerto = false,
                tipoSesgo = "grupo",
            ),
        )
        compose.setContent {
            LaLupaJustaTheme {
                CuadernoScreen(uiState = CuadernoUiState(entradas = entradas, cargando = false))
            }
        }
        compose.onNodeWithText("El Juego Favorito").assertExists()
        compose.onNodeWithText("El Delegado del Salón").assertExists()
    }
}
