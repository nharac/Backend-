package co.edu.ucundinamarca.saesu.core.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/** Subida de imágenes a S3: validaciones y nombre seguro (sin llamar a AWS de verdad). */
@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock private S3Client s3;
    private StorageService servicio;

    @BeforeEach
    void preparar() {
        servicio = new StorageService(s3, "saesu-bucket", "us-east-2");
    }

    @Test
    @DisplayName("Una imagen PNG se sube con un nombre aleatorio dentro de la carpeta")
    void subeImagenValida() throws Exception {
        MockMultipartFile png = new MockMultipartFile("archivo", "mi foto ../rara.png", "image/png", new byte[]{1, 2, 3});

        String url = servicio.subirImagen(png, "foro");

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3).putObject(captor.capture(), any(RequestBody.class));
        String clave = captor.getValue().key();
        assertThat(clave).matches("foro/[0-9a-f\\-]{36}\\.png");   // no usa el nombre original
        assertThat(url).isEqualTo("https://saesu-bucket.s3.us-east-2.amazonaws.com/" + clave);
        assertThat(servicio.esUrlPropia(url)).isTrue();
    }

    @Test
    @DisplayName("Un archivo que no es imagen se rechaza")
    void rechazaNoImagen() {
        MockMultipartFile pdf = new MockMultipartFile("archivo", "tarea.pdf", "application/pdf", new byte[]{1});

        assertThatThrownBy(() -> servicio.subirImagen(pdf, "foro"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JPG, PNG");
        verify(s3, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("Una imagen de más de 5 MB se rechaza")
    void rechazaImagenGrande() {
        MockMultipartFile grande = new MockMultipartFile("archivo", "g.jpg", "image/jpeg",
                new byte[(int) StorageService.MAX_BYTES + 1]);

        assertThatThrownBy(() -> servicio.subirImagen(grande, "foro"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5 MB");
    }

    @Test
    @DisplayName("Sin archivo se rechaza")
    void rechazaVacio() {
        MockMultipartFile vacio = new MockMultipartFile("archivo", "x.png", "image/png", new byte[0]);

        assertThatThrownBy(() -> servicio.subirImagen(vacio, "foro")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Una URL de otro sitio no se considera propia")
    void urlAjena() {
        assertThat(servicio.esUrlPropia("https://evil.com/x.png")).isFalse();
        assertThat(servicio.esUrlPropia(null)).isFalse();
    }
}
