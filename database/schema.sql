-- Esquema real de la base de datos de La Lupa Justa (Room 2.8.4), exportado desde
-- app/schemas/pe.appmobile.lalupajusta.data.AppDatabase/1.json (version 1).

CREATE TABLE IF NOT EXISTS `perfil` (`id` INTEGER NOT NULL, `alias` TEXT NOT NULL, `avatarId` INTEGER NOT NULL, PRIMARY KEY(`id`));

CREATE TABLE IF NOT EXISTS `caso` (`id` TEXT NOT NULL, `nombre` TEXT NOT NULL, `pregunta` TEXT NOT NULL, `tamanoMuestraMaximo` INTEGER NOT NULL, `orden` INTEGER NOT NULL, PRIMARY KEY(`id`));

CREATE TABLE IF NOT EXISTS `personaje_poblacion` (`id` TEXT NOT NULL, `casoId` TEXT NOT NULL, `nombre` TEXT NOT NULL, `grupo` TEXT NOT NULL, `zona` TEXT NOT NULL, `rasgo` TEXT NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`casoId`) REFERENCES `caso`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE );
CREATE INDEX IF NOT EXISTS `index_personaje_poblacion_casoId` ON `personaje_poblacion` (`casoId`);

CREATE TABLE IF NOT EXISTS `muestra_armada` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `casoId` TEXT NOT NULL, `personajesElegidosCsv` TEXT NOT NULL, `fecha` INTEGER NOT NULL, FOREIGN KEY(`casoId`) REFERENCES `caso`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE );
CREATE INDEX IF NOT EXISTS `index_muestra_armada_casoId` ON `muestra_armada` (`casoId`);

CREATE TABLE IF NOT EXISTS `resultado_caso` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `casoId` TEXT NOT NULL, `fecha` INTEGER NOT NULL, `prediccionMuestra` TEXT, `valorRealPoblacion` TEXT, `acerto` INTEGER NOT NULL, `tipoSesgo` TEXT, FOREIGN KEY(`casoId`) REFERENCES `caso`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE );
CREATE INDEX IF NOT EXISTS `index_resultado_caso_casoId` ON `resultado_caso` (`casoId`);

CREATE TABLE IF NOT EXISTS `insignia` (`id` TEXT NOT NULL, `nombre` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `fechaObtenida` INTEGER, PRIMARY KEY(`id`));

CREATE TABLE IF NOT EXISTS `racha` (`id` INTEGER NOT NULL, `diasConsecutivos` INTEGER NOT NULL, `ultimaFechaActividad` INTEGER NOT NULL, PRIMARY KEY(`id`));

CREATE TABLE IF NOT EXISTS `repaso_pendiente` (`itemId` TEXT NOT NULL, `fechaUltimoFallo` INTEGER NOT NULL, `intervaloDias` INTEGER NOT NULL, `proximaRevision` INTEGER NOT NULL, PRIMARY KEY(`itemId`));

CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT);
INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '52229d39d501ae6f2248d3261f3f7a8f');
