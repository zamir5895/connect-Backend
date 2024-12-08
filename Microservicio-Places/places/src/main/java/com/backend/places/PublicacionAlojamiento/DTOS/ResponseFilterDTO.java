package com.backend.places.PublicacionAlojamiento.DTOS;

import com.backend.places.Alojamiento.DTOS.ResponseAlojamientoDTO;
import com.backend.places.Review.DTOS.ResponseReviewDTO;
import lombok.Data;

import java.util.List;

@Data
public class ResponseFilterDTO {
    private Long publicacionId;
    private String titulo;
    private Double promedioRating;
    private Integer cantidadReviews;
    private List<ResponseReviewDTO> reviews;
    private String fullName;
    private ResponseAlojamientoDTO alojamiento;
    private Long idUsuario;

}
