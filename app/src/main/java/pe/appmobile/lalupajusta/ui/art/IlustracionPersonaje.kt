package pe.appmobile.lalupajusta.ui.art

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

private val Profundo = Color(0xFF2B2438)
private val Menta = Color(0xFF4FA88A)

/**
 * Silueta de un personaje de la poblacion. Su forma y color de grupo se calculan en tiempo de
 * ejecucion (uno de 4-5 tonos reales por caso, ver asignarColoresPorGrupo en CasoScreen), por
 * eso va en Canvas y no en un vector estatico -- 30 a 48 personajes por caso, cada uno pintado
 * segun a que grupo pertenece, no segun su identidad individual (arte/68-...md, seccion 2).
 */
@Composable
fun IlustracionPersonaje(
    colorGrupo: Color,
    elegido: Boolean,
    vecesElegido: Int,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(64.dp)) {
        dibujarSombraDeContacto()
        dibujarCuerpo(colorGrupo)
        dibujarCabeza(colorGrupo)
        if (elegido) dibujarAnilloDeSeleccion()
    }
}

private fun DrawScope.dibujarSombraDeContacto() {
    drawOval(
        color = Profundo.copy(alpha = 0.2f),
        topLeft = Offset(size.width * 0.25f, size.height * 0.88f),
        size = Size(size.width * 0.5f, size.height * 0.1f),
    )
}

private fun DrawScope.dibujarCuerpo(colorGrupo: Color) {
    val cuerpo = Path().apply {
        moveTo(size.width * 0.35f, size.height * 0.85f)
        cubicTo(
            size.width * 0.3f, size.height * 0.6f,
            size.width * 0.32f, size.height * 0.5f,
            size.width * 0.4f, size.height * 0.45f,
        )
        lineTo(size.width * 0.6f, size.height * 0.45f)
        cubicTo(
            size.width * 0.68f, size.height * 0.5f,
            size.width * 0.7f, size.height * 0.6f,
            size.width * 0.65f, size.height * 0.85f,
        )
        close()
    }
    drawPath(cuerpo, brush = Brush.linearGradient(listOf(colorGrupo, colorGrupo.copy(alpha = 0.75f))))
}

private fun DrawScope.dibujarCabeza(colorGrupo: Color) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(colorGrupo.copy(alpha = 0.9f), colorGrupo.copy(alpha = 0.6f)),
            center = Offset(size.width * 0.5f, size.height * 0.28f),
            radius = size.width * 0.2f,
        ),
        radius = size.width * 0.18f,
        center = Offset(size.width * 0.5f, size.height * 0.28f),
    )
}

private fun DrawScope.dibujarAnilloDeSeleccion() {
    drawCircle(
        color = Menta,
        radius = size.width * 0.45f,
        center = Offset(size.width * 0.5f, size.height * 0.5f),
        style = Stroke(width = 6f),
    )
}
