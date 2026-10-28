package co.edu.ucundinamarca.saesu.quiz.domain.model;

import java.time.Instant;

/**
 * Un quiz que alguien empezó.
 * @param tema        null si es mixto
 * @param correctas   null mientras no lo ha finalizado
 */
public record IntentoQuiz(Long id, Long usuarioId, String tema, int cantidad,
                          Instant iniciadoEn, Instant limiteEn, Instant finalizadoEn,
                          Integer correctas, boolean fueraDeTiempo) {

    public boolean finalizado() {
        return finalizadoEn != null;
    }
}
