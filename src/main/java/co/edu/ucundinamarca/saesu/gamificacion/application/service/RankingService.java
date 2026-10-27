package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.MiPosicionResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.RankingResponse;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.RankingPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

/**
 * Ranking de estudiantes por periodo: total (desde siempre), semana (desde el lunes) o mes (desde el día 1).
 * Las semanas y meses empiezan a la medianoche de Colombia; como puntos_historial guarda la hora del
 * servidor, el inicio se convierte a la zona del servidor antes de consultar.
 */
@Service
public class RankingService {

    public static final int TAMANO_TOP = 10;
    private static final int MOVIMIENTOS = 6;
    private static final ZoneId COLOMBIA = ZoneId.of("America/Bogota");

    public enum Periodo {
        TOTAL, SEMANA, MES;

        public static Periodo de(String valor) {
            if (valor == null || valor.isBlank()) {
                return TOTAL;
            }
            try {
                return valueOf(valor.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Periodo no válido: usa total, semana o mes");
            }
        }
    }

    private final RankingPort port;
    private final Clock clock;

    @Autowired
    public RankingService(RankingPort port) {
        this(port, Clock.systemDefaultZone());
    }

    /** Constructor para pruebas (reloj fijo). */
    RankingService(RankingPort port, Clock clock) {
        this.port = port;
        this.clock = clock;
    }

    public List<RankingResponse> top(Periodo periodo) {
        return port.top(desde(periodo), TAMANO_TOP).stream()
                .map(f -> new RankingResponse(f.usuarioId(), f.nombre(), f.fotoPerfilUrl(), f.puntos(), f.racha()))
                .toList();
    }

    public MiPosicionResponse miPosicion(Long usuarioId, Periodo periodo) {
        LocalDateTime desde = desde(periodo);
        var posicion = port.posicionDe(usuarioId, desde);

        Integer lugar = posicion.map(RankingPort.PosicionUsuario::posicion).orElse(null);
        long puntos = posicion.map(RankingPort.PosicionUsuario::puntos).orElse(0L);
        long participantes = posicion.map(RankingPort.PosicionUsuario::participantes)
                .orElseGet(() -> port.participantes(desde));

        // Para pasar al de arriba hay que superarlo: con empate gana quien llegó primero
        Long paraSubir = posicion
                .filter(p -> p.puntosDelDeArriba() != null)
                .map(p -> p.puntosDelDeArriba() - p.puntos() + 1)
                .orElse(null);
        String siguiente = posicion.map(RankingPort.PosicionUsuario::nombreDelDeArriba).orElse(null);

        List<MiPosicionResponse.Movimiento> movimientos = port.ultimosMovimientos(usuarioId, MOVIMIENTOS).stream()
                .map(m -> new MiPosicionResponse.Movimiento(m.cantidad(), m.motivo(), m.fecha()))
                .toList();

        return new MiPosicionResponse(
                periodo.name().toLowerCase(Locale.ROOT),
                lugar,
                puntos,
                participantes,
                paraSubir,
                siguiente,
                port.rachaVigente(usuarioId),
                port.puntosDesde(usuarioId, inicioDe(LocalDate.now(clock.withZone(COLOMBIA)))),
                port.puntosDesde(usuarioId, desde(Periodo.SEMANA)),
                port.puntosDesde(usuarioId, null),
                movimientos);
    }

    /** Inicio del periodo en la hora del servidor (null = desde siempre). */
    LocalDateTime desde(Periodo periodo) {
        LocalDate hoy = LocalDate.now(clock.withZone(COLOMBIA));
        return switch (periodo) {
            case TOTAL -> null;
            case SEMANA -> inicioDe(hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
            case MES -> inicioDe(hoy.withDayOfMonth(1));
        };
    }

    private LocalDateTime inicioDe(LocalDate dia) {
        return dia.atStartOfDay(COLOMBIA).withZoneSameInstant(clock.getZone()).toLocalDateTime();
    }
}
