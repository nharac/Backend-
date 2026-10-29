package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.RankingPort;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Los puertos de entrada de gamificación resuelven al usuario por el correo del JWT. */
@ExtendWith(MockitoExtension.class)
class GamificacionPuertosTest {

    @Mock private RankingPort rankingPort;
    @Mock private LogrosPort logrosPort;
    @Mock private UsuarioRepositoryPort usuarios;

    @Test
    @DisplayName("Ranking: correo que no existe -> UsuarioNoEncontradoException (404)")
    void rankingSinUsuario() {
        RankingService service = new RankingService(rankingPort, usuarios, Clock.systemUTC());
        when(usuarios.buscarPorCorreo("nadie@ucundinamarca.edu.co")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.miPosicion("nadie@ucundinamarca.edu.co", "semana"))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    @DisplayName("Logros: busca el id por correo y lista los 11 logros")
    void logrosPorCorreo() {
        Usuario laura = mock(Usuario.class);
        when(laura.getId()).thenReturn(7L);
        when(usuarios.buscarPorCorreo("laura@ucundinamarca.edu.co")).thenReturn(Optional.of(laura));
        when(logrosPort.obtenidos(7L)).thenReturn(new HashMap<>());
        when(logrosPort.estadisticas(7L)).thenReturn(
                new LogrosPort.Estadisticas(0, 0, 0, 0, 0, 0, 0, new HashMap<>()));

        LogrosService service = new LogrosService(logrosPort, usuarios);

        assertThat(service.listar("laura@ucundinamarca.edu.co")).hasSize(11);
        verify(logrosPort).estadisticas(7L);
    }

    @Test
    @DisplayName("Logros: correo que no existe -> UsuarioNoEncontradoException")
    void logrosSinUsuario() {
        when(usuarios.buscarPorCorreo(any())).thenReturn(Optional.empty());
        LogrosService service = new LogrosService(logrosPort, usuarios);

        assertThatThrownBy(() -> service.listar("x@y.co")).isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
