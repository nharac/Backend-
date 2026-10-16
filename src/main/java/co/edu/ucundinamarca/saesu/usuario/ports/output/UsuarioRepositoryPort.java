package co.edu.ucundinamarca.saesu.usuario.ports.output;

import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<Usuario> buscarPorCorreo(String correo);

    /**
     * Busca un usuario por su ID.
     * Usado por el módulo foro para construir el AutorResponse.
     */
    Optional<Usuario> buscarPorId(Long id);

    Usuario guardar(Usuario usuario);

    boolean existePorCorreo(String correo);
}