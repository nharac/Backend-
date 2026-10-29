package co.edu.ucundinamarca.saesu.usuario.ports.input;

import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RegistroRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.RegistroResponse;

public interface RegistrarUsuarioUseCase {
    /** Crea la cuenta sin verificar y envía el código al correo (no inicia sesión). */
    RegistroResponse registrar(RegistroRequest request);
}
