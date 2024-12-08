package com.backend.places.Review.DTOS;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.ZonedDateTime;


@Data
public class ResponseReviewDTO {
    private Long reviewId;
    @NotEmpty
    private String contenido;
    @Range(min = 1, max = 5)
    @NotNull
    private Integer calificacion;
    private String autorFotoUrl;
    private String autorName;
    private Long publicacionId;
    private Long autorId;
    @NotNull
    private ZonedDateTime dateTime;

}
