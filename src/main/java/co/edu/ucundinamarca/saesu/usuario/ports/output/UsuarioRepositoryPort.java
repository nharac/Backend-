package co.edu.ucundinamarca.saesu.usuario.ports.output;

import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> buscarPorCorreo(String correo);
    Usuario guardar(Usuario usuario);
    boolean existePorCorreo(String correo);
}