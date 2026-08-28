package pe.appmobile.lalupajusta.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pe.appmobile.lalupajusta.R
import pe.appmobile.lalupajusta.ui.viewmodel.CuadernoUiState
import pe.appmobile.lalupajusta.ui.viewmodel.EntradaCuaderno

@Composable
fun CuadernoScreen(uiState: CuadernoUiState) {
    if (uiState.cargando) return

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.cuaderno_titulo), style = MaterialTheme.typography.headlineLarge)
        Text(
            stringResource(R.string.cuaderno_contador, uiState.entradas.size),
            style = MaterialTheme.typography.labelLarge,
        )
        if (uiState.entradas.isEmpty()) {
            Text(stringResource(R.string.cuaderno_vacio), style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.entradas) { entrada -> EntradaDelCuaderno(entrada) }
            }
        }
    }
}

@Composable
private fun EntradaDelCuaderno(entrada: EntradaCuaderno) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(entrada.nombreCaso, style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.caso_resultado_prediccion, entrada.prediccion ?: "?"))
            Text(stringResource(R.string.caso_resultado_real, entrada.valorReal ?: "?"))
            val estado = when {
                entrada.acerto -> stringResource(R.string.cuaderno_acerto)
                entrada.tipoSesgo == "grupo" -> stringResource(R.string.cuaderno_sesgo_grupo)
                entrada.tipoSesgo == "cercania" -> stringResource(R.string.cuaderno_sesgo_cercania)
                entrada.tipoSesgo == "repeticion" -> stringResource(R.string.cuaderno_sesgo_repeticion)
                else -> stringResource(R.string.cuaderno_sin_sesgo)
            }
            Text(estado, style = MaterialTheme.typography.labelLarge)
        }
    }
}
