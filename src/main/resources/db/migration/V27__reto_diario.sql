-- ============================================
-- V27: Reto del día con una sola oportunidad
-- ============================================
-- Cada estudiante responde el reto del día una sola vez: la llave (usuario_id, fecha)
-- impide un segundo intento aunque se envíen dos respuestas al mismo tiempo.
-- Acertar da 30 Puntos Sumi (y sus Sumi Monedas); fallar pierde el reto de ese día.
CREATE TABLE IF NOT EXISTS reto_diario_intentos (
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    fecha DATE NOT NULL,
    pregunta_id BIGINT NOT NULL REFERENCES preguntas(id) ON DELETE CASCADE,
    opcion_id BIGINT NOT NULL REFERENCES opciones(id) ON DELETE CASCADE,
    correcta BOOLEAN NOT NULL,
    respondido_en TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_id, fecha)
);
