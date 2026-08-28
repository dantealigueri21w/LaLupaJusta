# Bitácora de compilación — La Lupa Justa

## Parte 1: scaffolding y dominio

- `./gradlew clean testDebugUnitTest`: BUILD SUCCESSFUL, **29 tests, 0 fallos** (verificado
  desde estado limpio). 26 corresponden exactamente a los que fija la ficha; los 3 adicionales
  cubren `MotorProgreso.estaDesbloqueado` (sección 5.1 v13 del maestro: al menos 3 casos
  abiertos desde el primer minuto, el resto por progreso real).
- `./gradlew lintDebug`: BUILD SUCCESSFUL, sin errores.
- Motores (`domain/engine/`, 29 tests): `MotorSesgo` (8, clasifica repetición/grupo/cercanía
  comparando distribuciones contra un umbral de 30 puntos, no un promedio), `MotorMuestra` (7,
  predicción en vivo con desempate determinista), `MotorPoblacionReal` (5, dato real de toda la
  población + composición por grupo), `MotorProgreso` (9, insignias calculables desde historial,
  racha, y desbloqueo por progreso real).
- Sin UI todavía, sin Room todavía — eso es la Parte 2 y 3.
- Corrección hecha sobre el texto del plan (Task 1, Paso 7): `Theme.Material3.DayNight.NoActionBar`
  no es un estilo real de Android; se usó `Theme.LaLupaJusta` propio con
  `parent="android:Theme.Material.Light.NoActionBar"`, el mismo patrón de Base de Campo.
  Detalle completo en `handoffs/INCIDENCIAS-68-LaLupaJusta.md`, I-01.

## Parte 2: Room y datos semilla

- `./gradlew clean testDebugUnitTest`: BUILD SUCCESSFUL, **54 tests, 0 fallos** (verificado
  desde estado limpio).
- `./gradlew lintDebug`: BUILD SUCCESSFUL, sin errores.
- Persistencia real con Room 2.8.4, probada con Robolectric 4.16.1 (`@Config(sdk = [34])`).
- 8 tablas (las 7 de la ficha + `repaso_pendiente`).
- Datos semilla reales: 8 casos, **270 personajes** repartidos en 8 poblaciones (30-48 cada
  una), generados de forma determinista desde clústeres reales (grupo/zona/rasgo/cantidad) — 7
  casos aíslan un solo eje de sesgo, El Caso Final combina los dos a propósito. 11 insignias.
- `MotorRepaso` (6 tests, nuevo, no toca la Parte 1).
- `LupaJustaRepository` (8 tests): compone Room con `MotorMuestra`, `MotorPoblacionReal`,
  `MotorSesgo` y `MotorProgreso` ya construidos en la Parte 1 — verificado con datos semilla
  reales, no solo con fixtures sintéticas de test.
- **Bug conocido, a propósito sin corregir todavía:** `resolverCaso` arma la muestra con
  `poblacion.filter { it.id in personajesElegidosIds }`, que no puede producir duplicados —
  el sesgo por `"repeticion"` de `MotorSesgo` nunca se dispara jugando de verdad aunque el motor
  sí sabe detectarlo. Se corrige en la Parte 3 (Task 5 de su plan), con un test que lo prueba.
  Anotado explícitamente en el handoff de esta app, sección 0.2.
- Corrección hecha sobre el texto del plan (Task 4, Paso 3): el test "los personajes solo
  devuelven los de su propio caso" insertaba `PersonajePoblacionEntity` sin su `CasoEntity`
  padre, violando la `ForeignKey` real de la entidad. Detalle en
  `handoffs/INCIDENCIAS-68-LaLupaJusta.md`, I-02.
- Sin UI todavía — eso es la Parte 3.

## Parte 3: tema, pantallas y arte

- `./gradlew clean testDebugUnitTest lintDebug assembleDebug`: BUILD SUCCESSFUL, **72 tests, 0
  fallos**, lint sin errores, APK real generado (13 452 987 bytes).
- Tema con contraste WCAG verificado — `onSecondary` es Profundo, no blanco (única de las 5 apps
  del lote con esta combinación).
- **Bug real corregido en `resolverCaso`** (Task 5): construía la muestra filtrando la población
  ya única por pertenencia, lo que eliminaba cualquier repetición antes de que `MotorSesgo`
  pudiera verla. Se corrigió mapeando la lista de ids elegidos directamente (preserva
  repeticiones y orden). **Verificado jugando de verdad en el emulador** (no solo con el test
  unitario): tocar dos veces a la misma persona en "El Delegado del Salón" mostró el mensaje
  "Tocaste a la misma persona más de una vez" — el sesgo por repetición nunca se había podido
  disparar jugando antes de esta corrección.
- Los 8 casos comparten `CasoScreen`, una sola pantalla parametrizada.
- Población de hasta 48 personajes en `LazyVerticalGrid`, con altura delimitada por `weight(1f)`
  dentro de una `Column` sin `verticalScroll` propio — nunca anidada en un contenedor que ya hace
  scroll (sección 7.1 punto 6 del maestro).
- Objetivo táctil de los personajes: 64dp (excepción documentada frente a los 120dp de la sección
  6 — con 30-48 personajes visibles a la vez, 120dp es físicamente imposible). Se sintió razonable
  en el emulador; queda pendiente probarlo en un dispositivo físico real.
- **Arte SVG → VectorDrawable** (sección 4.0/4.1.5, script
  `documentos-fuente/_scripts-generadores/gen_lalupajusta_vector.py`): 8 iconos de caso, 11
  insignias, 12 avatares, 9 fondos de escena, portada e ilustración de resultado — 42 piezas,
  `aapt2 compile` limpio. Iteradas 2 vueltas mirando la hoja de contactos en Chrome antes de
  integrar (ver incidencias I-03 e I-05 sobre bugs reales encontrados en esa revisión).
- Arte Canvas (dato en vivo, sección 4.3): `IlustracionPersonaje` — color por grupo asignado en
  tiempo de ejecución, 30-48 instancias por caso.
- Chihua: las 6 poses ya generadas se recortaron, se les quitó el fondo y se convirtieron a WebP
  con un script Python (Pillow) — cajas de recorte calculadas por detección automática de
  contenido (columnas/filas sin píxeles no blancos), no a ojo. `app/src/main/res/drawable-nodpi/`
  pesa 172 KB.
- **Pantalla de perfil completa** (sección 5.11 del maestro / sección 0.6b del handoff): alias +
  12 avatares reales, se elige en el onboarding (página 5) y se puede cambiar después desde
  `PerfilScreen`, accesible desde el Home. El botón de perfil del Home **no** navega al parental
  gate — ese es un botón de ajustes aparte (el ícono de engranaje).
- **Desbloqueo por progreso real** (sección 5.1 / sección 0.6a del handoff): los 3 primeros casos
  del orden semilla abiertos desde el arranque, el resto bloqueado con la condición visible
  ("Completa N caso(s) más"), calculado con `MotorProgreso.estaDesbloqueado` — sin columna nueva
  en `CasoEntity`. Verificado jugando: con 0 completados había 3 abiertos; al resolver 1, el caso
  de orden 4 se desbloqueó solo, sin reinstalar la app (gracias a `LifecycleResumeEffect` en la
  ruta Home).
- `MainActivity` corregido respecto al texto del plan: `LaunchedEffect` dentro de `setContent`,
  nunca `runBlocking` en `onCreate` (bug conocido de la sección 1.9 del handoff).
- **Ciclo real jugado en el emulador `fabrica34`** (sección 10.3 del maestro, obligatorio):
  onboarding completo (5 páginas, incluida la de perfil) → Home con datos reales → caso con sesgo
  de grupo (predicción "saltar_soga" vs. real "futbol", explicación correcta) → confirmar sin
  penalización → volver al Home y ver el progreso actualizado (1 de 8) y el siguiente caso
  desbloqueado → segundo caso con sesgo por repetición, verificado en vivo. Emulador cerrado
  limpio al terminar (`adb emu kill`); no se creó ningún AVD nuevo (`-list-avds` sigue
  devolviendo solo `fabrica34`).
- Dos bugs de arte encontrados y corregidos **jugando en el emulador, no en la hoja de
  contactos** (sección 4.1.5 solo prueba a tamaño declarado, no como lo estira el layout real):
  el comentario XML con `--` que rompía la compilación de las 42 piezas (I-03), y el fondo de
  escena que se distorsionaba al estirarse con `ContentScale.Crop` sobre una pantalla vertical
  (I-05) — dos vueltas de corrección hasta que el fondo se vio bien instalado de verdad.
- Corrección sobre el texto del plan (Task 7/8): los imports `androidx.compose.foundation.layout.weight`
  y `androidx.compose.ui.test.assertExists` no existen como tales — son miembros de
  `ColumnScope`/`RowScope` y de `SemanticsNodeInteraction` respectivamente, se resuelven solos sin
  import (I-04).
- Sumado `nombre` a `PersonajePoblacion` (dominio) para que los lectores de pantalla lean el
  nombre real de cada personaje en los `contentDescription`, no su id interno.

## Cierre de la Fase 1

- `git push origin main` hecho con permiso explícito de Rodrigo.
- Workflow `android-build` (run 33207258097): **completed / success**. Confirmado entrando a la
  corrida por la API de GitHub, no solo por el check verde — el artifact `apk` existe de verdad
  (12 923 908 bytes, `expired: false`), a diferencia del bug real de Base de Campo donde el job
  marcaba éxito con un archivo oculto vacío.
- APK descargado del artifact, extraído como `LaLupaJusta.v1.0.0.apk` (13 451 871 bytes) y
  copiado a `68.LaLupaJusta/4.LaLupaJusta.v1.0.0.apk` — este es el entregable, no la build local.
- `apksigner verify --print-certs` sobre ese archivo: firmado con el keystore de depuración por
  defecto (`CN=Android Debug`), sin datos personales.
- `aapt2 dump badging` sobre ese archivo: `versionName='1.0.0'` coincide con el nombre del
  archivo; sin permiso `INTERNET` (solo el `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` que agrega
  el propio sistema, no una capacidad real de red); `icon='res/mipmap-anydpi-v26/ic_launcher.xml'`
  real, nunca vacío.
- SHA-256 del APK que de verdad se entrega:
  `cf4937ea9878284a87725e58d888db3b632d3518cb66f6241f0eb289f05ba9da`. No coincide (ni tiene por
  qué) con el hash de la build local — cada máquina firma con su propio keystore de depuración.
