package co.edu.ucundinamarca.saesu.usuario.ports.input;

import co.edu.ucundinamarca.saesu.usuario.application.dto.request.ActualizarPerfilRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.UsuarioResponse;

/**
 * Puerto de entrada: actualizar el perfil (nombre y, opcionalmente, contraseña)
 * del usuario autenticado.
 */
public interface ActualizarPerfilUseCase {
    UsuarioResponse actualizarPerfil(String correo, ActualizarPerfilRequest request);
}
