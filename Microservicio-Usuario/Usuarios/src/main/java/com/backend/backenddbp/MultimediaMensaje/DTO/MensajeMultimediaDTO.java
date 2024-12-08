package com.backend.backenddbp.MultimediaMensaje.DTO;

import com.backend.backenddbp.Tipo;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class MensajeMultimediaDTO {
    private String id;
    private String url;
    private Tipo tipo;
    private ZonedDateTime fecha;
    private Long mensajeId;
}
