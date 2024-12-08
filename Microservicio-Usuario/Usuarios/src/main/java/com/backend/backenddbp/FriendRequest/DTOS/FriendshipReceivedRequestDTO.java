package com.backend.backenddbp.FriendRequest.DTOS;

import com.backend.backenddbp.FriendRequest.Domain.FriendRequestStatus;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class FriendshipReceivedRequestDTO {
    private Long friendshipRequestId;
    private FriendRequestStatus status;
    private String senderName;
    private String receiverUsername;
    private String senderFotoUrl;
    private Long senderId;
    private Long receiverId;
    private ZonedDateTime requestDate;
}
