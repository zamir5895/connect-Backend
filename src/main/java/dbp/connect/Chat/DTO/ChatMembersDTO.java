package dbp.connect.Chat.DTO;

import lombok.Data;

@Data
public class ChatMembersDTO {
    private Long chatId;
    private Long userId;
    private String chatName;
    private String chatImage;
    private boolean isGroup;
    private boolean isDeleted;
    private boolean isAdmin;

}
