package co.edu.ucundinamarca.saesu.core.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {

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

    public String subirArchivo(MultipartFile file) throws IOException {
        // Generar un nombre único para que no se sobreescriban (UUID + nombre original)
        String nombreOriginal = file.getOriginalFilename();
        String nombreUnico = UUID.randomUUID().toString() + "_" + nombreOriginal;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(nombreUnico)
                .contentType(file.getContentType())
                .build();

        // Subir el archivo a AWS
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // Devolver la URL pública del archivo
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, nombreUnico);
    }
}