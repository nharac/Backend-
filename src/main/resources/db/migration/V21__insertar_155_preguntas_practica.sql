-- ============================================
-- V21: 155 preguntas nuevas tipo ICFES para completar 200 en el banco de práctica
-- ============================================
-- Con las 45 de V3 y V13 quedan 200 en total: 67 de límites, 67 de derivadas y 66 de integrales,
-- repartidas entre básicas, intermedias y avanzadas. Cada pregunta trae una pista.
-- Todas las respuestas se verificaron con cálculo simbólico (límites, derivadas e integrales).
-- Es idempotente como V3 y V13: si ya existe una pregunta con el mismo enunciado, no se vuelve a insertar,
-- y solo se agregan opciones a preguntas que aún no tienen ninguna.
-- La letra de la opción correcta va rotando para que no siempre sea la misma.


-- ============================================
-- LÍMITES (52)
-- ============================================

-- L16
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = x³ - 2x + 1 cuando x → 2?', 'basico',
       'Es un polinomio: reemplaza x por 2 directamente.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = x³ - 2x + 1 cuando x → 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '5', TRUE),
    ('B', '9', FALSE),
    ('C', '3', FALSE),
    ('D', '1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = x³ - 2x + 1 cuando x → 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L17
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x + 4)/(x - 1) cuando x → 3?', 'basico',
       'El denominador no se anula en x = 3, así que basta con evaluar.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x + 4)/(x - 1) cuando x → 3?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '7', FALSE),
    ('B', '7/2', TRUE),
    ('C', '2', FALSE),
    ('D', '-7/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x + 4)/(x - 1) cuando x → 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L18
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = √(x + 5) cuando x → 4?', 'basico',
       'La raíz es continua donde su argumento es positivo: evalúa en x = 4.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = √(x + 5) cuando x → 4?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '9', FALSE),
    ('B', '√5', FALSE),
    ('C', '3', TRUE),
    ('D', '2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = √(x + 5) cuando x → 4?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L19
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = 4x² - x cuando x → -1?', 'basico',
       'Cuidado con los signos al elevar -1 al cuadrado.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = 4x² - x cuando x → -1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3', FALSE),
    ('B', '-5', FALSE),
    ('C', '-3', FALSE),
    ('D', '5', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = 4x² - x cuando x → -1?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L20
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = 7 cuando x → 10?', 'basico',
       'El límite de una función constante es la misma constante.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = 7 cuando x → 10?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '7', TRUE),
    ('B', '10', FALSE),
    ('C', '70', FALSE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = 7 cuando x → 10?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L21
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x² - 9)/(x - 3) cuando x → 3?', 'basico',
       'Factoriza x² - 9 como diferencia de cuadrados y simplifica.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x² - 9)/(x - 3) cuando x → 3?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', '6', TRUE),
    ('C', '9', FALSE),
    ('D', '3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 9)/(x - 3) cuando x → 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L22
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x² - 25)/(x + 5) cuando x → -5?', 'basico',
       'x² - 25 = (x - 5)(x + 5). Simplifica antes de reemplazar.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x² - 25)/(x + 5) cuando x → -5?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '10', FALSE),
    ('B', '0', FALSE),
    ('C', '-10', TRUE),
    ('D', '-5', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 25)/(x + 5) cuando x → -5?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L23
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = 3/x² cuando x → ∞?', 'basico',
       'Si el denominador crece sin límite y el numerador es fijo, la fracción se hace muy pequeña.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = 3/x² cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3', FALSE),
    ('B', '∞', FALSE),
    ('C', '1/3', FALSE),
    ('D', '0', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = 3/x² cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L24
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (2x + 1)/(x - 3) cuando x → ∞?', 'basico',
       'Mismo grado arriba y abajo: divide los coeficientes principales.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (2x + 1)/(x - 3) cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2', TRUE),
    ('B', '1/3', FALSE),
    ('C', '-1/3', FALSE),
    ('D', '∞', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (2x + 1)/(x - 3) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L25
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = cos(x) cuando x → 0?', 'basico',
       'El coseno es continuo: evalúa cos(0).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = cos(x) cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', '1', TRUE),
    ('C', '-1', FALSE),
    ('D', 'No existe', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = cos(x) cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L26
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = e^x cuando x → 0?', 'basico',
       'La exponencial es continua en todos los reales.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = e^x cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', 'e', FALSE),
    ('C', '1', TRUE),
    ('D', 'No existe', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = e^x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L27
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = ln(x) cuando x → e?', 'basico',
       'Recuerda que ln(e) es el exponente al que hay que elevar e para obtener e.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = ln(x) cuando x → e?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', 'e', FALSE),
    ('C', 'No existe', FALSE),
    ('D', '1', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = ln(x) cuando x → e?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L28
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x² + 2x)/x cuando x → 0?', 'basico',
       'Saca x como factor común en el numerador.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x² + 2x)/x cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2', TRUE),
    ('B', '0', FALSE),
    ('C', '1', FALSE),
    ('D', 'No existe', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² + 2x)/x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L29
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = x² - 4x + 4 cuando x → 2?', 'basico',
       'Es un polinomio; también puedes verlo como (x - 2)².'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = x² - 4x + 4 cuando x → 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4', FALSE),
    ('B', '0', TRUE),
    ('C', '-4', FALSE),
    ('D', '8', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = x² - 4x + 4 cuando x → 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L30
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = 1/(x - 2) cuando x → 2⁺ (por la derecha)?', 'basico',
       'Prueba con valores como 2.1, 2.01, 2.001: ¿el denominador es positivo o negativo?'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = 1/(x - 2) cuando x → 2⁺ (por la derecha)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-∞', FALSE),
    ('B', '0', FALSE),
    ('C', '∞', TRUE),
    ('D', '1/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = 1/(x - 2) cuando x → 2⁺ (por la derecha)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L31
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = 1/(x - 2) cuando x → 2⁻ (por la izquierda)?', 'basico',
       'Prueba con valores como 1.9, 1.99, 1.999: el denominador es negativo y muy pequeño.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = 1/(x - 2) cuando x → 2⁻ (por la izquierda)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '∞', FALSE),
    ('B', '0', FALSE),
    ('C', '-1/2', FALSE),
    ('D', '-∞', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = 1/(x - 2) cuando x → 2⁻ (por la izquierda)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L32
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x + 1)² cuando x → -1?', 'basico',
       'Polinomio: reemplaza directamente.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x + 1)² cuando x → -1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', TRUE),
    ('B', '1', FALSE),
    ('C', '4', FALSE),
    ('D', '-1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x + 1)² cuando x → -1?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L33
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = 5x/(x + 5) cuando x → ∞?', 'basico',
       'Divide numerador y denominador entre x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = 5x/(x + 5) cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '5', TRUE),
    ('C', '0', FALSE),
    ('D', '∞', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = 5x/(x + 5) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L34
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x² - 5x + 6)/(x - 2) cuando x → 2?', 'intermedio',
       'Factoriza el numerador: busca dos números que multiplicados den 6 y sumados -5.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x² - 5x + 6)/(x - 2) cuando x → 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '0', FALSE),
    ('C', '-1', TRUE),
    ('D', 'No existe', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 5x + 6)/(x - 2) cuando x → 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L35
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x² + 3x - 10)/(x - 2) cuando x → 2?', 'intermedio',
       'x² + 3x - 10 = (x + 5)(x - 2).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x² + 3x - 10)/(x - 2) cuando x → 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-3', FALSE),
    ('B', '0', FALSE),
    ('C', '5', FALSE),
    ('D', '7', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² + 3x - 10)/(x - 2) cuando x → 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L36
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x³ - 1)/(x - 1) cuando x → 1?', 'intermedio',
       'Usa la diferencia de cubos: a³ - b³ = (a - b)(a² + ab + b²).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x³ - 1)/(x - 1) cuando x → 1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3', TRUE),
    ('B', '1', FALSE),
    ('C', '0', FALSE),
    ('D', '2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x³ - 1)/(x - 1) cuando x → 1?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L37
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (√(x + 1) - 1)/x cuando x → 0?', 'intermedio',
       'Multiplica por el conjugado √(x + 1) + 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (√(x + 1) - 1)/x cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '1/2', TRUE),
    ('C', '0', FALSE),
    ('D', '2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (√(x + 1) - 1)/x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L38
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (√x - 3)/(x - 9) cuando x → 9?', 'intermedio',
       'x - 9 = (√x - 3)(√x + 3).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (√x - 3)/(x - 9) cuando x → 9?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/3', FALSE),
    ('B', '6', FALSE),
    ('C', '1/6', TRUE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (√x - 3)/(x - 9) cuando x → 9?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L39
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = sen(5x)/(2x) cuando x → 0?', 'intermedio',
       'Usa que sen(u)/u → 1 cuando u → 0 y ajusta las constantes.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = sen(5x)/(2x) cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2/5', FALSE),
    ('B', '5', FALSE),
    ('C', '1', FALSE),
    ('D', '5/2', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = sen(5x)/(2x) cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L40
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = tan(x)/x cuando x → 0?', 'intermedio',
       'Escribe tan(x) = sen(x)/cos(x) y usa el límite notable de sen(x)/x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = tan(x)/x cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', TRUE),
    ('B', '0', FALSE),
    ('C', '∞', FALSE),
    ('D', 'No existe', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = tan(x)/x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L41
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (4x² - 3)/(2x² + 5x) cuando x → ∞?', 'intermedio',
       'Mismo grado: compara los coeficientes de x².'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (4x² - 3)/(2x² + 5x) cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4', FALSE),
    ('B', '2', TRUE),
    ('C', '-3/5', FALSE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (4x² - 3)/(2x² + 5x) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L42
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x² + 1)/(x³ - 2) cuando x → ∞?', 'intermedio',
       'El grado del denominador es mayor que el del numerador.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x² + 1)/(x³ - 2) cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '∞', FALSE),
    ('C', '0', TRUE),
    ('D', '-1/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² + 1)/(x³ - 2) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L43
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x³ + x)/(x² + 4) cuando x → ∞?', 'intermedio',
       'El grado del numerador es mayor que el del denominador.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x³ + x)/(x² + 4) cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '0', FALSE),
    ('C', '1/4', FALSE),
    ('D', '∞', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x³ + x)/(x² + 4) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L44
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (e^x - 1)/x cuando x → 0?', 'intermedio',
       'Es un límite notable; también es la definición de la derivada de e^x en 0.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (e^x - 1)/x cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', TRUE),
    ('B', '0', FALSE),
    ('C', 'e', FALSE),
    ('D', '∞', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (e^x - 1)/x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L45
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (1 + 2/x)^x cuando x → ∞?', 'intermedio',
       'Recuerda que (1 + k/x)^x → e^k.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (1 + 2/x)^x cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e', FALSE),
    ('B', 'e²', TRUE),
    ('C', '2e', FALSE),
    ('D', '1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 + 2/x)^x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L46
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = |x|/x cuando x → 0?', 'intermedio',
       'Calcula los límites laterales por separado: ¿coinciden?'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = |x|/x cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '-1', FALSE),
    ('C', 'No existe', TRUE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = |x|/x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L47
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x - 4)/(√x - 2) cuando x → 4?', 'intermedio',
       'x - 4 = (√x - 2)(√x + 2).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x - 4)/(√x - 2) cuando x → 4?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2', FALSE),
    ('B', '0', FALSE),
    ('C', '1/4', FALSE),
    ('D', '4', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x - 4)/(√x - 2) cuando x → 4?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L48
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (2x² - 8)/(x + 2) cuando x → -2?', 'intermedio',
       'Saca factor común 2 y factoriza la diferencia de cuadrados.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (2x² - 8)/(x + 2) cuando x → -2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-8', TRUE),
    ('B', '8', FALSE),
    ('C', '0', FALSE),
    ('D', '-4', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (2x² - 8)/(x + 2) cuando x → -2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L49
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = sen(x)/(3x) cuando x → 0?', 'intermedio',
       'Separa la constante: (1/3)·sen(x)/x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = sen(x)/(3x) cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3', FALSE),
    ('B', '1/3', TRUE),
    ('C', '1', FALSE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = sen(x)/(3x) cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L50
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (1/x - 1/2)/(x - 2) cuando x → 2?', 'intermedio',
       'Resta las fracciones del numerador: 1/x - 1/2 = (2 - x)/(2x).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (1/x - 1/2)/(x - 2) cuando x → 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/4', FALSE),
    ('B', '-1/2', FALSE),
    ('C', '-1/4', TRUE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1/x - 1/2)/(x - 2) cuando x → 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L51
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = x · sen(1/x) cuando x → ∞?', 'intermedio',
       'Haz el cambio u = 1/x: queda sen(u)/u con u → 0.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = x · sen(1/x) cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', '∞', FALSE),
    ('C', 'No existe', FALSE),
    ('D', '1', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = x · sen(1/x) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L52
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (3x - 1)/√(x² + 1) cuando x → ∞?', 'intermedio',
       'Para x positivo grande, √(x² + 1) se comporta como x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (3x - 1)/√(x² + 1) cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3', TRUE),
    ('B', '1', FALSE),
    ('C', '-1', FALSE),
    ('D', '∞', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (3x - 1)/√(x² + 1) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L53
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x² - 1)/(x² + x - 2) cuando x → 1?', 'intermedio',
       'Factoriza ambos: (x - 1)(x + 1) y (x - 1)(x + 2).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x² - 1)/(x² + x - 2) cuando x → 1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', '2/3', TRUE),
    ('C', '1', FALSE),
    ('D', '3/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 1)/(x² + x - 2) cuando x → 1?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L54
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es la asíntota horizontal de f(x) = (6x² + 1)/(3x² - x)?', 'intermedio',
       'Calcula el límite de f(x) cuando x → ∞.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la asíntota horizontal de f(x) = (6x² + 1)/(3x² - x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'y = 6', FALSE),
    ('B', 'x = 0', FALSE),
    ('C', 'y = 2', TRUE),
    ('D', 'y = 0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la asíntota horizontal de f(x) = (6x² + 1)/(3x² - x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L55
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (1 - cos(2x))/x² cuando x → 0?', 'avanzado',
       'Usa la identidad 1 - cos(2x) = 2sen²(x).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (1 - cos(2x))/x² cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '1/2', FALSE),
    ('C', '0', FALSE),
    ('D', '2', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 - cos(2x))/x² cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L56
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (e^(2x) - 1)/sen(x) cuando x → 0?', 'avanzado',
       'Divide arriba y abajo entre x y usa dos límites notables.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (e^(2x) - 1)/sen(x) cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2', TRUE),
    ('B', '1', FALSE),
    ('C', '1/2', FALSE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (e^(2x) - 1)/sen(x) cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L57
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = √(x² + x) - x cuando x → ∞?', 'avanzado',
       'Multiplica y divide por el conjugado √(x² + x) + x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = √(x² + x) - x cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', '1/2', TRUE),
    ('C', '1', FALSE),
    ('D', '∞', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = √(x² + x) - x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L58
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (1 + 1/x)^(3x) cuando x → ∞?', 'avanzado',
       'Escribe la expresión como [(1 + 1/x)^x]³.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (1 + 1/x)^(3x) cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e', FALSE),
    ('B', '3e', FALSE),
    ('C', 'e³', TRUE),
    ('D', '1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 + 1/x)^(3x) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L59
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = x · ln(x) cuando x → 0⁺ (por la derecha)?', 'avanzado',
       'Escríbelo como ln(x)/(1/x) y aplica L''Hôpital.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = x · ln(x) cuando x → 0⁺ (por la derecha)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-∞', FALSE),
    ('B', '1', FALSE),
    ('C', 'No existe', FALSE),
    ('D', '0', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = x · ln(x) cuando x → 0⁺ (por la derecha)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L60
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = ln(x)/x cuando x → ∞?', 'avanzado',
       'Es una forma ∞/∞: aplica L''Hôpital o compara qué crece más rápido.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = ln(x)/x cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', TRUE),
    ('B', '1', FALSE),
    ('C', '∞', FALSE),
    ('D', 'e', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = ln(x)/x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L61
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = x²/e^x cuando x → ∞?', 'avanzado',
       'La exponencial crece más rápido que cualquier polinomio. Aplica L''Hôpital dos veces.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = x²/e^x cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '∞', FALSE),
    ('B', '0', TRUE),
    ('C', '1', FALSE),
    ('D', '2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = x²/e^x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L62
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (sen(x) - x)/x³ cuando x → 0?', 'avanzado',
       'Aplica L''Hôpital varias veces o usa sen(x) ≈ x - x³/6.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (sen(x) - x)/x³ cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/6', FALSE),
    ('B', '0', FALSE),
    ('C', '-1/6', TRUE),
    ('D', '-1/3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (sen(x) - x)/x³ cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L63
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (1 + x)^(1/x) cuando x → 0?', 'avanzado',
       'Haz el cambio x = 1/n y relaciónalo con la definición del número e.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (1 + x)^(1/x) cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '0', FALSE),
    ('C', '∞', FALSE),
    ('D', 'e', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 + x)^(1/x) cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L64
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (tan(x) - sen(x))/x³ cuando x → 0?', 'avanzado',
       'Saca sen(x) como factor común y usa 1 - cos(x) ≈ x²/2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (tan(x) - sen(x))/x³ cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/2', TRUE),
    ('B', '0', FALSE),
    ('C', '1', FALSE),
    ('D', '-1/2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (tan(x) - sen(x))/x³ cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L65
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (ln(x) - ln(2))/(x - 2) cuando x → 2?', 'avanzado',
       'Reconoce la definición de la derivada de ln(x) en x = 2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (ln(x) - ln(2))/(x - 2) cuando x → 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2', FALSE),
    ('B', '1/2', TRUE),
    ('C', 'ln(2)', FALSE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (ln(x) - ln(2))/(x - 2) cuando x → 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L66
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (2^x - 1)/x cuando x → 0?', 'avanzado',
       'Escribe 2^x = e^(x·ln 2) y usa el límite notable de (e^u - 1)/u.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (2^x - 1)/x cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '2', FALSE),
    ('C', 'ln(2)', TRUE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el límite de f(x) = (2^x - 1)/x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- L67
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Para qué valor de k es continua en x = 1 la función f(x) = x² + k si x < 1, y f(x) = 3x si x ≥ 1?', 'avanzado',
       'Para que sea continua, los límites laterales en x = 1 deben ser iguales al valor f(1).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Para qué valor de k es continua en x = 1 la función f(x) = x² + k si x < 1, y f(x) = 3x si x ≥ 1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3', FALSE),
    ('B', '1', FALSE),
    ('C', '4', FALSE),
    ('D', '2', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Para qué valor de k es continua en x = 1 la función f(x) = x² + k si x < 1, y f(x) = 3x si x ≥ 1?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);


-- ============================================
-- DERIVADAS (52)
-- ============================================

-- D16
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 7x³?', 'basico',
       'Regla de la potencia: d/dx(xⁿ) = n·xⁿ⁻¹. La constante multiplica.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 7x³?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '21x²', TRUE),
    ('B', '7x²', FALSE),
    ('C', '21x³', FALSE),
    ('D', '3x²', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 7x³?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D17
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = x⁵ - 3x²?', 'basico',
       'Deriva término a término con la regla de la potencia.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = x⁵ - 3x²?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x⁴ - 6x', FALSE),
    ('B', '5x⁴ - 6x', TRUE),
    ('C', '5x⁴ - 3x', FALSE),
    ('D', '5x⁶ - 6x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x⁵ - 3x²?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D18
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 4x + 9?', 'basico',
       'La derivada de una constante es cero.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 4x + 9?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4x', FALSE),
    ('B', '9', FALSE),
    ('C', '4', TRUE),
    ('D', '13', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 4x + 9?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D19
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 12?', 'basico',
       'Una función constante no cambia.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 12?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '12', FALSE),
    ('B', '1', FALSE),
    ('C', '12x', FALSE),
    ('D', '0', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 12?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D20
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 1/x?', 'basico',
       'Escribe 1/x como x elevado a la -1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 1/x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-1/x²', TRUE),
    ('B', '1/x²', FALSE),
    ('C', 'ln(x)', FALSE),
    ('D', '-1/x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 1/x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D21
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 2sen(x)?', 'basico',
       'La derivada de sen(x) es cos(x).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 2sen(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-2cos(x)', FALSE),
    ('B', '2cos(x)', TRUE),
    ('C', '2sen(x)', FALSE),
    ('D', 'cos(2x)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 2sen(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D22
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 3cos(x)?', 'basico',
       'La derivada de cos(x) es -sen(x).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 3cos(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3sen(x)', FALSE),
    ('B', '-3cos(x)', FALSE),
    ('C', '-3sen(x)', TRUE),
    ('D', 'sen(3x)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 3cos(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D23
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = tan(x)?', 'basico',
       'Deriva sen(x)/cos(x) con la regla del cociente.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = tan(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-sec²(x)', FALSE),
    ('B', 'cot(x)', FALSE),
    ('C', 'sen(x)/cos(x)', FALSE),
    ('D', 'sec²(x)', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = tan(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D24
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = e^x + x²?', 'basico',
       'La exponencial e^x es su propia derivada.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = e^x + x²?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e^x + 2x', TRUE),
    ('B', 'e^x + x', FALSE),
    ('C', 'x·e^(x - 1) + 2x', FALSE),
    ('D', 'e^x + 2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^x + x²?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D25
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 5ln(x)?', 'basico',
       'La derivada de ln(x) es 1/x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 5ln(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '5ln(x)', FALSE),
    ('B', '5/x', TRUE),
    ('C', '1/(5x)', FALSE),
    ('D', '5x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 5ln(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D26
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 1/x³?', 'basico',
       'Escribe 1/x³ como x⁻³ y aplica la regla de la potencia.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 1/x³?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-3/x²', FALSE),
    ('B', '3/x⁴', FALSE),
    ('C', '-3/x⁴', TRUE),
    ('D', '1/(3x²)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 1/x³?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D27
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 6√x?', 'basico',
       '√x = x^(1/2); su derivada es 1/(2√x).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 6√x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '6/√x', FALSE),
    ('B', '3√x', FALSE),
    ('C', '12√x', FALSE),
    ('D', '3/√x', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 6√x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D28
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = x² + 5x - 8?', 'basico',
       'Deriva término a término.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = x² + 5x - 8?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2x + 5', TRUE),
    ('B', '2x - 3', FALSE),
    ('C', 'x + 5', FALSE),
    ('D', '2x² + 5', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x² + 5x - 8?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D29
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es el valor de f′(1) si f(x) = 3x² + 2x?', 'basico',
       'Primero deriva y después reemplaza x = 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de f′(1) si f(x) = 3x² + 2x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '5', FALSE),
    ('B', '8', TRUE),
    ('C', '6', FALSE),
    ('D', '3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de f′(1) si f(x) = 3x² + 2x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D30
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la pendiente de la recta tangente a f(x) = x³ en x = 2?', 'basico',
       'La pendiente de la tangente es f′(2).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la pendiente de la recta tangente a f(x) = x³ en x = 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '8', FALSE),
    ('B', '6', FALSE),
    ('C', '12', TRUE),
    ('D', '4', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la pendiente de la recta tangente a f(x) = x³ en x = 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D31
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Qué representa geométricamente la derivada de una función en un punto?', 'basico',
       'Piensa en la recta que apenas toca la curva en ese punto.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Qué representa geométricamente la derivada de una función en un punto?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'El área bajo la curva', FALSE),
    ('B', 'El valor de la función en ese punto', FALSE),
    ('C', 'La distancia del punto al origen', FALSE),
    ('D', 'La pendiente de la recta tangente en ese punto', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Qué representa geométricamente la derivada de una función en un punto?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D32
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = x/4?', 'basico',
       'x/4 es lo mismo que (1/4)·x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = x/4?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/4', TRUE),
    ('B', '4', FALSE),
    ('C', 'x/4', FALSE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x/4?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D33
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 3e^x?', 'basico',
       'La constante 3 se conserva y e^x no cambia al derivar.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 3e^x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e^x', FALSE),
    ('B', '3e^x', TRUE),
    ('C', '3x·e^(x - 1)', FALSE),
    ('D', '3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 3e^x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D34
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = (x² + 1)⁴?', 'intermedio',
       'Regla de la cadena: deriva la potencia y multiplica por la derivada de lo de adentro.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = (x² + 1)⁴?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4(x² + 1)³', FALSE),
    ('B', '4x(x² + 1)³', FALSE),
    ('C', '8x(x² + 1)³', TRUE),
    ('D', '8x(x² + 1)⁴', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = (x² + 1)⁴?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D35
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = sen(x²)?', 'intermedio',
       'Regla de la cadena: cos(x²) por la derivada de x².'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = sen(x²)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'cos(x²)', FALSE),
    ('B', 'cos(2x)', FALSE),
    ('C', '-2x·cos(x²)', FALSE),
    ('D', '2x·cos(x²)', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = sen(x²)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D36
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = cos(3x)?', 'intermedio',
       'Regla de la cadena: no olvides multiplicar por la derivada de 3x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = cos(3x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-3sen(3x)', TRUE),
    ('B', '3sen(3x)', FALSE),
    ('C', '-sen(3x)', FALSE),
    ('D', '-3cos(3x)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = cos(3x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D37
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = e^(x²)?', 'intermedio',
       'La derivada de e^u es e^u·u′.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = e^(x²)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e^(x²)', FALSE),
    ('B', '2x·e^(x²)', TRUE),
    ('C', 'x²·e^(x² - 1)', FALSE),
    ('D', '2e^(x²)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^(x²)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D38
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = ln(3x)?', 'intermedio',
       'ln(3x) = ln(3) + ln(x), y ln(3) es una constante.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = ln(3x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3/x', FALSE),
    ('B', '1/(3x)', FALSE),
    ('C', '1/x', TRUE),
    ('D', 'ln(3)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = ln(3x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D39
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = x²·sen(x)?', 'intermedio',
       'Regla del producto: (uv)′ = u′v + uv′.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = x²·sen(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2x·cos(x)', FALSE),
    ('B', '2x·sen(x) - x²·cos(x)', FALSE),
    ('C', 'x²·cos(x)', FALSE),
    ('D', '2x·sen(x) + x²·cos(x)', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x²·sen(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D40
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = (x + 1)/(x - 1)?', 'intermedio',
       'Regla del cociente: (u′v - uv′)/v².'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = (x + 1)/(x - 1)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-2/(x - 1)²', TRUE),
    ('B', '2/(x - 1)²', FALSE),
    ('C', '1', FALSE),
    ('D', '-1/(x - 1)²', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = (x + 1)/(x - 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D41
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = √(2x + 1)?', 'intermedio',
       'Regla de la cadena con la raíz: 1/(2√u) por u′.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = √(2x + 1)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/(2√(2x + 1))', FALSE),
    ('B', '1/√(2x + 1)', TRUE),
    ('C', '2/√(2x + 1)', FALSE),
    ('D', '√2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = √(2x + 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D42
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = x·ln(x)?', 'intermedio',
       'Regla del producto con u = x y v = ln(x).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = x·ln(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/x', FALSE),
    ('B', 'ln(x)', FALSE),
    ('C', 'ln(x) + 1', TRUE),
    ('D', 'x + 1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x·ln(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D43
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = e^x·sen(x)?', 'intermedio',
       'Regla del producto; luego saca e^x como factor común.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = e^x·sen(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e^x·cos(x)', FALSE),
    ('B', 'e^x(sen(x) - cos(x))', FALSE),
    ('C', 'e^x·sen(x)', FALSE),
    ('D', 'e^x(sen(x) + cos(x))', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^x·sen(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D44
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 1/(x² + 1)?', 'intermedio',
       'Escribe (x² + 1)⁻¹ y aplica la regla de la cadena.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 1/(x² + 1)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-2x/(x² + 1)²', TRUE),
    ('B', '2x/(x² + 1)²', FALSE),
    ('C', '1/(2x)', FALSE),
    ('D', '-1/(x² + 1)²', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 1/(x² + 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D45
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la segunda derivada de f(x) = x⁴ - 3x²?', 'intermedio',
       'Deriva dos veces seguidas.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la segunda derivada de f(x) = x⁴ - 3x²?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4x³ - 6x', FALSE),
    ('B', '12x² - 6', TRUE),
    ('C', '12x - 6', FALSE),
    ('D', '12x²', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la segunda derivada de f(x) = x⁴ - 3x²?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D46
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿En qué valor de x tiene su punto crítico la función f(x) = x² - 6x + 5?', 'intermedio',
       'Los puntos críticos están donde f′(x) = 0.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿En qué valor de x tiene su punto crítico la función f(x) = x² - 6x + 5?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x = -3', FALSE),
    ('B', 'x = 5', FALSE),
    ('C', 'x = 3', TRUE),
    ('D', 'x = 1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿En qué valor de x tiene su punto crítico la función f(x) = x² - 6x + 5?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D47
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la ecuación de la recta tangente a f(x) = x² en x = 1?', 'intermedio',
       'Usa y - f(1) = f′(1)(x - 1).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la ecuación de la recta tangente a f(x) = x² en x = 1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'y = 2x + 1', FALSE),
    ('B', 'y = x + 1', FALSE),
    ('C', 'y = 2x', FALSE),
    ('D', 'y = 2x - 1', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la ecuación de la recta tangente a f(x) = x² en x = 1?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D48
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', 'Si s(t) = 4t² + 3t es la posición de un objeto en metros, ¿cuál es su velocidad en t = 2 s?', 'intermedio',
       'La velocidad es la derivada de la posición.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si s(t) = 4t² + 3t es la posición de un objeto en metros, ¿cuál es su velocidad en t = 2 s?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '19 m/s', TRUE),
    ('B', '22 m/s', FALSE),
    ('C', '16 m/s', FALSE),
    ('D', '8 m/s', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si s(t) = 4t² + 3t es la posición de un objeto en metros, ¿cuál es su velocidad en t = 2 s?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D49
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = ln(cos(x))?', 'intermedio',
       'Regla de la cadena: (1/cos(x))·(-sen(x)).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = ln(cos(x))?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'tan(x)', FALSE),
    ('B', '-tan(x)', TRUE),
    ('C', '1/cos(x)', FALSE),
    ('D', '-sen(x)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = ln(cos(x))?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D50
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = sen(x)·cos(x)?', 'intermedio',
       'Regla del producto: la derivada de sen es cos y la de cos es -sen.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = sen(x)·cos(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'sen²(x) - cos²(x)', FALSE),
    ('B', '-sen(x)·cos(x)', FALSE),
    ('C', 'cos²(x) - sen²(x)', TRUE),
    ('D', '1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = sen(x)·cos(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D51
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = (3x - 2)⁵?', 'intermedio',
       'Regla de la cadena: no olvides la derivada de 3x - 2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = (3x - 2)⁵?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '5(3x - 2)⁴', FALSE),
    ('B', '15(3x - 2)⁵', FALSE),
    ('C', '3(3x - 2)⁴', FALSE),
    ('D', '15(3x - 2)⁴', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = (3x - 2)⁵?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D52
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = x³·e^x?', 'intermedio',
       'Regla del producto; luego factoriza x²·e^x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = x³·e^x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x²·e^x(x + 3)', TRUE),
    ('B', '3x²·e^x', FALSE),
    ('C', 'x³·e^x', FALSE),
    ('D', 'x²·e^x(x - 3)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x³·e^x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D53
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es el valor de f′(0) si f(x) = e^(2x) + sen(x)?', 'intermedio',
       'Deriva cada término (con regla de la cadena en e^(2x)) y evalúa en 0.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de f′(0) si f(x) = e^(2x) + sen(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2', FALSE),
    ('B', '3', TRUE),
    ('C', '1', FALSE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de f′(0) si f(x) = e^(2x) + sen(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D54
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = x^x?', 'avanzado',
       'Escribe x^x = e^(x·ln(x)) y deriva con la regla de la cadena.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = x^x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x·x^(x - 1)', FALSE),
    ('B', 'x^x·ln(x)', FALSE),
    ('C', 'x^x(ln(x) + 1)', TRUE),
    ('D', 'x^x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x^x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D55
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = arctan(x)?', 'avanzado',
       'Es una derivada de función trigonométrica inversa que conviene memorizar.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = arctan(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-1/(1 + x²)', FALSE),
    ('B', '1/√(1 - x²)', FALSE),
    ('C', 'sec²(x)', FALSE),
    ('D', '1/(1 + x²)', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = arctan(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D56
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = arcsen(x)?', 'avanzado',
       'Usa derivación implícita sobre sen(y) = x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = arcsen(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/√(1 - x²)', TRUE),
    ('B', '-1/√(1 - x²)', FALSE),
    ('C', '1/(1 + x²)', FALSE),
    ('D', 'cos(x)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = arcsen(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D57
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = e^(sen(x))?', 'avanzado',
       'La derivada de e^u es e^u·u′, con u = sen(x).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = e^(sen(x))?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e^(sen(x))', FALSE),
    ('B', 'cos(x)·e^(sen(x))', TRUE),
    ('C', 'sen(x)·e^(sen(x) - 1)', FALSE),
    ('D', '-cos(x)·e^(sen(x))', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^(sen(x))?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D58
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = ln(√x)?', 'avanzado',
       'Usa la propiedad ln(√x) = (1/2)·ln(x) antes de derivar.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = ln(√x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/√x', FALSE),
    ('B', '1/x', FALSE),
    ('C', '1/(2x)', TRUE),
    ('D', '2/x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = ln(√x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D59
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = sen³(x)?', 'avanzado',
       'Es [sen(x)]³: deriva la potencia y multiplica por cos(x).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = sen³(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3cos²(x)', FALSE),
    ('B', 'cos³(x)', FALSE),
    ('C', '-3sen²(x)·cos(x)', FALSE),
    ('D', '3sen²(x)·cos(x)', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = sen³(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D60
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = (x² + 1)/e^x?', 'avanzado',
       'Escríbelo como (x² + 1)·e^(-x) y usa la regla del producto.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = (x² + 1)/e^x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-(x - 1)²/e^x', TRUE),
    ('B', '(x - 1)²/e^x', FALSE),
    ('C', '2x/e^x', FALSE),
    ('D', '(x² + 2x + 1)/e^x', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = (x² + 1)/e^x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D61
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', 'Si x² + y² = 25, ¿cuál es el valor de dy/dx en el punto (3, 4)?', 'avanzado',
       'Deriva implícitamente: 2x + 2y·y′ = 0, y despeja y′.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si x² + y² = 25, ¿cuál es el valor de dy/dx en el punto (3, 4)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '3/4', FALSE),
    ('B', '-3/4', TRUE),
    ('C', '-4/3', FALSE),
    ('D', '4/3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si x² + y² = 25, ¿cuál es el valor de dy/dx en el punto (3, 4)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D62
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', 'El radio de un globo esférico crece a 2 cm/s. ¿A qué ritmo crece su volumen cuando r = 5 cm?', 'avanzado',
       'V = (4/3)πr³. Deriva respecto al tiempo: dV/dt = 4πr²·dr/dt.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'El radio de un globo esférico crece a 2 cm/s. ¿A qué ritmo crece su volumen cuando r = 5 cm?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '100π cm³/s', FALSE),
    ('B', '50π cm³/s', FALSE),
    ('C', '200π cm³/s', TRUE),
    ('D', '500π/3 cm³/s', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'El radio de un globo esférico crece a 2 cm/s. ¿A qué ritmo crece su volumen cuando r = 5 cm?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D63
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es el valor máximo de f(x) = -x² + 4x + 1?', 'avanzado',
       'Encuentra dónde f′(x) = 0 y evalúa f en ese punto.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor máximo de f(x) = -x² + 4x + 1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2', FALSE),
    ('B', '1', FALSE),
    ('C', '4', FALSE),
    ('D', '5', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor máximo de f(x) = -x² + 4x + 1?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D64
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', 'Con 40 m de cerca se quiere encerrar un terreno rectangular. ¿Cuál es el área máxima posible?', 'avanzado',
       'Si un lado mide x, el otro mide 20 - x. Maximiza A(x) = x(20 - x).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Con 40 m de cerca se quiere encerrar un terreno rectangular. ¿Cuál es el área máxima posible?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '100 m²', TRUE),
    ('B', '400 m²', FALSE),
    ('C', '80 m²', FALSE),
    ('D', '96 m²', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Con 40 m de cerca se quiere encerrar un terreno rectangular. ¿Cuál es el área máxima posible?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D65
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿En qué intervalos es creciente la función f(x) = x³ - 3x?', 'avanzado',
       'f es creciente donde f′(x) = 3x² - 3 > 0.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿En qué intervalos es creciente la función f(x) = x³ - 3x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(-1, 1)', FALSE),
    ('B', '(-∞, -1) ∪ (1, ∞)', TRUE),
    ('C', '(0, ∞)', FALSE),
    ('D', '(-∞, 0)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿En qué intervalos es creciente la función f(x) = x³ - 3x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D66
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es el punto de inflexión de f(x) = x³ - 6x² + 2?', 'avanzado',
       'Busca dónde f″(x) = 0 y luego calcula f en ese valor.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el punto de inflexión de f(x) = x³ - 6x² + 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(0, 2)', FALSE),
    ('B', '(4, -30)', FALSE),
    ('C', '(2, -14)', TRUE),
    ('D', '(2, 0)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el punto de inflexión de f(x) = x³ - 6x² + 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- D67
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = tan(2x)?', 'avanzado',
       'La derivada de tan(u) es sec²(u)·u′.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = tan(2x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'sec²(2x)', FALSE),
    ('B', '2tan(2x)', FALSE),
    ('C', '-2sec²(2x)', FALSE),
    ('D', '2sec²(2x)', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la derivada de f(x) = tan(2x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);


-- ============================================
-- INTEGRALES (51)
-- ============================================

-- I16
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ x³ dx?', 'basico',
       'Regla de la potencia: ∫xⁿ dx = xⁿ⁺¹/(n + 1) + C.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ x³ dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x⁴/4 + C', TRUE),
    ('B', '3x² + C', FALSE),
    ('C', 'x⁴ + C', FALSE),
    ('D', 'x³/3 + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ x³ dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I17
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 5x⁴ dx?', 'basico',
       'Saca la constante y aplica la regla de la potencia.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 5x⁴ dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '20x³ + C', FALSE),
    ('B', 'x⁵ + C', TRUE),
    ('C', '5x⁵ + C', FALSE),
    ('D', 'x⁵/5 + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 5x⁴ dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I18
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 6x dx?', 'basico',
       '∫x dx = x²/2 + C; multiplica por 6.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 6x dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '6x² + C', FALSE),
    ('B', '6 + C', FALSE),
    ('C', '3x² + C', TRUE),
    ('D', 'x² + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 6x dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I19
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 2x + 3 dx?', 'basico',
       'Integra término a término.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 2x + 3 dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2x² + 3x + C', FALSE),
    ('B', 'x² + C', FALSE),
    ('C', '2 + C', FALSE),
    ('D', 'x² + 3x + C', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 2x + 3 dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I20
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ √x dx?', 'basico',
       'Escribe √x como x^(1/2).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ √x dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(2/3)x^(3/2) + C', TRUE),
    ('B', '(3/2)x^(3/2) + C', FALSE),
    ('C', '1/(2√x) + C', FALSE),
    ('D', 'x^(1/2) + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ √x dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I21
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 1/x² dx?', 'basico',
       'Escribe 1/x² como x⁻² y aplica la regla de la potencia.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 1/x² dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/x + C', FALSE),
    ('B', '-1/x + C', TRUE),
    ('C', 'ln(x²) + C', FALSE),
    ('D', '-2/x³ + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 1/x² dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I22
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 4e^x dx?', 'basico',
       'La integral de e^x es e^x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 4e^x dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e^(4x) + C', FALSE),
    ('B', '4x·e^x + C', FALSE),
    ('C', '4e^x + C', TRUE),
    ('D', 'e^x/4 + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 4e^x dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I23
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 3/x dx?', 'basico',
       'La integral de 1/x es ln|x|.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 3/x dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'ln|3x| + C', FALSE),
    ('B', '-3/x² + C', FALSE),
    ('C', '3x + C', FALSE),
    ('D', '3ln|x| + C', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 3/x dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I24
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 2cos(x) dx?', 'basico',
       'La integral de cos(x) es sen(x).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 2cos(x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2sen(x) + C', TRUE),
    ('B', '-2sen(x) + C', FALSE),
    ('C', 'sen(2x) + C', FALSE),
    ('D', '2cos(x) + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 2cos(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I25
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ x² + x dx?', 'basico',
       'Integra cada término por separado.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ x² + x dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2x + 1 + C', FALSE),
    ('B', 'x³/3 + x²/2 + C', TRUE),
    ('C', 'x³ + x² + C', FALSE),
    ('D', 'x³/3 + x + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ x² + x dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I26
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = 2x desde x = 1 hasta x = 3?', 'basico',
       'Una antiderivada es x². Evalúa en 3 y resta el valor en 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = 2x desde x = 1 hasta x = 3?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4', FALSE),
    ('B', '9', FALSE),
    ('C', '8', TRUE),
    ('D', '6', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = 2x desde x = 1 hasta x = 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I27
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = 4 desde x = 0 hasta x = 5?', 'basico',
       'Es el área de un rectángulo de altura 4.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = 4 desde x = 0 hasta x = 5?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4', FALSE),
    ('B', '5', FALSE),
    ('C', '9', FALSE),
    ('D', '20', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = 4 desde x = 0 hasta x = 5?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I28
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = x³ desde x = 0 hasta x = 2?', 'basico',
       'Una antiderivada es x⁴/4.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = x³ desde x = 0 hasta x = 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '4', TRUE),
    ('B', '8', FALSE),
    ('C', '2', FALSE),
    ('D', '16', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = x³ desde x = 0 hasta x = 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I29
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = 3x² desde x = 1 hasta x = 2?', 'basico',
       'Una antiderivada es x³.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = 3x² desde x = 1 hasta x = 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '8', FALSE),
    ('B', '7', TRUE),
    ('C', '3', FALSE),
    ('D', '9', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = 3x² desde x = 1 hasta x = 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I30
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Qué representa la integral definida de f(x) entre a y b cuando f(x) ≥ 0 en ese intervalo?', 'basico',
       'Piensa en la región entre la gráfica y el eje x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Qué representa la integral definida de f(x) entre a y b cuando f(x) ≥ 0 en ese intervalo?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'La pendiente de la curva en a', FALSE),
    ('B', 'El valor máximo de f en [a, b]', FALSE),
    ('C', 'El área bajo la curva entre a y b', TRUE),
    ('D', 'La longitud de la curva', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Qué representa la integral definida de f(x) entre a y b cuando f(x) ≥ 0 en ese intervalo?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I31
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Por qué se suma una constante C en la integral indefinida?', 'basico',
       '¿Qué pasa con una constante cuando la derivas?'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Por qué se suma una constante C en la integral indefinida?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'Porque la integral siempre vale C', FALSE),
    ('B', 'Para que el resultado sea positivo', FALSE),
    ('C', 'Porque C es el área bajo la curva', FALSE),
    ('D', 'Porque la derivada de una constante es cero y hay infinitas antiderivadas', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Por qué se suma una constante C en la integral indefinida?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I32
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ -sen(x) dx?', 'basico',
       '¿Qué función tiene como derivada -sen(x)?'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ -sen(x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'cos(x) + C', TRUE),
    ('B', '-cos(x) + C', FALSE),
    ('C', 'sen(x) + C', FALSE),
    ('D', '-sen(x) + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ -sen(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I33
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 7 dx?', 'basico',
       'La integral de una constante k es k·x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 7 dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '7 + C', FALSE),
    ('B', '7x + C', TRUE),
    ('C', 'x⁷ + C', FALSE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 7 dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I34
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ (x + 1)² dx?', 'intermedio',
       'Sustitución u = x + 1, o expande el cuadrado.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ (x + 1)² dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2(x + 1) + C', FALSE),
    ('B', '(x + 1)³ + C', FALSE),
    ('C', '(x + 1)³/3 + C', TRUE),
    ('D', 'x³/3 + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ (x + 1)² dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I35
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ sen(3x) dx?', 'intermedio',
       'Sustitución u = 3x: aparece un factor 1/3.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ sen(3x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'cos(3x)/3 + C', FALSE),
    ('B', '-3cos(3x) + C', FALSE),
    ('C', '3cos(3x) + C', FALSE),
    ('D', '-cos(3x)/3 + C', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ sen(3x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I36
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ cos(2x) dx?', 'intermedio',
       'Sustitución u = 2x: aparece un factor 1/2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ cos(2x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'sen(2x)/2 + C', TRUE),
    ('B', '2sen(2x) + C', FALSE),
    ('C', '-sen(2x)/2 + C', FALSE),
    ('D', 'sen(2x) + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ cos(2x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I37
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ e^(-x) dx?', 'intermedio',
       'Sustitución u = -x. Cuidado con el signo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ e^(-x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e^(-x) + C', FALSE),
    ('B', '-e^(-x) + C', TRUE),
    ('C', '-x·e^(-x) + C', FALSE),
    ('D', 'e^x + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ e^(-x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I38
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 1/(2x + 1) dx?', 'intermedio',
       'Sustitución u = 2x + 1, du = 2 dx.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 1/(2x + 1) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'ln|2x + 1| + C', FALSE),
    ('B', '2ln|2x + 1| + C', FALSE),
    ('C', 'ln|2x + 1|/2 + C', TRUE),
    ('D', '-1/(2x + 1)² + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 1/(2x + 1) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I39
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ x·(x² + 3)⁴ dx?', 'intermedio',
       'Sustitución u = x² + 3, du = 2x dx.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ x·(x² + 3)⁴ dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '(x² + 3)⁵/5 + C', FALSE),
    ('B', '(x² + 3)⁵ + C', FALSE),
    ('C', 'x²(x² + 3)⁵/10 + C', FALSE),
    ('D', '(x² + 3)⁵/10 + C', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ x·(x² + 3)⁴ dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I40
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ cos(x)·sen²(x) dx?', 'intermedio',
       'Sustitución u = sen(x), du = cos(x) dx.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ cos(x)·sen²(x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'sen³(x)/3 + C', TRUE),
    ('B', 'cos³(x)/3 + C', FALSE),
    ('C', '-sen³(x)/3 + C', FALSE),
    ('D', 'sen²(x)/2 + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ cos(x)·sen²(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I41
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 2x/(x² + 1) dx?', 'intermedio',
       'El numerador es la derivada del denominador.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 2x/(x² + 1) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '2ln(x² + 1) + C', FALSE),
    ('B', 'ln(x² + 1) + C', TRUE),
    ('C', '1/(x² + 1) + C', FALSE),
    ('D', 'arctan(x) + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 2x/(x² + 1) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I42
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 1/(1 + x²) dx?', 'intermedio',
       'Es la derivada de una función trigonométrica inversa.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 1/(1 + x²) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'ln(1 + x²) + C', FALSE),
    ('B', '-1/x + C', FALSE),
    ('C', 'arctan(x) + C', TRUE),
    ('D', 'arcsen(x) + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 1/(1 + x²) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I43
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = e^x desde x = 0 hasta x = 1?', 'intermedio',
       'Evalúa e^x en 1 y en 0; recuerda que e⁰ = 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = e^x desde x = 0 hasta x = 1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e', FALSE),
    ('B', '1', FALSE),
    ('C', 'e + 1', FALSE),
    ('D', 'e - 1', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = e^x desde x = 0 hasta x = 1?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I44
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = 1/x desde x = 1 hasta x = e?', 'intermedio',
       'Una antiderivada es ln(x); ln(e) = 1 y ln(1) = 0.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = 1/x desde x = 1 hasta x = e?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', TRUE),
    ('B', 'e', FALSE),
    ('C', '0', FALSE),
    ('D', 'e - 1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = 1/x desde x = 1 hasta x = e?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I45
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = sen(x) desde x = 0 hasta x = π?', 'intermedio',
       'Una antiderivada es -cos(x). Recuerda que cos(π) = -1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = sen(x) desde x = 0 hasta x = π?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '0', FALSE),
    ('B', '2', TRUE),
    ('C', '1', FALSE),
    ('D', 'π', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = sen(x) desde x = 0 hasta x = π?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I46
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = x² - 4 desde x = 0 hasta x = 2?', 'intermedio',
       'La función es negativa en ese intervalo, así que la integral también puede serlo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = x² - 4 desde x = 0 hasta x = 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '16/3', FALSE),
    ('B', '-8/3', FALSE),
    ('C', '-16/3', TRUE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = x² - 4 desde x = 0 hasta x = 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I47
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el área de la región entre las curvas y = x y y = x² en el intervalo [0, 1]?', 'intermedio',
       'Integra (curva de arriba - curva de abajo). En [0, 1], x ≥ x².'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el área de la región entre las curvas y = x y y = x² en el intervalo [0, 1]?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/2', FALSE),
    ('B', '1/3', FALSE),
    ('C', '1', FALSE),
    ('D', '1/6', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el área de la región entre las curvas y = x y y = x² en el intervalo [0, 1]?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I48
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ sec²(x) dx?', 'intermedio',
       '¿Qué función tiene como derivada sec²(x)?'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ sec²(x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'tan(x) + C', TRUE),
    ('B', 'sec(x) + C', FALSE),
    ('C', '-tan(x) + C', FALSE),
    ('D', 'sec³(x)/3 + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ sec²(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I49
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ (3x² + 1)/x dx?', 'intermedio',
       'Divide término a término: 3x + 1/x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ (3x² + 1)/x dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x³ + x + C', FALSE),
    ('B', '(3/2)x² + ln|x| + C', TRUE),
    ('C', '6x + C', FALSE),
    ('D', '3x² + ln|x| + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ (3x² + 1)/x dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I50
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ x·e^(x²) dx?', 'intermedio',
       'Sustitución u = x², du = 2x dx.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ x·e^(x²) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e^(x²) + C', FALSE),
    ('B', '2x·e^(x²) + C', FALSE),
    ('C', 'e^(x²)/2 + C', TRUE),
    ('D', 'x²·e^(x²)/2 + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ x·e^(x²) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I51
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = x + 1 desde x = 0 hasta x = 4?', 'intermedio',
       'Una antiderivada es x²/2 + x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = x + 1 desde x = 0 hasta x = 4?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '8', FALSE),
    ('B', '16', FALSE),
    ('C', '10', FALSE),
    ('D', '12', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = x + 1 desde x = 0 hasta x = 4?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I52
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', 'La velocidad de un móvil es v(t) = 3t² m/s. ¿Qué distancia recorre entre t = 0 s y t = 2 s?', 'intermedio',
       'La distancia es la integral de la velocidad en ese intervalo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'La velocidad de un móvil es v(t) = 3t² m/s. ¿Qué distancia recorre entre t = 0 s y t = 2 s?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '8 m', TRUE),
    ('B', '12 m', FALSE),
    ('C', '6 m', FALSE),
    ('D', '4 m', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'La velocidad de un móvil es v(t) = 3t² m/s. ¿Qué distancia recorre entre t = 0 s y t = 2 s?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I53
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = 1/√x desde x = 1 hasta x = 4?', 'intermedio',
       'Una antiderivada de x^(-1/2) es 2√x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = 1/√x desde x = 1 hasta x = 4?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', FALSE),
    ('B', '2', TRUE),
    ('C', '4', FALSE),
    ('D', '3', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = 1/√x desde x = 1 hasta x = 4?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I54
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ x·sen(x) dx?', 'avanzado',
       'Integración por partes con u = x y dv = sen(x) dx.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ x·sen(x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'x·cos(x) - sen(x) + C', FALSE),
    ('B', '-x·cos(x) + C', FALSE),
    ('C', 'sen(x) - x·cos(x) + C', TRUE),
    ('D', 'x·sen(x) + cos(x) + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ x·sen(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I55
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ ln(x) dx?', 'avanzado',
       'Por partes con u = ln(x) y dv = dx.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ ln(x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1/x + C', FALSE),
    ('B', 'x·ln(x) + C', FALSE),
    ('C', 'ln(x)²/2 + C', FALSE),
    ('D', 'x·ln(x) - x + C', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ ln(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I56
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ x²·e^x dx?', 'avanzado',
       'Aplica integración por partes dos veces.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ x²·e^x dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e^x(x² - 2x + 2) + C', TRUE),
    ('B', 'x²·e^x + C', FALSE),
    ('C', 'e^x(x² + 2x + 2) + C', FALSE),
    ('D', '(x³/3)·e^x + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ x²·e^x dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I57
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ e^x·sen(x) dx?', 'avanzado',
       'Por partes dos veces: la integral original vuelve a aparecer y puedes despejarla.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ e^x·sen(x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e^x(sen(x) + cos(x))/2 + C', FALSE),
    ('B', 'e^x(sen(x) - cos(x))/2 + C', TRUE),
    ('C', 'e^x·cos(x) + C', FALSE),
    ('D', '-e^x·cos(x) + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ e^x·sen(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I58
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 1/(x² - 1) dx?', 'avanzado',
       'Fracciones parciales: 1/(x² - 1) = (1/2)[1/(x - 1) - 1/(x + 1)].'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 1/(x² - 1) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'ln|x² - 1| + C', FALSE),
    ('B', 'arctan(x) + C', FALSE),
    ('C', 'ln|(x - 1)/(x + 1)|/2 + C', TRUE),
    ('D', 'ln|(x + 1)/(x - 1)|/2 + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ 1/(x² - 1) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I59
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ sen²(x) dx?', 'avanzado',
       'Usa la identidad sen²(x) = (1 - cos(2x))/2.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ sen²(x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'sen³(x)/3 + C', FALSE),
    ('B', 'x/2 + sen(2x)/4 + C', FALSE),
    ('C', '-cos²(x) + C', FALSE),
    ('D', 'x/2 - sen(2x)/4 + C', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ sen²(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I60
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ x/√(x² + 9) dx?', 'avanzado',
       'Sustitución u = x² + 9.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ x/√(x² + 9) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '√(x² + 9) + C', TRUE),
    ('B', '2√(x² + 9) + C', FALSE),
    ('C', 'ln(x² + 9) + C', FALSE),
    ('D', '√(x² + 9)/2 + C', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es la integral de ∫ x/√(x² + 9) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I61
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = x·e^x desde x = 0 hasta x = 1?', 'avanzado',
       'Por partes: una antiderivada es e^x(x - 1).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = x·e^x desde x = 0 hasta x = 1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'e', FALSE),
    ('B', '1', TRUE),
    ('C', 'e - 1', FALSE),
    ('D', '0', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = x·e^x desde x = 0 hasta x = 1?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I62
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida de f(x) = x·cos(x) desde x = 0 hasta x = π/2?', 'avanzado',
       'Por partes: una antiderivada es x·sen(x) + cos(x).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida de f(x) = x·cos(x) desde x = 0 hasta x = π/2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', 'π/2', FALSE),
    ('B', '1', FALSE),
    ('C', 'π/2 - 1', TRUE),
    ('D', 'π/2 + 1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral definida de f(x) = x·cos(x) desde x = 0 hasta x = π/2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I63
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', 'Se gira alrededor del eje x la región bajo y = x entre x = 0 y x = 3. ¿Qué volumen tiene el sólido?', 'avanzado',
       'Método de discos: V = π∫[f(x)]² dx.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Se gira alrededor del eje x la región bajo y = x entre x = 0 y x = 3. ¿Qué volumen tiene el sólido?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '27π', FALSE),
    ('B', '3π', FALSE),
    ('C', '9', FALSE),
    ('D', '9π', TRUE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Se gira alrededor del eje x la región bajo y = x entre x = 0 y x = 3. ¿Qué volumen tiene el sólido?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I64
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral impropia de 1/x² desde x = 1 hasta ∞?', 'avanzado',
       'Integra hasta un valor b y luego haz que b → ∞.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral impropia de 1/x² desde x = 1 hasta ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '1', TRUE),
    ('B', '∞', FALSE),
    ('C', '0', FALSE),
    ('D', '2', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor de la integral impropia de 1/x² desde x = 1 hasta ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I65
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor medio de f(x) = x² en el intervalo [0, 3]?', 'avanzado',
       'Valor medio = (1/(b - a))·∫ f(x) dx en [a, b].'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor medio de f(x) = x² en el intervalo [0, 3]?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '9', FALSE),
    ('B', '3', TRUE),
    ('C', '4.5', FALSE),
    ('D', '1', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = '¿Cuál es el valor medio de f(x) = x² en el intervalo [0, 3]?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);

-- I66
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', 'Si F(x) es la integral de cos(t²) desde t = 0 hasta t = x, ¿cuánto vale F′(x)?', 'avanzado',
       'Teorema fundamental del cálculo: la derivada de la integral con límite superior x es el integrando evaluado en x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = 'Si F(x) es la integral de cos(t²) desde t = 0 hasta t = x, ¿cuánto vale F′(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, v.letra, v.texto, v.correcta
FROM preguntas p
CROSS JOIN (VALUES
    ('A', '-sen(x²)', FALSE),
    ('B', '2x·cos(x²)', FALSE),
    ('C', 'cos(x²)', TRUE),
    ('D', 'sen(x²)', FALSE)
) AS v(letra, texto, correcta)
WHERE p.enunciado = 'Si F(x) es la integral de cos(t²) desde t = 0 hasta t = x, ¿cuánto vale F′(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id);
