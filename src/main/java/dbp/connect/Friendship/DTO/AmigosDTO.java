package dbp.connect.Friendship.DTO;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Data
public class AmigosDTO {
    private Long amigoId;
    private String nombreCompleto;
    private String apellidoCompleto;
    private String fotoPerfilUrl;
    private String userName;
    private ZonedDateTime fechaAmistad;
    private Long amistadId;
}
