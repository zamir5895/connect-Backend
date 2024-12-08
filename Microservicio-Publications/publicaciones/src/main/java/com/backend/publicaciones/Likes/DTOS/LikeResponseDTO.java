package com.backend.publicaciones.Likes.DTOS;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class LikeResponseDTO {
    private Long id;
    private ZonedDateTime fechaLike;
    private Long publicacionInicioId;
    private Long usuarioLikeId;
}
