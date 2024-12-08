package com.backend.backenddbp.FriendRequest.Domain;

import com.backend.backenddbp.FriendRequest.DTOS.FriendshipReceivedRequestDTO;
import com.backend.backenddbp.FriendRequest.DTOS.FriendshipRequestStatusDTO;
import com.backend.backenddbp.FriendRequest.DTOS.Status;
import com.backend.backenddbp.FriendRequest.Infrastructure.FriendshipRequestRepositorio;
import com.backend.backenddbp.Friendship.Domain.FriendshipServicio;
import com.backend.backenddbp.Friendship.Infrastructure.FriendshipRepositorio;
import com.backend.backenddbp.Security.Auth.AuthService;
import com.backend.backenddbp.User.Domain.User;
import com.backend.backenddbp.User.Infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FriendshipRequestServicio {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRequestRepositorio friendshipRequestRepositorio;
    @Autowired
    private FriendshipServicio friendshipServicio;
    @Autowired
    private AuthService authService;


    @Transactional
    public FriendshipRequestStatusDTO sendFriendRequest(Long senderId, Long receiverId) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);

        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            throw new EntityNotFoundException("Uno o ambos usuarios no están registrados");
        }

        Optional<FriendshipRequest> existingRequest = friendshipRequestRepositorio.findBySenderAndReceiverOrReceiverAndSender(senderId, receiverId);

        if (existingRequest.isPresent()) {
            throw new IllegalStateException("Ya existe una solicitud de amistad entre estos usuarios.");
        }
        if(friendshipServicio.areFriends(senderId, receiverId) == false){
            throw new IllegalStateException("Ya son amigos");
        }
        FriendshipRequest friendRequest = new FriendshipRequest();
        friendRequest.setSender(senderOpt.get());
        friendRequest.setReceiver(receiverOpt.get());
        friendRequest.setStatus(FriendRequestStatus.PENDIENTE);
        friendRequest.setRequestDate(ZonedDateTime.now(ZoneId.systemDefault()));

        friendshipRequestRepositorio.save(friendRequest);

        return convert(friendRequest, senderOpt.get(), receiverOpt.get());
    }


    @Transactional
    public FriendshipRequestStatusDTO respondToFriendRequest(Long requestId, boolean accept) {
        FriendshipRequest friendRequest = friendshipRequestRepositorio.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de amistad no encontrada para el ID: " + requestId));

        if (friendRequest.getStatus() == FriendRequestStatus.ACEPTADO || friendRequest.getStatus() == FriendRequestStatus.RECHAZADO) {
            throw new IllegalStateException("La solicitud ya ha sido procesada anteriormente con el estado: " + friendRequest.getStatus());
        }

        if (accept) {
            friendRequest.setStatus(FriendRequestStatus.ACEPTADO);
            friendshipRequestRepositorio.save(friendRequest);

            try {
                friendshipServicio.createFriendship(friendRequest.getSender().getId(), friendRequest.getReceiver().getId());
            } catch (Exception e) {
                throw new RuntimeException("Error al crear la amistad: " + e.getMessage());
            }

        } else {
            friendRequest.setStatus(FriendRequestStatus.RECHAZADO);
            friendshipRequestRepositorio.save(friendRequest);
        }

        return convert(friendRequest, friendRequest.getSender(), friendRequest.getReceiver());
    }


    public Set<FriendshipRequestStatusDTO> getReceivedRequests(Long receiverId) {
        User receiver = userRepository.findById(receiverId).orElseThrow(() ->
                new EntityNotFoundException("Usuario no encontrado"));
        Set<FriendshipRequest> requests = receiver.getReceivedFriendRequests();
        Set<FriendshipRequestStatusDTO> dto = new HashSet<>();
        for(FriendshipRequest request : requests){
            System.out.println(request.getSender().getUsername());
            dto.add(convert(request, request.getSender(),receiver ));
        }
        return dto;
    }
    public Set<FriendshipRequestStatusDTO> getSentRequests(Long senderId) {
        User sender = userRepository.findById(senderId).orElseThrow(() ->
                new EntityNotFoundException("Usuario no encontrado"));
        Set<FriendshipRequest> requests = sender.getSentFriendRequests();
        Set<FriendshipRequestStatusDTO> dto = new HashSet<>();
        for(FriendshipRequest request : requests){
            FriendshipRequestStatusDTO req = new FriendshipRequestStatusDTO();
            req.setFriendshipRequestId(request.getId());
            req.setSenderId(senderId);
            req.setSenderName(sender.getUsername());
            req.setReceiverId(request.getReceiver().getId());
            req.setReceiverUsername(request.getReceiver().getUsername());
            req.setReceiverFotoUrl(request.getReceiver().getFotoUrl());
            req.setRequestDate(request.getRequestDate());
            req.setStatus(request.getStatus());
            dto.add(req);
        }
        return dto;
    }
    public void cancelSentRequest(Long requestId) {
        Optional<FriendshipRequest> requestOpt = friendshipRequestRepositorio.findById(requestId);
        if (requestOpt.isPresent()) {
            FriendshipRequest request = requestOpt.get();
            if (request.getStatus().equals(FriendRequestStatus.PENDIENTE)) {
                friendshipRequestRepositorio.delete(request);
            } else {
                throw new EntityNotFoundException("No se puede cancelar una solicitud que ya ha sido respondida");
            }
        } else {
            throw new EntityNotFoundException("Solicitud no encontrada");
        }
    }
    public void deleteReceivedRequest(Long requestId, String token) {
        String email = authService.autentificarByToken(token);
        User usuario = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        Optional<FriendshipRequest> requestOpt = friendshipRequestRepositorio.findById(requestId);
        if (requestOpt.isPresent()) {
            FriendshipRequest request = requestOpt.get();
            if (request.getStatus().equals(FriendRequestStatus.PENDIENTE) && request.getReceiver().getId() == usuario.getId()) {
                friendshipRequestRepositorio.delete(request);
            } else {
                throw new EntityNotFoundException("No se puede eliminar una solicitud que ya ha sido respondida");
            }
        } else {
            throw new EntityNotFoundException("Solicitud no encontrada");
        }
    }

    public Status getRequestStatus(Long senderId, Long receiverId) {
        List<FriendshipRequest> requests = friendshipRequestRepositorio.findBySenderAndReceiver(senderId, receiverId);

        if (requests.isEmpty()) {
            throw new EntityNotFoundException("Solicitud no encontrada");
        }

        FriendshipRequest request = requests.get(0);
        Status status = new Status();
        status.setStatus(request.getStatus());
        status.setFriendshipRequestId(request.getId());
        status.setSenderId(request.getSender().getId());
        status.setSenderName(request.getSender().getPrimerNombre() + " " + request.getSender().getPrimerApellido() + " " + request.getSender().getSegundoApellido());
        status.setSenderfoto(request.getSender().getFotoUrl());
        status.setReceiverId(request.getReceiver().getId());
        status.setReceiverName(request.getReceiver().getPrimerNombre() + " " + request.getReceiver().getPrimerApellido() + " " + request.getReceiver().getSegundoApellido());
        status.setReceiverfoto(request.getReceiver().getFotoUrl());
        return status;
    }


    public FriendshipRequestStatusDTO convert(FriendshipRequest friendRequest, User sender, User receiver) {
        FriendshipRequestStatusDTO statusDTO = new FriendshipRequestStatusDTO();
        statusDTO.setFriendshipRequestId(friendRequest.getId());
        statusDTO.setStatus(friendRequest.getStatus());
        statusDTO.setRequestDate(friendRequest.getRequestDate());
        statusDTO.setSenderId(sender.getId());
        statusDTO.setReceiverId(receiver.getId());
        statusDTO.setReceiverFotoUrl(receiver.getFotoUrl());
        statusDTO.setReceiverUsername(receiver.getUsername());
        statusDTO.setSenderUsername(sender.getUsername());
        statusDTO.setSendfotoUrl(sender.getFotoUrl());
        statusDTO.setSenderFullName(sender.getPrimerNombre() + " " + receiver.getPrimerApellido() + " " + sender.getSegundoApellido());
        statusDTO.setReceiverFullName(receiver.getPrimerNombre() + " " + sender.getPrimerApellido() + " " + receiver.getSegundoApellido());
        return statusDTO;
    }
    public void deletFriendShip(Long id){
        Optional<FriendshipRequest> requestOpt = friendshipRequestRepositorio.findById(id);
        if (requestOpt.isPresent()) {
            friendshipRequestRepositorio.deleteById(requestOpt.get().getId());
        }

    }



}
