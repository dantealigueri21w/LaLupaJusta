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
