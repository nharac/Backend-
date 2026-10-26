-- ============================================
-- V13: 30 preguntas nuevas tipo ICFES (10 por tema)
-- ============================================
-- Con las 15 de V3 quedan 45 en total (15 por tema):
-- Límites y derivadas: 4 básicas, 4 intermedias y 2 avanzadas. Integrales: 4, 5 y 1.
-- Igual que V3, es idempotente: si una pregunta ya existe (mismo enunciado) se omite.
-- La opción correcta cambia de letra entre preguntas para que no siempre sea la misma.


-- ============================================
-- LÍMITES (10)
-- ============================================

-- L1: ¿Cuál es el límite de f(x) = 2x + 5 cuando x → 3?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = 2x + 5 cuando x → 3?', 'basico',
       'Es un polinomio: basta con reemplazar x por 3.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = 2x + 5 cuando x → 3?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '8', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 2x + 5 cuando x → 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '11', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 2x + 5 cuando x → 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '6', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 2x + 5 cuando x → 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '13', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 2x + 5 cuando x → 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- L2: ¿Cuál es el límite de f(x) = (x² + x) / x cuando x → 0?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x² + x) / x cuando x → 0?', 'basico',
       'Saca x como factor común en el numerador y simplifica antes de reemplazar.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x² + x) / x cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '1', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² + x) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² + x) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '∞', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² + x) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', 'No existe', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² + x) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- L3: ¿Cuál es el límite de f(x) = 1/x cuando x → ∞?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = 1/x cuando x → ∞?', 'basico',
       'Piensa qué pasa al dividir 1 entre un número cada vez más grande.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = 1/x cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '1', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 1/x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '∞', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 1/x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '-∞', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 1/x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '0', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 1/x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- L4: ¿Cuál es el límite de f(x) = 1/x cuando x → 0 por la derecha (x → 0⁺)?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = 1/x cuando x → 0 por la derecha (x → 0⁺)?', 'basico',
       'Prueba con x = 0.1, 0.01, 0.001… ¿el resultado crece o decrece?'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = 1/x cuando x → 0 por la derecha (x → 0⁺)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 1/x cuando x → 0 por la derecha (x → 0⁺)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '1', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 1/x cuando x → 0 por la derecha (x → 0⁺)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '+∞', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 1/x cuando x → 0 por la derecha (x → 0⁺)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '-∞', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = 1/x cuando x → 0 por la derecha (x → 0⁺)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- L5: ¿Cuál es el límite de f(x) = (x³ - 8) / (x - 2) cuando x → 2?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x³ - 8) / (x - 2) cuando x → 2?', 'intermedio',
       'Usa la diferencia de cubos: x³ - 8 = (x - 2)(x² + 2x + 4).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x³ - 8) / (x - 2) cuando x → 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '4', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x³ - 8) / (x - 2) cuando x → 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '12', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x³ - 8) / (x - 2) cuando x → 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '8', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x³ - 8) / (x - 2) cuando x → 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x³ - 8) / (x - 2) cuando x → 2?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- L6: ¿Cuál es el límite de f(x) = (5x³ - 2) / (2x³ + x) cuando x → ∞?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (5x³ - 2) / (2x³ + x) cuando x → ∞?', 'intermedio',
       'Divide numerador y denominador entre la mayor potencia de x (x³).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (5x³ - 2) / (2x³ + x) cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '5/2', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (5x³ - 2) / (2x³ + x) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (5x³ - 2) / (2x³ + x) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '∞', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (5x³ - 2) / (2x³ + x) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '5', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (5x³ - 2) / (2x³ + x) cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- L7: ¿Cuál es el límite de f(x) = sen(3x) / x cuando x → 0?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = sen(3x) / x cuando x → 0?', 'intermedio',
       'Multiplica y divide por 3 para usar el límite fundamental sen(u)/u → 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = sen(3x) / x cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '1', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = sen(3x) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '1/3', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = sen(3x) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = sen(3x) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '3', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = sen(3x) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- L8: ¿Cuál es el límite de f(x) = (1 + 1/x)^x cuando x → ∞?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (1 + 1/x)^x cuando x → ∞?', 'intermedio',
       'Es la definición de un número muy famoso del cálculo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (1 + 1/x)^x cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '1', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 + 1/x)^x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '∞', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 + 1/x)^x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', 'e', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 + 1/x)^x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 + 1/x)^x cuando x → ∞?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- L9: ¿Cuál es el límite de f(x) = (1 - cos(x)) / x cuando x → 0?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (1 - cos(x)) / x cuando x → 0?', 'avanzado',
       'Multiplica por (1 + cos x) / (1 + cos x) y usa sen²x + cos²x = 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (1 - cos(x)) / x cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '1', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 - cos(x)) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '0', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 - cos(x)) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '1/2', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 - cos(x)) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', 'No existe', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (1 - cos(x)) / x cuando x → 0?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- L10: ¿Cuál es el límite de f(x) = (√x - 2) / (x - 4) cuando x → 4?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (√x - 2) / (x - 4) cuando x → 4?', 'avanzado',
       'Racionaliza: multiplica numerador y denominador por (√x + 2).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (√x - 2) / (x - 4) cuando x → 4?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '1/4', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (√x - 2) / (x - 4) cuando x → 4?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '1/2', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (√x - 2) / (x - 4) cuando x → 4?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (√x - 2) / (x - 4) cuando x → 4?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '4', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (√x - 2) / (x - 4) cuando x → 4?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');


-- ============================================
-- DERIVADAS (10)
-- ============================================

-- D1: ¿Cuál es la derivada de f(x) = 5x⁴?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = 5x⁴?', 'basico',
       'Regla de la potencia: baja el exponente multiplicando y réstale 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = 5x⁴?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '5x³', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 5x⁴?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '20x⁴', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 5x⁴?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '4x³', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 5x⁴?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '20x³', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = 5x⁴?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- D2: ¿Cuál es la derivada de f(x) = cos(x)?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = cos(x)?', 'basico',
       'La derivada del coseno cambia de función y de signo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = cos(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'sen(x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = cos(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '-cos(x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = cos(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '-sen(x)', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = cos(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', 'cos(x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = cos(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- D3: ¿Cuál es la derivada de f(x) = ln(x)?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = ln(x)?', 'basico',
       'Es una de las derivadas básicas que conviene memorizar.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = ln(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'x', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = ln(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '1/x', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = ln(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', 'ln(x)/x', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = ln(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', 'e^x', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = ln(x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- D4: ¿Cuál es la derivada de f(x) = √x?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = √x?', 'basico',
       'Escribe √x como x^(1/2) y aplica la regla de la potencia.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = √x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '1/(2√x)', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = √x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '2√x', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = √x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '√x/2', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = √x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '1/√x', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = √x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- D5: ¿Cuál es la derivada de f(x) = sen(2x)?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = sen(2x)?', 'intermedio',
       'Regla de la cadena: deriva la función de afuera y multiplica por la derivada de 2x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = sen(2x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'cos(2x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = sen(2x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '-2cos(2x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = sen(2x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '2sen(2x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = sen(2x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '2cos(2x)', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = sen(2x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- D6: ¿Cuál es la derivada de f(x) = x · e^x?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = x · e^x?', 'intermedio',
       'Regla del producto: (uv)'' = u''v + uv''.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = x · e^x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'e^x', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x · e^x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', 'x · e^x', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x · e^x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', 'e^x(x + 1)', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x · e^x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', 'e^x(x - 1)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x · e^x?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- D7: ¿Cuál es la derivada de f(x) = e^(3x)?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = e^(3x)?', 'intermedio',
       'La derivada de e^u es e^u · u''. Aquí u = 3x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = e^(3x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'e^(3x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^(3x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '3e^(3x)', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^(3x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '3x · e^(3x - 1)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^(3x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', 'e^(3x) / 3', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^(3x)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- D8: ¿Cuál es la pendiente de la recta tangente a f(x) = x² en x = 3?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la pendiente de la recta tangente a f(x) = x² en x = 3?', 'intermedio',
       'La pendiente de la tangente es la derivada evaluada en ese punto.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la pendiente de la recta tangente a f(x) = x² en x = 3?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '6', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la pendiente de la recta tangente a f(x) = x² en x = 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '9', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la pendiente de la recta tangente a f(x) = x² en x = 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '3', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la pendiente de la recta tangente a f(x) = x² en x = 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '2', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la pendiente de la recta tangente a f(x) = x² en x = 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- D9: ¿Cuál es la derivada de f(x) = x² / (x + 1)?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = x² / (x + 1)?', 'avanzado',
       'Regla del cociente: (u/v)'' = (u''v - uv'') / v².'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = x² / (x + 1)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '2x / (x + 1)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x² / (x + 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '(x² - 2x) / (x + 1)²', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x² / (x + 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '2x', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x² / (x + 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '(x² + 2x) / (x + 1)²', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x² / (x + 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- D10: ¿Cuál es la derivada de f(x) = ln(x² + 1)?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = ln(x² + 1)?', 'avanzado',
       'La derivada de ln(u) es u''/u. Aquí u = x² + 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = ln(x² + 1)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '1 / (x² + 1)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = ln(x² + 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '2x · ln(x² + 1)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = ln(x² + 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '2x / (x² + 1)', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = ln(x² + 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '2 / (x² + 1)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = ln(x² + 1)?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');


-- ============================================
-- INTEGRALES (10)
-- ============================================

-- I1: ¿Cuál es la integral de ∫ 3 dx?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 3 dx?', 'basico',
       'La integral de una constante k es k·x. No olvides la constante C.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 3 dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '3 + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 3 dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '3x + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 3 dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', 'x³ + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 3 dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 3 dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- I2: ¿Cuál es la integral de ∫ x⁴ dx?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ x⁴ dx?', 'basico',
       'Súmale 1 al exponente y divide entre el nuevo exponente.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ x⁴ dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'x⁵/5 + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x⁴ dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '4x³ + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x⁴ dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', 'x⁵ + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x⁴ dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', 'x⁴/4 + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x⁴ dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- I3: ¿Cuál es la integral de ∫ cos(x) dx?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ cos(x) dx?', 'basico',
       'Piensa: ¿qué función tiene como derivada a cos(x)?'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ cos(x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '-sen(x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ cos(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', 'cos(x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ cos(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '-cos(x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ cos(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', 'sen(x) + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ cos(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- I4: ¿Cuál es la integral de ∫ sen(x) dx?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ sen(x) dx?', 'basico',
       'Piensa: ¿qué función tiene como derivada a sen(x)? Cuidado con el signo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ sen(x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'cos(x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ sen(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', 'sen(x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ sen(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '-cos(x) + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ sen(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '-sen(x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ sen(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- I5: ¿Cuál es el valor de la integral definida ∫₀² x dx?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida ∫₀² x dx?', 'intermedio',
       'Calcula la antiderivada x²/2 y evalúa F(2) - F(0).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida ∫₀² x dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '4', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el valor de la integral definida ∫₀² x dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '2', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el valor de la integral definida ∫₀² x dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '1', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el valor de la integral definida ∫₀² x dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el valor de la integral definida ∫₀² x dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- I6: ¿Cuál es el valor de la integral definida ∫₀¹ 3x² dx?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el valor de la integral definida ∫₀¹ 3x² dx?', 'intermedio',
       'La antiderivada de 3x² es x³. Evalúa entre 0 y 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el valor de la integral definida ∫₀¹ 3x² dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '1', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el valor de la integral definida ∫₀¹ 3x² dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '3', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el valor de la integral definida ∫₀¹ 3x² dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '1/3', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el valor de la integral definida ∫₀¹ 3x² dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el valor de la integral definida ∫₀¹ 3x² dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- I7: ¿Cuál es la integral de ∫ e^(2x) dx?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ e^(2x) dx?', 'intermedio',
       'Sustitución u = 2x: recuerda dividir entre la derivada de u.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ e^(2x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '2e^(2x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ e^(2x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', 'e^(2x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ e^(2x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', 'e^(x²) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ e^(2x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', 'e^(2x)/2 + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ e^(2x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- I8: ¿Cuál es la integral de ∫ 1/(x + 1) dx?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 1/(x + 1) dx?', 'intermedio',
       'Es como ∫ 1/u du con u = x + 1.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 1/(x + 1) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '1/(x + 1)² + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 1/(x + 1) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '-1/(x + 1)² + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 1/(x + 1) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', 'ln|x + 1| + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 1/(x + 1) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '(x + 1)²/2 + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 1/(x + 1) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- I9: ¿Cuál es el área bajo la curva f(x) = x² entre x = 0 y x = 3?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es el área bajo la curva f(x) = x² entre x = 0 y x = 3?', 'intermedio',
       'El área es la integral definida ∫₀³ x² dx = [x³/3] de 0 a 3.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el área bajo la curva f(x) = x² entre x = 0 y x = 3?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '27', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el área bajo la curva f(x) = x² entre x = 0 y x = 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', '9', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el área bajo la curva f(x) = x² entre x = 0 y x = 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '3', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el área bajo la curva f(x) = x² entre x = 0 y x = 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', '6', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el área bajo la curva f(x) = x² entre x = 0 y x = 3?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');

-- I10: ¿Cuál es la integral de ∫ x · cos(x) dx?
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ x · cos(x) dx?', 'avanzado',
       'Integración por partes con u = x y dv = cos(x) dx.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ x · cos(x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'x·sen(x) + cos(x) + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x · cos(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'A')
UNION ALL
SELECT p.id, 'B', 'x·sen(x) - cos(x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x · cos(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'B')
UNION ALL
SELECT p.id, 'C', '-x·sen(x) + cos(x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x · cos(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'C')
UNION ALL
SELECT p.id, 'D', 'sen(x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x · cos(x) dx?'
  AND NOT EXISTS (SELECT 1 FROM opciones o WHERE o.pregunta_id = p.id AND o.letra = 'D');
