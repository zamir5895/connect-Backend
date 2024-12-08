package com.backend.publicaciones.PublicacionInicio.DTOS;

import com.backend.publicaciones.PublicacionInicioMultimedia.DTOS.MultimediaInicioDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.LifecycleState;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
public class PublicacionInicioResponseDTO {
    private Long id;
    private String contenido;
    private Integer cantidadLikes;
    private Integer cantidadComentarios;
    private List<MultimediaInicioDTO> multimediaInicioDTO = new ArrayList<>();
    private ZonedDateTime fechaPublicacion;
    private Long autorId;
    private String autorNombre;
    private String autorFotoUrl;


}
