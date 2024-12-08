package com.backend.publicaciones.Comentarios.Aplication;


import com.backend.publicaciones.Comentarios.DTOS.CambioContenidoDTO;
import com.backend.publicaciones.Comentarios.DTOS.ComentarioDto;
import com.backend.publicaciones.Comentarios.DTOS.ComentarioRespuestaDTO;
import com.backend.publicaciones.Comentarios.Domain.Comentario;
import com.backend.publicaciones.Comentarios.Domain.ComentarioService;
import com.backend.publicaciones.ComentariosMultimedia.DTOS.ResponseComMultimediaDTO;
import com.backend.publicaciones.ComentariosMultimedia.Domain.ComentarioMultimediaServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private ComentarioMultimediaServicio comentarioMultimediaServicio;

    @PostMapping(value = "/{publicacionId}")
    public ResponseEntity<ComentarioRespuestaDTO> agregarComentario(@PathVariable Long publicacionId,
                                                        @Valid @RequestBody ComentarioDto comentarioDTO) {
        try {
            ComentarioRespuestaDTO comentario = comentarioService.createNewComentario(publicacionId, comentarioDTO);
            return ResponseEntity.ok(comentario);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }



    @PostMapping(value = "/{comentarioId}/multimedia")
    public ResponseEntity<ResponseComMultimediaDTO> agregarMultimedia(
                                                                      @PathVariable Long comentarioId,
                                                                      @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ResponseComMultimediaDTO dto = comentarioService.agregarMultimediaToComment( comentarioId, file);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/{publicacionId}/commentario/{parentId}/respuestas", consumes = "multipart/form-data")
    public ResponseEntity<ComentarioRespuestaDTO> agregarRespuesta(@PathVariable Long publicacionId,
                                                             @PathVariable Long parentId,
                                                             @RequestBody ComentarioDto comentarioDTO) {
        try {
            ComentarioRespuestaDTO comentario = comentarioService.createNewComentarioHijo(publicacionId, parentId, comentarioDTO);
            return ResponseEntity.ok(comentario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{publicacionId}/comentario")
    public ResponseEntity<Page<ComentarioRespuestaDTO>> getComentario(@PathVariable Long publicacionId,
                                                                      @RequestParam Integer page,
                                                                      @RequestParam Integer size) {
        try {
            return ResponseEntity.ok(comentarioService.getComentario(publicacionId, page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{publicacionId}/comentario/{parentId}/respuestas")
    public ResponseEntity<Page<ComentarioRespuestaDTO>> getRespuestas(@PathVariable Long publicacionId,
                                                                      @PathVariable Long parentId,
                                                                      @RequestParam Integer page,
                                                                      @RequestParam Integer size) {
        try {
            return ResponseEntity.ok(comentarioService.getResponseComentarios(publicacionId, parentId, page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{publicacionID}/comentario/{ComentarioId}")
    public ResponseEntity<Void> eliminarComentario(@PathVariable Long publicacionID, @PathVariable Long ComentarioId) {
        try {
            comentarioService.deleteComentarioById(publicacionID, ComentarioId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{publicacionID}/comentario/{parentID}/respuestas/{comentarioId}")
    public ResponseEntity<Void> eliminarRespuesta(@PathVariable Long publicacionID,
                                                  @PathVariable Long parentID,
                                                  @PathVariable Long comentarioId) {
        try {
            comentarioService.deleteComentarioRespuestaById(publicacionID, parentID, comentarioId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{publicacionId}/comentario/{comentarioId}")
    public ResponseEntity<ComentarioRespuestaDTO> actualizarComentario(@PathVariable Long publicacionId,
                                                                       @PathVariable Long comentarioId,
                                                                       @RequestBody CambioContenidoDTO cambioContenidoDTO) {
        try {
            ComentarioRespuestaDTO comentarioRespuestaDTO = comentarioService.actualizarComentario(publicacionId, comentarioId, cambioContenidoDTO);
            return ResponseEntity.ok(comentarioRespuestaDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("{publicacionId}/comentario/{parentID}/respuestas/{comentarioId}")
    public ResponseEntity<ComentarioRespuestaDTO> actualizarComentarioRespuesta(@PathVariable Long publicacionId,
                                                                                @PathVariable Long parentID,
                                                                                @PathVariable Long comentarioId,
                                                                                @RequestBody CambioContenidoDTO cambioContenidoDTO) {
        try {
            ComentarioRespuestaDTO comentarioRespuestaDTO = comentarioService.actualizarContenidoDeComentarioRespuesta(publicacionId, parentID, comentarioId, cambioContenidoDTO);
            return ResponseEntity.ok(comentarioRespuestaDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{publicacionId}/comentario/likes/{comentarioId}")
    public ResponseEntity<Void> actualizarComentarioLikes(@PathVariable Long publicacionId,
                                                          @PathVariable Long comentarioId) {
        try {
            comentarioService.actualizarComentariolikes(publicacionId, comentarioId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("{publicacionId}/comentario/{parentID}/respuestas/likes/{comentarioId}")
    public ResponseEntity<Void> actualizarComentarioRespuestaLikes(@PathVariable Long publicacionId,
                                                                   @PathVariable Long parentID,
                                                                   @PathVariable Long comentarioId) {
        try {
            comentarioService.actualizarContenidoDeComentarioRespuestaLikes(publicacionId, parentID, comentarioId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{publicacionId}/comentario/dislikes/{comentarioId}")
    public ResponseEntity<Void> actualizarComentarioDislikes(@PathVariable Long publicacionId,
                                                             @PathVariable Long comentarioId) {
        try {
            comentarioService.actualizarComentarioDislikes(publicacionId, comentarioId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("{publicacionId}/comentario/{parentID}/respuestas/dislikes/{comentarioId}")
    public ResponseEntity<Void> actualizarComentarioRespuestaDislikes(@PathVariable Long publicacionId,
                                                                      @PathVariable Long parentID,
                                                                      @PathVariable Long comentarioId) {
        try {
            comentarioService.actualizarContenidoDeComentarioRespuestaDislikes(publicacionId, parentID, comentarioId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{comentarioId}/multimedia[MultimediaId]")
    public ResponseEntity<Void> eliminarComentarioMultimedia(@PathVariable Long comentarioId, @PathVariable String multimediaId) {
        try {
            comentarioMultimediaServicio.eliminarArchivo(comentarioId, multimediaId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("{comentarioId}/multimedia[MultimediaId]")
    public ResponseEntity<ResponseComMultimediaDTO> obtenerMultimedia(@PathVariable Long comentarioId, @PathVariable String multimediaId) {
        try {
            return ResponseEntity.ok(comentarioMultimediaServicio.obtenerMultimedia(comentarioId, multimediaId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping(value = "{comentarioId}/multimedia[MultimediaId]", consumes = "multipart/form-data")
    public ResponseEntity<Void> modificarArchivo(@PathVariable Long comentarioId, @PathVariable String multimediaId, @RequestPart MultipartFile multimediaDTO) {
        try {
            comentarioMultimediaServicio.modificarArchivo(comentarioId, multimediaId, multimediaDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

