package co.edu.ucundinamarca.saesu.gamificacion.domain.model;

import lombok.Getter;

import static co.edu.ucundinamarca.saesu.gamificacion.domain.model.CategoriaLogro.*;
import static co.edu.ucundinamarca.saesu.gamificacion.domain.model.Rareza.*;

/**
 * Catálogo de logros. Cada uno se cumple cuando su {@link Cifra} llega a la meta
 * (LogrosService lo evalúa). El ícono es un nombre de Material Symbols que usa el frontend.
 * Meta 0 = depende del banco o del catálogo: todas las preguntas del tema, del banco, las avanzadas,
 * o todos los demás logros (cambia si el docente agrega preguntas).
 * Los códigos existentes no se renombran: ya están guardados en usuario_logros.
 */
@Getter
public enum Logro {

    // ---------------- Práctica ----------------
    PRIMER_ACIERTO("Primer paso", "Primera respuesta correcta", "rocket_launch", PRACTICA, COMUN, Cifra.ACIERTOS, 1),
    ACIERTOS_25("Calentando motores", "25 respuestas correctas", "directions_run", PRACTICA, COMUN, Cifra.ACIERTOS, 25),
    ACIERTOS_100("Centenario", "100 respuestas correctas", "looks_one", PRACTICA, RARO, Cifra.ACIERTOS, 100),
    ACIERTOS_250("Imparable", "250 respuestas correctas", "all_inclusive", PRACTICA, EPICO, Cifra.ACIERTOS, 250),
    A_LA_PRIMERA("A la primera", "10 preguntas acertadas al primer intento", "bolt", PRACTICA, COMUN, Cifra.PRIMERA, 10),
    A_LA_PRIMERA_50("Ojo de halcón", "50 preguntas acertadas al primer intento", "visibility", PRACTICA, EPICO, Cifra.PRIMERA, 50),
    PRECISION_80("Precisión quirúrgica", "80 % de acierto o más, con al menos 50 respuestas", "my_location", PRACTICA, EPICO, Cifra.RESPONDIDAS, 50),
    AVANZADAS_10("Mente maestra", "10 preguntas avanzadas resueltas", "psychology", PRACTICA, RARO, Cifra.AVANZADAS, 10),

    // ---------------- Temas ----------------
    EXPLORADOR("Explorador", "Resolver al menos una pregunta de cada tema", "explore", TEMAS, COMUN, Cifra.TEMAS_EXPLORADOS, 3),
    APRENDIZ_LIMITES("Aprendiz de Límites", "10 preguntas de límites resueltas", "trending_flat", TEMAS, COMUN, Cifra.RESUELTAS_LIMITES, 10),
    APRENDIZ_DERIVADAS("Aprendiz de Derivadas", "10 preguntas de derivadas resueltas", "show_chart", TEMAS, COMUN, Cifra.RESUELTAS_DERIVADAS, 10),
    APRENDIZ_INTEGRALES("Aprendiz de Integrales", "10 preguntas de integrales resueltas", "functions", TEMAS, COMUN, Cifra.RESUELTAS_INTEGRALES, 10),
    DOMINIO_LIMITES("Dominio de Límites", "Acertar todas las preguntas de límites", "trending_flat", TEMAS, EPICO, Cifra.RESUELTAS_LIMITES, 0),
    DOMINIO_DERIVADAS("Dominio de Derivadas", "Acertar todas las preguntas de derivadas", "show_chart", TEMAS, EPICO, Cifra.RESUELTAS_DERIVADAS, 0),
    DOMINIO_INTEGRALES("Dominio de Integrales", "Acertar todas las preguntas de integrales", "functions", TEMAS, EPICO, Cifra.RESUELTAS_INTEGRALES, 0),

    // ---------------- Rachas ----------------
    RACHA_3("En llamas", "Racha de 3 días", "local_fire_department", RACHAS, COMUN, Cifra.RACHA, 3),
    RACHA_7("Semana perfecta", "Racha de 7 días", "whatshot", RACHAS, RARO, Cifra.RACHA, 7),
    RACHA_15("Quincena de fuego", "Racha de 15 días", "mode_heat", RACHAS, EPICO, Cifra.RACHA, 15),
    RACHA_30("Llama eterna", "Racha de 30 días", "sunny", RACHAS, LEGENDARIO, Cifra.RACHA, 30),

    // ---------------- Quiz ----------------
    QUIZ_1("A prueba", "Terminar tu primer quiz", "timer", QUIZ, COMUN, Cifra.QUICES, 1),
    QUIZ_10("Veterano de quices", "Terminar 10 quices", "alarm_on", QUIZ, RARO, Cifra.QUICES, 10),

    // ---------------- Comunidad ----------------
    CURIOSO("Curioso", "Primera publicación en el foro", "help", COMUNIDAD, COMUN, Cifra.PUBLICACIONES, 1),
    MENTE_INQUIETA("Mente inquieta", "5 publicaciones en el foro", "lightbulb", COMUNIDAD, RARO, Cifra.PUBLICACIONES, 5),
    COLABORADOR("Colaborador", "10 respuestas en el foro", "volunteer_activism", COMUNIDAD, RARO, Cifra.RESPUESTAS_FORO, 10),
    RESPUESTA_DE_ORO("Respuesta de oro", "Un docente marcó tu respuesta como oficial", "verified", COMUNIDAD, RARO, Cifra.OFICIALES, 1),
    MENTOR("Mentor", "5 respuestas oficiales en el foro", "school", COMUNIDAD, EPICO, Cifra.OFICIALES, 5),
    CHARLA_CON_SUMI("Charla con Sumi", "10 preguntas al tutor", "chat", COMUNIDAD, COMUN, Cifra.MENSAJES_TUTOR, 10),
    AMIGO_DE_SUMI("Amigo de Sumi", "50 preguntas al tutor", "favorite", COMUNIDAD, RARO, Cifra.MENSAJES_TUTOR, 50),

    // ---------------- Puntos Sumi ----------------
    PUNTOS_100("Primeros brillos", "Llegar a 100 Puntos Sumi", "star", PUNTOS, COMUN, Cifra.PUNTOS, 100),
    PUNTOS_500("Constante", "Llegar a 500 Puntos Sumi", "military_tech", PUNTOS, RARO, Cifra.PUNTOS, 500),
    PUNTOS_1000("Mil Puntos Sumi", "Llegar a 1.000 Puntos Sumi", "workspace_premium", PUNTOS, EPICO, Cifra.PUNTOS, 1000),
    PUNTOS_2500("Leyenda Sumi", "Llegar a 2.500 Puntos Sumi", "diamond", PUNTOS, LEGENDARIO, Cifra.PUNTOS, 2500),

    // ---------------- Retos (insignias especiales: muy difíciles) ----------------
    EN_LA_ZONA("Mente imparable", "50 respuestas correctas seguidas, sin fallar ninguna", "target", RETOS, LEGENDARIO, Cifra.MAX_SEGUIDAS, 50),
    MARATON("Maratón del páramo", "100 aciertos en un mismo día", "sprint", RETOS, LEGENDARIO, Cifra.MAX_ACIERTOS_DIA, 100),
    MATEMATICO("Matemático", "Resolver todas las preguntas del banco de práctica", "calculate", RETOS, LEGENDARIO, Cifra.PREGUNTAS_RESUELTAS, 0),
    GENIO_DEL_CALCULO("Genio del cálculo", "Resolver todas las preguntas avanzadas", "neurology", RETOS, LEGENDARIO, Cifra.AVANZADAS, 0),
    FRANCOTIRADOR("Francotirador", "95 % de acierto o más, con al menos 200 respuestas", "crisis_alert", RETOS, LEGENDARIO, Cifra.RESPONDIDAS, 200),
    CERO_ERRORES("Perfeccionista", "5 quices de 10 o más preguntas sin fallar ninguna", "verified_user", RETOS, LEGENDARIO, Cifra.QUICES_PERFECTOS, 5),
    SIN_DESCANSO("Voluntad de hierro", "Racha de 60 días seguidos practicando", "bolt", RETOS, LEGENDARIO, Cifra.RACHA, 60),
    TRIPLE_CORONA("Triple corona", "Dominar los tres temas: todas sus preguntas resueltas", "crown", RETOS, LEGENDARIO, Cifra.TEMAS_DOMINADOS, 3),
    BUHO_NOCTURNO("Búho nocturno", "30 respuestas entre las 10 p. m. y las 4 a. m.", "dark_mode", RETOS, EPICO, Cifra.NOCTURNAS, 30,
            "Hay quienes estudian cuando todos duermen…"),
    MADRUGADOR("Madrugador", "30 respuestas entre las 5 y las 7 a. m.", "wb_twilight", RETOS, EPICO, Cifra.MADRUGADAS, 30,
            "Al que madruga, Sumi lo ayuda…"),
    // Meta 0 = todos los demás logros que se pueden ganar. Va casi de último para contar los de la misma evaluación.
    COLECCIONISTA("Leyenda de SAESU", "Conseguir todos los demás logros", "hotel_class", RETOS, LEGENDARIO, Cifra.LOGROS, 0),

    // ---------------- Exclusivos (no se ganan jugando) ----------------
    DESARROLLADOR("Desarrollador de SAESU", "Ayudó a construir SAESU. ¡Gracias por crear esta plataforma!", "code_blocks",
            RETOS, EXCLUSIVO, Cifra.DESARROLLADOR, 1);


    private final String nombre;
    private final String descripcion;
    private final String icono;
    private final CategoriaLogro categoria;
    private final Rareza rareza;
    private final Cifra cifra;
    private final long meta;
    /** Pista que se muestra en lugar del nombre y la descripción mientras no se obtiene; null si no es secreto. */
    private final String pistaSecreta;

    Logro(String nombre, String descripcion, String icono, CategoriaLogro categoria, Rareza rareza, Cifra cifra, long meta) {
        this(nombre, descripcion, icono, categoria, rareza, cifra, meta, null);
    }

    Logro(String nombre, String descripcion, String icono, CategoriaLogro categoria, Rareza rareza, Cifra cifra,
          long meta, String pistaSecreta) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = icono;
        this.categoria = categoria;
        this.rareza = rareza;
        this.cifra = cifra;
        this.meta = meta;
        this.pistaSecreta = pistaSecreta;
    }

    public boolean esSecreto() {
        return pistaSecreta != null;
    }

    /** Lo asigna el sistema; solo lo ve quien lo tiene y no cuenta para el coleccionista. */
    public boolean esExclusivo() {
        return rareza == Rareza.EXCLUSIVO;
    }

    /** Porcentaje mínimo de acierto que exige el logro, además de su meta (0 = ninguno). */
    public int precisionMinima() {
        return switch (this) {
            case PRECISION_80 -> 80;
            case FRANCOTIRADOR -> 95;
            default -> 0;
        };
    }
}
