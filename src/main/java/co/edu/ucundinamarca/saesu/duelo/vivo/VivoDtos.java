package co.edu.ucundinamarca.saesu.duelo.vivo;

import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Peticiones y respuestas de los retos en vivo. */
public final class VivoDtos {

    private VivoDtos() {}

    /** Sin invitadoId la sala queda abierta: entra el primero que tenga el código. */
    public record CrearSalaRequest(
            Long invitadoId,
            @NotNull(message = "El tema es obligatorio") @Pattern(regexp = Tema.PATRON, message = "Tema no válido") String tema) {}

    public record ResponderRequest(@NotNull Integer orden, @NotNull @Size(min = 1, max = 2) String letra) {}

    public record Opcion(String letra, String texto) {}

    public record PreguntaVivo(int orden, Long id, String enunciado, List<Opcion> opciones) {}

    public record Jugador(String nombre, boolean soyYo, int puntos, int aciertos, boolean respondioActual) {}

    /** Mi respuesta a la pregunta actual (la correcta se muestra apenas respondo). */
    public record MiRespuesta(String letra, boolean correcta, String letraCorrecta, int puntos) {}

    /**
     * Foto del juego en este momento. El cliente la pide cada segundo.
     *
     * @param estado            ESPERANDO, JUGANDO, TERMINADA o CANCELADA
     * @param cuentaRegresiva   segundos para empezar (solo antes de la primera pregunta)
     * @param indice            pregunta actual (0..total-1)
     * @param segundosRestantes de la pregunta actual
     * @param resultado         GANASTE, PERDISTE o EMPATE al terminar
     */
    public record EstadoSala(String codigo, String tema, String temaNombre, String estado, boolean soyAnfitrion,
                             boolean abierta, String rival, Integer cuentaRegresiva, int indice, int total,
                             int segundosPorPregunta, Integer segundosRestantes, PreguntaVivo pregunta, MiRespuesta miRespuesta,
                             List<Jugador> jugadores, String resultado) {}
}
