package com.backend.backenddbp.Chat.DTO;


import com.backend.backenddbp.Mensaje.DTOS.MensajeResponseDTO;
import com.backend.backenddbp.Security.Auth.DTOS.AuthenticationResponseDTO;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class SIngleChatResponseDTO {
    private Long chatId;
    private ZonedDateTime fechaCreacion;
    private ZonedDateTime ultimoMensaje;
    private List<AuthenticationResponseDTO> usuarios = new ArrayList<>();
    private List<MensajeResponseDTO> mensajes = new ArrayList<>();
    private Boolean isgroup = false;
}
