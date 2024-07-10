package dbp.connect.Review.DTOS;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.ZonedDateTime;

@Setter
@Getter
@Data
public class ResponseReviewDTO {
    @NotNull
    private String autorFullname;
    @NotEmpty
    private String contenido;
    @Range(min = 1, max = 5)
    @NotNull
    private Integer calificacion;
    private String autorFotoUrl;
    @NotNull
    private ZonedDateTime dateTime; //Suficientes atributos?

}
