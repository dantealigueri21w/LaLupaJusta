package pe.appmobile.lalupajusta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import pe.appmobile.lalupajusta.ui.navigation.NavGraph
import pe.appmobile.lalupajusta.ui.theme.LaLupaJustaTheme

/**
 * El primer-lanzamiento se decide de forma asincrona (LaunchedEffect), nunca con runBlocking en
 * onCreate: bloquear el hilo principal esperando a Room es la causa clasica de ANR al arrancar
 * en frio (handoffs/00-LEE-ESTO-ANTES-QUE-EL-HANDOFF.md, seccion 1.9).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as LaLupaJustaApplication
        setContent {
            LaLupaJustaTheme {
                var esPrimerLanzamiento by remember { mutableStateOf<Boolean?>(null) }
                LaunchedEffect(Unit) {
                    esPrimerLanzamiento = app.repository.obtenerCasosConEstado().isEmpty()
                }
                esPrimerLanzamiento?.let { primero ->
                    NavGraph(repository = app.repository, esPrimerLanzamiento = primero)
                }
            }
        }
    }
}
