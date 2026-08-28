package pe.appmobile.lalupajusta.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pe.appmobile.lalupajusta.data.entity.CasoEntity
import pe.appmobile.lalupajusta.domain.model.PersonajePoblacion
import pe.appmobile.lalupajusta.ui.theme.LaLupaJustaTheme
import pe.appmobile.lalupajusta.ui.viewmodel.CasoUiState

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CasoScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private val casoDePrueba = CasoEntity("juego_favorito", "El Juego Favorito", "¿Cuál es el juego favorito?", tamanoMuestraMaximo = 6, orden = 1)
    private val poblacionDePrueba = listOf(
        PersonajePoblacion("p1", "amigos_patio", "patio", "saltar_soga", nombre = "Ana"),
        PersonajePoblacion("p2", "salon_a", "patio", "futbol", nombre = "Luis"),
    )

    @Test
    fun `la pantalla de caso no revienta con datos reales`() {
        compose.setContent {
            LaLupaJustaTheme {
                CasoScreen(
                    uiState = CasoUiState(caso = casoDePrueba, poblacion = poblacionDePrueba, cargando = false),
                    onTocarPersonaje = {}, onDeshacerUltimoToque = {}, onConfirmarMuestra = {}, onAlternarAyuda = {},
                )
            }
        }
    }

    @Test
    fun `tocar un personaje dispara el callback con su id`() {
        var idTocado: String? = null
        compose.setContent {
            LaLupaJustaTheme {
                CasoScreen(
                    uiState = CasoUiState(caso = casoDePrueba, poblacion = poblacionDePrueba, cargando = false),
                    onTocarPersonaje = { idTocado = it }, onDeshacerUltimoToque = {}, onConfirmarMuestra = {}, onAlternarAyuda = {},
                )
            }
        }
        compose.onNodeWithContentDescription("Ana, sin elegir todavía").performClick()
        assertEquals("p1", idTocado)
    }

    @Test
    fun `un personaje ya elegido muestra cuantas veces en su descripcion`() {
        compose.setContent {
            LaLupaJustaTheme {
                CasoScreen(
                    uiState = CasoUiState(
                        caso = casoDePrueba, poblacion = poblacionDePrueba,
                        idsElegidos = listOf("p1", "p1"), cargando = false,
                    ),
                    onTocarPersonaje = {}, onDeshacerUltimoToque = {}, onConfirmarMuestra = {}, onAlternarAyuda = {},
                )
            }
        }
        compose.onNodeWithContentDescription("Ana, elegido 2 veces").assertExists()
    }

    @Test
    fun `confirmar la muestra dispara el callback`() {
        var confirmado = false
        compose.setContent {
            LaLupaJustaTheme {
                CasoScreen(
                    uiState = CasoUiState(
                        caso = casoDePrueba, poblacion = poblacionDePrueba,
                        idsElegidos = listOf("p1"), cargando = false,
                    ),
                    onTocarPersonaje = {}, onDeshacerUltimoToque = {}, onConfirmarMuestra = { confirmado = true }, onAlternarAyuda = {},
                )
            }
        }
        compose.onNodeWithText("Confirmar muestra").performClick()
        assertTrue(confirmado)
    }

    @Test
    fun `pedir ayuda a Chihua muestra el texto de ayuda`() {
        compose.setContent {
            LaLupaJustaTheme {
                CasoScreen(
                    uiState = CasoUiState(caso = casoDePrueba, poblacion = poblacionDePrueba, mostrarAyuda = true, cargando = false),
                    onTocarPersonaje = {}, onDeshacerUltimoToque = {}, onConfirmarMuestra = {}, onAlternarAyuda = {},
                )
            }
        }
        compose.onNodeWithText("Toca a varios personajes distintos entre sí para armar tu muestra, y luego confirma.").assertExists()
    }
}
