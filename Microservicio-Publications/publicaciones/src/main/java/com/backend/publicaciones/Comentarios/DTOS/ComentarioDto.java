package com.backend.publicaciones.Comentarios.DTOS;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;



@Setter
@Getter
@Data
public class ComentarioDto {

    @NotEmpty(message = "El mensaje no pued eestar vacio")
    @Size(min=1, max = 600)
    private String message;
    @NotNull
    private Long autorId;
}
