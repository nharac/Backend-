-- ============================================
-- V3: Insertar preguntas iniciales de ejemplo
-- ============================================
-- Este script carga 15 preguntas tipo ICFES (5 por tema).
-- Es idempotente: si las preguntas ya existen (por enunciado), las omite.
--
-- IMPORTANTE: Solo para desarrollo y demo. En producción, las preguntas
-- deben ser creadas por docentes mediante el panel correspondiente.

-- ============================================
-- PREGUNTAS DE LÍMITES (5)
-- ============================================

-- L1: Límite básico polinomial
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = x² + 3x cuando x → 2?', 'basico',
       'Sustituye directamente el valor de x en la función.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = x² + 3x cuando x → 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '10', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = x² + 3x cuando x → 2?'
UNION ALL
SELECT p.id, 'B', '11', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = x² + 3x cuando x → 2?'
UNION ALL
SELECT p.id, 'C', '9', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = x² + 3x cuando x → 2?'
UNION ALL
SELECT p.id, 'D', '12', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = x² + 3x cuando x → 2?';

-- L2: Límite indeterminado 0/0
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x² - 4) / (x - 2) cuando x → 2?', 'intermedio',
       'Factoriza el numerador como diferencia de cuadrados.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x² - 4) / (x - 2) cuando x → 2?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 4) / (x - 2) cuando x → 2?'
UNION ALL
SELECT p.id, 'B', '2', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 4) / (x - 2) cuando x → 2?'
UNION ALL
SELECT p.id, 'C', '4', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 4) / (x - 2) cuando x → 2?'
UNION ALL
SELECT p.id, 'D', '∞', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 4) / (x - 2) cuando x → 2?';

-- L3: Límite trigonométrico fundamental
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = sen(x) / x cuando x → 0?', 'intermedio',
       'Es un límite fundamental. Recuerda el teorema del emparedado.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = sen(x) / x cuando x → 0?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = sen(x) / x cuando x → 0?'
UNION ALL
SELECT p.id, 'B', '∞', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = sen(x) / x cuando x → 0?'
UNION ALL
SELECT p.id, 'C', '1', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = sen(x) / x cuando x → 0?'
UNION ALL
SELECT p.id, 'D', 'No existe', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = sen(x) / x cuando x → 0?';

-- L4: Límite al infinito
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (3x² + 2x) / (x² + 1) cuando x → ∞?', 'avanzado',
       'Divide numerador y denominador por la potencia más alta de x.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (3x² + 2x) / (x² + 1) cuando x → ∞?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (3x² + 2x) / (x² + 1) cuando x → ∞?'
UNION ALL
SELECT p.id, 'B', '∞', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (3x² + 2x) / (x² + 1) cuando x → ∞?'
UNION ALL
SELECT p.id, 'C', '3', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (3x² + 2x) / (x² + 1) cuando x → ∞?'
UNION ALL
SELECT p.id, 'D', '2', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (3x² + 2x) / (x² + 1) cuando x → ∞?';

-- L5: Límite de función racional en punto
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'limites', '¿Cuál es el límite de f(x) = (x² - 1) / (x - 1) cuando x → 1?', 'basico',
       'Factoriza el numerador como diferencia de cuadrados.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es el límite de f(x) = (x² - 1) / (x - 1) cuando x → 1?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '1', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 1) / (x - 1) cuando x → 1?'
UNION ALL
SELECT p.id, 'B', '2', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 1) / (x - 1) cuando x → 1?'
UNION ALL
SELECT p.id, 'C', '0', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 1) / (x - 1) cuando x → 1?'
UNION ALL
SELECT p.id, 'D', 'No existe', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es el límite de f(x) = (x² - 1) / (x - 1) cuando x → 1?';

-- ============================================
-- PREGUNTAS DE DERIVADAS (5)
-- ============================================

-- D1: Derivada de polinomio
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = x² + 3x?', 'basico',
       'Usa la regla de la potencia: d/dx[x^n] = n·x^(n-1).'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = x² + 3x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '2x + 3', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x² + 3x?'
UNION ALL
SELECT p.id, 'B', 'x² + 3', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x² + 3x?'
UNION ALL
SELECT p.id, 'C', '2x', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x² + 3x?'
UNION ALL
SELECT p.id, 'D', 'x + 3', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x² + 3x?';

-- D2: Derivada de producto con logaritmo
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = x³ · ln(x)?', 'avanzado',
       'Aplica la regla del producto: (u·v)'' = u''·v + u·v''.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = x³ · ln(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '3x² · ln(x) + x²', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x³ · ln(x)?'
UNION ALL
SELECT p.id, 'B', '3x² · ln(x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x³ · ln(x)?'
UNION ALL
SELECT p.id, 'C', 'x² + ln(x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x³ · ln(x)?'
UNION ALL
SELECT p.id, 'D', '3x² / x', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = x³ · ln(x)?';

-- D3: Derivada de seno
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = sen(x)?', 'basico',
       'Es una derivada fundamental que debes memorizar.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = sen(x)?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'cos(x)', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = sen(x)?'
UNION ALL
SELECT p.id, 'B', '-cos(x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = sen(x)?'
UNION ALL
SELECT p.id, 'C', '-sen(x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = sen(x)?'
UNION ALL
SELECT p.id, 'D', 'tan(x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = sen(x)?';

-- D4: Derivada de composición (regla de la cadena)
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = (2x + 1)³?', 'intermedio',
       'Aplica la regla de la cadena: deriva la potencia y multiplica por la derivada interna.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = (2x + 1)³?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '3(2x + 1)²', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = (2x + 1)³?'
UNION ALL
SELECT p.id, 'B', '6(2x + 1)²', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = (2x + 1)³?'
UNION ALL
SELECT p.id, 'C', '2(2x + 1)²', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = (2x + 1)³?'
UNION ALL
SELECT p.id, 'D', '3(2x + 1)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = (2x + 1)³?';

-- D5: Derivada de exponencial
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'derivadas', '¿Cuál es la derivada de f(x) = e^x?', 'basico',
       'Es una función especial: su derivada es ella misma.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la derivada de f(x) = e^x?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'x·e^(x-1)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^x?'
UNION ALL
SELECT p.id, 'B', 'e^x', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^x?'
UNION ALL
SELECT p.id, 'C', 'ln(x)', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^x?'
UNION ALL
SELECT p.id, 'D', '1/e^x', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la derivada de f(x) = e^x?';

-- ============================================
-- PREGUNTAS DE INTEGRALES (5)
-- ============================================

-- I1: Integral de potencia
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ x² dx?', 'basico',
       'Usa la regla de la potencia para integrales: ∫x^n dx = x^(n+1)/(n+1) + C.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ x² dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'x³ + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x² dx?'
UNION ALL
SELECT p.id, 'B', 'x³/3 + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x² dx?'
UNION ALL
SELECT p.id, 'C', '2x + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x² dx?'
UNION ALL
SELECT p.id, 'D', 'x³/2 + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x² dx?';

-- I2: Integral de 1/x
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ (1/x) dx?', 'basico',
       'Esta integral da como resultado el logaritmo natural.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ (1/x) dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'ln|x| + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ (1/x) dx?'
UNION ALL
SELECT p.id, 'B', 'x²/2 + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ (1/x) dx?'
UNION ALL
SELECT p.id, 'C', '-1/x² + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ (1/x) dx?'
UNION ALL
SELECT p.id, 'D', 'e^x + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ (1/x) dx?';

-- I3: Integral de e^x
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ e^x dx?', 'basico',
       'Es una integral fundamental: la derivada y la integral dan lo mismo.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ e^x dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'e^x + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ e^x dx?'
UNION ALL
SELECT p.id, 'B', 'x·e^x + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ e^x dx?'
UNION ALL
SELECT p.id, 'C', 'e^x / x + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ e^x dx?'
UNION ALL
SELECT p.id, 'D', 'ln(e^x) + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ e^x dx?';

-- I4: Integral por sustitución
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ 2x·(x² + 1)³ dx?', 'intermedio',
       'Usa sustitución. Haz u = x² + 1 y calcula du.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ 2x·(x² + 1)³ dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', '(x² + 1)⁴ / 4 + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 2x·(x² + 1)³ dx?'
UNION ALL
SELECT p.id, 'B', '(x² + 1)³ / 3 + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 2x·(x² + 1)³ dx?'
UNION ALL
SELECT p.id, 'C', '2x·(x² + 1)⁴ / 4 + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 2x·(x² + 1)³ dx?'
UNION ALL
SELECT p.id, 'D', '(x² + 1)² / 2 + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ 2x·(x² + 1)³ dx?';

-- I5: Integral por partes
INSERT INTO preguntas (tema, enunciado, dificultad, pista)
SELECT 'integrales', '¿Cuál es la integral de ∫ x·e^x dx?', 'avanzado',
       'Usa integración por partes: ∫u dv = u·v - ∫v du.'
WHERE NOT EXISTS (SELECT 1 FROM preguntas WHERE enunciado = '¿Cuál es la integral de ∫ x·e^x dx?');

INSERT INTO opciones (pregunta_id, letra, texto, es_correcta)
SELECT p.id, 'A', 'x·e^x - e^x + C', TRUE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x·e^x dx?'
UNION ALL
SELECT p.id, 'B', 'x·e^x + e^x + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x·e^x dx?'
UNION ALL
SELECT p.id, 'C', 'x²·e^x / 2 + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x·e^x dx?'
UNION ALL
SELECT p.id, 'D', 'e^x / x + C', FALSE FROM preguntas p WHERE p.enunciado = '¿Cuál es la integral de ∫ x·e^x dx?';

-- ============================================
-- Verificación
-- ============================================
-- Después de esta migración debe haber:
--   - 15 preguntas (5 por tema)
--   - 60 opciones (4 por pregunta)
--   - 15 opciones marcadas como correctas (1 por pregunta)