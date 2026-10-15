-- ============================================
-- V2: Crear tablas del módulo práctica
-- ============================================
-- Este módulo incluye:
--   - preguntas: preguntas tipo ICFES con tema y dificultad
--   - opciones: 4 opciones (A/B/C/D) por pregunta
--   - respuestas_estudiante: registro de intentos del estudiante
--
-- Diseño: 
--   - Índices en columnas usadas para filtros (tema, dificultad, usuario_id)
--   - UNIQUE constraint en opciones para evitar letras duplicadas por pregunta
--   - CASCADE en FKs para evitar registros huérfanos
--   - Timestamps en UTC con TIMESTAMP WITH TIME ZONE

-- ============================================
-- Tabla: preguntas
-- ============================================
CREATE TABLE IF NOT EXISTS preguntas (
    id BIGSERIAL PRIMARY KEY,
    tema VARCHAR(50) NOT NULL CHECK (tema IN ('limites', 'derivadas', 'integrales')),
    enunciado TEXT NOT NULL,
    dificultad VARCHAR(20) NOT NULL CHECK (dificultad IN ('basico', 'intermedio', 'avanzado')),
    pista TEXT,
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Índices para acelerar filtros y paginación
CREATE INDEX IF NOT EXISTS idx_preguntas_tema ON preguntas(tema);
CREATE INDEX IF NOT EXISTS idx_preguntas_dificultad ON preguntas(dificultad);
CREATE INDEX IF NOT EXISTS idx_preguntas_tema_dificultad ON preguntas(tema, dificultad);

-- ============================================
-- Tabla: opciones
-- ============================================
CREATE TABLE IF NOT EXISTS opciones (
    id BIGSERIAL PRIMARY KEY,
    pregunta_id BIGINT NOT NULL REFERENCES preguntas(id) ON DELETE CASCADE,
    letra CHAR(1) NOT NULL CHECK (letra IN ('A', 'B', 'C', 'D')),
    texto TEXT NOT NULL,
    es_correcta BOOLEAN NOT NULL DEFAULT FALSE,
    -- Evita dos opciones con la misma letra en la misma pregunta
    CONSTRAINT uk_opciones_pregunta_letra UNIQUE (pregunta_id, letra)
);

CREATE INDEX IF NOT EXISTS idx_opciones_pregunta ON opciones(pregunta_id);
-- Índice parcial: solo indexa la opción correcta (útil para validaciones rápidas)
CREATE INDEX IF NOT EXISTS idx_opciones_correcta ON opciones(pregunta_id) WHERE es_correcta = TRUE;

-- ============================================
-- Tabla: respuestas_estudiante
-- ============================================
CREATE TABLE IF NOT EXISTS respuestas_estudiante (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    pregunta_id BIGINT NOT NULL REFERENCES preguntas(id) ON DELETE CASCADE,
    opcion_id BIGINT NOT NULL REFERENCES opciones(id) ON DELETE CASCADE,
    es_correcta BOOLEAN NOT NULL,
    fecha_intento TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Índices para consultas de progreso (agregaciones por usuario)
CREATE INDEX IF NOT EXISTS idx_respuestas_usuario ON respuestas_estudiante(usuario_id);
CREATE INDEX IF NOT EXISTS idx_respuestas_pregunta ON respuestas_estudiante(pregunta_id);
CREATE INDEX IF NOT EXISTS idx_respuestas_usuario_pregunta ON respuestas_estudiante(usuario_id, pregunta_id);
CREATE INDEX IF NOT EXISTS idx_respuestas_fecha ON respuestas_estudiante(fecha_intento);