package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.core.storage.StorageService;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SubirFotoPerfilService {

    private final UsuarioRepositoryPort usuarioRepository;
    private final StorageService storageService;

    public SubirFotoPerfilService(UsuarioRepositoryPort usuarioRepository, StorageService storageService) {
        this.usuarioRepository = usuarioRepository;
        this.storageService = storageService;
    }

    public String ejecutar(String correo, MultipartFile archivo) throws IOException {
        // 1. Validar que sea una imagen
        if (archivo.isEmpty() || archivo.getContentType() == null || !archivo.getContentType().startsWith("image/")) {
            throw new RuntimeException("El archivo debe ser una imagen válida (JPG, PNG, etc.)");
        }

        // 2. Buscar usuario en la BD
        Usuario usuario = usuarioRepository.buscarPorCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Subir a AWS S3
        String urlFoto = storageService.subirArchivo(archivo);

        // 4. Actualizar usuario y guardar en BD
        usuario.setFotoPerfilUrl(urlFoto);
        usuarioRepository.guardar(usuario);

        return urlFoto;
    }
}