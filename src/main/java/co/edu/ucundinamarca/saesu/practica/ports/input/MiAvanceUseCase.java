package co.edu.ucundinamarca.saesu.practica.ports.input;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.ActividadResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.AnalisisResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.MetaSemanalResponse;

/** Puerto de entrada de las estadísticas personales del perfil. El estudiante se identifica por su correo (JWT). */
public interface MiAvanceUseCase {

    /** Calendario de los últimos {@code dias} días (se limita entre 7 y 371). */
    ActividadResponse actividad(String correo, int dias);

    AnalisisResponse analisis(String correo);

    MetaSemanalResponse meta(String correo);

    MetaSemanalResponse cambiarMeta(String correo, int meta);
}
