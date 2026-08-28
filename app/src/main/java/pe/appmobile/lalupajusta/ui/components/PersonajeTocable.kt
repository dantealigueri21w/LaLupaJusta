package pe.appmobile.lalupajusta.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.appmobile.lalupajusta.ui.art.IlustracionPersonaje

@Composable
fun PersonajeTocable(
    colorGrupo: Color,
    elegido: Boolean,
    vecesElegido: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        IlustracionPersonaje(
            colorGrupo = colorGrupo,
            elegido = elegido,
            vecesElegido = vecesElegido,
            modifier = Modifier.fillMaxSize(),
        )
        if (vecesElegido > 1) {
            Text(
                text = "$vecesElegido",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.TopEnd).padding(2.dp),
            )
        }
    }
}
