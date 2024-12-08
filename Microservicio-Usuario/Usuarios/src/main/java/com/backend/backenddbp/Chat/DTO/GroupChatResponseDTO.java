package com.backend.backenddbp.Chat.DTO;

import com.backend.backenddbp.Mensaje.DTOS.MensajeResponseDTO;
import com.backend.backenddbp.Security.Auth.DTOS.AuthenticationResponseDTO;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class GroupChatResponseDTO {
    private Long chatId;
    private ZonedDateTime fechaCreacion;
    private ZonedDateTime ultimoMensaje;
    private List<AuthenticationResponseDTO> usuarios = new ArrayList<>();
    private List<MensajeResponseDTO> mensajes = new ArrayList<>();
    private Boolean isgroup = true;
    private String chatImage;
    private String chatName;
    private List<AuthenticationResponseDTO> admins = new ArrayList<>();
    private AuthenticationResponseDTO createdBy;
}
