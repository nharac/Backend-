package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.repository.UsuarioJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class RachaSchedulerService {

    private final UsuarioJpaRepository usuarioRepository;

    public RachaSchedulerService(UsuarioJpaRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Se ejecuta todos los días a las 11:59 PM
    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void revisarRachasPerdidas() {
        log.info("🔥 [CRON] Revisando rachas perdidas...");
        
        LocalDate ayer = LocalDate.now().minusDays(1);
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();

        int rachasRotas = 0;

        for (UsuarioEntity usuario : usuarios) {
            // Si tenía racha, pero su última práctica fue ANTES de ayer, pierde la racha.
            if (usuario.getRachaActual() != null && usuario.getRachaActual() > 0 && 
                usuario.getUltimaFechaPractica() != null && 
                usuario.getUltimaFechaPractica().isBefore(ayer)) {
                
                usuario.setRachaActual(0); // Se le rompe la racha
                usuarioRepository.save(usuario);
                rachasRotas++;
            }
        }

        log.info("🔥 [CRON] Rachas rotas procesadas: {}", rachasRotas);
    }
}