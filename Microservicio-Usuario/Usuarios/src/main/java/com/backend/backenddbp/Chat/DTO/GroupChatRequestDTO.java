package com.backend.backenddbp.Chat.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class GroupChatRequestDTO {
    private List<Long> usersId;
    private String chatName;
}
