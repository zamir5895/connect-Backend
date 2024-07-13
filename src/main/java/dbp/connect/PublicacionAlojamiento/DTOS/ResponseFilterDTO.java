package dbp.connect.PublicacionAlojamiento.DTOS;

import dbp.connect.Alojamiento.DTOS.ResponseAlojamientoDTO;
import dbp.connect.Review.DTOS.ResponseReviewDTO;
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

}
