package co.edu.ucundinamarca.saesu.usuario.ports.output;

import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<Usuario> buscarPorCorreo(String correo);

    /**
     * Busca un usuario por su ID.
     * Usado por el módulo foro para construir el AutorResponse.
     */
    Optional<Usuario> buscarPorId(Long id);

    /**
     * Varios usuarios de una vez (id → usuario); los que no existan no aparecen.
     * Usado por el foro para no buscar el autor de cada respuesta por separado.
     */
    default Map<Long, Usuario> buscarPorIds(Collection<Long> ids) {
        Map<Long, Usuario> mapa = new HashMap<>();
        for (Long id : ids) {
            buscarPorId(id).ifPresent(u -> mapa.put(id, u));
        }
        return mapa;
    }

    Usuario guardar(Usuario usuario);

    boolean existePorCorreo(String correo);
}