package dbp.connect.Comentarios.DTOS;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.time.ZonedDateTime;


@Data
public class ComentarioRespuestaDTO {
    @NotNull
    private String autorNombreCompleto;
    @NotEmpty()
    @Size(min=0, max = 500)
    private String message;
    private String urlAutorImagen;
    private Integer likes;
    private String urlMulimedia;
    private ZonedDateTime fechaCreacion;
    private Long id;
    private String multimediaId;
}
