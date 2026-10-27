package co.edu.ucundinamarca.saesu.usuario.application.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String correo;
    private String rol;
    private LocalDateTime fechaRegistro;
    /** URL de la foto en S3 (null si no ha subido). */
    private String fotoPerfilUrl;
    /** Gamificación: se llenan en GET /api/auth/perfil. */
    private Integer rachaActual;
    private Long puntosTotales;

    public UsuarioResponse(Long id, String nombre, String correo, String rol, LocalDateTime fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
    }
}