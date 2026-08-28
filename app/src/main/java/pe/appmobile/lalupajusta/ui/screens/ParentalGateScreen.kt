package pe.appmobile.lalupajusta.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pe.appmobile.lalupajusta.BuildConfig
import pe.appmobile.lalupajusta.R
import pe.appmobile.lalupajusta.data.repository.LupaJustaRepository

@Composable
fun ParentalGateScreen(repository: LupaJustaRepository) {
    var desbloqueado by remember { mutableStateOf(false) }
    var progresoMs by remember { mutableStateOf(0L) }
    var presionando by remember { mutableStateOf(false) }

    LaunchedEffect(presionando) {
        while (presionando && progresoMs < 3000L) {
            delay(50)
            progresoMs += 50
        }
        if (progresoMs >= 3000L) desbloqueado = true
        if (!presionando) progresoMs = 0L
    }

    if (!desbloqueado) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            presionando = true
                            tryAwaitRelease()
                            presionando = false
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.gate_instruccion))
                LinearProgressIndicator(progress = { (progresoMs / 3000f).coerceIn(0f, 1f) })
            }
        }
    } else {
        var casosResueltos by remember { mutableStateOf(0) }
        LaunchedEffect(Unit) {
            casosResueltos = repository.obtenerCasosConEstado().count { it.resuelto }
        }
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text(stringResource(R.string.gate_titulo), style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
            Card(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    "${stringResource(R.string.gate_progreso_titulo)}: $casosResueltos / 8",
                    modifier = Modifier.padding(16.dp),
                )
            }
            AjustesSonidoYVibracion()
            Text(
                stringResource(R.string.gate_version, BuildConfig.VERSION_NAME),
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun AjustesSonidoYVibracion() {
    var sonido by remember { mutableStateOf(true) }
    var vibracion by remember { mutableStateOf(true) }
    Column {
        Row {
            Text(stringResource(R.string.gate_ajustes_sonido))
            Switch(checked = sonido, onCheckedChange = { sonido = it })
        }
        Row {
            Text(stringResource(R.string.gate_ajustes_vibracion))
            Switch(checked = vibracion, onCheckedChange = { vibracion = it })
        }
    }
}
