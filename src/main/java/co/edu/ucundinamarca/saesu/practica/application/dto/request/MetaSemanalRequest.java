package co.edu.ucundinamarca.saesu.practica.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/** Nueva meta semanal: entre 5 y 300 preguntas (igual que el CHECK de V22). */
public record MetaSemanalRequest(
        @Min(value = 5, message = "La meta debe ser de al menos 5 preguntas")
        @Max(value = 300, message = "La meta no puede pasar de 300 preguntas")
        int meta) { }
