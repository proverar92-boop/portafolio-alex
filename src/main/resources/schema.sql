CREATE TABLE IF NOT EXISTS administrador (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(255) NOT NULL,
    correo VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS perfil (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    correo VARCHAR(255) NOT NULL UNIQUE,
    foto VARCHAR(500) DEFAULT '/img/perfil.jpg',
    password_hash VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS semanas (
    id_semana INT AUTO_INCREMENT PRIMARY KEY,
    numero_semana INT NOT NULL UNIQUE,
    titulo VARCHAR(255),
    descripcion TEXT,
    enlace VARCHAR(1000),
    archivo VARCHAR(255),
    estado VARCHAR(50) DEFAULT 'Activo'
);

CREATE TABLE IF NOT EXISTS archivos_evidencia (
    id_archivo INT AUTO_INCREMENT PRIMARY KEY,
    id_semana INT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    tipo_mime VARCHAR(100),
    contenido LONGBLOB NOT NULL,
    UNIQUE (id_semana, nombre_archivo),
    CONSTRAINT fk_archivo_semana FOREIGN KEY (id_semana) REFERENCES semanas (id_semana) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS enlaces_evidencia (
    id_enlace INT AUTO_INCREMENT PRIMARY KEY,
    id_semana INT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    CONSTRAINT fk_enlace_semana FOREIGN KEY (id_semana) REFERENCES semanas (id_semana) ON DELETE CASCADE
);

MERGE INTO administrador (usuario, correo, password) KEY (correo)
VALUES ('admin', 'alex@gmail.com', 'admin123');
