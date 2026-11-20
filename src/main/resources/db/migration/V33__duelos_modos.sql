-- Modos de reto: CLASICO (5 preguntas), MUERTE_SUBITA (racha hasta el primer error),
-- PUNTOS (cada pregunta vale según su dificultad) y RELAMPAGO (10 preguntas en 60 segundos).
-- "correctas" en duelo_entregas pasa a ser el puntaje del modo.
ALTER TABLE duelos ADD COLUMN modo VARCHAR(20) NOT NULL DEFAULT 'CLASICO';
ALTER TABLE duelos ADD COLUMN puntaje_maximo INT;
ALTER TABLE duelos ADD CONSTRAINT duelos_modo_check CHECK (modo IN ('CLASICO', 'MUERTE_SUBITA', 'PUNTOS', 'RELAMPAGO'));
UPDATE duelos d SET puntaje_maximo = (SELECT COUNT(*) FROM duelo_preguntas dp WHERE dp.duelo_id = d.id);
