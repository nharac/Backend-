-- Recordatorios de tareas: un correo (y una notificación) por estudiante, por tarea y por día,
-- cuando la tarea vence hoy o mañana y todavía no la completa.
CREATE TABLE tarea_recordatorios (
    tarea_id   BIGINT NOT NULL REFERENCES tareas (id) ON DELETE CASCADE,
    usuario_id BIGINT NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    dia        DATE   NOT NULL,
    PRIMARY KEY (tarea_id, usuario_id, dia)
);
