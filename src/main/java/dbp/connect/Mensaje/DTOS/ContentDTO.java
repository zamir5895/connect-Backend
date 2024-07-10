package dbp.connect.Mensaje.DTOS;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ContentDTO {
    Long mensajeId;
    Long autorId;
    String mensaje;
}
