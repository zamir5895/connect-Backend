package com.backend.places.Favoritos.DTOS;

import lombok.Data;

@Data
public class ResponseFavoritosDTO {
    private Long id;
    private Long publicacionId;
    private Long usuarioId;

}
