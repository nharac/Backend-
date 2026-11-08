-- ============================================
-- V23: "Me sirvió" en las respuestas del foro
-- ============================================
-- Cada estudiante o docente puede marcar una vez que una respuesta le sirvió.
-- Si se borra la respuesta o el usuario, sus marcas se borran con ellos.

CREATE TABLE IF NOT EXISTS respuestas_utiles (
    respuesta_id BIGINT NOT NULL REFERENCES respuestas(id) ON DELETE CASCADE,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    fecha TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    PRIMARY KEY (respuesta_id, usuario_id)
);

CREATE INDEX IF NOT EXISTS idx_respuestas_utiles_usuario ON respuestas_utiles(usuario_id);
