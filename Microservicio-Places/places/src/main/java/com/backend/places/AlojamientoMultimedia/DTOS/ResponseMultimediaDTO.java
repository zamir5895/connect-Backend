package com.backend.places.AlojamientoMultimedia.DTOS;

import com.backend.places.Tipo;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ResponseMultimediaDTO {
    private String id;
    private String url_contenido;
    private Tipo tipo;
    private ZonedDateTime fechaCreacion;
}
