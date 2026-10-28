-- V19: corrección de una respuesta del banco de práctica + índices para el ranking.

-- 1) "¿Cuál es el límite de f(x) = x² + 3x cuando x → 2?"
--    Estaba marcada la B) 11, pero 2² + 3·2 = 4 + 6 = 10, así que la correcta es la A) 10.
--    Se busca por enunciado y por el texto de la opción para no tocar otra pregunta si el id cambia.
UPDATE opciones o
SET es_correcta = (o.texto = '10')
FROM preguntas p
WHERE p.id = o.pregunta_id
  AND p.enunciado = '¿Cuál es el límite de f(x) = x² + 3x cuando x → 2?'
  AND EXISTS (SELECT 1 FROM opciones x WHERE x.pregunta_id = p.id AND x.texto = '10');

-- 2) El ranking y el perfil suman puntos por usuario y por fecha; sin índice se recorre toda la tabla.
CREATE INDEX IF NOT EXISTS idx_puntos_historial_usuario_fecha ON puntos_historial (usuario_id, fecha);
CREATE INDEX IF NOT EXISTS idx_puntos_historial_fecha ON puntos_historial (fecha);
