package co.edu.ucundinamarca.saesu.duelo.application.service;

import co.edu.ucundinamarca.saesu.duelo.application.dto.response.TorneoResponse;
import co.edu.ucundinamarca.saesu.duelo.application.dto.response.TorneoResponse.Fila;
import co.edu.ucundinamarca.saesu.duelo.ports.output.DueloPort;
import co.edu.ucundinamarca.saesu.duelo.ports.output.DueloPort.FilaDuelo;
import co.edu.ucundinamarca.saesu.notificacion.ports.input.NotificarUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Torneo semanal de retos. No guarda nada: la tabla se calcula con los retos terminados de la semana.
 * Gana 3 puntos quien gana un reto y 1 cada uno si empatan. Desempate: más ganados y luego menos jugados.
 */
@Service
public class TorneoService {

    static final int PUNTOS_GANAR = 3;
    static final int PUNTOS_EMPATE = 1;

    private final DueloPort port;
    private final Clock clock;
    private NotificarUseCase avisos;

    public TorneoService(DueloPort port, Clock clock) {
        this.port = port;
        this.clock = clock;
    }

    @Autowired(required = false)
    public void setAvisos(NotificarUseCase avisos) {
        this.avisos = avisos;
    }

    public TorneoResponse semanaActual(String correo) {
        Long yo = port.idPorCorreo(correo).orElse(null);
        LocalDate hoy = LocalDate.now(clock);
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<Fila> tabla = tabla(lunes, yo);
        List<Fila> pasada = tabla(lunes.minusWeeks(1), yo);
        return new TorneoResponse(lunes, lunes.plusDays(6), ChronoUnit.DAYS.between(hoy, lunes.plusDays(6)), tabla,
                pasada.isEmpty() ? null : pasada.get(0));
    }

    /** Tabla de la semana que empieza en {@code lunes}. */
    List<Fila> tabla(LocalDate lunes, Long yo) {
        Map<Long, int[]> stats = new LinkedHashMap<>(); // jugados, ganados, empates, perdidos
        Map<Long, String> nombres = new LinkedHashMap<>();
        for (FilaDuelo d : port.terminadosEntre(lunes.atStartOfDay(), lunes.plusWeeks(1).atStartOfDay())) {
            nombres.put(d.retadorId(), d.retador());
            nombres.put(d.rivalId(), d.rival());
            int r = comparar(d.correctasRetador(), d.segundosRetador(), d.correctasRival(), d.segundosRival());
            sumar(stats, d.retadorId(), r);
            sumar(stats, d.rivalId(), -r);
        }
        List<Fila> filas = new ArrayList<>();
        stats.forEach((id, s) -> filas.add(new Fila(0, id, nombres.get(id), s[0], s[1], s[2], s[3],
                s[1] * PUNTOS_GANAR + s[2] * PUNTOS_EMPATE, id.equals(yo))));
        filas.sort(Comparator.comparingInt(Fila::puntos).reversed()
                .thenComparing(Comparator.comparingInt(Fila::ganados).reversed())
                .thenComparingInt(Fila::jugados)
                .thenComparing(f -> f.nombre() == null ? "" : f.nombre().toLowerCase()));
        List<Fila> conPosicion = new ArrayList<>();
        for (int i = 0; i < filas.size(); i++) {
            Fila f = filas.get(i);
            conPosicion.add(new Fila(i + 1, f.usuarioId(), f.nombre(), f.jugados(), f.ganados(), f.empates(), f.perdidos(), f.puntos(), f.soyYo()));
        }
        return conPosicion;
    }

    /** 1 si gana el primero, -1 si gana el segundo, 0 si empatan (más puntaje y luego menos tiempo). */
    static int comparar(int puntaje1, int seg1, int puntaje2, int seg2) {
        if (puntaje1 != puntaje2) return puntaje1 > puntaje2 ? 1 : -1;
        if (seg1 != seg2) return seg1 < seg2 ? 1 : -1;
        return 0;
    }

    private static void sumar(Map<Long, int[]> stats, Long id, int resultado) {
        int[] s = stats.computeIfAbsent(id, k -> new int[4]);
        s[0]++;
        if (resultado > 0) s[1]++;
        else if (resultado == 0) s[2]++;
        else s[3]++;
    }

    /** Cada lunes a las 6:00 avisa al podio de la semana que terminó. */
    @Scheduled(cron = "0 0 6 * * MON", zone = "America/Bogota")
    public void premiarSemanaAnterior() {
        if (avisos == null) return;
        LocalDate lunesPasado = LocalDate.now(clock).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
        List<Fila> tabla = tabla(lunesPasado, null);
        String[] medallas = {"🥇 ¡Ganaste el torneo de la semana!", "🥈 Quedaste de 2.º en el torneo", "🥉 Quedaste de 3.º en el torneo"};
        for (int i = 0; i < Math.min(3, tabla.size()); i++) {
            Fila f = tabla.get(i);
            avisos.notificar(f.usuarioId(), "TORNEO", medallas[i], f.puntos() + " puntos con " + f.ganados() + " retos ganados.", "/retos");
        }
    }
}
