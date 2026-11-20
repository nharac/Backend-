package co.edu.ucundinamarca.saesu.notificacion.application.dto;

import java.time.LocalDateTime;

public record NotificacionResponse(Long id, String tipo, String titulo, String mensaje, String enlace, boolean leida,
                                   LocalDateTime creadaEn) {}
