package co.edu.ucundinamarca.saesu.docente.ports.output;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteResumenResponse;

import java.time.LocalDateTime;
import java.util.List;

/** Genera el reporte descargable del progreso de los estudiantes (hoy: Excel .xlsx). */
public interface ReporteEstudiantesPort {

    byte[] generar(List<EstudianteResumenResponse> estudiantes, EstadisticasGrupoResponse grupo, LocalDateTime generadoEn);
}
