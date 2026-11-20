-- Retos entre compañeros: los dos responden las mismas preguntas y gana quien acierte más
-- (si empatan, quien termine en menos tiempo).
CREATE TABLE duelos (
    id         BIGSERIAL PRIMARY KEY,
    retador_id BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    rival_id   BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    tema       VARCHAR(30) NOT NULL,
    creado_en  TIMESTAMP   NOT NULL,
    CONSTRAINT duelos_tema_check CHECK (tema IN ('fundamentos', 'algebra', 'trigonometria', 'limites', 'derivadas', 'integrales')),
    CONSTRAINT duelos_distintos CHECK (retador_id <> rival_id)
);

CREATE TABLE duelo_preguntas (
    duelo_id    BIGINT NOT NULL REFERENCES duelos (id) ON DELETE CASCADE,
    orden       INT    NOT NULL,
    pregunta_id BIGINT NOT NULL REFERENCES preguntas (id) ON DELETE CASCADE,
    PRIMARY KEY (duelo_id, orden)
);

CREATE TABLE duelo_entregas (
    duelo_id     BIGINT    NOT NULL REFERENCES duelos (id) ON DELETE CASCADE,
    usuario_id   BIGINT    NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    correctas    INT       NOT NULL,
    segundos     INT       NOT NULL,
    entregado_en TIMESTAMP NOT NULL,
    PRIMARY KEY (duelo_id, usuario_id)
);

CREATE INDEX idx_duelos_retador ON duelos (retador_id);
CREATE INDEX idx_duelos_rival ON duelos (rival_id);
