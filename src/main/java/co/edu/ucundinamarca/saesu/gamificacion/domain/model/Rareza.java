package co.edu.ucundinamarca.saesu.gamificacion.domain.model;

/**
 * Qué tan difícil es un logro; el frontend le da un color y un brillo distinto a cada una.
 * EXCLUSIVO no se gana jugando: lo asigna el sistema (p. ej. a los desarrolladores) y solo lo ve quien lo tiene.
 */
public enum Rareza {
    COMUN, RARO, EPICO, LEGENDARIO, EXCLUSIVO
}
