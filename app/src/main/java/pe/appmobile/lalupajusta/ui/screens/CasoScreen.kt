package pe.appmobile.lalupajusta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import pe.appmobile.lalupajusta.R
import pe.appmobile.lalupajusta.data.repository.ResultadoCasoResumen
import pe.appmobile.lalupajusta.domain.model.PersonajePoblacion
import pe.appmobile.lalupajusta.ui.art.ArteEstatico
import pe.appmobile.lalupajusta.ui.components.PersonajeTocable
import pe.appmobile.lalupajusta.ui.viewmodel.CasoUiState

private val COLORES_POR_GRUPO = listOf(
    Color(0xFF6B4E8E), // Primario
    Color(0xFF4FA88A), // Acento
    Color(0xFF2B2438).copy(alpha = 0.7f), // Profundo atenuado
    Color(0xFFB45C7A), // rosa terracota (arte/68-...md, seccion 2)
    Color(0xFFE0A438), // Secundario
)

private fun asignarColoresPorGrupo(poblacion: List<PersonajePoblacion>): Map<String, Color> {
    val gruposOrdenados = poblacion.map { it.grupo }.distinct()
    return gruposOrdenados.mapIndexed { indice, grupo -> grupo to COLORES_POR_GRUPO[indice % COLORES_POR_GRUPO.size] }.toMap()
}

@Composable
fun CasoScreen(
    uiState: CasoUiState,
    onTocarPersonaje: (String) -> Unit,
    onDeshacerUltimoToque: () -> Unit,
    onConfirmarMuestra: () -> Unit,
    onAlternarAyuda: () -> Unit,
) {
    if (uiState.cargando) return
    val caso = uiState.caso ?: return
    val coloresPorGrupo = remember(uiState.poblacion) { asignarColoresPorGrupo(uiState.poblacion) }
    val conteoPorPersona = remember(uiState.idsElegidos) { uiState.idsElegidos.groupingBy { it }.eachCount() }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(ArteEstatico.fondoDeCaso(caso.id)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // La Column NO tiene verticalScroll propio -- el LazyVerticalGrid de abajo queda con
        // altura delimitada por el weight(1f), nunca anidado dentro de un padre que ya hace
        // scroll (seccion 7.1 punto 6 del maestro).
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(caso.nombre, style = MaterialTheme.typography.headlineLarge)
                    Text(caso.pregunta, style = MaterialTheme.typography.bodyLarge)
                }
                val ayudaCd = stringResource(R.string.caso_ayuda_boton)
                Image(
                    painter = painterResource(R.drawable.chihua_perfil),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(56.dp)
                        .clickable(onClick = onAlternarAyuda)
                        .semantics { contentDescription = ayudaCd },
                )
            }
            if (uiState.mostrarAyuda) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text(
                        stringResource(R.string.caso_ayuda_texto),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Text(
                stringResource(R.string.caso_muestra_contador, uiState.idsElegidos.size, caso.tamanoMuestraMaximo),
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 64.dp),
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.poblacion, key = { it.id }) { personaje ->
                    val vecesElegido = conteoPorPersona[personaje.id] ?: 0
                    val nombreLegible = personaje.nombre.ifBlank { personaje.id }
                    val descripcion = if (vecesElegido > 0) {
                        stringResource(R.string.caso_cd_personaje_elegido, nombreLegible, vecesElegido)
                    } else {
                        stringResource(R.string.caso_cd_personaje, nombreLegible)
                    }
                    PersonajeTocable(
                        colorGrupo = coloresPorGrupo[personaje.grupo] ?: MaterialTheme.colorScheme.primary,
                        elegido = vecesElegido > 0,
                        vecesElegido = vecesElegido,
                        onClick = { onTocarPersonaje(personaje.id) },
                        modifier = Modifier.semantics { contentDescription = descripcion },
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            uiState.prediccionEnVivo?.let {
                Text(stringResource(R.string.caso_prediccion_en_vivo, it), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDeshacerUltimoToque, enabled = uiState.idsElegidos.isNotEmpty() && uiState.resultado == null) {
                    Text(stringResource(R.string.caso_deshacer))
                }
                Button(
                    onClick = onConfirmarMuestra,
                    enabled = uiState.idsElegidos.isNotEmpty() && uiState.resultado == null,
                ) {
                    Text(stringResource(R.string.caso_confirmar_muestra))
                }
            }

            uiState.resultado?.let { ResultadoDelCaso(it) }
        }
    }
}

@Composable
private fun ResultadoDelCaso(resultado: ResultadoCasoResumen) {
    Spacer(Modifier.height(16.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.ilustracion_resultado),
            contentDescription = null,
            modifier = Modifier.height(96.dp),
        )
        Image(
            painter = painterResource(
                if (resultado.acerto) R.drawable.chihua_alas_abiertas else R.drawable.chihua_erizada,
            ),
            contentDescription = null,
            modifier = Modifier.height(72.dp).padding(start = 8.dp),
        )
    }
    Column {
        Text(stringResource(R.string.caso_resultado_prediccion, resultado.prediccionMuestra ?: "?"))
        Text(stringResource(R.string.caso_resultado_real, resultado.valorRealPoblacion ?: "?"))
        val color = if (resultado.acerto) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
        val texto = if (resultado.acerto) {
            stringResource(R.string.caso_resultado_acierto)
        } else {
            when (resultado.tipoSesgo) {
                "grupo" -> stringResource(R.string.caso_sesgo_grupo)
                "cercania" -> stringResource(R.string.caso_sesgo_cercania)
                "repeticion" -> stringResource(R.string.caso_sesgo_repeticion)
                else -> stringResource(R.string.caso_resultado_fallo_sin_sesgo)
            }
        }
        Text(texto, color = color)
    }
}
