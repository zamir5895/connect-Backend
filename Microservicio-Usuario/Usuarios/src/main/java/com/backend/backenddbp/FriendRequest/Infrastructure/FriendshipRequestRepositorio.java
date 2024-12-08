package com.backend.backenddbp.FriendRequest.Infrastructure;

import com.backend.backenddbp.FriendRequest.Domain.FriendshipRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRequestRepositorio extends JpaRepository<FriendshipRequest, Long> {
    @Query("SELECT fr FROM FriendshipRequest fr WHERE fr.sender.id = :senderId " +
            "AND fr.receiver.id = :receiverId")
    List<FriendshipRequest> findBySenderAndReceiver(@Param("senderId") Long senderId,
                                                    @Param("receiverId") Long receiverId);


    @Query("SELECT fr FROM FriendshipRequest fr WHERE (fr.sender.id = :senderId AND fr.receiver.id = :receiverId) " +
            "OR (fr.sender.id = :receiverId AND fr.receiver.id = :senderId)")
    Optional<FriendshipRequest> findBySenderAndReceiverOrReceiverAndSender(@Param("senderId") Long senderId,
                                                                           @Param("receiverId") Long receiverId);

}
