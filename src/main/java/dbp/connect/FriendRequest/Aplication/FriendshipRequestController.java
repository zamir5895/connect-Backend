package dbp.connect.FriendRequest.Aplication;

import dbp.connect.FriendRequest.DTOS.FriendshipReceivedRequestDTO;
import dbp.connect.FriendRequest.DTOS.FriendshipRequestStatusDTO;
import dbp.connect.FriendRequest.DTOS.Status;
import dbp.connect.FriendRequest.Domain.FriendshipRequest;
import dbp.connect.FriendRequest.Domain.FriendshipRequestServicio;
import dbp.connect.Friendship.Domain.FriendshipServicio;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Void> sendFriendRequest(@PathVariable Long senderId, @PathVariable Long receiverId) {
        friendshipRequestServicio.sendFriendRequest(senderId, receiverId);
        return ResponseEntity.ok().build();

    }

    @PatchMapping("/{requestId}/respond")
    public ResponseEntity respondToFriendRequest(@PathVariable Long requestId,
                                                 @RequestParam boolean accept) {
        friendshipRequestServicio.respondToFriendRequest(requestId, accept);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/received/{receiverId}")
    public ResponseEntity<Set<FriendshipReceivedRequestDTO>> getReceivedRequests(@PathVariable Long receiverId) {
        return ResponseEntity.ok(friendshipRequestServicio.getReceivedRequests(receiverId));
    }

    @GetMapping("/sent/{senderId}")
    public ResponseEntity<Set<FriendshipRequestStatusDTO>> getSentRequests(@PathVariable Long senderId) {
        return ResponseEntity.ok(friendshipRequestServicio.getSentRequests(senderId));
    }

    @DeleteMapping("/cancel/{requestId}")
    public ResponseEntity<Void> cancelSentRequest(@PathVariable Long requestId) {
        friendshipRequestServicio.cancelSentRequest(requestId);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/delete/{requestId}")
    public ResponseEntity<Void> deleteReceivedRequest(@PathVariable Long requestId) {
        friendshipRequestServicio.deleteReceivedRequest(requestId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/status/{senderId}/{receiverId}")
    public ResponseEntity<Status> getRequestStatus(@PathVariable Long senderId,
                                                   @PathVariable Long receiverId) {
        return ResponseEntity.ok(friendshipRequestServicio.getRequestStatus(senderId, receiverId));
    }


}
