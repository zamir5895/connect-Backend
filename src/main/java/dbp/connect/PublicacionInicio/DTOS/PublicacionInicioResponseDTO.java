package dbp.connect.PublicacionInicio.DTOS;

import dbp.connect.PublicacionInicioMultimedia.DTOS.MultimediaInicioDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.LifecycleState;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Data
public class PublicacionInicioResponseDTO {
    private Long id;
    private String contenido;
    private String fotPerfilUrl;
    @NotNull
    private String username;
    private Integer cantidadLikes;
    private Integer cantidadComentarios;
    private List<MultimediaInicioDTO> multimediaInicioDTO;
    private ZonedDateTime fechaPublicacion;

}
