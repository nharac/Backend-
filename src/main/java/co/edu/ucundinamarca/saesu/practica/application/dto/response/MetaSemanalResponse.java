package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import java.time.LocalDate;

/**
 * Meta semanal (GET/PUT /api/practica/meta). La semana va de lunes a domingo, hora de Colombia.
 * @param diasRestantes días que quedan incluyendo hoy (domingo = 1)
 */
public record MetaSemanalResponse(int meta, long respondidas, long aciertos, LocalDate inicioSemana,
                                  LocalDate finSemana, int diasRestantes, boolean cumplida) { }
