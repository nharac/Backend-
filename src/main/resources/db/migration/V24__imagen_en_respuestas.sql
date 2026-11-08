-- ============================================
-- V24: Imágenes en las respuestas del foro
-- ============================================
-- Igual que en las publicaciones (V7): la imagen se sube antes a POST /api/foro/imagenes
-- (bucket de S3) y aquí solo se guarda su URL. Es opcional.

ALTER TABLE respuestas ADD COLUMN IF NOT EXISTS imagen_url VARCHAR(500);
