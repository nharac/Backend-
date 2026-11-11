package co.edu.ucundinamarca.saesu.practica.domain.exception;

/** El reto del día ya se respondió: solo hay una oportunidad. */
public class RetoYaRespondidoException extends RuntimeException {
    public RetoYaRespondidoException() {
        super("Ya respondiste el reto de hoy. Vuelve mañana por otro.");
    }
}
