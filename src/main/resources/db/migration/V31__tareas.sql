-- Tareas que el docente asigna: practicar N preguntas de un tema antes de una fecha.
-- estudiante_id NULL significa "todo el grupo". El avance no se guarda: se calcula con
-- las preguntas distintas que el estudiante resolvió bien en ese tema desde que se creó la tarea.
CREATE TABLE tareas (
    id             BIGSERIAL PRIMARY KEY,
    docente_id     BIGINT       NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    estudiante_id  BIGINT       REFERENCES usuarios (id) ON DELETE CASCADE,
    titulo         VARCHAR(120) NOT NULL,
    instrucciones  VARCHAR(500),
    tema           VARCHAR(30)  NOT NULL,
    meta_preguntas INT          NOT NULL,
    fecha_limite   DATE         NOT NULL,
    creada_en      TIMESTAMP    NOT NULL,
    CONSTRAINT tareas_tema_check CHECK (tema IN ('fundamentos', 'algebra', 'trigonometria', 'limites', 'derivadas', 'integrales')),
    CONSTRAINT tareas_meta_check CHECK (meta_preguntas BETWEEN 1 AND 50)
);

CREATE INDEX idx_tareas_docente ON tareas (docente_id);
CREATE INDEX idx_tareas_estudiante ON tareas (estudiante_id);
