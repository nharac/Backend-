package co.edu.ucundinamarca.saesu.usuario.application.dto.response;

/**
 * Respuesta de POST /api/auth/registro: la cuenta queda creada pero sin verificar.
 * No trae token: se obtiene al verificar el correo con el código que llegó.
 */
public record RegistroResponse(String correo, String mensaje, int minutosVigencia) {}
