package com.backend.backenddbp.FriendRequest.Domain;

import com.backend.backenddbp.User.Domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@Entity
public class FriendshipRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;
    @JoinColumn(name="requestDate")
    private ZonedDateTime requestDate;
    @Column(name="status")
    private FriendRequestStatus status;

}
