package dbp.connect.Chat.Infrastructure;

import dbp.connect.Chat.Domain.Chat;

import dbp.connect.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.isGroup=false AND :user MEMBER OF " +
            "c.users AND :reqUser MEMBER OF c.users")
    Chat findSingleChatByUsersIds(@Param("user") User user , @Param("reqUser")User reqUser);

    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId")
    List<Chat> findChatByUserId(@Param("userId") Long userId);
    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId")
    List<Chat> findAllByUserId(Long userId);
    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :usuarioId AND LOWER(c.chat_name) LIKE LOWER(CONCAT('%',:name,'%'))")
    List<Chat> findChatsByNameAndUserId(@Param("name") String name, @Param("usuarioId") Long usuarioId);


}
