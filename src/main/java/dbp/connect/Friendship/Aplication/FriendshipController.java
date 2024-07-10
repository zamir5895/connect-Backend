package dbp.connect.Friendship.Aplication;

//import dbp.connect.Friendship.DTO.AmigoPersonalizado;
import dbp.connect.Friendship.DTO.AmigoDTO;
import dbp.connect.Friendship.DTO.AmigosDTO;
import dbp.connect.Friendship.Domain.Friendship;
import dbp.connect.Friendship.Domain.FriendshipServicio;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController("/api/amigos")
public class FriendshipController {
    @Autowired
    private FriendshipServicio friendshipServicio;

    @PatchMapping("/bloquear/{amistadId}/{usuarioId}/{pAmigoId}")
    public ResponseEntity<Void> blockearAmigo(@PathVariable Long amistadId,
                                              @PathVariable Long usuarioId,
                                                        @PathVariable Long pAmigoId){
        friendshipServicio.blockearAmigo(amistadId,usuarioId,pAmigoId);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/unblock/{amistadId}/{usuarioId}/{pAmigoId}")
    public ResponseEntity<Void> unblockAmigo(@PathVariable Long amistadId,
                                              @PathVariable Long usuarioId,
                                              @PathVariable Long pAmigoId){
        friendshipServicio.unblockAmigo(amistadId,usuarioId,pAmigoId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("{usuarioId}")
    public ResponseEntity<Page<AmigosDTO>> mostraramigosNoblockeados(@PathVariable Long usuarioId,
                                                                     @RequestParam Integer page,
                                                                     @RequestParam Integer size){
        return ResponseEntity.ok(friendshipServicio.obtenerAmigosNoBloqueados(usuarioId,page,size));
    }
    @GetMapping("/bloqueados/{usuarioId}")
    public ResponseEntity<Page<AmigosDTO>> mostraramigosBlockeados(@PathVariable Long usuarioId,
                                                                     @RequestParam Integer page,
                                                                     @RequestParam Integer size){
        return ResponseEntity.ok(friendshipServicio.obtenerAmigosBloqueados(usuarioId,page,size));
    }
    // Listar todas las amistades
    @GetMapping("/{usuarioid}/all")
    public ResponseEntity<Page<AmigosDTO>> getAllFriendships(@PathVariable Long userId,@RequestParam Integer page,
                                                             @RequestParam Integer size) {
        return ResponseEntity.ok(friendshipServicio.findAllAmigos(userId, page, size));
    }

    // Eliminar amistad
    @DeleteMapping("/{amistadId}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable Long amistadId) {
        friendshipServicio.deleteFriendship(amistadId);
        return ResponseEntity.noContent().build();
    }

    // Obtener detalles de una amistad específica
    @GetMapping("/details/{amistadId}")
    public ResponseEntity<AmigosDTO> getFriendshipDetails(@PathVariable Long amistadId) {
        return ResponseEntity.ok(friendshipServicio.findFriendshipById(amistadId));
    }

    @PatchMapping("/updateDate/{amistadId}")
    public ResponseEntity<Void> updateFriendshipDate(@PathVariable Long amistadId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime newDate) {
        friendshipServicio.updateFriendshipDate(amistadId, newDate);
        return ResponseEntity.accepted().build();
    }
    // Crear una nueva amistad
    @PostMapping("/crear/{userId}/{friendId}")
    public ResponseEntity<Void> createFriendship(@PathVariable Long userId, @PathVariable Long friendId) {
        friendshipServicio.createFriendship(userId, friendId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("{userId}/friends")
    public ResponseEntity<List<AmigoDTO>> getFriends(@PathVariable Long userId                                                      ) {
        return ResponseEntity.ok(friendshipServicio.getFriends(userId));
    }


    // Obtener el número total de amigos
    @GetMapping("/totalAmigos/{userId}")
    public ResponseEntity<Long> getTotalFriends(@PathVariable Long userId) {
        Long totalFriends = friendshipServicio.getTotalFriends(userId);
        return ResponseEntity.ok(totalFriends);
    }
    @GetMapping("/totalBloqueados/{userId}")
    public ResponseEntity<Long> getTotalBlockedFriends(@PathVariable Long userId) {
        Long totalBlockedFriends = friendshipServicio.getTotalBlockedFriends(userId);
        return ResponseEntity.ok(totalBlockedFriends);
    }
    @GetMapping("/totalNoBloqueado/{userId}")
    public ResponseEntity<Long> getTotalNonBlockedFriends(@PathVariable Long userId) {
        Long totalNonBlockedFriends = friendshipServicio.getTotalNonBlockedFriends(userId);
        return ResponseEntity.ok(totalNonBlockedFriends);
    }

//    // Buscar amigos por nombre
//    @GetMapping("/buscar/{userId}")
//    public ResponseEntity<List<AmigoPersonalizado>>searchFriendsByName(@PathVariable Long userId,
//                                                                       @RequestParam String name) {
//        return ResponseEntity.ok(friendshipServicio.searchFriendsByName(userId, name));
//    }

}
