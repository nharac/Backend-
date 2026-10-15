-- ============================================
-- V1: Crear tabla usuarios
-- ============================================
-- Esta migración crea la tabla 'usuarios' si no existe.
-- Como Hibernate ya la había creado en el Sprint 1, el IF NOT EXISTS
-- permite que esta migración se aplique sin errores sobre la BD existente.

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    correo VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_usuarios_correo ON usuarios(correo);