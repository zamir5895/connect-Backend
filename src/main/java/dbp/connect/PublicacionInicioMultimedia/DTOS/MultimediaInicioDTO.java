package dbp.connect.PublicacionInicioMultimedia.DTOS;

import dbp.connect.Tipo;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class MultimediaInicioDTO {
    private String id;
    private String contenidoUrl;
    private Tipo tipo;
    private ZonedDateTime fechaCreacion;

}
