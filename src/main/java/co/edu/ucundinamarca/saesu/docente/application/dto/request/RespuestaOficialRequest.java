package co.edu.ucundinamarca.saesu.docente.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class RespuestaOficialRequest {
    private Long publicacionId; // Para saber de qué publicación quitar las otras oficiales
}