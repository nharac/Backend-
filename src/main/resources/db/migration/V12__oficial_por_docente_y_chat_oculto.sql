-- ============================================
-- V12: Trazabilidad de respuestas oficiales y límite del chat
-- ============================================

-- SCRUM-91: qué docente marcó la respuesta como oficial y cuándo
ALTER TABLE respuestas ADD COLUMN IF NOT EXISTS marcada_oficial_por BIGINT REFERENCES usuarios(id) ON DELETE SET NULL;
ALTER TABLE respuestas ADD COLUMN IF NOT EXISTS fecha_marcada_oficial TIMESTAMP WITH TIME ZONE;

-- Borrar el historial del chat ahora solo lo oculta: así el límite diario
-- sigue contando los mensajes enviados (antes se podía "reiniciar" borrando).
ALTER TABLE chat_mensaje ADD COLUMN IF NOT EXISTS oculto BOOLEAN NOT NULL DEFAULT FALSE;
