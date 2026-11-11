package co.edu.ucundinamarca.saesu.practica.ports.output;

import java.time.LocalDate;
import java.util.Optional;

/** Puerto de salida: pregunta del reto diario y el único intento de cada estudiante (V27). */
public interface RetoDiarioPort {

    /** Lo que respondió ese día. */
    record Intento(Long preguntaId, Long opcionId, boolean correcta) {
    }

    /** Pregunta número {@code indice} del banco (dando la vuelta si hay menos), o vacío si no hay preguntas. */
    Optional<Long> preguntaDelDia(long indice);

    Optional<Intento> intento(Long usuarioId, LocalDate fecha);

    /** @return false si ya había respondido ese día (no se guarda un segundo intento) */
    boolean registrar(Long usuarioId, LocalDate fecha, Intento intento);
}
