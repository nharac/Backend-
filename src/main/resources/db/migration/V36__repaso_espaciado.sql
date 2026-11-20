-- Repaso espaciado (cajas de Leitner): cada pregunta fallada vuelve al día siguiente; si la aciertas
-- pasa a 3 días, luego a 7, y después queda dominada (proxima = NULL). Un nuevo error la regresa a la caja 1.
CREATE TABLE repaso_espaciado (
    usuario_id    BIGINT    NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    pregunta_id   BIGINT    NOT NULL REFERENCES preguntas (id) ON DELETE CASCADE,
    caja          INT       NOT NULL DEFAULT 1,
    proxima       DATE,
    actualizado   TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_id, pregunta_id)
);
CREATE INDEX idx_repaso_espaciado_proxima ON repaso_espaciado (usuario_id, proxima);

-- Lo que ya estaba pendiente (último intento fallado) entra hoy a la caja 1
INSERT INTO repaso_espaciado (usuario_id, pregunta_id, caja, proxima)
SELECT DISTINCT ON (r.usuario_id, r.pregunta_id) r.usuario_id, r.pregunta_id, 1, (NOW() AT TIME ZONE 'America/Bogota')::date
FROM respuestas_estudiante r
ORDER BY r.usuario_id, r.pregunta_id, r.fecha_intento DESC, r.id DESC
ON CONFLICT DO NOTHING;
DELETE FROM repaso_espaciado re
WHERE EXISTS (SELECT 1 FROM respuestas_estudiante r
              WHERE r.usuario_id = re.usuario_id AND r.pregunta_id = re.pregunta_id
                AND r.id = (SELECT r2.id FROM respuestas_estudiante r2
                            WHERE r2.usuario_id = re.usuario_id AND r2.pregunta_id = re.pregunta_id
                            ORDER BY r2.fecha_intento DESC, r2.id DESC LIMIT 1)
                AND r.es_correcta);
