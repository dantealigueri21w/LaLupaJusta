package pe.appmobile.lalupajusta.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pe.appmobile.lalupajusta.ui.theme.LaLupaJustaTheme
import pe.appmobile.lalupajusta.ui.viewmodel.PerfilUiState

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PerfilScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `la pantalla de perfil no revienta con datos reales`() {
        compose.setContent {
            LaLupaJustaTheme {
                PerfilScreen(
                    uiState = PerfilUiState(alias = "Reportero Curioso", avatarId = 1, cargando = false),
                    onAliasChange = {}, onAvatarSeleccionado = {}, onGuardar = {},
                )
            }
        }
    }

    @Test
    fun `escribir un alias nuevo dispara el callback`() {
        var aliasNuevo: String? = null
        compose.setContent {
            LaLupaJustaTheme {
                PerfilScreen(
                    uiState = PerfilUiState(alias = "Reportero Curioso", avatarId = 1, cargando = false),
                    onAliasChange = { aliasNuevo = it }, onAvatarSeleccionado = {}, onGuardar = {},
                )
            }
        }
        compose.onNodeWithText("Reportero Curioso").performTextReplacement("Lupa Veloz")
        assertEquals("Lupa Veloz", aliasNuevo)
    }

    @Test
    fun `elegir un avatar dispara el callback con su id`() {
        var avatarElegido: Int? = null
        compose.setContent {
            LaLupaJustaTheme {
                PerfilScreen(
                    uiState = PerfilUiState(alias = "Reportero Curioso", avatarId = 1, cargando = false),
                    onAliasChange = {}, onAvatarSeleccionado = { avatarElegido = it }, onGuardar = {},
                )
            }
        }
        compose.onNodeWithContentDescription("Avatar 3").performClick()
        assertEquals(3, avatarElegido)
    }

    @Test
    fun `guardar dispara el callback`() {
        var guardado = false
        compose.setContent {
            LaLupaJustaTheme {
                PerfilScreen(
                    uiState = PerfilUiState(alias = "Reportero Curioso", avatarId = 1, cargando = false),
                    onAliasChange = {}, onAvatarSeleccionado = {}, onGuardar = { guardado = true },
                )
            }
        }
        compose.onNodeWithText("Guardar").performClick()
        assertTrue(guardado)
    }
}
