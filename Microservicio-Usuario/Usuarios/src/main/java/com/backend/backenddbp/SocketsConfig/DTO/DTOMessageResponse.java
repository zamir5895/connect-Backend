package com.backend.backenddbp.SocketsConfig.DTO;

import com.backend.backenddbp.Mensaje.Domain.StatusMensaje;
import com.backend.backenddbp.MultimediaMensaje.DTO.MensajeMultimediaDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DTOMessageResponse {

    @NotNull
    private Long chatId;
    @NotNull
    @Size(min = 2, max=1000)
    private String contenido;
    private Long mensajeId;
    private StatusMensaje statusMensaje;
    private String userImage;
    private ZonedDateTime fecha;
    private Long userId;
    private String fullName;


}
