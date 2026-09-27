package co.edu.ucundinamarca.saesu.usuario.ports.input;

import co.edu.ucundinamarca.saesu.usuario.application.dto.request.LoginRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.AuthResponse;

public interface LoginUseCase {
    AuthResponse login(LoginRequest request);
}