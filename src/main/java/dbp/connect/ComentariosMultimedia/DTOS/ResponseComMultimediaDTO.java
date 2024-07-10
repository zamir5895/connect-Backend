package dbp.connect.ComentariosMultimedia.DTOS;

import dbp.connect.Tipo;
import lombok.Data;

@Data
public class ResponseComMultimediaDTO {
    private String id;
    private String url_contenido;
    private Tipo tipo;
}
