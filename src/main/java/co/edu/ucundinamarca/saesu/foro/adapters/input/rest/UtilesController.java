package co.edu.ucundinamarca.saesu.foro.adapters.input.rest;

import co.edu.ucundinamarca.saesu.foro.application.dto.response.UtilResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.UtilesResponse;
import co.edu.ucundinamarca.saesu.foro.application.service.UtilesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Adaptador de entrada: "Me sirvió" en las respuestas del foro. */
@RestController
@RequestMapping("/api/foro")
@Tag(name = "Foro", description = "Publicaciones y respuestas del foro colaborativo")
public class UtilesController {

    private final UtilesService utilesService;

    public UtilesController(UtilesService utilesService) {
        this.utilesService = utilesService;
    }

    @Operation(summary = "\"Me sirvió\" de una publicación",
            description = "Cuántos \"Me sirvió\" tiene cada respuesta y cuáles marcó el usuario. Requiere JWT.")
    @GetMapping("/publicaciones/{id}/utiles")
    public ResponseEntity<UtilesResponse> dePublicacion(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(utilesService.dePublicacion(id, authentication.getName()));
    }

    @Operation(summary = "Marcar o quitar \"Me sirvió\"",
            description = "Si la respuesta no estaba marcada la marca; si ya lo estaba, quita la marca. Requiere JWT.")
    @PostMapping("/respuestas/{id}/util")
    public ResponseEntity<UtilResponse> alternar(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(utilesService.alternar(id, authentication.getName()));
    }
}
