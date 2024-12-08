package com.backend.publicaciones.PublicacionInicio.Aplication;


import com.backend.publicaciones.PublicacionInicio.DTOS.AmigosDTO;
import com.backend.publicaciones.PublicacionInicio.DTOS.DTOcreacionFiles;
import com.backend.publicaciones.PublicacionInicio.DTOS.PostInicioDTO;
import com.backend.publicaciones.PublicacionInicio.DTOS.PublicacionInicioResponseDTO;
import com.backend.publicaciones.PublicacionInicio.Domain.PublicacionInicio;
import com.backend.publicaciones.PublicacionInicio.Domain.PublicacionInicioServicio;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;

@RestController()
@RequestMapping("/api/publicacionInicio")
public class PublicacionInicioController {
    @Autowired
    private PublicacionInicioServicio publicacionInicioServicio;

    private static final Logger logger = LoggerFactory.getLogger(PublicacionInicioController.class);



    @PostMapping("/")
    public ResponseEntity<PublicacionInicioResponseDTO> crearPublicacionInicio(
            @RequestBody PostInicioDTO postInicioDTO)
    {
        try {
                System.out.println(postInicioDTO.getCuerpo());
                System.out.println(postInicioDTO.getAutorPId());
               publicacionInicioServicio.createPostInicioDTO(postInicioDTO);
            return ResponseEntity.ok(publicacionInicioServicio.createPostInicioDTO(postInicioDTO));
        } catch (Exception e) {
            logger.error("Error al crear la publicación: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/subir-archivos/{postId}")
    public ResponseEntity<DTOcreacionFiles> subirArchivos(
            @PathVariable Long postId,
            @RequestPart(value = "files") List<MultipartFile> multimedia
    ) {
        try {
            return ResponseEntity.ok(publicacionInicioServicio.addFilesToPost(postId, multimedia));
        } catch (Exception e) {
            logger.error("Error al subir archivos para la publicación con ID {}: {}", postId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }



    @GetMapping("/{publicacionId}")
    public ResponseEntity<PublicacionInicioResponseDTO> obtenerPublicacionInicio(@PathVariable Long publicacionId) {
        try {
            return ResponseEntity.ok(publicacionInicioServicio.obtenerPublciacionesInicio(publicacionId));
        } catch (Exception e) {
            logger.error("Error al obtener la publicación: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{publicacionId}")
    public ResponseEntity<Void> eliminarPublicacion(@PathVariable Long publicacionId) {
        try {
            publicacionInicioServicio.eliminarPublicacionInicio(publicacionId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error al eliminar la publicación: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<PublicacionInicioResponseDTO>> obtenerPublicacionesUsuario(
            @PathVariable Long usuarioId, @RequestParam Integer page, @RequestParam Integer size) {
        try {
            return ResponseEntity.ok(publicacionInicioServicio.obtenerPublicacionByUsuario(usuarioId, page, size));
        } catch (Exception e) {
            logger.error("Error al obtener publicaciones del usuario: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{usuarioId}/{publicacionId}/contenido")
    public ResponseEntity<PublicacionInicioResponseDTO> cambiarContenido(
            @PathVariable Long usuarioId, @PathVariable Long publicacionId, @RequestParam String contenido) {
        try {
            return ResponseEntity.ok(publicacionInicioServicio.actualizarContenido(usuarioId, publicacionId, contenido));
        } catch (Exception e) {
            logger.error("Error al actualizar el contenido: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping(value = "/{usuarioId}/{publicacionId}/multimedia", consumes = "multipart/form-data")
    public ResponseEntity<PublicacionInicioResponseDTO> cambiarMultimedia(
            @PathVariable Long usuarioId, @PathVariable Long publicacionId, @RequestPart("file") List<MultipartFile> multimedia) {
        try {
            return ResponseEntity.ok(publicacionInicioServicio.actualizarMultimedia(usuarioId, publicacionId, multimedia));
        } catch (Exception e) {
            logger.error("Error al actualizar la multimedia: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<PublicacionInicioResponseDTO>> buscarPublicaciones(
            @RequestParam String palabraClave, @RequestParam Integer page, @RequestParam Integer size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(publicacionInicioServicio.buscarPorPalabraClave(palabraClave, pageable));
        } catch (Exception e) {
            logger.error("Error al buscar publicaciones: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<PublicacionInicioResponseDTO>> obtenerPublicacionesInicio(
            @RequestParam Integer page, @RequestParam Integer size) {
        try {
            return ResponseEntity.ok(publicacionInicioServicio.encontrarTodos(page, size));
        } catch (Exception e) {
            logger.error("Error al obtener todas las publicaciones: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/feed/publicaciones/")
    public ResponseEntity<Page<PublicacionInicioResponseDTO>> obtenerPublicaciones(
            @RequestParam AmigosDTO amigosDTO, @RequestParam Integer page, @RequestParam Integer size) {
        try {
            return ResponseEntity.ok(publicacionInicioServicio.encontrarPublicacionAMigos(amigosDTO, page, size));
        } catch (Exception e) {
            logger.error("Error al obtener publicaciones del feed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}