package co.edu.ucundinamarca.saesu.usuario.adapters.input.rest;

import co.edu.ucundinamarca.saesu.usuario.application.service.SubirFotoPerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Perfil", description = "Gestión del perfil del usuario")
public class PerfilController {

    private final SubirFotoPerfilService subirFotoPerfilService;

    public PerfilController(SubirFotoPerfilService subirFotoPerfilService) {
        this.subirFotoPerfilService = subirFotoPerfilService;
    }

    @Operation(summary = "Subir foto de perfil", description = "Sube una imagen a AWS S3 y actualiza el perfil del usuario. Requiere JWT.")
    @PostMapping(value = "/foto", consumes = "multipart/form-data") // <-- Esto indica que es un endpoint que recibe un archivo
    public ResponseEntity<Map<String, String>> subirFoto(@RequestParam("archivo") MultipartFile archivo,
                                                         Authentication authentication) throws IOException {
        
        String urlFoto = subirFotoPerfilService.ejecutar(authentication.getName(), archivo);
        
        return ResponseEntity.ok(Map.of("mensaje", "Foto actualizada", "url", urlFoto));
    }
}