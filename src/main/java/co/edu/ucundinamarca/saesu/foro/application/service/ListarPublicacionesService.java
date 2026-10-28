package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionResumenResponse;
import co.edu.ucundinamarca.saesu.foro.ports.input.ListarPublicacionesUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionResumenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lista del foro. Autor y número de respuestas vienen en la misma consulta
 * (antes eran dos consultas extra por cada publicación y por eso el foro tardaba en abrir).
 */
@Service
@RequiredArgsConstructor
public class ListarPublicacionesService implements ListarPublicacionesUseCase {

    private final PublicacionResumenPort resumenPort;
    private final PublicacionRepositoryPort publicacionRepository;

    @Override
    public List<PublicacionResumenResponse> listar(String tema, String busqueda, String orden, int page, int size) {
        return resumenPort.listar(tema, busqueda, orden, page, size).stream()
                .map(ResumenForo::aResponse)
                .toList();
    }

    @Override
    public long contar(String tema, String busqueda) {
        return publicacionRepository.contar(tema, busqueda);
    }
}
