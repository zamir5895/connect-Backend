package com.backend.publicaciones.PublicacionInicio.DTOS;


import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class PostInicioDTO {
    @Size(min=1, max=255)
    private String Cuerpo;
    @NotNull
    private Long autorPId;
}
 