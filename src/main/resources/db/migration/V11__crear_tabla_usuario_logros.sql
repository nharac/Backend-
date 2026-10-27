-- ============================================
-- V11: Logros de gamificación
-- ============================================
-- Guarda qué logros ya desbloqueó cada usuario.
-- El catálogo (nombre, descripción, ícono) vive en el enum Logro del backend,
-- así cambiar un texto no requiere otra migración.

CREATE TABLE IF NOT EXISTS usuario_logros (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    codigo VARCHAR(50) NOT NULL,
    fecha_obtenido TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_usuario_logro UNIQUE (usuario_id, codigo)
);

CREATE INDEX IF NOT EXISTS idx_usuario_logros_usuario ON usuario_logros(usuario_id);
