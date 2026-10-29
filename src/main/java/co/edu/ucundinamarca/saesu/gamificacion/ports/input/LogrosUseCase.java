package co.edu.ucundinamarca.saesu.gamificacion.ports.input;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.LogroResponse;

import java.util.List;

/** Puerto de entrada de los logros: lo usa LogrosController sin conocer la implementación. */
public interface LogrosUseCase {

    /** Los logros del estudiante autenticado (identificado por su correo del JWT) con su estado. */
    List<LogroResponse> listar(String correo);
}
