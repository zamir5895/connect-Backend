package com.backend.publicaciones.PublicacionInicioMultimedia.Domain;

import com.backend.publicaciones.PublicacionInicio.Domain.PublicacionInicio;
import com.backend.publicaciones.PublicacionInicio.Infrastructure.PublicacionInicioRepositorio;
import com.backend.publicaciones.PublicacionInicioMultimedia.Infrastructure.PublicacionInicioMultimediaRepositorio;
import com.backend.publicaciones.S3.StorageService;
import com.backend.publicaciones.Tipo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class PublicacionInicioMultimediaServicio {
    @Autowired
    private PublicacionInicioMultimediaRepositorio publicacionInicioMultimediaRepositorio;
    @Autowired
    private PublicacionInicioRepositorio publicacionInicioRepositorio;
    @Autowired
    private StorageService storageService;

    public PublicacionInicioMultimedia guardarArchivo(MultipartFile archivo, PublicacionInicio publicacionInicio) {
        try {
            PublicacionInicioMultimedia archivoMultimedia = new PublicacionInicioMultimedia();
            String id = generarNuevoId();
            archivoMultimedia.setId(id);
            if (Objects.requireNonNull(archivo.getContentType()).startsWith("image/")) {
                archivoMultimedia.setTipo(Tipo.FOTO);
            } else if (Objects.requireNonNull(archivo.getContentType()).startsWith("video/")) {
                archivoMultimedia.setTipo(Tipo.VIDEO);
            } else {
                throw new IllegalArgumentException("Tipo de archivo no soportado");
            }
            archivoMultimedia.setFechaCreacion(ZonedDateTime.now(ZoneId.systemDefault()));
            archivoMultimedia.setPublicacionInicio(publicacionInicio);

            String key = storageService.subirAlS3File(archivo,id);
            archivoMultimedia.setContenidoUrl(storageService.obtenerURL(key));
            PublicacionInicioMultimedia saved2 = publicacionInicioMultimediaRepositorio.save(archivoMultimedia);

            return saved2;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
        private String generarNuevoId() {
            return UUID.randomUUID().toString();
        }

}