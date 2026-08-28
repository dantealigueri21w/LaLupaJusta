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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import pe.appmobile.lalupajusta.R
import pe.appmobile.lalupajusta.ui.art.Avatares
import pe.appmobile.lalupajusta.ui.viewmodel.PerfilUiState

@Composable
fun PerfilScreen(
    uiState: PerfilUiState,
    onAliasChange: (String) -> Unit,
    onAvatarSeleccionado: (Int) -> Unit,
    onGuardar: () -> Unit,
) {
    if (uiState.cargando) return

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(stringResource(R.string.perfil_titulo), style = MaterialTheme.typography.headlineLarge)

        Text(
            stringResource(R.string.perfil_alias_etiqueta),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 16.dp),
        )
        OutlinedTextField(
            value = uiState.alias,
            onValueChange = onAliasChange,
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
            Avatares.RECURSOS.forEachIndexed { indice, recurso ->
                val avatarId = indice + 1
                val elegido = avatarId == uiState.avatarId
                val cd = if (elegido) {
                    stringResource(R.string.perfil_cd_avatar_elegido, avatarId)
                } else {
                    stringResource(R.string.perfil_cd_avatar, avatarId)
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
                        .clickable { onAvatarSeleccionado(avatarId) }
                        .semantics { contentDescription = cd },
                )
            }
        }

        Button(
            onClick = onGuardar,
            modifier = Modifier.padding(top = 24.dp),
        ) {
            Text(stringResource(R.string.perfil_guardar))
        }
    }
}
