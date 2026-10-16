-- ============================================
-- V5: Crear tabla chat_mensaje
-- ============================================
-- Almacena los mensajes del chat entre el estudiante y la IA.
-- Cada mensaje pertenece a un usuario y opcionalmente a una pregunta.
--   - pregunta_id NULL: chat libre (sin contexto de pregunta)
--   - pregunta_id NOT NULL: chat contextual a una pregunta específica
--
-- El campo rol_emisor indica si el mensaje lo envió el usuario o la IA.
-- ============================================

CREATE TABLE IF NOT EXISTS chat_mensaje (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    pregunta_id BIGINT REFERENCES preguntas(id) ON DELETE SET NULL,
    rol_emisor VARCHAR(20) NOT NULL CHECK (rol_emisor IN ('usuario', 'ia')),
    mensaje TEXT NOT NULL,
    fecha_hora TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Índices para acelerar búsquedas de historial
CREATE INDEX IF NOT EXISTS idx_chat_usuario ON chat_mensaje(usuario_id);
CREATE INDEX IF NOT EXISTS idx_chat_usuario_pregunta ON chat_mensaje(usuario_id, pregunta_id);
CREATE INDEX IF NOT EXISTS idx_chat_fecha ON chat_mensaje(fecha_hora);
-- Índice parcial para contar mensajes del día (para el límite diario)
CREATE INDEX IF NOT EXISTS idx_chat_fecha_usuario ON chat_mensaje(usuario_id, fecha_hora);