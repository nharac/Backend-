package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RegistroRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.RegistroResponse;
import co.edu.ucundinamarca.saesu.usuario.ports.input.RegistrarUsuarioUseCase;
import org.springframework.stereotype.Service;

/**
 * Registro de estudiantes con doble verificación: todavía no crea la cuenta.
 * Guarda el registro como pendiente y envía un código al correo institucional;
 * la cuenta se crea al verificar el código (VerificarCorreoService.verificar).
 */
@Service
public class RegistrarUsuarioService implements RegistrarUsuarioUseCase {

    private final VerificarCorreoService verificarCorreoService;

    public RegistrarUsuarioService(VerificarCorreoService verificarCorreoService) {
        this.verificarCorreoService = verificarCorreoService;
    }

    @Override
    public RegistroResponse registrar(RegistroRequest request) {
        verificarCorreoService.solicitar(request);
        return new RegistroResponse(request.getCorreo().trim(),
                "Te enviamos un código de 6 dígitos a tu correo institucional. Tu cuenta se crea cuando lo escribas.",
                VerificarCorreoService.MINUTOS_VIGENCIA);
    }
}
