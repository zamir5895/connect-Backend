package dbp.connect.Friendship.Infrastructure;

import dbp.connect.Friendship.Domain.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepositorio extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f WHERE ((f.user.id = :userId AND f.blocked = false) OR (f.friend.id = :userId AND f.blocked = false))")
    Page<Friendship> findNonBlockedFriendsByUserId(@Param("userId") Long userId, Pageable pageable);
    @Query("SELECT f FROM Friendship f WHERE ((f.user.id = :userId AND f.blocked = true) OR (f.friend.id = :userId AND f.blocked = true))")
    Page<Friendship> findBlockedFriendsByUserId(@Param("userId") Long userId, Pageable pageable);
    @Query("SELECT f FROM Friendship f WHERE f.user.id = :userId OR f.friend.id = :userId")
    Page<Friendship> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
    @Query("SELECT f FROM Friendship f WHERE (f.user.id = :userId OR f.friend.id = :userId) AND " +
            "(LOWER(f.user.primerNombre) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(f.friend.primerNombre) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(f.user.segundoNombre) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(f.friend.segundoNombre) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Friendship> searchByUserIdAndFriendName(@Param("userId") Long userId, @Param("name") String name);
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friendship f WHERE (f.user.id = ?1 AND f.friend.id = ?2) OR (f.user.id = ?2 AND f.friend.id = ?1) AND f.blocked = false")
    boolean areFriends(Long userId1, Long userId2);
    List<Friendship> findByUserIdAndBlockedIsFalse(Long userId);
}


