package com.backend.places.Favoritos.Aplication;

import com.backend.places.Favoritos.DTOS.ResponseFavoritosDTO;
import com.backend.places.Favoritos.Domain.Favoritos;
import com.backend.places.Favoritos.Domain.FavoritosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/favoritos")
public class FavoritosController {
    @Autowired
    FavoritosService favoritosService;

    @PostMapping("{usuarioId}/{publicacionId}")
    public ResponseEntity<Void> postearFavorito(@PathVariable Long usuarioId, @PathVariable Long publicacionId) {
        try {
            favoritosService.postearFavoritos(usuarioId, publicacionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("{usuarioId}")
    public ResponseEntity<Page<ResponseFavoritosDTO>> getAllFavoritos(@PathVariable Long usuarioId, @RequestParam Integer page, @RequestParam Integer size) {
        try {
            Page<ResponseFavoritosDTO> favoritos = favoritosService.getAllMyFavorites(usuarioId, page, size);
            return ResponseEntity.ok(favoritos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{usuarioId}/{publicacionId}")
    public ResponseEntity<Void> deleteFavorito(@PathVariable Long usuarioId, @PathVariable Long publicacionId) {
        try {
            favoritosService.deleteFromMyFavorites(usuarioId, publicacionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("{usuarioId}/{publicacionId}")
    public ResponseEntity<ResponseFavoritosDTO> getFavorito(@PathVariable Long usuarioId, @PathVariable Long publicacionId) {
        try {
            ResponseFavoritosDTO favorito = favoritosService.getFavorito(usuarioId, publicacionId);
            return ResponseEntity.ok(favorito);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("{usuarioId}/count")
    public ResponseEntity<Long> countFavoritos(@PathVariable Long usuarioId) {
        try {
            Long count = favoritosService.countFavoritos(usuarioId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
