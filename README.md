# La Lupa Justa

Aplicación móvil gamificada para el muestreo representativo y control de sesgos (Muestra Justa).

Frente a una población visible de personajes, el niño toca un número limitado para armar una
muestra, predice algo sobre el total del pueblo, y descubre si su forma de elegir hizo mentir a
la predicción por sesgo de grupo, de cercanía o de repetición.

## Requisitos

- Android Studio o JDK 17 + Android SDK (compileSdk 37, minSdk 24)
- Gradle Wrapper incluido, no requiere instalar Gradle aparte

## Compilar

```bash
./gradlew assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

## Correr las pruebas

```bash
./gradlew testDebugUnitTest
```

## Estructura

```
app/src/main/java/pe/appmobile/lalupajusta/
  domain/   modelos y motores de dominio (MotorMuestra, MotorPoblacionReal, MotorSesgo,
            MotorProgreso, MotorRepaso) — sin dependencias de Android, probados en JVM
  data/     entidades Room, DAOs, repositorio y datos semilla
  ui/       tema, componentes, pantallas, navegación y arte
database/   schema.sql y sample_data.sql, exportados de Room y de los datos semilla reales
```

Todo el contenido funciona sin conexión a internet; no se recoge ningún dato personal del niño.
