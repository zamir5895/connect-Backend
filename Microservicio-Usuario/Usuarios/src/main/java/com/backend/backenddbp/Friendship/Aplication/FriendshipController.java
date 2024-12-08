package com.backend.backenddbp.Friendship.Aplication;

import com.backend.backenddbp.Friendship.DTO.AmigoDTO;
import com.backend.backenddbp.Friendship.DTO.AmigosDTO;
import com.backend.backenddbp.Friendship.DTO.SearchAmigoDTO;
import com.backend.backenddbp.Friendship.Domain.Friendship;
import com.backend.backenddbp.Friendship.Domain.FriendshipServicio;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@RestController()
@RequestMapping("/api/amigos")
public class FriendshipController {
    @Autowired
    private FriendshipServicio friendshipServicio;

    @PatchMapping("/bloquear/{amistadId}/{usuarioId}/{pAmigoId}")
    public ResponseEntity<?> blockearAmigo(@PathVariable Long amistadId,
                                           @PathVariable Long usuarioId,
                                           @PathVariable Long pAmigoId) {
        try {
            friendshipServicio.blockearAmigo(amistadId, usuarioId, pAmigoId);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al bloquear amigo");
        }
    }

    @PatchMapping("/unblock/{amistadId}/{usuarioId}/{pAmigoId}")
    public ResponseEntity<?> unblockAmigo(@PathVariable Long amistadId,
                                          @PathVariable Long usuarioId,
                                          @PathVariable Long pAmigoId) {
        try {
            friendshipServicio.unblockAmigo(amistadId, usuarioId, pAmigoId);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desbloquear amigo");
        }
    }

    @GetMapping("{usuarioId}")
    public ResponseEntity<?> mostraramigosNoblockeados(@PathVariable Long usuarioId,
                                                       @RequestParam Integer page,
                                                       @RequestParam Integer size) {
        try {
            Page<AmigosDTO> amigos = friendshipServicio.obtenerAmigosNoBloqueados(usuarioId, page, size);
            return ResponseEntity.ok(amigos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener amigos no bloqueados");
        }
    }

    @GetMapping("/bloqueados/{usuarioId}")
    public ResponseEntity<?> mostraramigosBlockeados(@PathVariable Long usuarioId,
                                                     @RequestParam Integer page,
                                                     @RequestParam Integer size) {
        try {
            Page<AmigosDTO> bloqueados = friendshipServicio.obtenerAmigosBloqueados(usuarioId, page, size);
            return ResponseEntity.ok(bloqueados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener amigos bloqueados");
        }
    }

    @GetMapping("/{userId}/all")
    public ResponseEntity<?> getAllFriendships(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<AmigosDTO> allFriends = friendshipServicio.findAllAmigos(userId, page, size);
            return ResponseEntity.ok(allFriends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener todas las amistades");
        }
    }

    @DeleteMapping("/{amistadId}")
    public ResponseEntity<?> deleteFriendship(@PathVariable Long amistadId) {
        try {
            friendshipServicio.deleteFriendship(amistadId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la amistad");
        }
    }

    @GetMapping("/detalles/{amistadId}")
    public ResponseEntity<?> getFriendshipDetails(@PathVariable Long amistadId) {
        try {
            AmigosDTO friendshipDetails = friendshipServicio.findFriendshipById(amistadId);
            return ResponseEntity.ok(friendshipDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener detalles de la amistad");
        }
    }

    @PatchMapping("/updateDate/{amistadId}")
    public ResponseEntity<?> updateFriendshipDate(@PathVariable Long amistadId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime newDate) {
        try {
            friendshipServicio.updateFriendshipDate(amistadId, newDate);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la fecha de amistad");
        }
    }



    @GetMapping("{userId}/friends")
    public ResponseEntity<?> getFriends(@PathVariable Long userId) {
        try {
            Set<AmigoDTO> friends = friendshipServicio.getFriends(userId);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener los amigos");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFriends(@RequestHeader("Authorization") String token, @RequestParam String query){
        try {
            Set <SearchAmigoDTO> search = friendshipServicio.searchAmigoDTO(token, query);
            return ResponseEntity.ok(search);

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener los amigos");
        }
    }


    @GetMapping("/totalAmigos/{userId}")
    public ResponseEntity<?> getTotalFriends(@PathVariable Long userId) {
        try {
            Long totalFriends = friendshipServicio.getTotalFriends(userId);
            return ResponseEntity.ok(totalFriends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el número total de amigos");
        }
    }

    @GetMapping("/totalBloqueados/{userId}")
    public ResponseEntity<?> getTotalBlockedFriends(@PathVariable Long userId) {
        try {
            Long totalBlockedFriends = friendshipServicio.getTotalBlockedFriends(userId);
            return ResponseEntity.ok(totalBlockedFriends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el número total de amigos bloqueados");
        }
    }

    @GetMapping("/totalNoBloqueado/{userId}")
    public ResponseEntity<?> getTotalNonBlockedFriends(@PathVariable Long userId) {
        try {
            Long totalNonBlockedFriends = friendshipServicio.getTotalNonBlockedFriends(userId);
            return ResponseEntity.ok(totalNonBlockedFriends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el número total de amigos no bloqueados");
        }
    }

    @GetMapping("/myfriend/{userId}")
    public ResponseEntity<?> getMyFriends(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        try{
            return ResponseEntity.ok(friendshipServicio.sonamigos(userId, token));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No son amigos");
        }
    }



}
