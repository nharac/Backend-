package co.edu.ucundinamarca.saesu.usuario.ports.input;

import co.edu.ucundinamarca.saesu.usuario.application.dto.response.UsuarioResponse;

public interface ObtenerPerfilUseCase {
    UsuarioResponse obtenerPerfil(String correo);
}