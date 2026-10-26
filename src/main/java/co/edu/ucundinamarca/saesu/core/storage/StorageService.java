package co.edu.ucundinamarca.saesu.core.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Sube imágenes a AWS S3 (fotos de perfil e imágenes del foro).
 */
@Service
public class StorageService {

    /** Tamaño máximo de una imagen (el multipart permite 10 MB, aquí somos más estrictos). */
    public static final long MAX_BYTES = 5L * 1024 * 1024;

    /** Tipos permitidos y su extensión. Se valida el tipo, no el nombre que manda el usuario. */
    private static final Map<String, String> TIPOS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp",
            "image/gif", "gif"
    );

    private final S3Client s3Client;
    private final String bucketName;
    private final String region;

    public StorageService(S3Client s3Client,
                          @Value("${aws.bucket-name}") String bucketName,
                          @Value("${aws.region}") String region) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.region = region;
    }

    /**
     * Valida y sube una imagen dentro de una carpeta del bucket ("perfiles", "foro"...).
     * El nombre en S3 es un UUID: no se usa el nombre original (puede traer espacios o rutas).
     * @return URL pública de la imagen
     */
    public String subirImagen(MultipartFile file, String carpeta) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debes adjuntar una imagen");
        }
        String extension = TIPOS.get(file.getContentType());
        if (extension == null) {
            throw new IllegalArgumentException("La imagen debe ser JPG, PNG, WEBP o GIF");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("La imagen no puede pesar más de 5 MB");
        }

        String clave = carpeta + "/" + UUID.randomUUID() + "." + extension;
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(clave)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return urlBase() + clave;
    }

    /** Compatibilidad con el código anterior: sube a la carpeta "perfiles". */
    public String subirArchivo(MultipartFile file) throws IOException {
        return subirImagen(file, "perfiles");
    }

    /** true si la URL apunta a nuestro bucket (para no aceptar imágenes de otros sitios). */
    public boolean esUrlPropia(String url) {
        return url != null && url.startsWith(urlBase());
    }

    private String urlBase() {
        return String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
    }
}
