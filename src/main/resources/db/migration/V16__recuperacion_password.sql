-- ============================================
-- V16: Recuperación de contraseña con código por correo
-- ============================================
-- El código de 6 dígitos se guarda cifrado (BCrypt), vence a los 15 minutos,
-- sirve una sola vez y se bloquea después de 5 intentos fallidos.
CREATE TABLE IF NOT EXISTS codigos_recuperacion (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    codigo_hash VARCHAR(100) NOT NULL,
    expira_en TIMESTAMP WITH TIME ZONE NOT NULL,
    intentos INT NOT NULL DEFAULT 0,
    usado BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_codigos_recuperacion_usuario ON codigos_recuperacion(usuario_id, creado_en DESC);
