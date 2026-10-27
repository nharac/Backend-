package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.TemaEstadistica;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteResumenResponse;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort.FilaEstudiante;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort.FilaTemaEstudiante;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

/** Estudiantes en riesgo, progreso por tema y exportación CSV del panel docente. */
@ExtendWith(MockitoExtension.class)
class EstadisticasDocenteServiceTest {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 26, 10, 0);

    @Mock private EstadisticasDocentePort port;

    private EstadisticasDocenteService servicio;

    @BeforeEach
    void preparar() {
        Clock reloj = Clock.fixed(AHORA.atZone(BOGOTA).toInstant(), BOGOTA);
        servicio = new EstadisticasDocenteService(port, reloj);
    }

    private static FilaEstudiante fila(Long id, String nombre, long intentos, long correctas, LocalDateTime ultima) {
        return new FilaEstudiante(id, nombre, id + "@u.edu.co", null, 100, 2, intentos, correctas, ultima, AHORA.minusMonths(1));
    }

    @Test
    @DisplayName("Al día: practicó hace 2 días y acierta más del 50 %")
    void estudianteAlDia() {
        assertThat(servicio.motivosRiesgo(AHORA.minusDays(2), 20, 80.0)).isEmpty();
    }

    @Test
    @DisplayName("En riesgo si nunca ha practicado")
    void nuncaPractico() {
        assertThat(servicio.motivosRiesgo(null, 0, 0.0)).containsExactly("Aún no ha practicado");
    }

    @Test
    @DisplayName("En riesgo si lleva más de 7 días sin practicar (7 justos todavía no)")
    void sinPracticar() {
        assertThat(servicio.motivosRiesgo(AHORA.minusDays(7), 10, 90.0)).isEmpty();
        assertThat(servicio.motivosRiesgo(AHORA.minusDays(10), 10, 90.0)).containsExactly("Sin practicar hace 10 días");
    }

    @Test
    @DisplayName("Aciertos bajos solo cuenta desde 5 intentos")
    void aciertosBajos() {
        assertThat(servicio.motivosRiesgo(AHORA.minusDays(1), 4, 0.0)).isEmpty();
        assertThat(servicio.motivosRiesgo(AHORA.minusDays(1), 6, 33.3)).containsExactly("Aciertos bajos (33,3 %)");
        assertThat(servicio.motivosRiesgo(AHORA.minusDays(1), 10, 50.0)).isEmpty();
    }

    @Test
    @DisplayName("Puede tener los dos motivos a la vez")
    void dosMotivos() {
        assertThat(servicio.motivosRiesgo(AHORA.minusDays(12), 8, 25.0))
                .containsExactly("Sin practicar hace 12 días", "Aciertos bajos (25 %)");
    }

    @Test
    @DisplayName("La lista arma el progreso de los 3 temas aunque el estudiante no tenga intentos en alguno")
    void listaConTemas() {
        when(port.preguntasActivasPorTema()).thenReturn(Map.of("limites", 15L, "derivadas", 15L, "integrales", 14L));
        when(port.progresoPorTema(isNull())).thenReturn(List.of(
                new FilaTemaEstudiante(1L, "limites", 10, 8, 7)));
        when(port.listarEstudiantes()).thenReturn(List.of(
                fila(1L, "Laura", 10, 8, AHORA.minusHours(3)),
                fila(2L, "Andrés", 0, 0, null)));

        List<EstudianteResumenResponse> lista = servicio.listarEstudiantes();

        EstudianteResumenResponse laura = lista.get(0);
        assertThat(laura.porcentaje()).isEqualTo(80.0);
        assertThat(laura.enRiesgo()).isFalse();
        assertThat(laura.porTema()).extracting(EstudianteResumenResponse.TemaProgreso::tema)
                .containsExactly("limites", "derivadas", "integrales");
        assertThat(laura.porTema().get(0).resueltas()).isEqualTo(7);
        assertThat(laura.porTema().get(2).totalPreguntas()).isEqualTo(14);
        assertThat(laura.porTema().get(1).intentos()).isZero();

        assertThat(lista.get(1).enRiesgo()).isTrue();
    }

    @Test
    @DisplayName("El panel del grupo suma aciertos de todos los temas y cuenta los estudiantes en riesgo")
    void panelGrupo() {
        when(port.preguntasActivasPorTema()).thenReturn(Map.of("limites", 15L, "derivadas", 15L, "integrales", 15L));
        when(port.progresoPorTema(isNull())).thenReturn(List.of());
        when(port.listarEstudiantes()).thenReturn(List.of(
                fila(1L, "Laura", 10, 9, AHORA.minusHours(1)),
                fila(2L, "Andrés", 6, 1, AHORA.minusDays(9))));
        when(port.resumenSemana()).thenReturn(new EstadisticasDocentePort.ResumenSemana(1, 10));
        when(port.estadisticasPorTema()).thenReturn(List.of(
                new TemaEstadistica("limites", 10, 9, 90.0, 1, 15),
                new TemaEstadistica("derivadas", 6, 1, 16.7, 1, 15),
                new TemaEstadistica("integrales", 0, 0, 0.0, 0, 15)));
        when(port.preguntasDificiles(5)).thenReturn(List.of());
        when(port.actividad(14, null)).thenReturn(List.of());

        EstadisticasGrupoResponse r = servicio.estadisticasGrupo();

        assertThat(r.totalEstudiantes()).isEqualTo(2);
        assertThat(r.estudiantesEnRiesgo()).isEqualTo(1);
        assertThat(r.porcentajeAciertoGeneral()).isEqualTo(62.5); // 10 de 16
        assertThat(r.preguntasActivas()).isEqualTo(45);
    }

    @Test
    @DisplayName("Detalle de un estudiante que no existe: error de no encontrado")
    void detalleNoExiste() {
        when(port.buscarEstudiante(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> servicio.detalleEstudiante(99L)).isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    @DisplayName("CSV: encabezados, separador ; y estado de cada estudiante")
    void csv() {
        when(port.preguntasActivasPorTema()).thenReturn(Map.of());
        when(port.progresoPorTema(isNull())).thenReturn(List.of(new FilaTemaEstudiante(1L, "limites", 4, 3, 3)));
        when(port.listarEstudiantes()).thenReturn(List.of(fila(1L, "Laura Gómez", 4, 3, AHORA.minusHours(2))));

        String csv = servicio.exportarCsv();
        String[] lineas = csv.substring(1).split("\r\n"); // sin el BOM

        assertThat(csv).startsWith("﻿");
        assertThat(lineas[0]).isEqualTo("Nombre;Correo;Puntos;Racha (días);Intentos;Aciertos (%);"
                + "Límites (%);Derivadas (%);Integrales (%);Última actividad;Estado;Motivo");
        assertThat(lineas[1]).isEqualTo("Laura Gómez;1@u.edu.co;100;2;4;75,0;75,0;;;26/10/2026 08:00;Al día;");
    }

    @Test
    @DisplayName("CSV: escapa comillas y ; y neutraliza fórmulas de Excel")
    void celdaCsv() {
        assertThat(EstadisticasDocenteService.celdaCsv("Ana; \"la profe\"")).isEqualTo("\"Ana; \"\"la profe\"\"\"");
        assertThat(EstadisticasDocenteService.celdaCsv("=HYPERLINK(\"x\")")).isEqualTo("\"'=HYPERLINK(\"\"x\"\")\"");
        assertThat(EstadisticasDocenteService.celdaCsv("+57 300")).isEqualTo("'+57 300");
        assertThat(EstadisticasDocenteService.celdaCsv(null)).isEmpty();
    }
}
