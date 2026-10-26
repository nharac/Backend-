package co.edu.ucundinamarca.saesu.core.exception;

import co.edu.ucundinamarca.saesu.practica.domain.exception.IaNoDisponibleException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.LimiteChatExcedidoException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.OpcionInvalidaException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.PreguntaNoEncontradaException;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.ErrorResponse;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.ContrasenaIncorrectaException;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.CorreoYaRegistradoException;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.CredencialesInvalidasException;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ============================================
    // Excepciones del módulo usuario
    // ============================================

    @ExceptionHandler(CorreoYaRegistradoException.class)
    public ResponseEntity<ErrorResponse> handleCorreoDuplicado(CorreoYaRegistradoException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleCredenciales(CredencialesInvalidasException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ContrasenaIncorrectaException.class)
    public ResponseEntity<ErrorResponse> handleContrasenaIncorrecta(ContrasenaIncorrectaException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrado(UsuarioNoEncontradoException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    // ============================================
    // Excepciones del módulo práctica
    // ============================================

    @ExceptionHandler(PreguntaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handlePreguntaNoEncontrada(PreguntaNoEncontradaException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(OpcionInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleOpcionInvalida(OpcionInvalidaException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    // ============================================
    // Excepciones del módulo chat IA
    // ============================================

    @ExceptionHandler(LimiteChatExcedidoException.class)
    public ResponseEntity<ErrorResponse> handleLimiteChat(LimiteChatExcedidoException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(IaNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleIaNoDisponible(IaNoDisponibleException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), req.getRequestURI());
    }

    // ============================================
    // Validación de DTOs (Bean Validation)
    // ============================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, mensaje, req.getRequestURI());
    }

    /** Datos inválidos (ej. imagen que no es JPG/PNG o URL de imagen ajena). */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleArgumentoInvalido(IllegalArgumentException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    /** Archivo más grande que spring.servlet.multipart.max-file-size. */
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleArchivoGrande(Exception ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, "El archivo es demasiado grande", req.getRequestURI());
    }

    // ============================================
    // Excepciones del módulo foro
    // ============================================

    @ExceptionHandler({
            co.edu.ucundinamarca.saesu.foro.domain.exception.PublicacionNoEncontradaException.class,
            co.edu.ucundinamarca.saesu.foro.domain.exception.RespuestaNoEncontradaException.class
    })
    public ResponseEntity<ErrorResponse> handleForoNoEncontrado(RuntimeException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    /** Rol sin permiso (ej. un estudiante llamando a /api/docente/**): 403 en vez de 500. */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccesoDenegado(Exception ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción", req.getRequestURI());
    }

    // ============================================
    // Fallback
    // ============================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest req) {
        // El detalle queda en los logs; al cliente no se le muestran mensajes internos
        org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class)
                .error("Error no controlado en {}", req.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Intenta de nuevo.", req.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String mensaje, String path) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                path
        );
        return ResponseEntity.status(status).body(error);
    }
}