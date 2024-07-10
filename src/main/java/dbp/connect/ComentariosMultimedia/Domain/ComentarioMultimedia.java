package dbp.connect.ComentariosMultimedia.Domain;

import dbp.connect.Alojamiento.Domain.Alojamiento;
import dbp.connect.Comentarios.Domain.Comentario;
import dbp.connect.Tipo;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@Entity
@EqualsAndHashCode
public class ComentarioMultimedia {
    @Id
    private String id;
    private String urlContenido;
    private Tipo tipo;
    private ZonedDateTime fechaCreacion;
    @ManyToOne
    @JoinColumn(name = "comentario_id")
    private Comentario comentario;

}
