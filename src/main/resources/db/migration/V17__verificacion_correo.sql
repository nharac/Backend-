-- ============================================
-- V17: Verificación del correo al crear la cuenta (doble verificación)
-- ============================================
-- Las cuentas que ya existían quedan verificadas (DEFAULT TRUE).
-- Las cuentas nuevas se crean sin verificar y no pueden iniciar sesión
-- hasta escribir el código de 6 dígitos que llega a su correo institucional.
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS correo_verificado BOOLEAN NOT NULL DEFAULT TRUE;

-- Mismas reglas que codigos_recuperacion (V16): cifrado con BCrypt, vence, un solo uso, máximo de intentos.
CREATE TABLE IF NOT EXISTS codigos_verificacion (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    codigo_hash VARCHAR(100) NOT NULL,
    expira_en TIMESTAMP WITH TIME ZONE NOT NULL,
    intentos INT NOT NULL DEFAULT 0,
    usado BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_codigos_verificacion_usuario ON codigos_verificacion(usuario_id, creado_en DESC);
