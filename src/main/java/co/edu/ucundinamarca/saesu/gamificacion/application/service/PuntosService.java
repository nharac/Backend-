package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jpa.entity.PuntoHistorialEntity;
import co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jpa.repository.PuntoHistorialJpaRepository;
import co.edu.ucundinamarca.saesu.gamificacion.ports.input.OtorgarPuntosUseCase;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.MonedasPort;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Puntos Sumi (para el ranking) y Sumi Monedas (para el vestidor).
 * Cada vez que se ganan puntos también se ganan monedas:
 * práctica y otros = puntos / 5 (mínimo 1), foro = 3, respuesta oficial = 10,
 * racha = 2 por día y 15 extra cada 7 días seguidos.
 */
@Service
public class PuntosService implements OtorgarPuntosUseCase {

    private final PuntoHistorialJpaRepository puntosRepository;
    private final UsuarioJpaRepository usuarioRepository;
    private final MonedasPort monedas;

    public PuntosService(PuntoHistorialJpaRepository puntosRepository, UsuarioJpaRepository usuarioRepository,
                         MonedasPort monedas) {
        this.puntosRepository = puntosRepository;
        this.usuarioRepository = usuarioRepository;
        this.monedas = monedas;
    }

    public static final int MONEDAS_FORO = 3;
    public static final int PUNTOS_RESPUESTA_OFICIAL = 15;
    public static final int MONEDAS_RESPUESTA_OFICIAL = 10;
    public static final int MONEDAS_RACHA = 2;
    public static final int MONEDAS_SEMANA_RACHA = 15;

    /** Motivo con el que se registran los puntos del foro. */
    public static final String MOTIVO_FORO = "Respondió en el foro";
    /** Máximo de respuestas del foro que dan puntos por día (evita inflar el ranking con spam). */
    public static final int MAX_RESPUESTAS_FORO_CON_PUNTOS_POR_DIA = 3;

    @Override
    @Transactional
    public void otorgarPuntos(Long usuarioId, int cantidad, String motivo) {
        dar(usuarioId, cantidad, Math.max(1, cantidad / 5), motivo);
    }

    /** Puntos y monedas con el mismo motivo, y luego la racha del día. */
    private void dar(Long usuarioId, int puntos, int cantidadMonedas, String motivo) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Guardar el historial de los puntos base y las monedas
        guardarHistorial(usuario, puntos, motivo);
        monedas.sumar(usuarioId, cantidadMonedas, motivo);

        // 2. Lógica de Racha y Puntos Extra
        actualizarRacha(usuario);
    }

    /**
     * Puntos por responder en el foro: +10, máximo 3 veces al día,
     * y nada por responder en una publicación propia.
     * @return true si se dieron puntos
     */
    @Override
    @Transactional
    public boolean otorgarPuntosForo(Long usuarioId, Long autorPublicacionId) {
        if (usuarioId.equals(autorPublicacionId)) {
            return false;
        }
        long hoy = puntosRepository.contarPorMotivoDesde(usuarioId, MOTIVO_FORO, LocalDate.now().atStartOfDay());
        if (hoy >= MAX_RESPUESTAS_FORO_CON_PUNTOS_POR_DIA) {
            return false;
        }
        dar(usuarioId, 10, MONEDAS_FORO, MOTIVO_FORO);
        return true;
    }

    /** Premio para quien escribió una respuesta que un docente marcó como oficial (una sola vez por respuesta). */
    @Override
    @Transactional
    public void otorgarRespuestaOficial(Long autorId, Long respuestaId) {
        String motivo = "Respuesta oficial #" + respuestaId;
        if (!puntosRepository.existePorMotivo(autorId, motivo)) {
            dar(autorId, PUNTOS_RESPUESTA_OFICIAL, MONEDAS_RESPUESTA_OFICIAL, motivo);
        }
    }

    /**
     * Cuenta el día para la racha sin dar puntos
     * (ej. acertar una pregunta que ya estaba resuelta).
     */
    @Override
    @Transactional
    public void registrarActividad(Long usuarioId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        actualizarRacha(usuario);
    }

    private void actualizarRacha(UsuarioEntity usuario) {
        LocalDate hoy = LocalDate.now();
        if (usuario.getUltimaFechaPractica() == null || !usuario.getUltimaFechaPractica().equals(hoy)) {
            
            // Si su última práctica fue AYER, aumentamos la racha y le damos 5 puntos extra
            if (usuario.getUltimaFechaPractica() != null && usuario.getUltimaFechaPractica().equals(hoy.minusDays(1))) {
                usuario.setRachaActual(usuario.getRachaActual() + 1);
                guardarHistorial(usuario, 5, "Bonus por mantener la racha diaria");
                monedas.sumar(usuario.getId(), MONEDAS_RACHA, "Racha diaria");
                if (usuario.getRachaActual() % 7 == 0) {
                    monedas.sumar(usuario.getId(), MONEDAS_SEMANA_RACHA, "¡Racha de " + usuario.getRachaActual() + " días!");
                }
            } 
            // Si no fue ayer (o es su primera vez), la racha vuelve a 1
            else {
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