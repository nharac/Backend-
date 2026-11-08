package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.foro.application.dto.response.UtilResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.UtilesResponse;
import co.edu.ucundinamarca.saesu.foro.domain.exception.RespuestaNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.ports.output.UtilesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** "Me sirvió" en las respuestas del foro: cada usuario marca una vez cada respuesta y puede quitarlo. */
@Service
public class UtilesService {

    private final UtilesPort utiles;

    public UtilesService(UtilesPort utiles) {
        this.utiles = utiles;
    }

    public UtilesResponse dePublicacion(Long publicacionId, String correo) {
        Long usuarioId = utiles.idUsuario(correo);
        List<Long> mias = usuarioId == null ? List.of() : utiles.marcadasPor(publicacionId, usuarioId).stream().sorted().toList();
        return new UtilesResponse(utiles.contarPorPublicacion(publicacionId), mias);
    }

    /** Marca la respuesta si no estaba marcada y la desmarca si ya lo estaba. */
    @Transactional
    public UtilResponse alternar(Long respuestaId, String correo) {
        Long usuarioId = utiles.idUsuario(correo);
        if (usuarioId == null || !utiles.respuestaVisible(respuestaId)) {
            throw new RespuestaNoEncontradaException("Respuesta no encontrada con id: " + respuestaId);
        }
        boolean marcada = utiles.marcar(respuestaId, usuarioId);
        if (!marcada) {
            utiles.desmarcar(respuestaId, usuarioId);
        }
        return new UtilResponse(respuestaId, utiles.contar(respuestaId), marcada);
    }
}
