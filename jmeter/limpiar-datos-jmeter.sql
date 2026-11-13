-- Limpieza de datos de prueba de JMeter (correr en el editor SQL de Supabase SOLO si hace falta).
-- Los escenarios 3, 4 y 7 eliminan solos lo que crean; esto es por si alguna petición falló a mitad.

-- 1) Revisar primero qué quedó
SELECT id, titulo, fecha_creacion FROM publicaciones WHERE titulo LIKE 'JMETER-%' ORDER BY id;

-- 2) Borrar (descomentar después de revisar el SELECT anterior)
-- (respuestas y votos se borran solos por ON DELETE CASCADE)
-- DELETE FROM publicaciones WHERE titulo LIKE 'JMETER-%';
