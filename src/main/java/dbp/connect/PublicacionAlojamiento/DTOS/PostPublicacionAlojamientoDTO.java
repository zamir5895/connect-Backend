package dbp.connect.PublicacionAlojamiento.DTOS;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Data
public class PostPublicacionAlojamientoDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;
    @NotEmpty(message = "Debe de tener un titulo")
    @Size(min=1, max = 200)
    private String titulo;
    @NotNull
    private Long autorId;
    @NotNull
    private Long alojamientoId;

}
