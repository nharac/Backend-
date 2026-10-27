-- ============================================
-- V15: Normalizar el rol de los usuarios
-- ============================================
-- Había roles guardados con espacios o saltos de línea (ej. 'DOCENTE\n'), en minúscula
-- o con el prefijo 'ROLE_'. El backend arma la autoridad como "ROLE_" + rol, así que un
-- rol con un salto de línea dejaba al docente sin acceso a /api/docente/** (403).
UPDATE usuarios
SET rol = REGEXP_REPLACE(UPPER(REGEXP_REPLACE(rol, '\s', '', 'g')), '^ROLE_', '')
WHERE rol IS DISTINCT FROM REGEXP_REPLACE(UPPER(REGEXP_REPLACE(rol, '\s', '', 'g')), '^ROLE_', '');
