package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest.OpcionRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.PreguntaDocenteResponse;
import co.edu.ucundinamarca.saesu.docente.ports.output.BancoPreguntasPort;
import co.edu.ucundinamarca.saesu.practica.domain.exception.PreguntaNoEncontradaException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Banco de preguntas administrado por el docente: crear, editar, activar y desactivar.
 * Las preguntas no se borran: desactivarlas las saca de la práctica sin perder el historial de los estudiantes.
 * Cada cambio limpia la caché de la práctica (ListarPreguntasService) para que se vea de inmediato.
 */
@Service
@RequiredArgsConstructor
public class BancoPreguntasService {

    private static final Set<String> LETRAS = Set.of("A", "B", "C", "D");

    private final BancoPreguntasPort bancoPort;
    private final UsuarioRepositoryPort usuarioRepository;

    public List<PreguntaDocenteResponse> listar(String tema, String dificultad, String busqueda, String estado) {
        Boolean activa = switch (estado == null ? "todas" : estado) {
            case "activas" -> Boolean.TRUE;
            case "inactivas" -> Boolean.FALSE;
            default -> null;
        };
        return bancoPort.listar(tema, dificultad, busqueda, activa);
    }

    public PreguntaDocenteResponse obtener(Long id) {
        return bancoPort.buscarPorId(id)
                .orElseThrow(() -> new PreguntaNoEncontradaException("Pregunta no encontrada con id: " + id));
    }

    @Transactional
    @CacheEvict(value = {"preguntas", "contadorPreguntas"}, allEntries = true)
    public PreguntaDocenteResponse crear(PreguntaDocenteRequest request, String correoDocente) {
        validar(request, null);
        Long docenteId = usuarioRepository.buscarPorCorreo(correoDocente).map(Usuario::getId).orElse(null);
        Long id = bancoPort.crear(request, docenteId);
        return obtener(id);
    }

    @Transactional
    @CacheEvict(value = {"preguntas", "contadorPreguntas"}, allEntries = true)
    public PreguntaDocenteResponse actualizar(Long id, PreguntaDocenteRequest request) {
        obtener(id); // 404 si no existe
        validar(request, id);
        bancoPort.actualizar(id, request);
        return obtener(id);
    }

    @Transactional
    @CacheEvict(value = {"preguntas", "contadorPreguntas"}, allEntries = true)
    public PreguntaDocenteResponse cambiarEstado(Long id, boolean activa) {
        if (!bancoPort.cambiarEstado(id, activa)) {
            throw new PreguntaNoEncontradaException("Pregunta no encontrada con id: " + id);
        }
        return obtener(id);
    }

    /** Reglas que la validación de anotaciones no cubre. */
    void validar(PreguntaDocenteRequest r, Long idActual) {
        List<OpcionRequest> opciones = r.opciones();
        if (opciones == null || opciones.size() != 4) {
            throw new IllegalArgumentException("Debe haber exactamente 4 opciones (A, B, C y D)");
        }
        Set<String> letras = opciones.stream()
                .map(o -> o.letra() == null ? "" : o.letra().trim().toUpperCase())
                .collect(Collectors.toSet());
        if (!letras.equals(LETRAS)) {
            throw new IllegalArgumentException("Las opciones deben ser A, B, C y D, sin repetir");
        }
        long correctas = opciones.stream().filter(OpcionRequest::esCorrecta).count();
        if (correctas != 1) {
            throw new IllegalArgumentException("Debe haber exactamente una opción correcta");
        }
        Set<String> textos = opciones.stream()
                .map(o -> o.texto() == null ? "" : o.texto().trim().toLowerCase())
                .collect(Collectors.toSet());
        if (textos.size() != 4) {
            throw new IllegalArgumentException("Las cuatro opciones deben ser diferentes");
        }
        if (bancoPort.existeEnunciado(r.enunciado(), idActual)) {
            throw new IllegalArgumentException("Ya existe una pregunta con ese enunciado");
        }
    }
}
