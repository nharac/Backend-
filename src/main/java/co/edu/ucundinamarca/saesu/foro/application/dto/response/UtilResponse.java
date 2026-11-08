package co.edu.ucundinamarca.saesu.foro.application.dto.response;

/** Estado del "Me sirvió" de una respuesta después de marcarlo o quitarlo. */
public record UtilResponse(Long respuestaId, long utiles, boolean marcada) {
}
