package com.backend.backenddbp.FriendRequest.DTOS;

import com.backend.backenddbp.FriendRequest.Domain.FriendRequestStatus;
import lombok.Data;

@Data
public class Status {
    private FriendRequestStatus status;
    private Long friendshipRequestId;
    private Long senderId;
    private String senderName;
    private String receiverName;
    private Long receiverId;
    private String senderfoto;
    private String receiverfoto;
}
