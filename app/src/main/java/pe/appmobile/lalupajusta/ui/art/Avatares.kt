package pe.appmobile.lalupajusta.ui.art

import androidx.annotation.DrawableRes
import pe.appmobile.lalupajusta.R

/**
 * Los 12 avatares de perfil de la ficha (seccion 5.11 del maestro), dibujados en SVG y
 * entregados como VectorDrawable (seccion 4.0) -- generados por
 * documentos-fuente/_scripts-generadores/gen_lalupajusta_vector.py. El id de cada avatar (1-12)
 * es el mismo que se guarda en PerfilEntity.avatarId.
 */
object Avatares {
    @DrawableRes
    fun recursoDe(avatarId: Int): Int = RECURSOS.getOrElse(avatarId - 1) { RECURSOS.first() }

    val RECURSOS: List<Int> = listOf(
        R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3, R.drawable.avatar_4,
        R.drawable.avatar_5, R.drawable.avatar_6, R.drawable.avatar_7, R.drawable.avatar_8,
        R.drawable.avatar_9, R.drawable.avatar_10, R.drawable.avatar_11, R.drawable.avatar_12,
    )
}
