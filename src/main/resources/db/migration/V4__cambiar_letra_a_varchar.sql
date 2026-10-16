-- ============================================
-- V4: Cambiar el tipo de la columna 'letra' de CHAR(1) a VARCHAR(1)
-- ============================================
-- Motivo: Hibernate 6.6 tiene un bug validando CHAR(1) contra String
-- (lo interpreta como bpchar y no coincide con varchar).
-- VARCHAR(1) es funcionalmente equivalente y 100% compatible con Hibernate.

ALTER TABLE opciones 
    ALTER COLUMN letra TYPE VARCHAR(1);