package co.edu.ucundinamarca.saesu.gamificacion.domain.model;

import lombok.Getter;

/**
 * Catálogo de logros. La condición de cada uno se evalúa en LogrosService.
 * El ícono es un nombre de Material Symbols que usa el frontend.
 */
@Getter
public enum Logro {

    PRIMER_ACIERTO("Primer paso", "Primera respuesta correcta", "rocket_launch"),
    A_LA_PRIMERA("A la primera", "10 preguntas acertadas al primer intento", "bolt"),
    DOMINIO_LIMITES("Dominio de Límites", "Acertar todas las preguntas de límites", "trending_flat"),
    DOMINIO_DERIVADAS("Dominio de Derivadas", "Acertar todas las preguntas de derivadas", "show_chart"),
    DOMINIO_INTEGRALES("Dominio de Integrales", "Acertar todas las preguntas de integrales", "functions"),
    RACHA_3("En llamas", "Racha de 3 días", "local_fire_department"),
    RACHA_7("Semana perfecta", "Racha de 7 días", "whatshot"),
    CURIOSO("Curioso", "Primera publicación en el foro", "help"),
    COLABORADOR("Colaborador", "10 respuestas en el foro", "volunteer_activism"),
    RESPUESTA_DE_ORO("Respuesta de oro", "Un docente marcó tu respuesta como oficial", "verified"),
    PUNTOS_500("Constante", "Llegar a 500 puntos", "military_tech");

    private final String nombre;
    private final String descripcion;
    private final String icono;

    Logro(String nombre, String descripcion, String icono) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = icono;
    }
}
