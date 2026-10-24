package co.edu.ucundinamarca.saesu.gamificacion.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RankingResponse {
    private Long usuarioId;
    private String nombre;
    private String fotoPerfilUrl;
    private Long puntosTotales;
    private Integer rachaActual;
}