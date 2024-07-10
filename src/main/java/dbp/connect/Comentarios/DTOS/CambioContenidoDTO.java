package dbp.connect.Comentarios.DTOS;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambioContenidoDTO {
    @NotNull
    @Size(min = 1, max = 600)
    private String contenido;
}
