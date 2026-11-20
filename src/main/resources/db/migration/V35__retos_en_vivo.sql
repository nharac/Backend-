-- Retos en vivo: dos estudiantes responden las mismas preguntas al mismo tiempo.
-- El reloj lo lleva el servidor (inicia_en + segundos_por_pregunta), así ambos ven la misma pregunta.
CREATE TABLE salas_vivo (
    id                    BIGSERIAL PRIMARY KEY,
    codigo                VARCHAR(6)  NOT NULL UNIQUE,
    anfitrion_id          BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    invitado_id           BIGINT      REFERENCES usuarios (id) ON DELETE CASCADE,
    tema                  VARCHAR(30) NOT NULL,
    estado                VARCHAR(15) NOT NULL DEFAULT 'ESPERANDO',
    segundos_por_pregunta INT         NOT NULL DEFAULT 20,
    inicia_en             TIMESTAMP,
    creada_en             TIMESTAMP   NOT NULL,
    CONSTRAINT salas_vivo_estado_check CHECK (estado IN ('ESPERANDO', 'JUGANDO', 'TERMINADA', 'CANCELADA')),
    CONSTRAINT salas_vivo_tema_check CHECK (tema IN ('fundamentos', 'algebra', 'trigonometria', 'limites', 'derivadas', 'integrales'))
);

CREATE TABLE sala_preguntas (
    sala_id     BIGINT NOT NULL REFERENCES salas_vivo (id) ON DELETE CASCADE,
    orden       INT    NOT NULL,
    pregunta_id BIGINT NOT NULL REFERENCES preguntas (id) ON DELETE CASCADE,
    PRIMARY KEY (sala_id, orden)
);

CREATE TABLE sala_respuestas (
    sala_id       BIGINT     NOT NULL REFERENCES salas_vivo (id) ON DELETE CASCADE,
    usuario_id    BIGINT     NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    orden         INT        NOT NULL,
    letra         VARCHAR(2) NOT NULL,
    correcta      BOOLEAN    NOT NULL,
    puntos        INT        NOT NULL,
    respondida_en TIMESTAMP  NOT NULL,
    PRIMARY KEY (sala_id, usuario_id, orden)
);
