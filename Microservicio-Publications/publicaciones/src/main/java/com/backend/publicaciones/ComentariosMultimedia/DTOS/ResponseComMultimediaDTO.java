package com.backend.publicaciones.ComentariosMultimedia.DTOS;

import com.backend.publicaciones.Tipo;
import lombok.Data;

@Data
public class ResponseComMultimediaDTO {
    private String id;
    private String url_contenido;
    private Tipo tipo;
}
