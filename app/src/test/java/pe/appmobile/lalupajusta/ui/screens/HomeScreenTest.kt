package pe.appmobile.lalupajusta.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pe.appmobile.lalupajusta.data.entity.CasoEntity
import pe.appmobile.lalupajusta.data.repository.CasoConEstado
import pe.appmobile.lalupajusta.ui.theme.LaLupaJustaTheme
import pe.appmobile.lalupajusta.ui.viewmodel.HomeUiState

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private fun caso(id: String, nombre: String, orden: Int) =
        CasoEntity(id, nombre, "¿Pregunta?", tamanoMuestraMaximo = 6, orden = orden)

    private val casoAbierto = CasoConEstado(caso("juego_favorito", "El Juego Favorito", 1), resuelto = false)
    private val casoBloqueado = CasoConEstado(caso("caso_final", "El Caso Final", 8), resuelto = false)

    @Test
    fun `la pantalla de inicio no revienta con datos reales`() {
        compose.setContent {
            LaLupaJustaTheme {
                HomeScreen(
                    uiState = HomeUiState(casos = listOf(casoAbierto, casoBloqueado), cargando = false),
                    onCasoClick = {}, onCuadernoClick = {}, onPerfilClick = {}, onAjustesClick = {},
                )
            }
        }
    }

    @Test
    fun `tocar un lugar del pueblo ya desbloqueado dispara la navegacion con su id`() {
        var idTocado: String? = null
        compose.setContent {
            LaLupaJustaTheme {
                HomeScreen(
                    uiState = HomeUiState(casos = listOf(casoAbierto), cargando = false),
                    onCasoClick = { idTocado = it }, onCuadernoClick = {}, onPerfilClick = {}, onAjustesClick = {},
                )
            }
        }
        compose.onNodeWithContentDescription("El Juego Favorito, Por resolver").performClick()
        assertEquals("juego_favorito", idTocado)
    }

    @Test
    fun `tocar un lugar bloqueado NO dispara la navegacion`() {
        var idTocado: String? = null
        compose.setContent {
            LaLupaJustaTheme {
                HomeScreen(
                    uiState = HomeUiState(casos = listOf(casoAbierto, casoBloqueado), cargando = false),
                    onCasoClick = { idTocado = it }, onCuadernoClick = {}, onPerfilClick = {}, onAjustesClick = {},
                )
            }
        }
        compose.onNodeWithContentDescription("El Caso Final, Completa 5 caso(s) más para desbloquear esto").performClick()
        assertNull(idTocado)
    }
}
