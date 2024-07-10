package dbp.connect.MultimediaMensaje.DTO;

import dbp.connect.Tipo;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class MensajeMultimediaDTO {
    private String id;
    private String url;
    private Tipo tipo;
    private ZonedDateTime fecha;

}
