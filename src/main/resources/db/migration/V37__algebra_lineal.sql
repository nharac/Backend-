-- ============================================
-- V37: Álgebra lineal como tema de práctica, quiz, foro, tareas y retos (40 preguntas)
-- ============================================
-- Las respuestas se calcularon con aritmética exacta (fracciones) al generar el archivo.
-- Es idempotente: no repite enunciados ni opciones ya existentes.

ALTER TABLE preguntas DROP CONSTRAINT IF EXISTS preguntas_tema_check;
ALTER TABLE preguntas ADD CONSTRAINT preguntas_tema_check
    CHECK (tema IN ('fundamentos', 'algebra', 'trigonometria', 'limites', 'derivadas', 'integrales', 'algebra_lineal'));
ALTER TABLE publicaciones DROP CONSTRAINT IF EXISTS publicaciones_tema_check;
ALTER TABLE publicaciones ADD CONSTRAINT publicaciones_tema_check
    CHECK (tema IN ('fundamentos', 'algebra', 'trigonometria', 'limites', 'derivadas', 'integrales', 'algebra_lineal'));
ALTER TABLE tareas DROP CONSTRAINT IF EXISTS tareas_tema_check;
ALTER TABLE tareas ADD CONSTRAINT tareas_tema_check
    CHECK (tema IN ('fundamentos', 'algebra', 'trigonometria', 'limites', 'derivadas', 'integrales', 'algebra_lineal'));
ALTER TABLE duelos DROP CONSTRAINT IF EXISTS duelos_tema_check;
ALTER TABLE duelos ADD CONSTRAINT duelos_tema_check
    CHECK (tema IN ('fundamentos', 'algebra', 'trigonometria', 'limites', 'derivadas', 'integrales', 'algebra_lineal'));
ALTER TABLE salas_vivo DROP CONSTRAINT IF EXISTS salas_vivo_tema_check;
ALTER TABLE salas_vivo ADD CONSTRAINT salas_vivo_tema_check
    CHECK (tema IN ('fundamentos', 'algebra', 'trigonometria', 'limites', 'derivadas', 'integrales', 'algebra_lineal'));

-- LIN1
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [0 3; 4 −5] y B = [−1 −3; −2 −1], ¿cuál es el elemento de la fila 1, columna 1 de A + B?', 'basico',
       'En la suma de matrices se suman los elementos que están en la misma posición.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [0 3; 4 −5] y B = [−1 −3; −2 −1], ¿cuál es el elemento de la fila 1, columna 1 de A + B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '−1', TRUE),
    ('B', '1', FALSE),
    ('C', '0', FALSE),
    ('D', '−3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [0 3; 4 −5] y B = [−1 −3; −2 −1], ¿cuál es el elemento de la fila 1, columna 1 de A + B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN2
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [3 −5; 4 −2] y B = [3 −5; 6 3], ¿cuál es el elemento de la fila 2, columna 2 de A + B?', 'basico',
       'En la suma de matrices se suman los elementos que están en la misma posición.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [3 −5; 4 −2] y B = [3 −5; 6 3], ¿cuál es el elemento de la fila 2, columna 2 de A + B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '−5', FALSE),
    ('B', '1', TRUE),
    ('C', '−6', FALSE),
    ('D', '2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [3 −5; 4 −2] y B = [3 −5; 6 3], ¿cuál es el elemento de la fila 2, columna 2 de A + B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN3
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [−3 0; 5 −3] y B = [−5 0; 4 −5], ¿cuál es el elemento de la fila 2, columna 1 de A + B?', 'basico',
       'En la suma de matrices se suman los elementos que están en la misma posición.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [−3 0; 5 −3] y B = [−5 0; 4 −5], ¿cuál es el elemento de la fila 2, columna 1 de A + B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '20', FALSE),
    ('C', '9', TRUE),
    ('D', '10', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [−3 0; 5 −3] y B = [−5 0; 4 −5], ¿cuál es el elemento de la fila 2, columna 1 de A + B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN4
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [−3 0; 1 2] y B = [−4 5; −5 −2], ¿cuál es el elemento de la fila 1, columna 1 de A + B?', 'basico',
       'En la suma de matrices se suman los elementos que están en la misma posición.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [−3 0; 1 2] y B = [−4 5; −5 −2], ¿cuál es el elemento de la fila 1, columna 1 de A + B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '12', FALSE),
    ('C', '−6', FALSE),
    ('D', '−7', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [−3 0; 1 2] y B = [−4 5; −5 −2], ¿cuál es el elemento de la fila 1, columna 1 de A + B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN5
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [6 3; 0 −1], ¿cuál es el elemento de la fila 1, columna 2 de 3A?', 'basico',
       'Multiplicar una matriz por un número multiplica cada uno de sus elementos.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [6 3; 0 −1], ¿cuál es el elemento de la fila 1, columna 2 de 3A?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '9', TRUE),
    ('B', '6', FALSE),
    ('C', '3', FALSE),
    ('D', '−9', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [6 3; 0 −1], ¿cuál es el elemento de la fila 1, columna 2 de 3A?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN6
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [−2 1; −5 −4], ¿cuál es el elemento de la fila 1, columna 1 de −2A?', 'basico',
       'Multiplicar una matriz por un número multiplica cada uno de sus elementos.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [−2 1; −5 −4], ¿cuál es el elemento de la fila 1, columna 1 de −2A?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '−4', FALSE),
    ('B', '4', TRUE),
    ('C', '−2', FALSE),
    ('D', '5', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [−2 1; −5 −4], ¿cuál es el elemento de la fila 1, columna 1 de −2A?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN7
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [−1 −3; 1 2], ¿cuál es el elemento de la fila 2, columna 2 de 2A?', 'basico',
       'Multiplicar una matriz por un número multiplica cada uno de sus elementos.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [−1 −3; 1 2], ¿cuál es el elemento de la fila 2, columna 2 de 2A?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2', FALSE),
    ('B', '−4', FALSE),
    ('C', '4', TRUE),
    ('D', '5', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [−1 −3; 1 2], ¿cuál es el elemento de la fila 2, columna 2 de 2A?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN8
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Cuál es el determinante de A = [−2 −5; −3 −2]?', 'basico',
       'Para una matriz 2×2 [a b; c d]: det = ad − bc.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el determinante de A = [−2 −5; −3 −2]?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '19', FALSE),
    ('B', '11', FALSE),
    ('C', '4', FALSE),
    ('D', '−11', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el determinante de A = [−2 −5; −3 −2]?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN9
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Cuál es el determinante de A = [3 −3; −4 3]?', 'basico',
       'Para una matriz 2×2 [a b; c d]: det = ad − bc.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el determinante de A = [3 −3; −4 3]?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '−3', TRUE),
    ('B', '21', FALSE),
    ('C', '3', FALSE),
    ('D', '−2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el determinante de A = [3 −3; −4 3]?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN10
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Cuál es el determinante de A = [1 1; 2 −1]?', 'basico',
       'Para una matriz 2×2 [a b; c d]: det = ad − bc.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el determinante de A = [1 1; 2 −1]?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '−3', TRUE),
    ('C', '3', FALSE),
    ('D', '−2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el determinante de A = [1 1; 2 −1]?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN11
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Cuál es el determinante de A = [−2 −5; 4 6]?', 'basico',
       'Para una matriz 2×2 [a b; c d]: det = ad − bc.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el determinante de A = [−2 −5; 4 6]?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '−32', FALSE),
    ('B', '−8', FALSE),
    ('C', '8', TRUE),
    ('D', '−14', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el determinante de A = [−2 −5; 4 6]?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN12
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A es una matriz 2×3, ¿de qué tamaño es su transpuesta Aᵀ?', 'basico',
       'La transpuesta intercambia filas y columnas.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A es una matriz 2×3, ¿de qué tamaño es su transpuesta Aᵀ?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2×3', FALSE),
    ('B', '3×3', FALSE),
    ('C', '2×2', FALSE),
    ('D', '3×2', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A es una matriz 2×3, ¿de qué tamaño es su transpuesta Aᵀ?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN13
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Qué condición deben cumplir A y B para poder calcular A + B?', 'basico',
       'La suma se hace posición por posición.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Qué condición deben cumplir A y B para poder calcular A + B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'Tener el mismo tamaño', TRUE),
    ('B', 'Ser cuadradas', FALSE),
    ('C', 'Tener determinante distinto de cero', FALSE),
    ('D', 'Que las columnas de A sean iguales a las filas de B', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Qué condición deben cumplir A y B para poder calcular A + B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN14
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Cuál es la traza de A = [5 5 0; −4 −4 −1; −1 3 1]? (La traza es la suma de la diagonal principal.)', 'basico',
       'Suma solo a₁₁ + a₂₂ + a₃₃.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la traza de A = [5 5 0; −4 −4 −1; −1 3 1]? (La traza es la suma de la diagonal principal.)');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '−5', FALSE),
    ('B', '2', TRUE),
    ('C', '10', FALSE),
    ('D', '3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la traza de A = [5 5 0; −4 −4 −1; −1 3 1]? (La traza es la suma de la diagonal principal.)'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN15
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [4 4; 0 −1] y B = [0 −2; −4 3], ¿cuál es el elemento de la fila 2, columna 1 de A·B?', 'intermedio',
       'Multiplica la fila 2 de A por la columna 1 de B, elemento a elemento, y suma.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [4 4; 0 −1] y B = [0 −2; −4 3], ¿cuál es el elemento de la fila 2, columna 1 de A·B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', '2', FALSE),
    ('C', '4', TRUE),
    ('D', '6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [4 4; 0 −1] y B = [0 −2; −4 3], ¿cuál es el elemento de la fila 2, columna 1 de A·B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN16
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [−2 5; −3 −1] y B = [3 0; 3 2], ¿cuál es el elemento de la fila 2, columna 2 de A·B?', 'intermedio',
       'Multiplica la fila 2 de A por la columna 2 de B, elemento a elemento, y suma.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [−2 5; −3 −1] y B = [3 0; 3 2], ¿cuál es el elemento de la fila 2, columna 2 de A·B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '−11', FALSE),
    ('B', '0', FALSE),
    ('C', '−1', FALSE),
    ('D', '−2', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [−2 5; −3 −1] y B = [3 0; 3 2], ¿cuál es el elemento de la fila 2, columna 2 de A·B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN17
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [2 4; 1 5] y B = [3 4; 3 −4], ¿cuál es el elemento de la fila 2, columna 1 de A·B?', 'intermedio',
       'Multiplica la fila 2 de A por la columna 1 de B, elemento a elemento, y suma.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [2 4; 1 5] y B = [3 4; 3 −4], ¿cuál es el elemento de la fila 2, columna 1 de A·B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '18', TRUE),
    ('B', '3', FALSE),
    ('C', '23', FALSE),
    ('D', '20', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [2 4; 1 5] y B = [3 4; 3 −4], ¿cuál es el elemento de la fila 2, columna 1 de A·B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN18
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [3 0; 5 4] y B = [0 −4; −4 2], ¿cuál es el elemento de la fila 2, columna 1 de A·B?', 'intermedio',
       'Multiplica la fila 2 de A por la columna 1 de B, elemento a elemento, y suma.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [3 0; 5 4] y B = [0 −4; −4 2], ¿cuál es el elemento de la fila 2, columna 1 de A·B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '−20', FALSE),
    ('B', '−16', TRUE),
    ('C', '−14', FALSE),
    ('D', '−15', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [3 0; 5 4] y B = [0 −4; −4 2], ¿cuál es el elemento de la fila 2, columna 1 de A·B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN19
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [1 −1; 1 5] y B = [−2 3; 5 4], ¿cuál es el elemento de la fila 2, columna 2 de A·B?', 'intermedio',
       'Multiplica la fila 2 de A por la columna 2 de B, elemento a elemento, y suma.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [1 −1; 1 5] y B = [−2 3; 5 4], ¿cuál es el elemento de la fila 2, columna 2 de A·B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '20', FALSE),
    ('B', '25', FALSE),
    ('C', '23', TRUE),
    ('D', '24', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [1 −1; 1 5] y B = [−2 3; 5 4], ¿cuál es el elemento de la fila 2, columna 2 de A·B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN20
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A es 2×3 y B es 3×4, ¿de qué tamaño es A·B?', 'intermedio',
       '(m×n)·(n×p) da m×p: las del medio deben coincidir.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A es 2×3 y B es 3×4, ¿de qué tamaño es A·B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3×3', FALSE),
    ('B', '4×2', FALSE),
    ('C', 'No se puede multiplicar', FALSE),
    ('D', '2×4', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A es 2×3 y B es 3×4, ¿de qué tamaño es A·B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN21
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A es 3×2 y B es 3×2, ¿se puede calcular A·B?', 'intermedio',
       'Para multiplicar, columnas de la primera = filas de la segunda.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A es 3×2 y B es 3×2, ¿se puede calcular A·B?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'No, porque las columnas de A (2) no coinciden con las filas de B (3)', TRUE),
    ('B', 'Sí, y da 3×3', FALSE),
    ('C', 'Sí, y da 2×2', FALSE),
    ('D', 'Sí, porque tienen el mismo tamaño', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A es 3×2 y B es 3×2, ¿se puede calcular A·B?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN22
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [4 3; 6 5], ¿cuál es el elemento de la fila 2, columna 2 de A⁻¹?', 'intermedio',
       'A⁻¹ = (1/det A)·[d −b; −c a]: cambia a y d, niega b y c y divide entre el determinante.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [4 3; 6 5], ¿cuál es el elemento de la fila 2, columna 2 de A⁻¹?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/5', FALSE),
    ('B', '2', TRUE),
    ('C', '−2', FALSE),
    ('D', '5/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [4 3; 6 5], ¿cuál es el elemento de la fila 2, columna 2 de A⁻¹?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN23
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [1 0; 1 5], ¿cuál es el elemento de la fila 2, columna 2 de A⁻¹?', 'intermedio',
       'A⁻¹ = (1/det A)·[d −b; −c a]: cambia a y d, niega b y c y divide entre el determinante.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [1 0; 1 5], ¿cuál es el elemento de la fila 2, columna 2 de A⁻¹?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '−1/5', FALSE),
    ('B', '1', FALSE),
    ('C', '1/5', TRUE),
    ('D', '1/6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [1 0; 1 5], ¿cuál es el elemento de la fila 2, columna 2 de A⁻¹?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN24
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si A = [−1 2; −3 2], ¿cuál es el elemento de la fila 2, columna 2 de A⁻¹?', 'intermedio',
       'A⁻¹ = (1/det A)·[d −b; −c a]: cambia a y d, niega b y c y divide entre el determinante.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si A = [−1 2; −3 2], ¿cuál es el elemento de la fila 2, columna 2 de A⁻¹?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/2', FALSE),
    ('B', '1/4', FALSE),
    ('C', '−1/5', FALSE),
    ('D', '−1/4', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si A = [−1 2; −3 2], ¿cuál es el elemento de la fila 2, columna 2 de A⁻¹?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN25
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Para qué valor de k la matriz [−3 4; −3 k] NO tiene inversa?', 'intermedio',
       'No tiene inversa cuando el determinante vale 0: a·k − b·c = 0.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Para qué valor de k la matriz [−3 4; −3 k] NO tiene inversa?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'k = 4', TRUE),
    ('B', 'k = −4', FALSE),
    ('C', 'k = 5', FALSE),
    ('D', 'k = −12', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Para qué valor de k la matriz [−3 4; −3 k] NO tiene inversa?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN26
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Para qué valor de k la matriz [−3 5; −3 k] NO tiene inversa?', 'intermedio',
       'No tiene inversa cuando el determinante vale 0: a·k − b·c = 0.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Para qué valor de k la matriz [−3 5; −3 k] NO tiene inversa?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'k = −5', FALSE),
    ('B', 'k = 5', TRUE),
    ('C', 'k = 6', FALSE),
    ('D', 'k = −15', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Para qué valor de k la matriz [−3 5; −3 k] NO tiene inversa?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN27
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Para qué valor de k la matriz [−5 0; −3 k] NO tiene inversa?', 'intermedio',
       'No tiene inversa cuando el determinante vale 0: a·k − b·c = 0.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Para qué valor de k la matriz [−5 0; −3 k] NO tiene inversa?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'k = 1', FALSE),
    ('B', 'k = −1', FALSE),
    ('C', 'k = 0', TRUE),
    ('D', 'k = −2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Para qué valor de k la matriz [−5 0; −3 k] NO tiene inversa?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN28
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Resuelve por Gauss-Jordan el sistema: −2x − 4y = 18; 3x + 4y = −21', 'intermedio',
       'Escribe la matriz aumentada y haz operaciones de fila hasta tener la identidad a la izquierda.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Resuelve por Gauss-Jordan el sistema: −2x − 4y = 18; 3x + 4y = −21');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = 3, y = −3', FALSE),
    ('B', 'x = −3, y = 3', FALSE),
    ('C', 'x = −3, y = −2', FALSE),
    ('D', 'x = −3, y = −3', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Resuelve por Gauss-Jordan el sistema: −2x − 4y = 18; 3x + 4y = −21'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN29
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Resuelve por Gauss-Jordan el sistema: −4x + y = 15; 2x − 3y = 5', 'intermedio',
       'Escribe la matriz aumentada y haz operaciones de fila hasta tener la identidad a la izquierda.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Resuelve por Gauss-Jordan el sistema: −4x + y = 15; 2x − 3y = 5');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = −5, y = −5', TRUE),
    ('B', 'x = 5, y = −5', FALSE),
    ('C', 'x = −5, y = 5', FALSE),
    ('D', 'x = −5, y = −4', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Resuelve por Gauss-Jordan el sistema: −4x + y = 15; 2x − 3y = 5'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN30
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Resuelve por Gauss-Jordan el sistema: 4y = −4; x − 4y = 9', 'intermedio',
       'Escribe la matriz aumentada y haz operaciones de fila hasta tener la identidad a la izquierda.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Resuelve por Gauss-Jordan el sistema: 4y = −4; x − 4y = 9');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = −1, y = 5', FALSE),
    ('B', 'x = 5, y = −1', TRUE),
    ('C', 'x = −5, y = −1', FALSE),
    ('D', 'x = 5, y = 1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Resuelve por Gauss-Jordan el sistema: 4y = −4; x − 4y = 9'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN31
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Cuál es el determinante de A = [1 2 1; 0 4 1; −2 −3 1]?', 'avanzado',
       'Desarrolla por la primera fila con signos + − + y los menores 2×2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el determinante de A = [1 2 1; 0 4 1; −2 −3 1]?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '−11', FALSE),
    ('B', '12', FALSE),
    ('C', '11', TRUE),
    ('D', '4', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el determinante de A = [1 2 1; 0 4 1; −2 −3 1]?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN32
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Cuál es el determinante de A = [3 1 −3; 1 0 2; 1 3 2]?', 'avanzado',
       'Desarrolla por la primera fila con signos + − + y los menores 2×2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el determinante de A = [3 1 −3; 1 0 2; 1 3 2]?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '27', FALSE),
    ('B', '−24', FALSE),
    ('C', '0', FALSE),
    ('D', '−27', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el determinante de A = [3 1 −3; 1 0 2; 1 3 2]?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN33
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Cuál es el determinante de A = [1 0 2; 4 −2 1; 0 4 0]?', 'avanzado',
       'Desarrolla por la primera fila con signos + − + y los menores 2×2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el determinante de A = [1 0 2; 4 −2 1; 0 4 0]?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '28', TRUE),
    ('B', '−28', FALSE),
    ('C', '29', FALSE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el determinante de A = [1 0 2; 4 −2 1; 0 4 0]?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN34
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', '¿Cuál es el determinante de A = [3 2 −2; 1 4 1; 4 3 1]?', 'avanzado',
       'Desarrolla por la primera fila con signos + − + y los menores 2×2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el determinante de A = [3 2 −2; 1 4 1; 4 3 1]?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '−35', FALSE),
    ('B', '35', TRUE),
    ('C', '38', FALSE),
    ('D', '12', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el determinante de A = [3 2 −2; 1 4 1; 4 3 1]?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN35
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Resuelve por Gauss-Jordan: −2x − y = 0; x + 2y − 2z = −1; −2x − y + 2z = 4', 'avanzado',
       'Pivote en 1 y ceros arriba y abajo, columna por columna. Verifica reemplazando en las tres ecuaciones.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Resuelve por Gauss-Jordan: −2x − y = 0; x + 2y − 2z = −1; −2x − y + 2z = 4');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = 2, y = −1, z = 2', FALSE),
    ('B', 'x = 1, y = 2, z = −2', FALSE),
    ('C', 'x = −1, y = 2, z = 2', TRUE),
    ('D', 'x = −1, y = 2, z = 3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Resuelve por Gauss-Jordan: −2x − y = 0; x + 2y − 2z = −1; −2x − y + 2z = 4'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN36
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Resuelve por Gauss-Jordan: x + 3y − 2z = −3; −x + 3y − z = 0; 2x − y − z = −1', 'avanzado',
       'Pivote en 1 y ceros arriba y abajo, columna por columna. Verifica reemplazando en las tres ecuaciones.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Resuelve por Gauss-Jordan: x + 3y − 2z = −3; −x + 3y − z = 0; 2x − y − z = −1');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = −2, y = −3, z = −3', FALSE),
    ('B', 'x = −3, y = −3, z = −2', FALSE),
    ('C', 'x = 3, y = −2, z = 3', FALSE),
    ('D', 'x = −3, y = −2, z = −3', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Resuelve por Gauss-Jordan: x + 3y − 2z = −3; −x + 3y − z = 0; 2x − y − z = −1'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN37
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Resuelve por Gauss-Jordan: 2x − 2y − z = 4; x − z = 4; 3x + y − 2z = 11', 'avanzado',
       'Pivote en 1 y ceros arriba y abajo, columna por columna. Verifica reemplazando en las tres ecuaciones.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Resuelve por Gauss-Jordan: 2x − 2y − z = 4; x − z = 4; 3x + y − 2z = 11');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = 2, y = 1, z = −2', TRUE),
    ('B', 'x = 1, y = 2, z = −2', FALSE),
    ('C', 'x = 2, y = −2, z = 1', FALSE),
    ('D', 'x = −2, y = 1, z = 2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Resuelve por Gauss-Jordan: 2x − 2y − z = 4; x − z = 4; 3x + y − 2z = 11'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN38
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si det(A) = 3 y A es 3×3, ¿cuánto vale det(2A)?', 'avanzado',
       'Al multiplicar una matriz n×n por k, el determinante se multiplica por kⁿ: 2³ · 3.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si det(A) = 3 y A es 3×3, ¿cuánto vale det(2A)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '6', FALSE),
    ('B', '24', TRUE),
    ('C', '12', FALSE),
    ('D', '9', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si det(A) = 3 y A es 3×3, ¿cuánto vale det(2A)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN39
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si det(A) = 5, ¿cuánto vale det(A⁻¹)?', 'avanzado',
       'det(A⁻¹) = 1 / det(A).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si det(A) = 5, ¿cuánto vale det(A⁻¹)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '5', FALSE),
    ('B', '−5', FALSE),
    ('C', '1/5', TRUE),
    ('D', '25', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si det(A) = 5, ¿cuánto vale det(A⁻¹)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- LIN40
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'algebra_lineal', 'Si al aplicar Gauss-Jordan a un sistema aparece la fila [0 0 0 | 4], ¿qué significa?', 'avanzado',
       'Esa fila dice 0 = 4, lo cual es imposible.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si al aplicar Gauss-Jordan a un sistema aparece la fila [0 0 0 | 4], ¿qué significa?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'El sistema tiene infinitas soluciones', FALSE),
    ('B', 'La solución es 4', FALSE),
    ('C', 'Hay que intercambiar filas', FALSE),
    ('D', 'El sistema no tiene solución', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si al aplicar Gauss-Jordan a un sistema aparece la fila [0 0 0 | 4], ¿qué significa?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);
