package co.edu.ucundinamarca.saesu.foro.adapters.input.rest;

import co.edu.ucundinamarca.saesu.core.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Sube la imagen de un ejercicio para adjuntarla a una publicación del foro.
 * Flujo: el frontend sube la imagen aquí, recibe la URL y la envía como imagenUrl
 * al crear la publicación (POST /api/foro/publicaciones).
 */
@RestController
@RequestMapping("/api/foro/imagenes")
@Tag(name = "Foro", description = "Imágenes de las publicaciones")
public class ForoImagenController {

    private final StorageService storageService;

    public ForoImagenController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Operation(summary = "Subir imagen del foro",
            description = "Recibe una imagen (JPG, PNG, WEBP o GIF, máx. 5 MB) en el campo 'archivo' y devuelve su URL en S3. Requiere JWT.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> subir(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        String url = storageService.subirImagen(archivo, "foro");
        return ResponseEntity.ok(Map.of("url", url));
    }
}
