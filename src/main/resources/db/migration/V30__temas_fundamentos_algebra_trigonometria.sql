-- ============================================
-- V30: temas nuevos (fundamentos, álgebra y trigonometría) con 40 preguntas cada uno
-- ============================================
-- Amplía las restricciones de tema de preguntas y publicaciones del foro, y agrega 120 preguntas
-- tipo ICFES con pista, repartidas entre básicas, intermedias y avanzadas.
-- Las respuestas numéricas y algebraicas se verificaron con cálculo simbólico (SymPy).
-- Es idempotente como V21: no repite enunciados ni opciones ya existentes.

ALTER TABLE preguntas DROP CONSTRAINT IF EXISTS preguntas_tema_check;
ALTER TABLE preguntas ADD CONSTRAINT preguntas_tema_check
    CHECK (tema IN ('fundamentos', 'algebra', 'trigonometria', 'limites', 'derivadas', 'integrales'));
ALTER TABLE publicaciones DROP CONSTRAINT IF EXISTS publicaciones_tema_check;
ALTER TABLE publicaciones ADD CONSTRAINT publicaciones_tema_check
    CHECK (tema IN ('fundamentos', 'algebra', 'trigonometria', 'limites', 'derivadas', 'integrales'));


-- ============================================
-- FUNDAMENTOS DE MATEMÁTICAS (40)
-- ============================================

-- FUN1
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 8 + 4 × 3?', 'basico',
       'Primero se multiplica y después se suma (jerarquía de operaciones).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 8 + 4 × 3?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '20', TRUE),
    ('B', '36', FALSE),
    ('C', '24', FALSE),
    ('D', '15', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 8 + 4 × 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN2
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es (12 - 4) ÷ 2 + 3?', 'basico',
       'Resuelve primero el paréntesis, luego la división y al final la suma.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es (12 - 4) ÷ 2 + 3?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '7', TRUE),
    ('C', '4', FALSE),
    ('D', '10', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es (12 - 4) ÷ 2 + 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN3
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es -5 + 9 - (-3)?', 'basico',
       'Restar un número negativo es lo mismo que sumarlo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es -5 + 9 - (-3)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '-1', FALSE),
    ('C', '7', TRUE),
    ('D', '17', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es -5 + 9 - (-3)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN4
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es (-4) × (-6)?', 'basico',
       'Menos por menos da más.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es (-4) × (-6)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-24', FALSE),
    ('B', '-10', FALSE),
    ('C', '10', FALSE),
    ('D', '24', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es (-4) × (-6)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN5
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 1/2 + 1/3?', 'basico',
       'Busca un denominador común: 6.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 1/2 + 1/3?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '5/6', TRUE),
    ('B', '2/5', FALSE),
    ('C', '1/6', FALSE),
    ('D', '2/6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 1/2 + 1/3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN6
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 3/4 - 1/6?', 'basico',
       'El mínimo común múltiplo de 4 y 6 es 12.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 3/4 - 1/6?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/3', FALSE),
    ('B', '7/12', TRUE),
    ('C', '1/2', FALSE),
    ('D', '5/12', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 3/4 - 1/6?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN7
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es (2/3) × (9/4)?', 'basico',
       'Multiplica numeradores entre sí y denominadores entre sí, y luego simplifica.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es (2/3) × (9/4)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '11/7', FALSE),
    ('B', '18/7', FALSE),
    ('C', '3/2', TRUE),
    ('D', '8/27', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es (2/3) × (9/4)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN8
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es (5/8) ÷ (5/2)?', 'basico',
       'Dividir es multiplicar por el inverso: 5/8 × 2/5.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es (5/8) ÷ (5/2)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '25/16', FALSE),
    ('B', '4', FALSE),
    ('C', '1/2', FALSE),
    ('D', '1/4', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es (5/8) ÷ (5/2)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN9
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuál es la forma más simple de 18/24?', 'basico',
       'Divide numerador y denominador por su máximo común divisor, que es 6.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la forma más simple de 18/24?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3/4', TRUE),
    ('B', '9/12', FALSE),
    ('C', '2/3', FALSE),
    ('D', '6/8', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la forma más simple de 18/24?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN10
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 2⁵?', 'basico',
       'Multiplica el 2 por sí mismo cinco veces.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 2⁵?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '10', FALSE),
    ('B', '32', TRUE),
    ('C', '25', FALSE),
    ('D', '16', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 2⁵?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN11
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 5⁰?', 'basico',
       'Todo número distinto de cero elevado a la cero vale 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 5⁰?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', '5', FALSE),
    ('C', '1', TRUE),
    ('D', 'No está definido', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 5⁰?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN12
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es √81?', 'basico',
       '¿Qué número multiplicado por sí mismo da 81?'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es √81?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '8', FALSE),
    ('B', '18', FALSE),
    ('C', '40,5', FALSE),
    ('D', '9', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es √81?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN13
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es el 20 % de 150?', 'basico',
       '20 % es lo mismo que multiplicar por 0,2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es el 20 % de 150?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '30', TRUE),
    ('B', '20', FALSE),
    ('C', '15', FALSE),
    ('D', '75', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es el 20 % de 150?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN14
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', 'Si x + 7 = 12, ¿cuánto vale x?', 'basico',
       'Pasa el 7 restando al otro lado.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si x + 7 = 12, ¿cuánto vale x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '19', FALSE),
    ('B', '5', TRUE),
    ('C', '-5', FALSE),
    ('D', '7', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si x + 7 = 12, ¿cuánto vale x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN15
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', 'Si 3x - 4 = 11, ¿cuánto vale x?', 'intermedio',
       'Suma 4 a ambos lados y luego divide entre 3.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si 3x - 4 = 11, ¿cuánto vale x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '7/3', FALSE),
    ('B', '15', FALSE),
    ('C', '5', TRUE),
    ('D', '-5', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si 3x - 4 = 11, ¿cuánto vale x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN16
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', 'Si 2(x - 3) = 10, ¿cuánto vale x?', 'intermedio',
       'Divide entre 2 primero o distribuye el 2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si 2(x - 3) = 10, ¿cuánto vale x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2', FALSE),
    ('B', '13/2', FALSE),
    ('C', '5', FALSE),
    ('D', '8', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si 2(x - 3) = 10, ¿cuánto vale x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN17
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 2³ × 2⁴?', 'intermedio',
       'Al multiplicar potencias de igual base se suman los exponentes: 2⁷.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 2³ × 2⁴?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '128', TRUE),
    ('B', '64', FALSE),
    ('C', '4096', FALSE),
    ('D', '256', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 2³ × 2⁴?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN18
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 3⁶ ÷ 3⁴?', 'intermedio',
       'Al dividir potencias de igual base se restan los exponentes.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 3⁶ ÷ 3⁴?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3', FALSE),
    ('B', '9', TRUE),
    ('C', '27', FALSE),
    ('D', '81', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 3⁶ ÷ 3⁴?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN19
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 4⁻²?', 'intermedio',
       'Un exponente negativo significa "uno sobre": 1/4².'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 4⁻²?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-16', FALSE),
    ('B', '-8', FALSE),
    ('C', '1/16', TRUE),
    ('D', '1/8', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 4⁻²?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN20
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 27^(1/3)?', 'intermedio',
       'Exponente 1/3 es raíz cúbica: ¿qué número al cubo da 27?'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 27^(1/3)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '9', FALSE),
    ('B', '1/3', FALSE),
    ('C', '81', FALSE),
    ('D', '3', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 27^(1/3)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN21
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es √50 simplificado?', 'intermedio',
       'Escribe 50 como 25 × 2 y saca la raíz de 25.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es √50 simplificado?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '5√2', TRUE),
    ('B', '25√2', FALSE),
    ('C', '2√5', FALSE),
    ('D', '10√5', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es √50 simplificado?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN22
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', 'Un artículo cuesta $80.000 y tiene 15 % de descuento. ¿Cuánto se paga?', 'intermedio',
       'Calcula el 15 % y réstalo del precio, o multiplica por 0,85.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Un artículo cuesta $80.000 y tiene 15 % de descuento. ¿Cuánto se paga?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '$65.000', FALSE),
    ('B', '$68.000', TRUE),
    ('C', '$12.000', FALSE),
    ('D', '$92.000', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Un artículo cuesta $80.000 y tiene 15 % de descuento. ¿Cuánto se paga?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN23
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', 'Si 3 cuadernos cuestan $7.500, ¿cuánto cuestan 5?', 'intermedio',
       'Halla primero el precio de un cuaderno (regla de tres).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si 3 cuadernos cuestan $7.500, ¿cuánto cuestan 5?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '$10.000', FALSE),
    ('B', '$15.000', FALSE),
    ('C', '$12.500', TRUE),
    ('D', '$11.500', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si 3 cuadernos cuestan $7.500, ¿cuánto cuestan 5?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN24
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 0,25 escrito como fracción?', 'intermedio',
       '0,25 son 25 centésimas: 25/100.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 0,25 escrito como fracción?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '25/10', FALSE),
    ('B', '1/25', FALSE),
    ('C', '2/5', FALSE),
    ('D', '1/4', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 0,25 escrito como fracción?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN25
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es -2² ?', 'intermedio',
       'El exponente afecta solo al 2, no al signo menos.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es -2² ?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-4', TRUE),
    ('B', '4', FALSE),
    ('C', '-2', FALSE),
    ('D', '2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es -2² ?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN26
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es (-2)³?', 'intermedio',
       'Un número negativo elevado a una potencia impar da negativo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es (-2)³?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '8', FALSE),
    ('B', '-8', TRUE),
    ('C', '-6', FALSE),
    ('D', '6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es (-2)³?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN27
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 1/(1/2 + 1/4)?', 'intermedio',
       'Suma primero las fracciones del denominador: 3/4.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 1/(1/2 + 1/4)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '6', FALSE),
    ('B', '3/4', FALSE),
    ('C', '4/3', TRUE),
    ('D', '1/6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 1/(1/2 + 1/4)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN28
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuál es el valor de 3x² - 2x cuando x = -2?', 'intermedio',
       'Reemplaza x por (-2) con paréntesis: 3(-2)² - 2(-2).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de 3x² - 2x cuando x = -2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '8', FALSE),
    ('B', '-16', FALSE),
    ('C', '40', FALSE),
    ('D', '16', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de 3x² - 2x cuando x = -2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN29
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuál es el mínimo común múltiplo de 6 y 8?', 'intermedio',
       'Busca el menor número que esté en la tabla del 6 y en la del 8.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el mínimo común múltiplo de 6 y 8?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '24', TRUE),
    ('B', '48', FALSE),
    ('C', '2', FALSE),
    ('D', '14', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el mínimo común múltiplo de 6 y 8?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN30
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuál es el máximo común divisor de 36 y 48?', 'intermedio',
       'Descompón en factores primos y toma los comunes con el menor exponente.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el máximo común divisor de 36 y 48?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '6', FALSE),
    ('B', '12', TRUE),
    ('C', '4', FALSE),
    ('D', '144', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el máximo común divisor de 36 y 48?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN31
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', 'Si (x/3) + (x/6) = 5, ¿cuánto vale x?', 'avanzado',
       'Multiplica toda la ecuación por 6 para quitar los denominadores.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si (x/3) + (x/6) = 5, ¿cuánto vale x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '15', FALSE),
    ('B', '30', FALSE),
    ('C', '10', TRUE),
    ('D', '5', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si (x/3) + (x/6) = 5, ¿cuánto vale x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN32
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es (2/3)⁻²?', 'avanzado',
       'El exponente negativo invierte la fracción: (3/2)².'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es (2/3)⁻²?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4/9', FALSE),
    ('B', '-4/9', FALSE),
    ('C', '-9/4', FALSE),
    ('D', '9/4', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es (2/3)⁻²?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN33
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 8^(2/3)?', 'avanzado',
       'Primero la raíz cúbica de 8 y luego elévala al cuadrado.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 8^(2/3)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4', TRUE),
    ('B', '16/3', FALSE),
    ('C', '2', FALSE),
    ('D', '64', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 8^(2/3)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN34
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es √12 + √27?', 'avanzado',
       'Simplifica cada raíz: 2√3 y 3√3.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es √12 + √27?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '√39', FALSE),
    ('B', '5√3', TRUE),
    ('C', '13√3', FALSE),
    ('D', '6√3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es √12 + √27?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN35
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 1/√2 racionalizado?', 'avanzado',
       'Multiplica arriba y abajo por √2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 1/√2 racionalizado?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2/√2', FALSE),
    ('B', '√2', FALSE),
    ('C', '√2/2', TRUE),
    ('D', '1/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 1/√2 racionalizado?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN36
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', 'Un precio sube 10 % y luego baja 10 %. ¿Cómo queda respecto al original?', 'avanzado',
       'Multiplica: 1,10 × 0,90.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Un precio sube 10 % y luego baja 10 %. ¿Cómo queda respecto al original?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'Igual', FALSE),
    ('B', '1 % más alto', FALSE),
    ('C', '10 % más bajo', FALSE),
    ('D', '1 % más bajo', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Un precio sube 10 % y luego baja 10 %. ¿Cómo queda respecto al original?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN37
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', 'Si 5 obreros hacen un muro en 12 días, ¿cuántos días tardan 6 obreros?', 'avanzado',
       'Es proporcionalidad inversa: más obreros, menos días (5 × 12 = 6 × d).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si 5 obreros hacen un muro en 12 días, ¿cuántos días tardan 6 obreros?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '10', TRUE),
    ('B', '14,4', FALSE),
    ('C', '13', FALSE),
    ('D', '11', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si 5 obreros hacen un muro en 12 días, ¿cuántos días tardan 6 obreros?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN38
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Cuánto es 2 - 3(4 - 6)² ÷ 6?', 'avanzado',
       'Paréntesis, potencia, luego multiplicación y división de izquierda a derecha, y al final la resta.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es 2 - 3(4 - 6)² ÷ 6?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-10', FALSE),
    ('B', '0', TRUE),
    ('C', '4', FALSE),
    ('D', '10/3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es 2 - 3(4 - 6)² ÷ 6?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN39
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', 'Si 2/(x - 1) = 4, ¿cuánto vale x?', 'avanzado',
       'Multiplica ambos lados por (x - 1) y despeja.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si 2/(x - 1) = 4, ¿cuánto vale x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/2', FALSE),
    ('B', '3', FALSE),
    ('C', '3/2', TRUE),
    ('D', '9', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si 2/(x - 1) = 4, ¿cuánto vale x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- FUN40
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'fundamentos', '¿Qué número es mayor: 2/3, 0,6 o 5/8?', 'avanzado',
       'Pásalos todos a decimal para compararlos.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Qué número es mayor: 2/3, 0,6 o 5/8?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0,6', FALSE),
    ('B', '5/8', FALSE),
    ('C', 'Son iguales', FALSE),
    ('D', '2/3', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Qué número es mayor: 2/3, 0,6 o 5/8?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ============================================
-- ÁLGEBRA (40)
-- ============================================

-- ALG1
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es el resultado de simplificar 3x + 5x - 2x?', 'basico',
       'Son términos semejantes: suma y resta solo los coeficientes.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el resultado de simplificar 3x + 5x - 2x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '6x', TRUE),
    ('B', '10x', FALSE),
    ('C', '8x', FALSE),
    ('D', '6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el resultado de simplificar 3x + 5x - 2x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG2
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es el desarrollo de 2(x + 4)?', 'basico',
       'El 2 multiplica a cada término del paréntesis.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el desarrollo de 2(x + 4)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2x + 4', FALSE),
    ('B', '2x + 8', TRUE),
    ('C', 'x + 8', FALSE),
    ('D', '2x + 6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el desarrollo de 2(x + 4)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG3
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es el desarrollo de (x + 3)²?', 'basico',
       'Cuadrado de un binomio: el primero al cuadrado, más el doble del primero por el segundo, más el segundo al cuadrado.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el desarrollo de (x + 3)²?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x² + 9', FALSE),
    ('B', 'x² + 3x + 9', FALSE),
    ('C', 'x² + 6x + 9', TRUE),
    ('D', 'x² + 6x + 6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el desarrollo de (x + 3)²?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG4
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es la factorización de x² - 9?', 'basico',
       'Es una diferencia de cuadrados: a² - b² = (a - b)(a + b).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la factorización de x² - 9?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(x - 3)²', FALSE),
    ('B', '(x - 9)(x + 1)', FALSE),
    ('C', '(x + 3)²', FALSE),
    ('D', '(x - 3)(x + 3)', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la factorización de x² - 9?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG5
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es el resultado de x³ · x⁴?', 'basico',
       'Al multiplicar potencias de igual base se suman los exponentes.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el resultado de x³ · x⁴?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x⁷', TRUE),
    ('B', 'x¹²', FALSE),
    ('C', '2x⁷', FALSE),
    ('D', 'x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el resultado de x³ · x⁴?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG6
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es el resultado de (x²)³?', 'basico',
       'Potencia de una potencia: se multiplican los exponentes.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el resultado de (x²)³?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x⁵', FALSE),
    ('B', 'x⁶', TRUE),
    ('C', 'x⁸', FALSE),
    ('D', '3x²', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el resultado de (x²)³?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG7
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', 'Si 4x - 3 = 2x + 7, ¿cuánto vale x?', 'basico',
       'Junta las x a un lado y los números al otro.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si 4x - 3 = 2x + 7, ¿cuánto vale x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2', FALSE),
    ('B', '10', FALSE),
    ('C', '5', TRUE),
    ('D', '-5', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si 4x - 3 = 2x + 7, ¿cuánto vale x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG8
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es la pendiente de la recta y = 3x - 2?', 'basico',
       'En y = mx + b, la pendiente es m.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la pendiente de la recta y = 3x - 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-2', FALSE),
    ('B', '2/3', FALSE),
    ('C', '-3', FALSE),
    ('D', '3', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la pendiente de la recta y = 3x - 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG9
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuánto vale f(2) si f(x) = x² - 3x + 1?', 'basico',
       'Reemplaza la x por 2 en toda la expresión.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale f(2) si f(x) = x² - 3x + 1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-1', TRUE),
    ('B', '3', FALSE),
    ('C', '1', FALSE),
    ('D', '-3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale f(2) si f(x) = x² - 3x + 1?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG10
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es la factorización de x² + 5x + 6?', 'basico',
       'Busca dos números que multiplicados den 6 y sumados den 5.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la factorización de x² + 5x + 6?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(x + 1)(x + 6)', FALSE),
    ('B', '(x + 2)(x + 3)', TRUE),
    ('C', '(x - 2)(x - 3)', FALSE),
    ('D', '(x + 5)(x + 1)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la factorización de x² + 5x + 6?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG11
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es el desarrollo de (x - 2)(x + 5)?', 'basico',
       'Multiplica cada término del primero por cada término del segundo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el desarrollo de (x - 2)(x + 5)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x² - 3x - 10', FALSE),
    ('B', 'x² + 3x + 10', FALSE),
    ('C', 'x² + 3x - 10', TRUE),
    ('D', 'x² - 10', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el desarrollo de (x - 2)(x + 5)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG12
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es el factor común de 6x² + 9x?', 'basico',
       'Busca el mayor número y la mayor potencia de x que dividen a ambos términos.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el factor común de 6x² + 9x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3(2x² + 3x)', FALSE),
    ('B', 'x(6x + 9)', FALSE),
    ('C', '6x(x + 9)', FALSE),
    ('D', '3x(2x + 3)', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el factor común de 6x² + 9x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG13
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuánto vale log₂ 8?', 'basico',
       '¿A qué número hay que elevar 2 para obtener 8?'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale log₂ 8?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3', TRUE),
    ('B', '4', FALSE),
    ('C', '2', FALSE),
    ('D', '16', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale log₂ 8?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG14
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', 'Si 2x ≥ 8, ¿qué valores puede tomar x?', 'basico',
       'Divide entre 2; como es positivo, la desigualdad no cambia.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si 2x ≥ 8, ¿qué valores puede tomar x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x ≤ 4', FALSE),
    ('B', 'x ≥ 4', TRUE),
    ('C', 'x ≥ 6', FALSE),
    ('D', 'x > 16', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si 2x ≥ 8, ¿qué valores puede tomar x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG15
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuáles son las soluciones de x² - 5x + 6 = 0?', 'intermedio',
       'Factoriza como (x - 2)(x - 3) = 0.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuáles son las soluciones de x² - 5x + 6 = 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = -2 y x = -3', FALSE),
    ('B', 'x = 1 y x = 6', FALSE),
    ('C', 'x = 2 y x = 3', TRUE),
    ('D', 'x = 5 y x = 6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuáles son las soluciones de x² - 5x + 6 = 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG16
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuáles son las soluciones de x² - 4x - 5 = 0?', 'intermedio',
       'Busca dos números que multiplicados den -5 y sumados den -4.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuáles son las soluciones de x² - 4x - 5 = 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = -5 y x = 1', FALSE),
    ('B', 'x = 4 y x = 5', FALSE),
    ('C', 'x = 1 y x = 5', FALSE),
    ('D', 'x = 5 y x = -1', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuáles son las soluciones de x² - 4x - 5 = 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG17
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuáles son las soluciones de 2x² - 8 = 0?', 'intermedio',
       'Despeja x² y saca raíz: recuerda las dos raíces.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuáles son las soluciones de 2x² - 8 = 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = 2 y x = -2', TRUE),
    ('B', 'x = 4 y x = -4', FALSE),
    ('C', 'x = 2 únicamente', FALSE),
    ('D', 'x = √8', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuáles son las soluciones de 2x² - 8 = 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG18
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', 'Resuelve el sistema x + y = 10, x - y = 2. ¿Cuánto vale x?', 'intermedio',
       'Suma las dos ecuaciones para eliminar la y.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Resuelve el sistema x + y = 10, x - y = 2. ¿Cuánto vale x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4', FALSE),
    ('B', '6', TRUE),
    ('C', '8', FALSE),
    ('D', '5', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Resuelve el sistema x + y = 10, x - y = 2. ¿Cuánto vale x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG19
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', 'Resuelve el sistema 2x + y = 7, x - y = 2. ¿Cuánto vale y?', 'intermedio',
       'Suma las ecuaciones para hallar x y luego reemplaza.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Resuelve el sistema 2x + y = 7, x - y = 2. ¿Cuánto vale y?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3', FALSE),
    ('B', '2', FALSE),
    ('C', '1', TRUE),
    ('D', '-1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Resuelve el sistema 2x + y = 7, x - y = 2. ¿Cuánto vale y?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG20
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es la factorización de x³ - 8?', 'intermedio',
       'Diferencia de cubos: a³ - b³ = (a - b)(a² + ab + b²).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la factorización de x³ - 8?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(x - 2)³', FALSE),
    ('B', '(x - 2)(x² - 2x + 4)', FALSE),
    ('C', '(x + 2)(x² - 2x + 4)', FALSE),
    ('D', '(x - 2)(x² + 2x + 4)', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la factorización de x³ - 8?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG21
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es la factorización de 4x² - 12x + 9?', 'intermedio',
       'Es un trinomio cuadrado perfecto: revisa si 4x² y 9 son cuadrados.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la factorización de 4x² - 12x + 9?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(2x - 3)²', TRUE),
    ('B', '(2x + 3)²', FALSE),
    ('C', '(4x - 3)(x - 3)', FALSE),
    ('D', '(2x - 3)(2x + 3)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la factorización de 4x² - 12x + 9?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG22
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es la simplificación de (x² - 1)/(x - 1)?', 'intermedio',
       'Factoriza el numerador como diferencia de cuadrados.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la simplificación de (x² - 1)/(x - 1)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x - 1', FALSE),
    ('B', 'x + 1', TRUE),
    ('C', 'x', FALSE),
    ('D', 'x²', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la simplificación de (x² - 1)/(x - 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG23
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es la simplificación de (x² + 3x)/x?', 'intermedio',
       'Saca factor común x en el numerador.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la simplificación de (x² + 3x)/x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x² + 3', FALSE),
    ('B', '3x', FALSE),
    ('C', 'x + 3', TRUE),
    ('D', 'x + 3x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la simplificación de (x² + 3x)/x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG24
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es el resultado de (2x³y²)(3xy⁴)?', 'intermedio',
       'Multiplica los números y suma los exponentes de cada letra.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el resultado de (2x³y²)(3xy⁴)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '5x⁴y⁶', FALSE),
    ('B', '6x³y⁸', FALSE),
    ('C', '6x⁴y⁸', FALSE),
    ('D', '6x⁴y⁶', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el resultado de (2x³y²)(3xy⁴)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG25
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', 'Si log₃ x = 4, ¿cuánto vale x?', 'intermedio',
       'Pásalo a potencia: x = 3⁴.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si log₃ x = 4, ¿cuánto vale x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '81', TRUE),
    ('B', '12', FALSE),
    ('C', '64', FALSE),
    ('D', '7', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si log₃ x = 4, ¿cuánto vale x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG26
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuánto es log 1000 (base 10)?', 'intermedio',
       '10 elevado a qué da 1000.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es log 1000 (base 10)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '100', FALSE),
    ('B', '3', TRUE),
    ('C', '10', FALSE),
    ('D', '4', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es log 1000 (base 10)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG27
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es la ecuación de la recta que pasa por (0, 2) y tiene pendiente -1?', 'intermedio',
       'Usa y = mx + b con m = -1 y b = 2 (corte con el eje y).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la ecuación de la recta que pasa por (0, 2) y tiene pendiente -1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'y = x + 2', FALSE),
    ('B', 'y = -x - 2', FALSE),
    ('C', 'y = -x + 2', TRUE),
    ('D', 'y = 2x - 1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la ecuación de la recta que pasa por (0, 2) y tiene pendiente -1?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG28
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es la pendiente de la recta que pasa por (1, 3) y (4, 9)?', 'intermedio',
       'Pendiente = cambio en y dividido cambio en x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la pendiente de la recta que pasa por (1, 3) y (4, 9)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/2', FALSE),
    ('B', '6', FALSE),
    ('C', '3', FALSE),
    ('D', '2', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la pendiente de la recta que pasa por (1, 3) y (4, 9)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG29
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', 'Si -3x + 1 < 7, ¿qué valores puede tomar x?', 'intermedio',
       'Al dividir entre un número negativo, la desigualdad cambia de sentido.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si -3x + 1 < 7, ¿qué valores puede tomar x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x > -2', TRUE),
    ('B', 'x < -2', FALSE),
    ('C', 'x > 2', FALSE),
    ('D', 'x < 2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si -3x + 1 < 7, ¿qué valores puede tomar x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG30
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es el vértice de la parábola y = (x - 2)² + 3?', 'intermedio',
       'En la forma y = (x - h)² + k, el vértice es (h, k).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el vértice de la parábola y = (x - 2)² + 3?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(-2, 3)', FALSE),
    ('B', '(2, 3)', TRUE),
    ('C', '(2, -3)', FALSE),
    ('D', '(3, 2)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el vértice de la parábola y = (x - 2)² + 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG31
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuáles son las soluciones de x² + 2x - 1 = 0?', 'avanzado',
       'Usa la fórmula cuadrática con a = 1, b = 2, c = -1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuáles son las soluciones de x² + 2x - 1 = 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = 1 ± √2', FALSE),
    ('B', 'x = -1 ± √3', FALSE),
    ('C', 'x = -1 ± √2', TRUE),
    ('D', 'x = -2 ± √2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuáles son las soluciones de x² + 2x - 1 = 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG32
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuántas soluciones reales tiene x² + x + 1 = 0?', 'avanzado',
       'Calcula el discriminante b² - 4ac y mira su signo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuántas soluciones reales tiene x² + x + 1 = 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'Una', FALSE),
    ('B', 'Dos', FALSE),
    ('C', 'Infinitas', FALSE),
    ('D', 'Ninguna', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuántas soluciones reales tiene x² + x + 1 = 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG33
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es la simplificación de (x² - 4)/(x² + 4x + 4)?', 'avanzado',
       'Factoriza: arriba diferencia de cuadrados, abajo trinomio cuadrado perfecto.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la simplificación de (x² - 4)/(x² + 4x + 4)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(x - 2)/(x + 2)', TRUE),
    ('B', '(x + 2)/(x - 2)', FALSE),
    ('C', '-1', FALSE),
    ('D', '1/(x + 2)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la simplificación de (x² - 4)/(x² + 4x + 4)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG34
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es el resultado de 1/x + 1/(x + 1)?', 'avanzado',
       'El denominador común es x(x + 1).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el resultado de 1/x + 1/(x + 1)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2/(2x + 1)', FALSE),
    ('B', '(2x + 1)/(x(x + 1))', TRUE),
    ('C', '1/(x(x + 1))', FALSE),
    ('D', '2/(x(x + 1))', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el resultado de 1/x + 1/(x + 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG35
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', 'Si 2ˣ = 32, ¿cuánto vale x?', 'avanzado',
       'Escribe 32 como potencia de 2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si 2ˣ = 32, ¿cuánto vale x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '16', FALSE),
    ('B', '6', FALSE),
    ('C', '5', TRUE),
    ('D', '4', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si 2ˣ = 32, ¿cuánto vale x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG36
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', 'Si 3ˣ⁺¹ = 27, ¿cuánto vale x?', 'avanzado',
       '27 = 3³, así que iguala los exponentes.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si 3ˣ⁺¹ = 27, ¿cuánto vale x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3', FALSE),
    ('B', '9', FALSE),
    ('C', '1', FALSE),
    ('D', '2', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si 3ˣ⁺¹ = 27, ¿cuánto vale x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG37
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuánto es log₂ 4 + log₂ 8?', 'avanzado',
       'Calcula cada logaritmo por separado (o usa log(a) + log(b) = log(ab)).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto es log₂ 4 + log₂ 8?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '5', TRUE),
    ('B', '12', FALSE),
    ('C', '32', FALSE),
    ('D', '6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto es log₂ 4 + log₂ 8?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG38
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', '¿Cuál es el dominio de f(x) = √(x - 3)?', 'avanzado',
       'Lo de adentro de una raíz cuadrada no puede ser negativo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el dominio de f(x) = √(x - 3)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x > 3', FALSE),
    ('B', 'x ≥ 3', TRUE),
    ('C', 'Todos los reales', FALSE),
    ('D', 'x ≤ 3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el dominio de f(x) = √(x - 3)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG39
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', 'Si |x - 4| = 6, ¿cuáles son los valores de x?', 'avanzado',
       'Hay dos casos: x - 4 = 6 o x - 4 = -6.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si |x - 4| = 6, ¿cuáles son los valores de x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = 10 únicamente', FALSE),
    ('B', 'x = 2 y x = -10', FALSE),
    ('C', 'x = 10 y x = -2', TRUE),
    ('D', 'x = 6 y x = -6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si |x - 4| = 6, ¿cuáles son los valores de x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ALG40
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra', 'Si f(x) = 2x + 1 y g(x) = x², ¿cuánto vale f(g(3))?', 'avanzado',
       'Primero calcula g(3) y ese resultado lo metes en f.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si f(x) = 2x + 1 y g(x) = x², ¿cuánto vale f(g(3))?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '49', FALSE),
    ('B', '13', FALSE),
    ('C', '7', FALSE),
    ('D', '19', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si f(x) = 2x + 1 y g(x) = x², ¿cuánto vale f(g(3))?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- ============================================
-- TRIGONOMETRÍA (40)
-- ============================================

-- TRI1
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale sen 30°?', 'basico',
       'Recuerda el triángulo de 30°-60°-90°: el cateto opuesto a 30° es la mitad de la hipotenusa.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale sen 30°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/2', TRUE),
    ('B', '√3/2', FALSE),
    ('C', '√2/2', FALSE),
    ('D', '1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale sen 30°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI2
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale cos 60°?', 'basico',
       'cos 60° es igual a sen 30°.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale cos 60°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '√3/2', FALSE),
    ('B', '1/2', TRUE),
    ('C', '0', FALSE),
    ('D', '√2/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale cos 60°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI3
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale tan 45°?', 'basico',
       'En 45° el seno y el coseno son iguales.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale tan 45°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', '√3', FALSE),
    ('C', '1', TRUE),
    ('D', '√2/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale tan 45°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI4
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale sen 90°?', 'basico',
       'En el círculo unitario, 90° es el punto más alto: (0, 1).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale sen 90°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', '-1', FALSE),
    ('C', '1/2', FALSE),
    ('D', '1', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale sen 90°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI5
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale cos 0°?', 'basico',
       'En el círculo unitario, 0° corresponde al punto (1, 0).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale cos 0°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', TRUE),
    ('B', '0', FALSE),
    ('C', '-1', FALSE),
    ('D', 'No está definido', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale cos 0°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI6
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale cos 180°?', 'basico',
       '180° es el punto (-1, 0) del círculo unitario.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale cos 180°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '-1', TRUE),
    ('C', '0', FALSE),
    ('D', '1/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale cos 180°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI7
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿A cuántos radianes equivalen 180°?', 'basico',
       'Media vuelta completa equivale a π radianes.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿A cuántos radianes equivalen 180°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2π', FALSE),
    ('B', 'π/2', FALSE),
    ('C', 'π', TRUE),
    ('D', '180π', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿A cuántos radianes equivalen 180°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI8
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿A cuántos radianes equivalen 60°?', 'basico',
       'Multiplica por π/180.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿A cuántos radianes equivalen 60°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'π/6', FALSE),
    ('B', 'π/4', FALSE),
    ('C', '2π/3', FALSE),
    ('D', 'π/3', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿A cuántos radianes equivalen 60°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI9
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿A cuántos grados equivalen π/4 radianes?', 'basico',
       'Multiplica por 180/π.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿A cuántos grados equivalen π/4 radianes?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '45°', TRUE),
    ('B', '90°', FALSE),
    ('C', '30°', FALSE),
    ('D', '60°', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿A cuántos grados equivalen π/4 radianes?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI10
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', 'En un triángulo rectángulo, ¿cómo se define el seno de un ángulo?', 'basico',
       'Recuerda SOH-CAH-TOA: Seno = Opuesto / Hipotenusa.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'En un triángulo rectángulo, ¿cómo se define el seno de un ángulo?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'Cateto adyacente / hipotenusa', FALSE),
    ('B', 'Cateto opuesto / hipotenusa', TRUE),
    ('C', 'Cateto opuesto / cateto adyacente', FALSE),
    ('D', 'Hipotenusa / cateto opuesto', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'En un triángulo rectángulo, ¿cómo se define el seno de un ángulo?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI11
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', 'En un triángulo rectángulo los catetos miden 3 y 4. ¿Cuánto mide la hipotenusa?', 'basico',
       'Usa el teorema de Pitágoras: c² = a² + b².'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'En un triángulo rectángulo los catetos miden 3 y 4. ¿Cuánto mide la hipotenusa?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '7', FALSE),
    ('B', '12', FALSE),
    ('C', '5', TRUE),
    ('D', '√7', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'En un triángulo rectángulo los catetos miden 3 y 4. ¿Cuánto mide la hipotenusa?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI12
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', 'Los ángulos internos de un triángulo suman:', 'basico',
       'Es una propiedad de todo triángulo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Los ángulos internos de un triángulo suman:');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '90°', FALSE),
    ('B', '360°', FALSE),
    ('C', '270°', FALSE),
    ('D', '180°', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Los ángulos internos de un triángulo suman:'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI13
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', 'Si dos ángulos de un triángulo miden 50° y 60°, ¿cuánto mide el tercero?', 'basico',
       'Los tres ángulos suman 180°.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si dos ángulos de un triángulo miden 50° y 60°, ¿cuánto mide el tercero?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '70°', TRUE),
    ('B', '80°', FALSE),
    ('C', '90°', FALSE),
    ('D', '110°', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si dos ángulos de un triángulo miden 50° y 60°, ¿cuánto mide el tercero?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI14
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale sen² x + cos² x?', 'basico',
       'Es la identidad pitagórica fundamental.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale sen² x + cos² x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', '1', TRUE),
    ('C', '2', FALSE),
    ('D', 'sen 2x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale sen² x + cos² x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI15
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale sen 60°?', 'intermedio',
       'En el triángulo 30°-60°-90°, el lado opuesto a 60° es √3 veces el menor.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale sen 60°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/2', FALSE),
    ('B', '√2/2', FALSE),
    ('C', '√3/2', TRUE),
    ('D', '√3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale sen 60°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI16
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale tan 30°?', 'intermedio',
       'tan = sen / cos = (1/2) / (√3/2).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale tan 30°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '√3', FALSE),
    ('B', '1/2', FALSE),
    ('C', '1', FALSE),
    ('D', '√3/3', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale tan 30°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI17
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale sen 135°?', 'intermedio',
       '135° está en el segundo cuadrante, donde el seno es positivo; su ángulo de referencia es 45°.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale sen 135°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '√2/2', TRUE),
    ('B', '-√2/2', FALSE),
    ('C', '1/2', FALSE),
    ('D', '-1/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale sen 135°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI18
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale cos 120°?', 'intermedio',
       '120° está en el segundo cuadrante (coseno negativo) y su ángulo de referencia es 60°.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale cos 120°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/2', FALSE),
    ('B', '-1/2', TRUE),
    ('C', '-√3/2', FALSE),
    ('D', '√3/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale cos 120°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI19
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale sen 210°?', 'intermedio',
       '210° está en el tercer cuadrante, donde el seno es negativo; referencia 30°.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale sen 210°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/2', FALSE),
    ('B', '-√3/2', FALSE),
    ('C', '-1/2', TRUE),
    ('D', '√3/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale sen 210°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI20
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale tan 150°?', 'intermedio',
       'Segundo cuadrante: la tangente es negativa; referencia 30°.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale tan 150°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '√3/3', FALSE),
    ('B', '-√3', FALSE),
    ('C', '√3', FALSE),
    ('D', '-√3/3', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale tan 150°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI21
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿En qué cuadrante el seno es negativo y el coseno positivo?', 'intermedio',
       'Recuerda los signos de x (coseno) e y (seno) en cada cuadrante.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿En qué cuadrante el seno es negativo y el coseno positivo?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'Cuarto', TRUE),
    ('B', 'Primero', FALSE),
    ('C', 'Segundo', FALSE),
    ('D', 'Tercero', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿En qué cuadrante el seno es negativo y el coseno positivo?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI22
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', 'Si sen θ = 3/5 y θ está en el primer cuadrante, ¿cuánto vale cos θ?', 'intermedio',
       'Usa sen² θ + cos² θ = 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si sen θ = 3/5 y θ está en el primer cuadrante, ¿cuánto vale cos θ?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3/4', FALSE),
    ('B', '4/5', TRUE),
    ('C', '5/3', FALSE),
    ('D', '2/5', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si sen θ = 3/5 y θ está en el primer cuadrante, ¿cuánto vale cos θ?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI23
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', 'Si cos θ = 5/13 y θ está en el primer cuadrante, ¿cuánto vale tan θ?', 'intermedio',
       'Halla primero el seno con Pitágoras (13² = 5² + 12²).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si cos θ = 5/13 y θ está en el primer cuadrante, ¿cuánto vale tan θ?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '5/12', FALSE),
    ('B', '12/13', FALSE),
    ('C', '12/5', TRUE),
    ('D', '13/5', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si cos θ = 5/13 y θ está en el primer cuadrante, ¿cuánto vale tan θ?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI24
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', 'Una escalera de 10 m forma 60° con el suelo. ¿A qué altura toca la pared?', 'intermedio',
       'La altura es el cateto opuesto al ángulo de 60°: usa el seno.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Una escalera de 10 m forma 60° con el suelo. ¿A qué altura toca la pared?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '5 m', FALSE),
    ('B', '10√3 m', FALSE),
    ('C', '20 m', FALSE),
    ('D', '5√3 m', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Una escalera de 10 m forma 60° con el suelo. ¿A qué altura toca la pared?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI25
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', 'Desde 20 m de un edificio se ve su punto más alto con un ángulo de 45°. ¿Cuánto mide el edificio?', 'intermedio',
       'tan 45° = altura / 20.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Desde 20 m de un edificio se ve su punto más alto con un ángulo de 45°. ¿Cuánto mide el edificio?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '20 m', TRUE),
    ('B', '10 m', FALSE),
    ('C', '20√2 m', FALSE),
    ('D', '40 m', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Desde 20 m de un edificio se ve su punto más alto con un ángulo de 45°. ¿Cuánto mide el edificio?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI26
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuál es el periodo de la función y = sen x?', 'intermedio',
       'Es lo que tarda la gráfica en repetirse: una vuelta completa.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el periodo de la función y = sen x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'π', FALSE),
    ('B', '2π', TRUE),
    ('C', 'π/2', FALSE),
    ('D', '1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el periodo de la función y = sen x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI27
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuál es la amplitud de y = 3 sen x?', 'intermedio',
       'La amplitud es el número que multiplica al seno.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la amplitud de y = 3 sen x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '6', FALSE),
    ('C', '3', TRUE),
    ('D', '1/3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la amplitud de y = 3 sen x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI28
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿A qué es igual tan x?', 'intermedio',
       'Es la definición de la tangente en términos de seno y coseno.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿A qué es igual tan x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'cos x / sen x', FALSE),
    ('B', '1 / sen x', FALSE),
    ('C', 'sen x · cos x', FALSE),
    ('D', 'sen x / cos x', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿A qué es igual tan x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI29
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿A qué es igual sec x?', 'intermedio',
       'La secante es la recíproca del coseno.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿A qué es igual sec x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1 / cos x', TRUE),
    ('B', '1 / sen x', FALSE),
    ('C', 'cos x / sen x', FALSE),
    ('D', '1 / tan x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿A qué es igual sec x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI30
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuál es el periodo de y = sen(2x)?', 'intermedio',
       'El periodo se divide entre el número que multiplica a la x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el periodo de y = sen(2x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2π', FALSE),
    ('B', 'π', TRUE),
    ('C', '4π', FALSE),
    ('D', 'π/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el periodo de y = sen(2x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI31
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿A qué es igual sen 2x?', 'avanzado',
       'Es la fórmula del ángulo doble para el seno.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿A qué es igual sen 2x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2 sen x', FALSE),
    ('B', 'sen² x - cos² x', FALSE),
    ('C', '2 sen x cos x', TRUE),
    ('D', 'sen x + cos x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿A qué es igual sen 2x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI32
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿A qué es igual cos 2x?', 'avanzado',
       'Es la fórmula del ángulo doble para el coseno.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿A qué es igual cos 2x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2 cos x', FALSE),
    ('B', '2 sen x cos x', FALSE),
    ('C', 'cos² x + sen² x', FALSE),
    ('D', 'cos² x - sen² x', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿A qué es igual cos 2x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI33
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale sen 75°?', 'avanzado',
       'Escribe 75° = 45° + 30° y usa sen(a + b) = sen a cos b + cos a sen b.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale sen 75°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(√6 + √2)/4', TRUE),
    ('B', '(√6 - √2)/4', FALSE),
    ('C', '√3/2', FALSE),
    ('D', '(√3 + 1)/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale sen 75°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI34
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuánto vale cos 15°?', 'avanzado',
       '15° = 45° - 30°; usa cos(a - b) = cos a cos b + sen a sen b.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuánto vale cos 15°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(√6 - √2)/4', FALSE),
    ('B', '(√6 + √2)/4', TRUE),
    ('C', '√3/2', FALSE),
    ('D', '1/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuánto vale cos 15°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI35
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuáles son las soluciones de sen x = 1/2 entre 0° y 360°?', 'avanzado',
       'El seno es positivo en el primer y segundo cuadrante.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuáles son las soluciones de sen x = 1/2 entre 0° y 360°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '30° y 210°', FALSE),
    ('B', '60° y 120°', FALSE),
    ('C', '30° y 150°', TRUE),
    ('D', '30° y 330°', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuáles son las soluciones de sen x = 1/2 entre 0° y 360°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI36
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿Cuáles son las soluciones de 2cos x - 1 = 0 entre 0° y 360°?', 'avanzado',
       'Despeja cos x = 1/2; el coseno es positivo en el primer y cuarto cuadrante.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuáles son las soluciones de 2cos x - 1 = 0 entre 0° y 360°?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '60° y 120°', FALSE),
    ('B', '30° y 330°', FALSE),
    ('C', '120° y 240°', FALSE),
    ('D', '60° y 300°', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuáles son las soluciones de 2cos x - 1 = 0 entre 0° y 360°?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI37
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿A qué se simplifica (1 - cos² x)/sen x?', 'avanzado',
       'Usa la identidad 1 - cos² x = sen² x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿A qué se simplifica (1 - cos² x)/sen x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'sen x', TRUE),
    ('B', 'cos x', FALSE),
    ('C', '1', FALSE),
    ('D', 'tan x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿A qué se simplifica (1 - cos² x)/sen x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI38
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', '¿A qué se simplifica tan x · cos x?', 'avanzado',
       'Escribe la tangente como sen x / cos x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿A qué se simplifica tan x · cos x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'cos x', FALSE),
    ('B', 'sen x', TRUE),
    ('C', '1', FALSE),
    ('D', 'sec x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿A qué se simplifica tan x · cos x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI39
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', 'En un triángulo, a = 8, A = 30° y B = 45°. Por la ley de senos, ¿cuánto mide b?', 'avanzado',
       'Ley de senos: a/sen A = b/sen B.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'En un triángulo, a = 8, A = 30° y B = 45°. Por la ley de senos, ¿cuánto mide b?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4√2', FALSE),
    ('B', '16', FALSE),
    ('C', '8√2', TRUE),
    ('D', '8√3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'En un triángulo, a = 8, A = 30° y B = 45°. Por la ley de senos, ¿cuánto mide b?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- TRI40
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'trigonometria', 'En un triángulo, a = 5, b = 8 y el ángulo entre ellos es 60°. ¿Cuánto mide el lado c?', 'avanzado',
       'Ley del coseno: c² = a² + b² - 2ab cos C.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'En un triángulo, a = 5, b = 8 y el ángulo entre ellos es 60°. ¿Cuánto mide el lado c?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '√89', FALSE),
    ('B', '√129', FALSE),
    ('C', '13', FALSE),
    ('D', '7', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'En un triángulo, a = 5, b = 8 y el ángulo entre ellos es 60°. ¿Cuánto mide el lado c?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);
