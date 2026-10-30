package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteResumenResponse;
import co.edu.ucundinamarca.saesu.docente.ports.output.ReporteEstudiantesPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportarEstudiantesServiceTest {

    @Mock private EstadisticasDocenteService estadisticas;
    @Mock private ReporteEstudiantesPort reporte;

    @Test
    @DisplayName("El Excel junta la lista de estudiantes y el resumen del grupo con la fecha de Colombia")
    void excel() {
        List<EstudianteResumenResponse> lista = List.of(mock(EstudianteResumenResponse.class), mock(EstudianteResumenResponse.class));
        EstadisticasGrupoResponse grupo = mock(EstadisticasGrupoResponse.class);
        when(estadisticas.listarEstudiantes()).thenReturn(lista);
        when(estadisticas.estadisticasGrupo()).thenReturn(grupo);
        when(reporte.generar(eq(lista), eq(grupo), any())).thenReturn(new byte[2048]);

        byte[] archivo = new ExportarEstudiantesService(estadisticas, reporte).excel();

        assertThat(archivo).hasSize(2048);
        verify(reporte).generar(eq(lista), eq(grupo), any());
    }

    @Test
    @DisplayName("El CSV sale del servicio de estadísticas")
    void csv() {
        when(estadisticas.exportarCsv()).thenReturn("nombre;correo\n");
        assertThat(new ExportarEstudiantesService(estadisticas, reporte).csv()).isEqualTo("nombre;correo\n");
    }
}
