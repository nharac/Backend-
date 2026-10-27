-- ============================================
-- V14: Banco de preguntas administrable y moderación del foro
-- ============================================

-- Banco de preguntas: el docente puede desactivar una pregunta en lugar de borrarla
-- (borrarla eliminaría en cascada los intentos de los estudiantes y su progreso).
ALTER TABLE preguntas ADD COLUMN IF NOT EXISTS activa BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE preguntas ADD COLUMN IF NOT EXISTS creada_por BIGINT REFERENCES usuarios(id) ON DELETE SET NULL;
CREATE INDEX IF NOT EXISTS idx_preguntas_activa ON preguntas(activa);

-- Moderación: una respuesta del foro puede quedar oculta por un docente (se puede restaurar).
ALTER TABLE respuestas ADD COLUMN IF NOT EXISTS oculta BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE respuestas ADD COLUMN IF NOT EXISTS oculta_por BIGINT REFERENCES usuarios(id) ON DELETE SET NULL;
ALTER TABLE respuestas ADD COLUMN IF NOT EXISTS fecha_oculta TIMESTAMP WITH TIME ZONE;

-- Quién ocultó una publicación y cuándo (la columna activa ya existe desde V7).
ALTER TABLE publicaciones ADD COLUMN IF NOT EXISTS oculta_por BIGINT REFERENCES usuarios(id) ON DELETE SET NULL;
ALTER TABLE publicaciones ADD COLUMN IF NOT EXISTS fecha_oculta TIMESTAMP WITH TIME ZONE;

-- V6 intentó crear idx_respuestas_usuario, pero ese nombre ya lo usaba respuestas_estudiante (V2),
-- así que la tabla respuestas del foro quedó sin índice por usuario.
CREATE INDEX IF NOT EXISTS idx_respuestas_foro_usuario ON respuestas(usuario_id);
