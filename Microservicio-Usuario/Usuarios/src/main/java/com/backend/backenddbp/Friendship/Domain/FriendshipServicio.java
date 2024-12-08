package com.backend.backenddbp.Friendship.Domain;

//import dbp.connect.Friendship.DTO.AmigoPersonalizado;
import com.backend.backenddbp.Friendship.DTO.AmigoDTO;
import com.backend.backenddbp.Friendship.DTO.AmigosDTO;
import com.backend.backenddbp.Friendship.DTO.SearchAmigoDTO;
import com.backend.backenddbp.Friendship.Infrastructure.FriendshipRepositorio;
import com.backend.backenddbp.Security.Auth.AuthService;
import com.backend.backenddbp.User.Domain.User;
import com.backend.backenddbp.User.Infrastructure.UserRepository;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FriendshipServicio {
    @Autowired
    private FriendshipRepositorio friendshipRepositorio;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

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

        Stream<AmigosDTO> uniqueNonBlockedAmigosStream = friendshipsPage.getContent()
                .stream()
                .filter(friendship -> !friendship.isBlocked()) // Asegura que solo procesemos no bloqueados
                .map(friendship -> mapToAmigosDTOFromFriendship(friendship, usuarioId))
                .distinct(); // Elimina duplicados basados en equals y hashCode de AmigosDTO

        List<AmigosDTO> uniqueNonBlockedAmigosList = uniqueNonBlockedAmigosStream.collect(Collectors.toList());

        return new PageImpl<>(uniqueNonBlockedAmigosList, pageable, uniqueNonBlockedAmigosList.size());
    }

    public void deleteFriendship(Long amistadId) {
        Friendship friendship = friendshipRepositorio.findById(amistadId).orElseThrow(() ->
                new EntityNotFoundException("Amistad no encontrada"));
        friendshipRepositorio.deleteById(amistadId);
    }


    public Page<AmigosDTO> obtenerAmigosBloqueados(Long usuarioId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Friendship> friendshipsPage = friendshipRepositorio.findBlockedFriendsByUserId(usuarioId, pageable);

        friendshipsPage.getContent().forEach(friendship -> {
            System.out.println("Friendship ID: " + friendship.getId());
            System.out.println("Blocked Status: " + friendship.isBlocked());
            System.out.println("User ID: " + friendship.getUser().getId());
            System.out.println("Friend ID: " + friendship.getFriend().getId());
            System.out.println("---------------");
        });

        Stream<AmigosDTO> uniqueBlockedAmigosStream = friendshipsPage.getContent()
                .stream()
                .filter(Friendship::isBlocked) // Asegura que solo procesemos bloqueados
                .map(friendship -> mapToAmigosDTOFromFriendship(friendship, usuarioId))
                .distinct(); // Elimina duplicados basados en equals y hashCode de AmigosDTO

        List<AmigosDTO> uniqueBlockedAmigosList = uniqueBlockedAmigosStream.collect(Collectors.toList());

        return new PageImpl<>(uniqueBlockedAmigosList, pageable, uniqueBlockedAmigosList.size());
    }



    public Page<AmigosDTO> findAllAmigos(Long usuarioId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Friendship> friendshipsPage = friendshipRepositorio.findAllByUserId(usuarioId, pageable);

        Stream<AmigosDTO> uniqueAmigosStream = friendshipsPage.getContent()
                .stream()
                .map(friendship -> mapToAmigosDTOFromFriendship(friendship, usuarioId))
                .distinct();

        List<AmigosDTO> uniqueAmigosList = uniqueAmigosStream.collect(Collectors.toList());

        return new PageImpl<>(uniqueAmigosList, pageable, uniqueAmigosList.size());
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
        return friendshipRepositorio.findAllByUserId(userId, PageRequest.of(0, Integer.MAX_VALUE))
                .getContent()
                .stream()
                .map(friendship -> mapToAmigosDTOFromFriendship(friendship, userId))
                .distinct() // Elimina duplicados basados en equals y hashCode de AmigosDTO
                .count(); // Realiza el conteo final
    }

    public Long getTotalBlockedFriends(Long userId) {
        return friendshipRepositorio.findBlockedFriendsByUserId(userId, PageRequest.of(0, Integer.MAX_VALUE))
                .getContent()
                .stream()
                .filter(Friendship::isBlocked) // Filtra solo bloqueados
                .map(friendship -> mapToAmigosDTOFromFriendship(friendship, userId))
                .distinct() // Elimina duplicados
                .count(); // Realiza el conteo final
    }

    public Long getTotalNonBlockedFriends(Long userId) {
        return friendshipRepositorio.findNonBlockedFriendsByUserId(userId, PageRequest.of(0, Integer.MAX_VALUE))
                .getContent()
                .stream()
                .filter(friendship -> !friendship.isBlocked()) // Filtra no bloqueados
                .map(friendship -> mapToAmigosDTOFromFriendship(friendship, userId))
                .distinct() // Elimina duplicados
                .count();
    }


    public Set<SearchAmigoDTO> searchAmigoDTO(String token, String query) {
        query = query.toLowerCase();
        String email = authService.autentificarByToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        List<Friendship> friends = friendshipRepositorio.findAmigosByName(user.getId(), query);
        Set<SearchAmigoDTO> amigos = new HashSet<>();

        for (Friendship friendship : friends) {
            User friend = user.getId().equals(friendship.getUser().getId()) ? friendship.getFriend() : friendship.getUser();

            SearchAmigoDTO amigo = new SearchAmigoDTO();
            amigo.setAmigoId(friend.getId());
            amigo.setAmistadId(friendship.getId());
            amigo.setAmigoName(friend.getPrimerNombre() + " " + friend.getPrimerApellido() + " " + friend.getSegundoApellido());
            amigos.add(amigo);
        }

        return amigos;
    }


    public Set<AmigoDTO> getFriends(Long userId) {
        List<Friendship> friendships = friendshipRepositorio.findByUserIdAndBlockedIsFalse(userId);
        Set<AmigoDTO> amigos = new HashSet<>();

        for (Friendship friendship : friendships) {
            User friend = userId.equals(friendship.getUser().getId()) ? friendship.getFriend() : friendship.getUser();

            AmigoDTO amigo = new AmigoDTO();
            amigo.setAmigoId(friend.getId());
            amigo.setAmistadId(friendship.getId());
            amigo.setUsername(friend.getUsername());
            amigo.setFotoUrl(friend.getFotoUrl());
            amigo.setFullName(friend.getPrimerNombre() + " " + friend.getPrimerApellido() + " " + friend.getSegundoApellido());
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
        dto.setAmistadId(friendshipId);
        return dto;
    }

    private AmigosDTO mapToAmigosDTOFromFriendship(Friendship friendship, Long usuarioId) {
        User friend = friendship.getUser().getId().equals(usuarioId) ? friendship.getFriend() : friendship.getUser();
        return mapToAmigosDTO(friend, friendship.getFechaAmistad(), friendship.getId());
    }

    public boolean sonamigos(Long userId1, String token) {
        String email = authService.autentificarByToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return friendshipRepositorio.areFriends(userId1, user.getId());
    }
    public boolean areFriends(Long userId1, Long userId2) {

        return friendshipRepositorio.areFriends(userId1, userId2);
    }





}
