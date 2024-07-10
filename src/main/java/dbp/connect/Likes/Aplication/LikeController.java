package dbp.connect.Likes.Aplication;

import dbp.connect.Likes.DTOS.LikeResponseDTO;
import dbp.connect.Likes.DTOS.LikesOfUserDTO;
import dbp.connect.Likes.Domain.Like;
import dbp.connect.Likes.Domain.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping("/{publicacionInicioId}/{usuarioLikeId}")
    public ResponseEntity<Void> postLike(Long publicacionInicioId, Long usuarioLikeId) {
        likeService.processLikeAsync(publicacionInicioId, usuarioLikeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{likeId}")
    public ResponseEntity<LikeResponseDTO> getLikeById(@PathVariable Long likeId) {
        try{
            LikeResponseDTO like = likeService.findLikeById(likeId);
            return ResponseEntity.ok(like);}
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{likeId}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long likeId) {
        likeService.deleteLikeByIdAsync(likeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/publicacion/{publicacionInicioId}")
    public ResponseEntity<List<LikeResponseDTO>> getLikesByPublicacion(@PathVariable Long publicacionInicioId) {
        List<LikeResponseDTO> likes = likeService.findLikesByPublicacionInicioId(publicacionInicioId);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/usuario/{usuarioLikeId}")
    public ResponseEntity<List<LikesOfUserDTO>> getLikesByUsuario(@PathVariable Long usuarioLikeId) {
        List<LikesOfUserDTO> likes = likeService.findLikesByUsuarioLikeId(usuarioLikeId);
        return ResponseEntity.ok(likes);
    }
    @GetMapping("{publicacionInicioId}/cantidad")
    public ResponseEntity<Integer> getLikesCountByPublicacion(@PathVariable Long publicacionInicioId) {
        Integer likes = likeService.findLikesCountByPublicacionInicioId(publicacionInicioId);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<LikeResponseDTO>> getLikesByDateRange(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime fin) {
        List<LikeResponseDTO> likes = likeService.findLikesByDateRange(inicio, fin);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/usuario/{usuarioLikeId}/recientes")
    public ResponseEntity<List<LikeResponseDTO>> getRecentLikesByUsuario(@PathVariable Long usuarioLikeId,
                                                                         @RequestParam(defaultValue = "5") int limit) {
        List<LikeResponseDTO> likes = likeService.findRecentLikesByUsuario(usuarioLikeId, limit);
        return ResponseEntity.ok(likes);
    }
    @GetMapping("{publicacionId}/likes")
    public ResponseEntity<Integer> totalLikes(@PathVariable Long publicacionId){
        return ResponseEntity.ok(likeService.totalLikes(publicacionId));
    }


}
