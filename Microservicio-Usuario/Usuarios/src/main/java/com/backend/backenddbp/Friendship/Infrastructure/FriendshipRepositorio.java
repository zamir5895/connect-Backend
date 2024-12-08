package com.backend.backenddbp.Friendship.Infrastructure;

import com.backend.backenddbp.Friendship.Domain.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepositorio extends JpaRepository<Friendship, Long> {
    @Query("SELECT  DISTINCT f FROM Friendship f WHERE ((f.user.id = :userId AND f.blocked = false) OR (f.friend.id = :userId AND f.blocked = false))")
    Page<Friendship> findNonBlockedFriendsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT DISTINCT f FROM Friendship f WHERE ((f.user.id = :userId AND f.blocked = true) OR (f.friend.id = :userId AND f.blocked = true))")
    Page<Friendship> findBlockedFriendsByUserId(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT  DISTINCT f FROM Friendship f WHERE f.user.id = :userId OR f.friend.id = :userId")
    Page<Friendship> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT DISTINCT f FROM Friendship f WHERE (f.user.id = :userId OR f.friend.id = :userId) AND " +
            "(LOWER(CONCAT(f.user.primerNombre, ' ', f.user.segundoNombre, ' ', f.user.primerApellido, ' ', f.user.segundoApellido)) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(CONCAT(f.friend.primerNombre, ' ', f.friend.segundoNombre, ' ', f.friend.primerApellido, ' ', f.friend.segundoApellido)) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Friendship> findAmigosByName(@Param("userId") Long userId, @Param("query") String query);


    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM Friendship f " +
            "WHERE ((f.user.id = ?1 AND f.friend.id = ?2) " +
            "OR (f.user.id = ?2 AND f.friend.id = ?1)) " +
            "AND f.blocked = false")
    boolean areFriends(Long userId1, Long userId2);

    @Query("SELECT DISTINCT f FROM Friendship f WHERE (f.user.id = :userId OR f.friend.id = :userId) AND f.blocked = false")
    List<Friendship> findByUserIdAndBlockedIsFalse(@Param("userId") Long userId);


}


