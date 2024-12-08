package com.backend.backenddbp.SocketsConfig.DTO;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DTOMessagePost {
    private Long userId;
    @NotNull
    private Long chatId;
    @NotNull
    @Size(min = 2, max=1000)
    private String contenido;
    private Long messageId;
    private String userFullName;
    private List<String> multimediaUrl = new ArrayList<>();
    private Boolean leido = false;
}
