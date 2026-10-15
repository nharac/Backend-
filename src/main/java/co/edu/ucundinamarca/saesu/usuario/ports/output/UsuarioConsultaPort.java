package co.edu.ucundinamarca.saesu.practica.ports.output;

/**
 * Puerto de salida del módulo práctica.
 * Solo expone las operaciones de usuario que práctica necesita.
 * Cumple ISP: interfaz pequeña y específica.
 */
public interface UsuarioConsultaPort {

    /**
     * Obtiene el ID del usuario dado su correo.
     *
     * @param correo correo del usuario.
     * @return ID del usuario.
     * @throws RuntimeException si el usuario no existe.
     */
    Long obtenerIdPorCorreo(String correo);
}