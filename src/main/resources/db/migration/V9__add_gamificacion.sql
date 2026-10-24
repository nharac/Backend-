-- 1. Agregamos el campo racha a la tabla usuarios
ALTER TABLE usuarios ADD COLUMN racha_actual INT NOT NULL DEFAULT 0;
ALTER TABLE usuarios ADD COLUMN ultima_fecha_practica DATE;

-- 2. Creamos la tabla de historial de puntos
CREATE TABLE puntos_historial (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    motivo VARCHAR(100) NOT NULL,
    fecha TIMESTAMP NOT NULL,
    CONSTRAINT fk_puntos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);