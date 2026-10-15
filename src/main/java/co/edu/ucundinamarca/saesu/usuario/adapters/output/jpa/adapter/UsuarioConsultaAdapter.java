package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.adapter;

import co.edu.ucundinamarca.saesu.practica.ports.output.UsuarioConsultaPort;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador que implementa UsuarioConsultaPort.
 * Internamente usa el UsuarioRepositoryPort del módulo usuario.
 */
@Component
@RequiredArgsConstructor
public class UsuarioConsultaAdapter implements UsuarioConsultaPort {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    @Override
    public Long obtenerIdPorCorreo(String correo) {
        return usuarioRepositoryPort.buscarPorCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + correo))
                .getId();
    }
}