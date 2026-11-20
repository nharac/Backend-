package co.edu.ucundinamarca.saesu.duelo.application.dto.response;

import java.time.LocalDate;
import java.util.List;

/**
 * Torneo de la semana (lunes a domingo): 3 puntos por reto ganado y 1 por empate.
 *
 * @param campeonAnterior quien ganó la semana pasada (null si nadie jugó)
 */
public record TorneoResponse(LocalDate inicio, LocalDate fin, long diasRestantes, List<Fila> tabla, Fila campeonAnterior) {

    public record Fila(int posicion, Long usuarioId, String nombre, int jugados, int ganados, int empates, int perdidos,
                       int puntos, boolean soyYo) {}
}
