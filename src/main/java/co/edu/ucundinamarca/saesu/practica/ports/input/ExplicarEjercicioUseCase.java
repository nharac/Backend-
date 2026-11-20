package co.edu.ucundinamarca.saesu.practica.ports.input;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.ExplicacionResponse;

/** Caso de uso: la calculadora paso a paso del tutor IA. */
public interface ExplicarEjercicioUseCase {

    ExplicacionResponse explicar(String ejercicio, String correoEstudiante);
}
