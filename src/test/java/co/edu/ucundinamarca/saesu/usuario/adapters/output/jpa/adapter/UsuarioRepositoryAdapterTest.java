package co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.adapter;

import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.mapper.UsuarioMapper;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.repository.UsuarioJpaRepository;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regresión: antes, guardar un usuario (cambiar nombre, contraseña o foto)
 * reiniciaba su racha a 0 porque la racha no está en el modelo de dominio.
 */
class UsuarioRepositoryAdapterTest {

    @Test
    @DisplayName("Actualizar un usuario conserva su racha y su última fecha de práctica")
    void conservaLaRacha() {
        UsuarioJpaRepository jpa = mock(UsuarioJpaRepository.class);
        UsuarioRepositoryAdapter adapter = new UsuarioRepositoryAdapter(jpa, new UsuarioMapper());

        UsuarioEntity enBaseDeDatos = new UsuarioEntity();
        enBaseDeDatos.setId(5L);
        enBaseDeDatos.setRachaActual(4);
        enBaseDeDatos.setUltimaFechaPractica(LocalDate.now().minusDays(1));
        when(jpa.findById(5L)).thenReturn(Optional.of(enBaseDeDatos));
        when(jpa.save(any(UsuarioEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario cambiado = new Usuario(5L, "Nuevo nombre", "j@ucundinamarca.edu.co", "hash", "ESTUDIANTE",
                LocalDateTime.now(), null);
        adapter.guardar(cambiado);

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(jpa).save(captor.capture());
        assertThat(captor.getValue().getNombre()).isEqualTo("Nuevo nombre");
        assertThat(captor.getValue().getRachaActual()).isEqualTo(4);
        assertThat(captor.getValue().getUltimaFechaPractica()).isEqualTo(LocalDate.now().minusDays(1));
    }

    @Test
    @DisplayName("Un usuario nuevo (sin id) empieza con racha 0")
    void usuarioNuevo() {
        UsuarioJpaRepository jpa = mock(UsuarioJpaRepository.class);
        UsuarioRepositoryAdapter adapter = new UsuarioRepositoryAdapter(jpa, new UsuarioMapper());
        when(jpa.save(any(UsuarioEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        adapter.guardar(new Usuario(null, "Ana", "a@ucundinamarca.edu.co", "hash", "ESTUDIANTE", LocalDateTime.now(), null));

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(jpa).save(captor.capture());
        assertThat(captor.getValue().getRachaActual()).isZero();
    }
}
