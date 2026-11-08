package co.edu.ucundinamarca.saesu.gamificacion.domain.model;

/**
 * Cada número del estudiante que puede medir el avance de un logro.
 * Las cifras "de base de datos" las calcula LogrosPort; las derivadas
 * (temas, logros) las arma LogrosService a partir de ellas.
 */
public enum Cifra {
    /** Respuestas en práctica (correctas o no). */
    RESPONDIDAS,
    /** Respuestas correctas en práctica. */
    ACIERTOS,
    /** Preguntas acertadas en el primer intento. */
    PRIMERA,
    /** Preguntas avanzadas (activas) distintas resueltas. */
    AVANZADAS,
    /** Preguntas avanzadas activas que hay en el banco. */
    TOTAL_AVANZADAS,
    /** Más aciertos en un mismo día (hora de Colombia). */
    MAX_ACIERTOS_DIA,
    /** Más respuestas correctas seguidas, sin fallar. */
    MAX_SEGUIDAS,
    /** Respuestas entre las 10 p. m. y las 4 a. m. */
    NOCTURNAS,
    /** Respuestas entre las 5 y las 7 a. m. */
    MADRUGADAS,
    /** Días seguidos practicando. */
    RACHA,
    /** Quices terminados. */
    QUICES,
    /** Quices de 10 o más preguntas con todo correcto. */
    QUICES_PERFECTOS,
    PUBLICACIONES,
    RESPUESTAS_FORO,
    /** Respuestas del foro marcadas como oficiales por un docente. */
    OFICIALES,
    /** Mensajes enviados al tutor (Sumi). */
    MENSAJES_TUTOR,
    PUNTOS,

    // ---- Derivadas (las calcula LogrosService) ----
    RESUELTAS_LIMITES,
    RESUELTAS_DERIVADAS,
    RESUELTAS_INTEGRALES,
    /** Temas con al menos una pregunta resuelta. */
    TEMAS_EXPLORADOS,
    /** Temas con todas sus preguntas resueltas. */
    TEMAS_DOMINADOS,
    /** Preguntas distintas resueltas de todo el banco. */
    PREGUNTAS_RESUELTAS,
    /** 1 si el correo está en la lista de desarrolladores (saesu.desarrolladores). */
    DESARROLLADOR,
    /** Logros ya obtenidos (para el coleccionista). */
    LOGROS
}
