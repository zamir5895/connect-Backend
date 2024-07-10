package dbp.connect.Comentarios.Aplication;


import dbp.connect.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import dbp.connect.Comentarios.DTOS.CambioContenidoDTO;
import dbp.connect.Comentarios.DTOS.ComentarioDto;
import dbp.connect.Comentarios.DTOS.ComentarioRespuestaDTO;
import dbp.connect.Comentarios.Domain.Comentario;
import dbp.connect.Comentarios.Domain.ComentarioService;
import dbp.connect.ComentariosMultimedia.DTOS.ResponseComMultimediaDTO;
import dbp.connect.ComentariosMultimedia.Domain.ComentarioMultimediaServicio;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
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
    ModelMapper modelMapper;
    @Autowired
    private ComentarioMultimediaServicio comentarioMultimediaServicio;

    @PostMapping("/{publicacionId}")
    public ResponseEntity<Comentario> agregarComentario(@PathVariable Long publicacionId,@Valid @RequestBody ComentarioDto comentarioDTO) {
        Comentario comentario = comentarioService.createNewComentario(publicacionId,comentarioDTO);
        return ResponseEntity.created(URI.create("/comentarios/" + comentario.getId())).build();
    }
    @PostMapping("/{publicacionId}/commentario/{parentId}/respuestas")
    public ResponseEntity<Page<Comentario>> agregarRespuesta(@PathVariable Long publicacionId,
                                                             @PathVariable Long parentId,
                                                             @RequestBody ComentarioDto comentarioDTO) {
        Comentario comentario = comentarioService.createNewComentarioHijo(publicacionId, parentId,comentarioDTO);
        return ResponseEntity.created(URI.create(parentId+"/comentarios/" + comentario.getId())).build();

    }
    @GetMapping("/{publicacionId}/comentario")
    public ResponseEntity<Page<ComentarioRespuestaDTO>> getComentario(
                                                           @PathVariable Long publicacionId,
                                                          @RequestParam int page,
                                                          @RequestParam int size) {
        return ResponseEntity.ok(comentarioService.getComentario(publicacionId, page, size));
    }

    @GetMapping("/{publicacionId}/comentario/{parentId}/respuestas")
    public ResponseEntity<Page<ComentarioRespuestaDTO>> getRespuestas(@PathVariable Long publicacionId,
                                                                   @PathVariable Long parentId,
                                                                   @RequestParam int page,
                                                                   @RequestParam int size) {
        return ResponseEntity.ok(comentarioService.getResponseComentarios(publicacionId,parentId, page, size));
    }
    @DeleteMapping("{publicacionID}/comentario/{ComentarioId}")
    public ResponseEntity<Void> eliminarComentario(@PathVariable Long publicacionID,@PathVariable Long ComentarioId) {
        comentarioService.deleteComentarioById(publicacionID,ComentarioId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{publicacionID}/comentario/{parentID}/respuestas/{comentarioId}")
    public ResponseEntity<Void> eliminarRespuesta(@PathVariable Long publicacionID,
                                                    @PathVariable Long parentID,
                                                    @PathVariable Long comentarioId) {
        comentarioService.deleteComentarioRespuestaById(publicacionID,parentID,comentarioId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{publicacionId}/comentario/{comentarioId}")
    public ResponseEntity<ComentarioRespuestaDTO> actualizarComentario(@PathVariable Long publicacionId,
                                                     @PathVariable Long comentarioId,
                                                     @RequestBody CambioContenidoDTO cambioContenidoDTO) {
        ComentarioRespuestaDTO comentarioRespuestaDTO = comentarioService.actualizarComentario(publicacionId,comentarioId,cambioContenidoDTO);
        return ResponseEntity.ok(comentarioRespuestaDTO);
    }
    @PatchMapping("{publicacionId}/comentario/{parentID}/respuestas/{comentarioId}")
    public ResponseEntity<ComentarioRespuestaDTO> actualizarComentarioRespuesta(@PathVariable Long publicacionId,
                                                              @PathVariable Long parentID,
                                                              @PathVariable Long comentarioId,
                                                              @RequestBody CambioContenidoDTO cambioContenidoDTO) {
        ComentarioRespuestaDTO comentarioRespuestaDTO = comentarioService.actualizarContenidoDeComentarioRespuesta(publicacionId,parentID,comentarioId,cambioContenidoDTO);
        return ResponseEntity.ok(comentarioRespuestaDTO);
    }
    @PatchMapping("/{publicacionId}/comentario/likes/{comentarioId}")
    public ResponseEntity<Void> actualizarComentarioLikes(@PathVariable Long publicacionId,
                                                                       @PathVariable Long comentarioId) {
        comentarioService.actualizarComentariolikes(publicacionId,comentarioId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("{publicacionId}/comentario/{parentID}/respuestas/likes/{comentarioId}")
    public ResponseEntity<Void> actualizarComentarioRespuestaLikes(@PathVariable Long publicacionId,
                                                                                @PathVariable Long parentID,
                                                                                @PathVariable Long comentarioId) {
        comentarioService.actualizarContenidoDeComentarioRespuestaLikes(publicacionId,parentID,comentarioId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/{publicacionId}/comentario/dislikes/{comentarioId}")
    public ResponseEntity<Void> actualizarComentarioDislikes(@PathVariable Long publicacionId,
                                                              @PathVariable Long comentarioId) {
        comentarioService.actualizarComentarioDislikes(publicacionId,comentarioId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("{publicacionId}/comentario/{parentID}/respuestas/dislikes/{comentarioId}")
    public ResponseEntity<Void> actualizarComentarioRespuestaDislikes(@PathVariable Long publicacionId,
                                                                       @PathVariable Long parentID,
                                                                       @PathVariable Long comentarioId) {
        comentarioService.actualizarContenidoDeComentarioRespuestaDislikes(publicacionId,parentID,comentarioId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("{comentarioId}/multimedia[MultimediaId]")
    public ResponseEntity<Void> eliminarComentarioMultimedia(@PathVariable Long comentarioId,@PathVariable String multimediaId) {
        comentarioMultimediaServicio.eliminarArchivo(comentarioId,multimediaId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("{comentarioId}/multimedia[MultimediaId]")
    public ResponseEntity<ResponseComMultimediaDTO> obtenerMultimedia(@PathVariable Long comentarioId, @PathVariable String multimediaId) {
        return ResponseEntity.ok(comentarioMultimediaServicio.obtenerMultimedia(comentarioId,multimediaId));
    }
    @PatchMapping("{comentarioId}/multimedia[MultimediaId]")
    public ResponseEntity<Void> modificarArchivo(@PathVariable Long comentarioId,@PathVariable String multimediaId,@RequestBody MultipartFile multimediaDTO) throws Exception {
        comentarioMultimediaServicio.modificarArchivo(comentarioId,multimediaId,multimediaDTO);
        return ResponseEntity.ok().build();
    }   

}
