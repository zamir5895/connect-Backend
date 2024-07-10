package dbp.connect.FriendRequest.DTOS;

import dbp.connect.FriendRequest.Domain.FriendRequestStatus;
import lombok.Data;

@Data
public class Status {
    private FriendRequestStatus status;
    private Long friendshipRequestId;
}
