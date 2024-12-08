package com.backend.backenddbp.Friendship.Domain;

import com.backend.backenddbp.FriendRequest.Domain.FriendshipRequest;
import com.backend.backenddbp.Friendship.DTO.AmigoDTO;
import com.backend.backenddbp.User.Domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    @Column(name="blocked")
    private boolean blocked;
    @Column(name = "fechaAmistad")
    private ZonedDateTime fechaAmistad;



}
