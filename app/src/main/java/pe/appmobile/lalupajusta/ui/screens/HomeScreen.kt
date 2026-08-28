package pe.appmobile.lalupajusta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.appmobile.lalupajusta.R
import pe.appmobile.lalupajusta.data.repository.CasoConEstado
import pe.appmobile.lalupajusta.domain.engine.MotorProgreso
import pe.appmobile.lalupajusta.ui.art.ArteEstatico
import pe.appmobile.lalupajusta.ui.art.Avatares
import pe.appmobile.lalupajusta.ui.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onCasoClick: (String) -> Unit,
    onCuadernoClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onAjustesClick: () -> Unit,
) {
    val casosCompletados = uiState.casos.count { it.resuelto }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fondo_home_mapa),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = stringResource(R.string.home_titulo),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    uiState.alias?.let {
                        Text(
                            text = stringResource(R.string.home_saludo, it),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
                val ajustesCd = stringResource(R.string.home_cd_ajustes)
                IconButton(
                    onClick = onAjustesClick,
                    modifier = Modifier.size(48.dp).semantics { contentDescription = ajustesCd },
                ) { Icon(Icons.Filled.Settings, contentDescription = null) }
            }

            Text(
                text = stringResource(R.string.home_progreso, casosCompletados, uiState.casos.size),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(16.dp))

            if (!uiState.cargando) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    uiState.casos.forEach { casoConEstado ->
                        val desbloqueado = MotorProgreso.estaDesbloqueado(casoConEstado.caso.orden, casosCompletados)
                        LugarDelPueblo(
                            casoConEstado = casoConEstado,
                            desbloqueado = desbloqueado,
                            casosFaltantes = (casoConEstado.caso.orden - 3 - casosCompletados).coerceAtLeast(1),
                            onClick = { if (desbloqueado) onCasoClick(casoConEstado.caso.id) },
                        )
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
            }

            val perfilCdTexto = stringResource(R.string.home_cd_perfil)
            val cuadernoCdTexto = stringResource(R.string.home_cd_cuaderno)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .clickable(onClick = onPerfilClick)
                        .semantics { contentDescription = perfilCdTexto },
                ) {
                    Image(
                        painter = painterResource(Avatares.recursoDe(uiState.avatarId)),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(onClick = onCuadernoClick)
                        .semantics { contentDescription = cuadernoCdTexto },
                ) {
                    Image(
                        painter = painterResource(R.drawable.insignia_cuaderno_lleno),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun LugarDelPueblo(
    casoConEstado: CasoConEstado,
    desbloqueado: Boolean,
    casosFaltantes: Int,
    onClick: () -> Unit,
) {
    val estadoTexto = when {
        !desbloqueado -> stringResource(R.string.home_bloqueado_condicion, casosFaltantes)
        casoConEstado.resuelto -> stringResource(R.string.home_estado_resuelto)
        else -> stringResource(R.string.home_estado_pendiente)
    }
    val descripcionCompleta = "${casoConEstado.caso.nombre}, $estadoTexto"
    val colorFilter = remember(desbloqueado) {
        if (desbloqueado) null else ColorFilter.colorMatrix(
            androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) },
        )
    }
    Column(
        modifier = Modifier
            .size(120.dp)
            .clickable(enabled = desbloqueado, onClick = onClick)
            .semantics { contentDescription = descripcionCompleta },
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.size(96.dp)) {
            Image(
                painter = painterResource(ArteEstatico.iconoDeCaso(casoConEstado.caso.id)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                colorFilter = colorFilter,
                alpha = if (desbloqueado) 1f else 0.4f,
            )
        }
        Text(
            text = estadoTexto,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}
