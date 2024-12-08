package com.backend.publicaciones.Likes.Aplication;

import com.backend.publicaciones.Likes.DTOS.LikeResponseDTO;
import com.backend.publicaciones.Likes.DTOS.LikesOfUserDTO;
import com.backend.publicaciones.Likes.Domain.Like;
import com.backend.publicaciones.Likes.Domain.LikeService;
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
    public ResponseEntity<Boolean> processLike(
            @PathVariable Long publicacionInicioId,
            @PathVariable Long usuarioLikeId) {
        try {
            boolean result = likeService.processLikeSync(publicacionInicioId, usuarioLikeId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(false);
        }
    }

    @GetMapping("/{likeId}")
    public ResponseEntity<LikeResponseDTO> getLikeById(@PathVariable Long likeId) {
        try {
            LikeResponseDTO like = likeService.findLikeById(likeId);
            return ResponseEntity.ok(like);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{publicacionId}/{usuarioId}")
    public ResponseEntity<Boolean> deleteLike(
            @PathVariable Long publicacionId,
            @PathVariable Long usuarioId) {
        try {
            boolean isDeleted = likeService.deleteLikesByPublicacionIdAndUsuarioId(publicacionId, usuarioId);
            return ResponseEntity.ok(isDeleted);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(false);
        }
    }

    @GetMapping("/publicacion/{publicacionInicioId}")
    public ResponseEntity<List<LikeResponseDTO>> getLikesByPublicacion(@PathVariable Long publicacionInicioId) {
        try {
            List<LikeResponseDTO> likes = likeService.findLikesByPublicacionInicioId(publicacionInicioId);
            return ResponseEntity.ok(likes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }




    @GetMapping("{publicacionId}/likes/{userId}")
    public ResponseEntity<Boolean> getLIkesByUserIdAndPublicacionId(@PathVariable Long publicacionId, @PathVariable Long userId) {
        try {
            return ResponseEntity.ok(likeService.existsLike(publicacionId, userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
