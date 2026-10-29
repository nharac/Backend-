package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.ports.output.ReporteEstudiantesPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Descarga del progreso de los estudiantes para el docente.
 * Junta los datos que ya calcula EstadisticasDocenteService y el adaptador arma el archivo.
 */
@Service
public class ExportarEstudiantesService {

    private static final Logger log = LoggerFactory.getLogger(ExportarEstudiantesService.class);
    private static final ZoneId COLOMBIA = ZoneId.of("America/Bogota");

    private final EstadisticasDocenteService estadisticas;
    private final ReporteEstudiantesPort reporte;
    private final Clock clock;

    public ExportarEstudiantesService(EstadisticasDocenteService estadisticas, ReporteEstudiantesPort reporte) {
        this.estadisticas = estadisticas;
        this.reporte = reporte;
        this.clock = Clock.system(COLOMBIA);
    }

    /** Excel con dos hojas: "Estudiantes" (una fila por estudiante) y "Resumen" (el grupo). */
    public byte[] excel() {
        var estudiantes = estadisticas.listarEstudiantes();
        byte[] archivo = reporte.generar(estudiantes, estadisticas.estadisticasGrupo(), LocalDateTime.now(clock));
        log.info("Reporte Excel generado: {} estudiantes, {} KB", estudiantes.size(), archivo.length / 1024);
        return archivo;
    }

    /** El mismo listado en CSV, por si alguien lo necesita en otro programa. */
    public String csv() {
        return estadisticas.exportarCsv();
    }
}
