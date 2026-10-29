-- ============================================
-- V18: La cuenta se crea solo DESPUÉS de verificar el correo
-- ============================================
-- En V17 la cuenta se guardaba en usuarios sin verificar. Ahora los datos del registro esperan
-- en registros_pendientes y el usuario se crea al escribir el código correcto.

CREATE TABLE IF NOT EXISTS registros_pendientes (
    id BIGSERIAL PRIMARY KEY,
    correo VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    codigo_hash VARCHAR(100) NOT NULL,
    expira_en TIMESTAMP WITH TIME ZONE NOT NULL,
    intentos INT NOT NULL DEFAULT 0,
    ultimo_envio TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Cuentas que quedaron creadas sin verificar con el flujo de V17 (solo pruebas): se eliminan
-- para que esas personas puedan registrarse de nuevo con el flujo correcto.
DELETE FROM usuarios WHERE correo_verificado = FALSE;

-- Lo de V17 ya no se usa
DROP TABLE IF EXISTS codigos_verificacion;
ALTER TABLE usuarios DROP COLUMN IF EXISTS correo_verificado;
