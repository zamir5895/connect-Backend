package com.backend.backenddbp.Chat.Infrastructure;

import com.backend.backenddbp.Chat.Domain.Chat;

import com.backend.backenddbp.User.Domain.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.is_group=false AND :user MEMBER OF " +
            "c.users AND :reqUser MEMBER OF c.users")
    Chat findSingleChatByUsersIds(@Param("user") User user , @Param("reqUser")User reqUser);

    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId")
    List<Chat> findChatByUserId(@Param("userId") Long userId);
    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId")
    List<Chat> findAllByUserId(Long userId);

    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :usuarioId " +
            "AND (LOWER(TRIM(c.chat_name)) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR EXISTS (SELECT u2 FROM c.users u2 WHERE u2.id <> :usuarioId AND " +
            "(LOWER(TRIM(u2.primerNombre)) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(TRIM(u2.primerApellido)) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(TRIM(u2.segundoApellido)) LIKE LOWER(CONCAT('%', :name, '%')))))")
    List<Chat> findChatsByNameAndUserId(@Param("name") String name, @Param("usuarioId") Long usuarioId);


    Chat findChatById(@NotNull Long chatId);
}
