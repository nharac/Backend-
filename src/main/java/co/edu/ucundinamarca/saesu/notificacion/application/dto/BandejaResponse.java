package co.edu.ucundinamarca.saesu.notificacion.application.dto;

import java.util.List;

/** Las últimas notificaciones y cuántas faltan por leer. */
public record BandejaResponse(long noLeidas, List<NotificacionResponse> notificaciones) {}
