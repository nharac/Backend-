-- V20: módulo de quiz (evaluación cronometrada con preguntas del banco de práctica).
-- Las respuestas correctas nunca se envían al navegador: se califican aquí al finalizar.

CREATE TABLE quiz_intentos (
    id             BIGSERIAL PRIMARY KEY,
    usuario_id     BIGINT      NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    tema           VARCHAR(50),                -- NULL = mixto (los tres temas)
    cantidad       INT         NOT NULL,
    correctas      INT,                        -- NULL mientras no se finaliza
    iniciado_en    TIMESTAMPTZ NOT NULL,
    limite_en      TIMESTAMPTZ NOT NULL,
    finalizado_en  TIMESTAMPTZ,
    fuera_de_tiempo BOOLEAN    NOT NULL DEFAULT FALSE,
    CONSTRAINT ck_quiz_cantidad CHECK (cantidad BETWEEN 1 AND 50)
);

CREATE INDEX idx_quiz_intentos_usuario ON quiz_intentos (usuario_id, iniciado_en DESC);

CREATE TABLE quiz_preguntas (
    intento_id    BIGINT  NOT NULL REFERENCES quiz_intentos(id) ON DELETE CASCADE,
    orden         INT     NOT NULL,
    pregunta_id   BIGINT  NOT NULL REFERENCES preguntas(id),
    letra_elegida VARCHAR(1),                  -- NULL = sin responder
    es_correcta   BOOLEAN,
    PRIMARY KEY (intento_id, orden)
);
