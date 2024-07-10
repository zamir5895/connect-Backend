package dbp.connect.Friendship.Domain;

//import dbp.connect.Friendship.DTO.AmigoPersonalizado;
import dbp.connect.Friendship.DTO.AmigoDTO;
import dbp.connect.Friendship.DTO.AmigosDTO;
import dbp.connect.Friendship.Infrastructure.FriendshipRepositorio;
import dbp.connect.User.Domain.User;
import dbp.connect.User.Infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FriendshipServicio {
    @Autowired
    private FriendshipRepositorio friendshipRepositorio;
    private UserRepository userRepository;

    public void createFriendship(Long senderId,Long receiverId){
        Friendship friendship = new Friendship();
        User sender = userRepository.findById(senderId).orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado. "));
        User friend = userRepository.findById(receiverId).orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado. "));

        friendship.setUser(sender);
        friendship.setFriend(friend);
        friendship.setBlocked(false);
        friendship.setFechaAmistad(ZonedDateTime.now(ZoneId.systemDefault()));
        friendshipRepositorio.save(friendship);
    }
    public void blockearAmigo(Long amistadId, Long usuarioId, Long amigoId){
        User usuario = userRepository.findById(usuarioId).orElseThrow(()->
                new EntityNotFoundException("Usuario no encontrado. "));
        User amigo = userRepository.findById(amigoId).orElseThrow(()->
                new EntityNotFoundException("Usuario no encontrado. "));
        Friendship friendship = friendshipRepositorio.findById(amistadId).orElseThrow(()
                -> new EntityNotFoundException("Amistad no encontrada. "));
        if(!friendship.getUser().equals(usuario)){
            throw  new IllegalArgumentException("Usuario no esta en la amistad. ");
        }
        if(!friendship.getFriend().equals(amigo)){
            throw new IllegalArgumentException("El usuario no es amigo de"+usuario.getPrimerNombre());
        }
        friendship.setBlocked(true);
        friendshipRepositorio.save(friendship);
    }
    public void unblockAmigo(Long amistadId, Long usuarioId,Long amigoId){
        User usuario = userRepository.findById(usuarioId).orElseThrow(()->
                new EntityNotFoundException("Usuario no encontrado. "));
        User amigo = userRepository.findById(amigoId).orElseThrow(()->
                new EntityNotFoundException("Usuario no encontrado. "));
        Friendship friendship = friendshipRepositorio.findById(amistadId).orElseThrow(()
                -> new EntityNotFoundException("Amistad no encontrada. "));
        if(!friendship.getUser().equals(usuario)){
            throw  new IllegalArgumentException("Usuario no esta en la amistad. ");
        }
        if(!friendship.getFriend().equals(amigo)){
            throw new IllegalArgumentException("El usuario no es amigo de"+usuario.getPrimerNombre());
        }
        friendship.setBlocked(false);
        friendshipRepositorio.save(friendship);
    }

    public Page<AmigosDTO> obtenerAmigosNoBloqueados(Long usuarioId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Friendship> friendshipsPage = friendshipRepositorio.findNonBlockedFriendsByUserId(usuarioId, pageable);

        return friendshipsPage.map(friendship -> mapToAmigosDTOFromFriendship(friendship, usuarioId));
    }

    public void deleteFriendship(Long amistadId) {
        Friendship friendship = friendshipRepositorio.findById(amistadId).orElseThrow(() ->
                new EntityNotFoundException("Amistad no encontrada"));
        friendshipRepositorio.deleteById(amistadId);
    }


    public Page<AmigosDTO> obtenerAmigosBloqueados(Long usuarioId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Friendship> friendshipsPage = friendshipRepositorio.findBlockedFriendsByUserId(usuarioId, pageable);

        return friendshipsPage.map(friendship -> mapToAmigosDTOFromFriendship(friendship, usuarioId));
    }

    public Page<AmigosDTO> findAllAmigos(Long usuarioId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Friendship> friendshipsPage = friendshipRepositorio.findAllByUserId(usuarioId, pageable);

        return friendshipsPage.map(friendship -> mapToAmigosDTOFromFriendship(friendship, usuarioId));
    }
    public AmigosDTO findFriendshipById(Long amistadId) {
        Friendship friendship = friendshipRepositorio.findById(amistadId).orElseThrow(() ->
                new EntityNotFoundException("Amistad no encontrada"));
        return mapToAmigosDTOFromFriendship(friendship, friendship.getUser().getId());
    }
    public void updateFriendshipDate(Long amistadId, ZonedDateTime newDate) {
        Friendship friendship = friendshipRepositorio.findById(amistadId).orElseThrow(() ->
                new EntityNotFoundException("Amistad no encontrada"));
        friendship.setFechaAmistad(newDate);
        friendshipRepositorio.save(friendship);
    }
    public Long getTotalFriends(Long userId) {
        return friendshipRepositorio.findAllByUserId(userId, PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
    }
    public Long getTotalBlockedFriends(Long userId) {
        return friendshipRepositorio.findBlockedFriendsByUserId(userId, PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
    }
    public Long getTotalNonBlockedFriends(Long userId) {
        return friendshipRepositorio.findNonBlockedFriendsByUserId(userId, PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
    }
    // Implementation in FriendshipServicio
//    public List<AmigoPersonalizado> searchFriendsByName(Long userId, String name) {
//        List<Friendship> friendships = friendshipRepositorio.searchByUserIdAndFriendName(userId, name);
//        List<AmigoPersonalizado> amigos = new ArrayList<>();
//        for (Friendship friendship : friendships) {
//            User friend = userId.equals(friendship.getUser().getId()) ? friendship.getFriend() : friendship.getUser();
//            AmigoPersonalizado amigo = new AmigoPersonalizado();
//            amigo.setUsuarioId(friend.getId());
//            amigo.setUserName(friend.getPrimerNombre() + " " + friend.getSegundoNombre());
//            amigo.setUserName(friend.getUsername());
//            amigos.add(amigo);
//        }
//        return amigos;
//    }
    public List<AmigoDTO> getFriends(Long userId) {
        List<Friendship> friendships = friendshipRepositorio.findByUserIdAndBlockedIsFalse(userId);
        List<AmigoDTO> amigos = new ArrayList<>();
        for (Friendship friendship : friendships) {
            User friend = userId.equals(friendship.getUser().getId()) ? friendship.getFriend() : friendship.getUser();
            AmigoDTO amigo = new AmigoDTO();
            amigo.setAmigoId(friendship.getFId());
            amigo.setUsername(friend.getUsername());
            amigo.setFotoUrl(friend.getFotoUrl());
            amigos.add(amigo);
        }
        return amigos;
    }

    private AmigosDTO mapToAmigosDTO(User user, ZonedDateTime fechaAmistad, Long friendshipId) {
        AmigosDTO dto = new AmigosDTO();
        dto.setAmigoId(friendshipId);
        dto.setAmigoId(user.getId());
        dto.setNombreCompleto(user.getPrimerNombre() + " " + user.getSegundoNombre());
        dto.setApellidoCompleto(user.getPrimerApellido() + " " + user.getSegundoApellido());
        dto.setUserName(user.getUsername());
        dto.setFechaAmistad(fechaAmistad);
        return dto;
    }

    private AmigosDTO mapToAmigosDTOFromFriendship(Friendship friendship, Long usuarioId) {
        User friend = friendship.getUser().getId().equals(usuarioId) ? friendship.getFriend() : friendship.getUser();
        return mapToAmigosDTO(friend, friendship.getFechaAmistad(), friendship.getFId());
    }
    public boolean isFriend(Long userId1, Long userId2) {
        return friendshipRepositorio.areFriends(userId1, userId2);
    }



}
