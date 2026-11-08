-- ============================================
-- V22: Meta semanal del estudiante
-- ============================================
-- Cuántas preguntas quiere responder por semana (lunes a domingo, hora de Colombia).
-- El perfil muestra el avance; arranca en 20 y el estudiante la puede cambiar entre 5 y 300.
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS meta_semanal INT NOT NULL DEFAULT 20;
ALTER TABLE usuarios ADD CONSTRAINT ck_usuarios_meta_semanal CHECK (meta_semanal BETWEEN 5 AND 300);
