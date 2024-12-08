package com.backend.backenddbp.Chat.DTO;

import com.backend.backenddbp.Mensaje.DTOS.MensajeResponseDTO;
import com.backend.backenddbp.Security.Auth.DTOS.AuthenticationResponseDTO;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class ChatResponseDTO {
    private Long chatId;
    private ZonedDateTime fechaCreacion;
    private ZonedDateTime ultimoMensaje;
    private List<AuthenticationResponseDTO> usuarios = new ArrayList<>();
    private List<MensajeResponseDTO> mensajes = new ArrayList<>();
    private Boolean isgroup;
    private String chatImage;
    private String chatName;
    private List<AuthenticationResponseDTO> admins = new ArrayList<>();
    private AuthenticationResponseDTO createdBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatResponseDTO that = (ChatResponseDTO) o;
        return Objects.equals(chatId, that.chatId) &&
                Objects.equals(chatName, that.chatName) &&
                Objects.equals(fechaCreacion, that.fechaCreacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, chatName, fechaCreacion);
    }
}

