package com.backend.places.PublicacionAlojamiento.DTOS;

import com.backend.places.Alojamiento.DTOS.AlojamientoRequest;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
public class PostPublicacionAlojamientoDTO {

    @NotEmpty(message = "Debe de tener un titulo")
    @Size(min=1, max = 200)
    private String titulo;


    private AlojamientoRequest alojamiento;

}
