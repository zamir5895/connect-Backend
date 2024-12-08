package com.backend.backenddbp.FriendRequest.Aplication;

import com.backend.backenddbp.FriendRequest.DTOS.FriendshipReceivedRequestDTO;
import com.backend.backenddbp.FriendRequest.DTOS.FriendshipRequestStatusDTO;
import com.backend.backenddbp.FriendRequest.DTOS.Status;
import com.backend.backenddbp.FriendRequest.Domain.FriendshipRequest;
import com.backend.backenddbp.FriendRequest.Domain.FriendshipRequestServicio;
import com.backend.backenddbp.Friendship.Domain.FriendshipServicio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/friendship-requests")
public class FriendshipRequestController {

    @Autowired
    private FriendshipRequestServicio friendshipRequestServicio;


    @PostMapping("/{senderId}/{receiverId}")
    public ResponseEntity<?> sendFriendRequest(@PathVariable Long senderId, @PathVariable Long receiverId) {
        try {
            FriendshipRequestStatusDTO response = friendshipRequestServicio.sendFriendRequest(senderId, receiverId);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La solicitud de amistad ya existe entre estos usuarios.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Uno o ambos usuarios no están registrados.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar solicitud de amistad.");
        }
    }



    @PutMapping("/{requestId}/respond")
    public ResponseEntity<?> respondToFriendRequest(@PathVariable Long requestId, @RequestParam boolean accept) {
        try {
            FriendshipRequestStatusDTO response = friendshipRequestServicio.respondToFriendRequest(requestId, accept);
            friendshipRequestServicio.deletFriendShip(response.getFriendshipRequestId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("La amistad ya ha sido respondida ");
        }
    }

    @GetMapping("/received/{receiverId}")
    public ResponseEntity<?> getReceivedRequests(@PathVariable Long receiverId) {
        try {
            Set<FriendshipRequestStatusDTO> receivedRequests = friendshipRequestServicio.getReceivedRequests(receiverId);
            return ResponseEntity.ok(receivedRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener las solicitudes de amistad recibidas");
        }
    }

    @GetMapping("/send/{senderId}")
    public ResponseEntity<?> getSentRequests(@PathVariable Long senderId) {
        try {
            Set<FriendshipRequestStatusDTO> sentRequests = friendshipRequestServicio.getSentRequests(senderId);
            return ResponseEntity.ok(sentRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener las solicitudes de amistad enviadas");
        }
    }

    @DeleteMapping("/cancel/{requestId}")
    public ResponseEntity<?> cancelSentRequest(@PathVariable Long requestId) {
        try {
            friendshipRequestServicio.cancelSentRequest(requestId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cancelar la solicitud de amistad enviada");
        }
    }


    @DeleteMapping("/delete/{requestId}")
    public ResponseEntity<?> deleteReceivedRequest(@PathVariable Long requestId, @RequestHeader("Authorization") String token) {
        try {

            friendshipRequestServicio.deleteReceivedRequest(requestId, token);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la solicitud de amistad recibida");
        }
    }
    @GetMapping("/status/{senderId}/{receiverId}")
    public ResponseEntity<?> getRequestStatus(@PathVariable Long senderId, @PathVariable Long receiverId) {
        try {
            Status status = friendshipRequestServicio.getRequestStatus(senderId, receiverId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el estado de la solicitud de amistad");
        }
    }


}
