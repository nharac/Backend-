package co.edu.ucundinamarca.saesu.usuario.ports.input;

import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RegistroRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.AuthResponse;

public interface RegistrarUsuarioUseCase {
    AuthResponse registrar(RegistroRequest request);
}