package com.backend.places.Review.DTOS;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;


@Data
public class ReviewRequest {
    @NotNull
    private Long autorId;
    @NotEmpty
    @Size(min=10, max=250)
    private String content;
    @NotNull
    private Long publicacionId;
    @NotNull
    @Range(min = 1, max = 5)
    private Integer rating;
}
