package com.backend.backenddbp.Chat.DTO;

import com.backend.backenddbp.Friendship.DTO.AmigoDTO;
import lombok.Data;

import java.util.Objects;

@Data
public class ChatMembersDTO {
    private Long userId;
    private String userFullName;
    private boolean isAdmin;
    private boolean isCreator;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMembersDTO menber = (ChatMembersDTO) o;
        return Objects.equals(userId, menber.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

}
