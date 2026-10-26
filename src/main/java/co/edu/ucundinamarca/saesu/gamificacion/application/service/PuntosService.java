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
        actualizarRacha(usuario);
    }

    /** Motivo con el que se registran los puntos del foro. */
    public static final String MOTIVO_FORO = "Respondió en el foro";
    /** Máximo de respuestas del foro que dan puntos por día (evita inflar el ranking con spam). */
    public static final int MAX_RESPUESTAS_FORO_CON_PUNTOS_POR_DIA = 3;

    /**
     * Puntos por responder en el foro: +10, máximo 3 veces al día,
     * y nada por responder en una publicación propia.
     * @return true si se dieron puntos
     */
    @Transactional
    public boolean otorgarPuntosForo(Long usuarioId, Long autorPublicacionId) {
        if (usuarioId.equals(autorPublicacionId)) {
            return false;
        }
        long hoy = puntosRepository.contarPorMotivoDesde(usuarioId, MOTIVO_FORO, LocalDate.now().atStartOfDay());
        if (hoy >= MAX_RESPUESTAS_FORO_CON_PUNTOS_POR_DIA) {
            return false;
        }
        otorgarPuntos(usuarioId, 10, MOTIVO_FORO);
        return true;
    }

    /** Da los puntos solo si nunca se dieron con ese mismo motivo (ej. "Respuesta oficial #12"). */
    @Transactional
    public void otorgarPuntosUnaVez(Long usuarioId, int cantidad, String motivo) {
        if (!puntosRepository.existePorMotivo(usuarioId, motivo)) {
            otorgarPuntos(usuarioId, cantidad, motivo);
        }
    }

    /**
     * Cuenta el día para la racha sin dar puntos
     * (ej. acertar una pregunta que ya estaba resuelta).
     */
    @Transactional
    public void registrarActividad(Long usuarioId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        actualizarRacha(usuario);
    }

    private void actualizarRacha(UsuarioEntity usuario) {
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