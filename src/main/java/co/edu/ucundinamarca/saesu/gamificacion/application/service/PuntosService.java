package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jpa.entity.PuntoHistorialEntity;
import co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jpa.repository.PuntoHistorialJpaRepository;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PuntosService {

    private final PuntoHistorialJpaRepository puntosRepository;
    private final UsuarioJpaRepository usuarioRepository;

    public PuntosService(PuntoHistorialJpaRepository puntosRepository, UsuarioJpaRepository usuarioRepository) {
        this.puntosRepository = puntosRepository;
        this.usuarioRepository = usuarioRepository;
    }

        @Transactional
    public void otorgarPuntos(Long usuarioId, int cantidad, String motivo) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Guardar el historial de los puntos base (ej. 10 por responder)
        guardarHistorial(usuario, cantidad, motivo);

        // 2. Lógica de Racha y Puntos Extra
        LocalDate hoy = LocalDate.now();
        if (usuario.getUltimaFechaPractica() == null || !usuario.getUltimaFechaPractica().equals(hoy)) {
            
            if (usuario.getUltimaFechaPractica() != null && usuario.getUltimaFechaPractica().equals(hoy.minusDays(1))) {
                usuario.setRachaActual(usuario.getRachaActual() + 1);
                // BONUS: Por mantener la racha, le damos 5 puntos extra
                guardarHistorial(usuario, 5, "Bonus por mantener la racha diaria");
            } else {
                usuario.setRachaActual(1);
            }
            usuario.setUltimaFechaPractica(hoy);
            usuarioRepository.save(usuario);
        }
    }

    private void guardarHistorial(UsuarioEntity usuario, int cantidad, String motivo) {
        PuntoHistorialEntity historial = new PuntoHistorialEntity();
        historial.setUsuario(usuario);
        historial.setCantidad(cantidad);
        historial.setMotivo(motivo);
        historial.setFecha(LocalDateTime.now());
        puntosRepository.save(historial);
    
    }
}