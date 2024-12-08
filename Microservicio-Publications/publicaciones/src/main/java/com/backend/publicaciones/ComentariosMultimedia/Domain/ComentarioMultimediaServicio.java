package com.backend.publicaciones.ComentariosMultimedia.Domain;


import com.backend.publicaciones.Comentarios.Domain.Comentario;
import com.backend.publicaciones.Comentarios.Infrastructure.ComentarioRepository;
import com.backend.publicaciones.ComentariosMultimedia.DTOS.ResponseComMultimediaDTO;
import com.backend.publicaciones.ComentariosMultimedia.Infrastructure.ComentarioMultimediaRepositorio;
import com.backend.publicaciones.S3.StorageService;
import com.backend.publicaciones.Tipo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComentarioMultimediaServicio {
    @Autowired
    ComentarioMultimediaRepositorio comentariosMultimediaRepositorio;
    @Autowired
    private StorageService storageService;
    private static Long idCounter =0L;

    @Autowired
    private ComentarioRepository comentarioRepository;

    public ComentarioMultimedia guardarArchivo(MultipartFile archivo, Comentario comentario) {
        try {
            ComentarioMultimedia archivoMultimedia = new ComentarioMultimedia();
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
            archivoMultimedia.setComentario(comentario);

            String key = storageService.subirAlS3File(archivo, archivoMultimedia.getId());
            archivoMultimedia.setUrlContenido(storageService.obtenerURL(key));
            ComentarioMultimedia multi = comentariosMultimediaRepositorio.save(archivoMultimedia);
            return multi;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo",e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarArchivo(Long comentarioId, String imagenId) {
        Optional<Comentario> comentarioOptional = comentarioRepository.findById(comentarioId);
        if (comentarioOptional.isPresent()) {
            Optional<ComentarioMultimedia> multimediaOptional = comentariosMultimediaRepositorio.findById(imagenId);
            if (multimediaOptional.isPresent()) {
                ComentarioMultimedia multimedia = multimediaOptional.get();
                if (multimedia.getComentario().getId().equals(comentarioId)) {
                    storageService.deleteFile(multimedia.getId());
                    comentariosMultimediaRepositorio.delete(multimedia);

                } else {
                    throw new EntityNotFoundException("La imagen no pertenece al comentario con id: " + comentarioId);
                }
            } else {
                throw new EntityNotFoundException("No se encontró la imagen con id: " + imagenId);
            }
        } else {
            throw new EntityNotFoundException("Comentario no encontrado con id: " + comentarioId);
        }
    }


    public void modificarArchivo(Long comentarioId, String imagenId, MultipartFile imagen) throws Exception {
        Optional<Comentario> comentarioOptional = comentarioRepository.findById(comentarioId);
        if (comentarioOptional.isPresent()) {
            Optional<ComentarioMultimedia> multimediaOptional = comentariosMultimediaRepositorio.findById(imagenId);
            if (multimediaOptional.isPresent()) {
                ComentarioMultimedia multimedia = multimediaOptional.get();
                if (multimedia.getComentario().getId().equals(comentarioId)) {
                    String key = storageService.subirAlS3File(imagen, multimedia.getId());
                    multimedia.setUrlContenido(storageService.obtenerURL(key));
                    comentariosMultimediaRepositorio.save(multimedia);
                } else {
                    throw new EntityNotFoundException("La imagen no pertenece al comentario con id: " + comentarioId);
                }
            } else {
                throw new EntityNotFoundException("No se encontró la imagen con id: " + imagenId);
            }
        } else {
            throw new EntityNotFoundException("Comentario no encontrado con id: " + comentarioId);
        }
    }

    public ResponseComMultimediaDTO obtenerMultimedia(Long comentarioId, String imagenId) {
        Optional<Comentario> comentarioOptional = comentarioRepository.findById(comentarioId);
        if (comentarioOptional.isPresent()) {
            ComentarioMultimedia multimedia = comentariosMultimediaRepositorio.findById(imagenId).orElseThrow(() -> new EntityNotFoundException("No se encontró la imagen con id: " + imagenId));
            ResponseComMultimediaDTO multimediaDTO = new ResponseComMultimediaDTO();

            if (multimedia.getId().equals((imagenId))) {
                multimediaDTO.setId(multimedia.getId());
                multimediaDTO.setTipo(multimedia.getTipo());
                multimediaDTO.setUrl_contenido(multimedia.getUrlContenido());
                return multimediaDTO;

            }
            throw new EntityNotFoundException("No se encontró la imagen con id: " + imagenId);
        } else {
            throw new EntityNotFoundException("Coomentario no encontrado con id: " + comentarioId);
        }
    }


    private String generarNuevoId() {
        return UUID.randomUUID().toString();
    }

}
