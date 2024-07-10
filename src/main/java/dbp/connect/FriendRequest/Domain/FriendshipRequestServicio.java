package dbp.connect.FriendRequest.Domain;

import dbp.connect.FriendRequest.DTOS.FriendshipReceivedRequestDTO;
import dbp.connect.FriendRequest.DTOS.FriendshipRequestStatusDTO;
import dbp.connect.FriendRequest.DTOS.Status;
import dbp.connect.FriendRequest.Infrastructure.FriendshipRequestRepositorio;
import dbp.connect.Friendship.Domain.FriendshipServicio;
import dbp.connect.Friendship.Infrastructure.FriendshipRepositorio;
import dbp.connect.User.Domain.User;
import dbp.connect.User.Infrastructure.UserRepository;
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
    private FriendshipRepositorio friendshipRepositorio;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRequestRepositorio friendshipRequestRepositorio;
    @Autowired
    private FriendshipServicio friendshipServicio;
    @Transactional
    public void sendFriendRequest(Long senderId, Long receiverId) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            User sender = senderOpt.get();
            User receiver = receiverOpt.get();

            FriendshipRequest friendRequest = new FriendshipRequest();
            friendRequest.setSender(sender);
            friendRequest.setReceiver(receiver);
            friendRequest.setStatus(FriendRequestStatus.PENDIENTE);
            friendRequest.setRequestDate(ZonedDateTime.now(ZoneId.systemDefault()));

            friendshipRequestRepositorio.save(friendRequest);
        }
        else{
            throw new EntityNotFoundException("Los usuarios no se encuentran registrados");
        }
    }

    @Transactional
    public void respondToFriendRequest(Long requestId, boolean accept) {
        Optional<FriendshipRequest> requestOpt = friendshipRequestRepositorio.findById(requestId);

        if (requestOpt.isPresent()) {
            FriendshipRequest friendRequest = requestOpt.get();

            if (accept) {
                friendRequest.setStatus(FriendRequestStatus.ACEPTADO);
                friendshipRequestRepositorio.save(friendRequest);
                friendshipServicio.createFriendship(friendRequest.getSender().getId(), friendRequest.getReceiver().getId());
            } else {
                friendRequest.setStatus(FriendRequestStatus.RECHAZADO);
                friendshipRequestRepositorio.save(friendRequest);
            }
        }
    }
    public Set<FriendshipReceivedRequestDTO> getReceivedRequests(Long receiverId) {
        User receiver = userRepository.findById(receiverId).orElseThrow(() ->
                new EntityNotFoundException("Usuario no encontrado"));
        Set<FriendshipRequest> requests = receiver.getReceivedFriendRequests();
        Set<FriendshipReceivedRequestDTO> dto = new HashSet<>();
        for(FriendshipRequest request : requests){
            System.out.println(request.getSender().getUsername());
            FriendshipReceivedRequestDTO req = new FriendshipReceivedRequestDTO();
            req.setFriendshipRequestId(request.getId());
            req.setReceiverId(receiverId);
            req.setReceiverUsername(receiver.getUsername());
            req.setSenderId(request.getSender().getId());
            req.setSenderName(request.getSender().getUsername());
            req.setSenderFotoUrl(request.getSender().getFotoUrl());
            req.setRequestDate(request.getRequestDate());
            req.setStatus(request.getStatus());
            dto.add(req);
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
    public void deleteReceivedRequest(Long requestId) {
        Optional<FriendshipRequest> requestOpt = friendshipRequestRepositorio.findById(requestId);
        if (requestOpt.isPresent()) {
            FriendshipRequest request = requestOpt.get();
            if (request.getStatus().equals(FriendRequestStatus.PENDIENTE)) {
                friendshipRequestRepositorio.delete(request);
            } else {
                throw new EntityNotFoundException("No se puede eliminar una solicitud que ya ha sido respondida");
            }
        } else {
            throw new EntityNotFoundException("Solicitud no encontrada");
        }
    }
    public Status getRequestStatus(Long senderId, Long receiverId) {
        Optional<FriendshipRequest> requestOpt = friendshipRequestRepositorio.findBySenderAndReceiver(senderId, receiverId);
        if (requestOpt.isPresent()) {
            FriendshipRequest request = requestOpt.get();
            Status status = new Status();
            status.setStatus(request.getStatus());
            status.setFriendshipRequestId(request.getId());
            return status;
        } else {
            throw new EntityNotFoundException("Solicitud no encontrada");
        }
    }



}
