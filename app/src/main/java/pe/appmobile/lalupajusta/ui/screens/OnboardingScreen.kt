package pe.appmobile.lalupajusta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import pe.appmobile.lalupajusta.R
import pe.appmobile.lalupajusta.ui.art.Avatares

private data class PantallaOnboarding(val tituloRes: Int, val textoRes: Int)

private val PANTALLAS = listOf(
    PantallaOnboarding(R.string.onboarding_1_titulo, R.string.onboarding_1_texto),
    PantallaOnboarding(R.string.onboarding_2_titulo, R.string.onboarding_2_texto),
    PantallaOnboarding(R.string.onboarding_3_titulo, R.string.onboarding_3_texto),
    PantallaOnboarding(R.string.onboarding_4_titulo, R.string.onboarding_4_texto),
)

@Composable
fun OnboardingScreen(onTerminar: (alias: String, avatarId: Int) -> Unit) {
    var indice by remember { mutableIntStateOf(0) }
    val esPaginaDePerfil = indice == PANTALLAS.size

    if (!esPaginaDePerfil) {
        val pantalla = PANTALLAS[indice]
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(pantalla.tituloRes), style = MaterialTheme.typography.headlineLarge)
            Text(stringResource(pantalla.textoRes), style = MaterialTheme.typography.bodyLarge)
            Button(onClick = { indice++ }) {
                Text(stringResource(R.string.onboarding_continuar))
            }
        }
    } else {
        PaginaDePerfilOnboarding(onTerminar = onTerminar)
    }
}

@Composable
private fun PaginaDePerfilOnboarding(onTerminar: (alias: String, avatarId: Int) -> Unit) {
    var alias by remember { mutableStateOf("") }
    var avatarId by remember { mutableIntStateOf(1) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(stringResource(R.string.onboarding_5_titulo), style = MaterialTheme.typography.headlineLarge)

        Text(
            stringResource(R.string.perfil_alias_etiqueta),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 16.dp),
        )
        OutlinedTextField(
            value = alias,
            onValueChange = { alias = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Text(
            stringResource(R.string.perfil_avatar_titulo),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Avatares.RECURSOS.forEachIndexed { indiceAvatar, recurso ->
                val idActual = indiceAvatar + 1
                val elegido = idActual == avatarId
                val cd = if (elegido) {
                    stringResource(R.string.perfil_cd_avatar_elegido, idActual)
                } else {
                    stringResource(R.string.perfil_cd_avatar, idActual)
                }
                Image(
                    painter = painterResource(recurso),
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (elegido) 4.dp else 0.dp,
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = CircleShape,
                        )
                        .clickable { avatarId = idActual }
                        .semantics { contentDescription = cd },
                )
            }
        }

        Button(
            onClick = {
                val aliasFinal = alias.ifBlank { "Reportero Curioso" }
                onTerminar(aliasFinal, avatarId)
            },
            modifier = Modifier.padding(top = 24.dp),
        ) {
            Text(stringResource(R.string.onboarding_empezar))
        }
    }
}
