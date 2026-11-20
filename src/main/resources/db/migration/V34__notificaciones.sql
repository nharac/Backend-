-- Notificaciones dentro de la app (campanita): retos, tareas, logros y respuestas del foro.
CREATE TABLE notificaciones (
    id         BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT       NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    tipo       VARCHAR(30)  NOT NULL,
    titulo     VARCHAR(140) NOT NULL,
    mensaje    VARCHAR(300),
    enlace     VARCHAR(200),
    leida      BOOLEAN      NOT NULL DEFAULT FALSE,
    creada_en  TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_notificaciones_usuario ON notificaciones (usuario_id, leida, creada_en DESC);
