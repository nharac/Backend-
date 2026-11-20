package co.edu.ucundinamarca.saesu.practica.ports.output;

import java.time.LocalDate;

/** Cajas del repaso espaciado (1 → 3 → 7 días → dominada). */
public interface RepasoEspaciadoPort {

    /** Días hasta el próximo repaso después de acertar estando en cada caja (caja 1 → 3 días, caja 2 → 7 días, caja 3 → dominada). */
    int[] INTERVALOS = {1, 3, 7};

    record Resumen(long paraHoy, long manana, long estaSemana, long dominadas, long enRepaso) {}

    /** Error: la pregunta vuelve a la caja 1 y se repasa mañana. */
    void fallo(Long usuarioId, Long preguntaId, LocalDate hoy);

    /** Acierto: si tocaba repasarla, sube de caja (o queda dominada). */
    void acierto(Long usuarioId, Long preguntaId, LocalDate hoy);

    Resumen resumen(Long usuarioId, LocalDate hoy);
}
