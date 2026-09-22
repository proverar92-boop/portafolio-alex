-- Ejecutar una vez después de portalex.sql.
-- La tabla perfil original no tiene contraseña, pero los usuarios registrados necesitan una.
CREATE DATABASE IF NOT EXISTS portalex;
USE portalex;

ALTER TABLE perfil ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255) NULL;

CREATE TABLE IF NOT EXISTS archivos_evidencia (
	id_archivo INT AUTO_INCREMENT PRIMARY KEY,
	id_semana INT NOT NULL,
	nombre_archivo VARCHAR(255) NOT NULL,
	tipo_mime VARCHAR(100),
	contenido LONGBLOB NOT NULL,
	UNIQUE KEY uq_archivo_semana_nombre (id_semana, nombre_archivo),
	CONSTRAINT fk_archivo_semana FOREIGN KEY (id_semana) REFERENCES semanas(id_semana) ON DELETE CASCADE
);

ALTER TABLE archivos_evidencia MODIFY COLUMN contenido LONGBLOB NOT NULL;

CREATE TABLE IF NOT EXISTS enlaces_evidencia (
	id_enlace INT AUTO_INCREMENT PRIMARY KEY,
	id_semana INT NOT NULL,
	nombre VARCHAR(255) NOT NULL,
	url VARCHAR(1000) NOT NULL,
	CONSTRAINT fk_enlace_semana FOREIGN KEY (id_semana) REFERENCES semanas(id_semana) ON DELETE CASCADE
);