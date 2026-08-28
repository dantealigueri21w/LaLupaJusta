package pe.appmobile.lalupajusta.ui.art

import androidx.annotation.DrawableRes
import pe.appmobile.lalupajusta.R

/**
 * Mapeo de cada pieza de arte estatico (SVG -> VectorDrawable, seccion 4.0 del maestro) al id
 * real de Room. Generados por documentos-fuente/_scripts-generadores/gen_lalupajusta_vector.py.
 */
object ArteEstatico {
    @DrawableRes
    fun iconoDeCaso(casoId: String): Int = ICONOS_CASO[casoId] ?: R.drawable.icono_lanzador

    @DrawableRes
    fun fondoDeCaso(casoId: String): Int = FONDOS_CASO[casoId] ?: R.drawable.fondo_home_mapa

    @DrawableRes
    fun insigniaDe(insigniaId: String): Int = INSIGNIAS[insigniaId] ?: R.drawable.insignia_primer_punado

    private val ICONOS_CASO: Map<String, Int> = mapOf(
        "juego_favorito" to R.drawable.icono_juego_favorito,
        "delegado_salon" to R.drawable.icono_delegado_salon,
        "sabor_helado" to R.drawable.icono_sabor_helado,
        "equipo_favorito" to R.drawable.icono_equipo_favorito,
        "hora_dormir" to R.drawable.icono_hora_dormir,
        "mascota_preferida" to R.drawable.icono_mascota_preferida,
        "transporte_colegio" to R.drawable.icono_transporte_colegio,
        "caso_final" to R.drawable.icono_caso_final,
    )

    private val FONDOS_CASO: Map<String, Int> = mapOf(
        "juego_favorito" to R.drawable.fondo_juego_favorito,
        "delegado_salon" to R.drawable.fondo_delegado_salon,
        "sabor_helado" to R.drawable.fondo_sabor_helado,
        "equipo_favorito" to R.drawable.fondo_equipo_favorito,
        "hora_dormir" to R.drawable.fondo_hora_dormir,
        "mascota_preferida" to R.drawable.fondo_mascota_preferida,
        "transporte_colegio" to R.drawable.fondo_transporte_colegio,
        "caso_final" to R.drawable.fondo_caso_final,
    )

    private val INSIGNIAS: Map<String, Int> = mapOf(
        "primer_punado" to R.drawable.insignia_primer_punado,
        "ojo_disperso" to R.drawable.insignia_ojo_disperso,
        "prediccion_exacta" to R.drawable.insignia_prediccion_exacta,
        "sesgo_detectado" to R.drawable.insignia_sesgo_detectado,
        "pueblo_completo" to R.drawable.insignia_pueblo_completo,
        "muestra_minima_justa" to R.drawable.insignia_muestra_minima_justa,
        "ayuda_a_chihua" to R.drawable.insignia_ayuda_a_chihua,
        "cuaderno_lleno" to R.drawable.insignia_cuaderno_lleno,
        "sin_trampas" to R.drawable.insignia_sin_trampas,
        "comparacion_real" to R.drawable.insignia_comparacion_real,
        "racha_de_reportero" to R.drawable.insignia_racha_de_reportero,
    )
}
