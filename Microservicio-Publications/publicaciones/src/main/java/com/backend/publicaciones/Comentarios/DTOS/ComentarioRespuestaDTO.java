package com.backend.publicaciones.Comentarios.DTOS;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.time.ZonedDateTime;


@Data
public class ComentarioRespuestaDTO {
    @Size(min=0, max = 500)
    private String message;
    private Integer likes;
    private String urlMulimedia;
    private ZonedDateTime fechaCreacion;
    private Long id;
    private String multimediaId;
    private Long autorId;
    private Long parentId;
    private String fotoUrl;
    private String nombre;
}
