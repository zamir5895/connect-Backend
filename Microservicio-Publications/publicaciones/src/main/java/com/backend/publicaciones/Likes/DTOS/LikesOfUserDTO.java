package com.backend.publicaciones.Likes.DTOS;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class LikesOfUserDTO {
    private Long likeId;
    private Long publicacionInicioId;
    private ZonedDateTime fechaLike;
    @Size(max = 50)
    private String descripcion;
}
