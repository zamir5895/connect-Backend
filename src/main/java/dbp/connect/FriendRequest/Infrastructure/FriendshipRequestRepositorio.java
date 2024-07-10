package dbp.connect.FriendRequest.Infrastructure;

import dbp.connect.FriendRequest.Domain.FriendshipRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendshipRequestRepositorio extends JpaRepository<FriendshipRequest, Long> {
  @Query("SELECT fr FROM FriendshipRequest fr WHERE fr.sender.id = :senderId " +
          "AND fr.receiver.id = :receiverId")
  Optional<FriendshipRequest> findBySenderAndReceiver(@Param("senderId")
                                                      Long senderId,
                                                      @Param("receiverId")
                                                      Long receiverId);

}
