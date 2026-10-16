-- ============================================
-- V6: Crear tablas del módulo foro
-- ============================================
-- Permite a estudiantes publicar dudas y responder.
-- Las respuestas pueden anidarse (1 nivel).
-- El campo es_oficial se usará en el Sprint 5 (panel docente).
-- ============================================

-- ============================================
-- Tabla: publicaciones
-- ============================================
CREATE TABLE IF NOT EXISTS publicaciones (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    titulo VARCHAR(255) NOT NULL,
    contenido TEXT NOT NULL,
    tema VARCHAR(50) CHECK (tema IN ('limites', 'derivadas', 'integrales')),
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_publicaciones_usuario ON publicaciones(usuario_id);
CREATE INDEX IF NOT EXISTS idx_publicaciones_tema ON publicaciones(tema);
CREATE INDEX IF NOT EXISTS idx_publicaciones_fecha ON publicaciones(fecha_creacion DESC);

-- ============================================
-- Tabla: respuestas
-- ============================================
CREATE TABLE IF NOT EXISTS respuestas (
    id BIGSERIAL PRIMARY KEY,
    publicacion_id BIGINT NOT NULL REFERENCES publicaciones(id) ON DELETE CASCADE,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    respuesta_padre_id BIGINT REFERENCES respuestas(id) ON DELETE CASCADE,
    contenido TEXT NOT NULL,
    es_oficial BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_respuestas_publicacion ON respuestas(publicacion_id);
CREATE INDEX IF NOT EXISTS idx_respuestas_usuario ON respuestas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_respuestas_padre ON respuestas(respuesta_padre_id);
CREATE INDEX IF NOT EXISTS idx_respuestas_oficial ON respuestas(publicacion_id, es_oficial) WHERE es_oficial = TRUE;