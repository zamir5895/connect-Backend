package com.backend.publicaciones.PublicacionInicioMultimedia.DTOS;

import com.backend.publicaciones.Tipo;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class MultimediaInicioDTO {
    private String id;
    private String contenidoUrl;
    private Tipo tipo;
    private ZonedDateTime fechaCreacion;

}
