package com.backend.places.PublicacionAlojamiento.DTOS;

import com.backend.places.Alojamiento.DTOS.ResponseAlojamientoDTO;
import com.backend.places.Alojamiento.Domain.Estado;
import com.backend.places.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import com.backend.places.Review.DTOS.ResponseReviewDTO;
import com.backend.places.TipoMoneda;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
public class ResponsePublicacionAlojamiento {
    @NotNull
    private Long publicacionId;

    @NotNull
    @Size(min = 1, max = 200)
    private String titulo;
    private ResponseAlojamientoDTO responseAlojamientoDTO;
    @NotNull
    private ZonedDateTime fechaPublicacion;
    private Integer cantidadReviews;
    private Double promedioRating;

}
