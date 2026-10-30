package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RachaSchedulerServiceTest {

    private static UsuarioEntity usuario(Integer racha, LocalDate ultima) {
        UsuarioEntity u = new UsuarioEntity();
        u.setRachaActual(racha);
        u.setUltimaFechaPractica(ultima);
        return u;
    }

    @Test
    @DisplayName("Rompe la racha solo de quien no practicó ni ayer ni hoy")
    void rompeRachasVencidas() {
        LocalDate hoy = LocalDate.now();
        UsuarioEntity vencida = usuario(5, hoy.minusDays(3));
        UsuarioEntity ayer = usuario(2, hoy.minusDays(1));
        UsuarioEntity hoyMismo = usuario(1, hoy);
        UsuarioEntity sinRacha = usuario(0, hoy.minusDays(10));
        UsuarioEntity nunca = usuario(null, null);
        UsuarioJpaRepository repo = mock(UsuarioJpaRepository.class);
        when(repo.findAll()).thenReturn(List.of(vencida, ayer, hoyMismo, sinRacha, nunca));

        new RachaSchedulerService(repo).revisarRachasPerdidas();

        assertThat(vencida.getRachaActual()).isZero();
        assertThat(ayer.getRachaActual()).isEqualTo(2);
        assertThat(hoyMismo.getRachaActual()).isEqualTo(1);
        verify(repo).save(vencida);
        verify(repo, never()).save(ayer);
        verify(repo, never()).save(sinRacha);
    }
}
