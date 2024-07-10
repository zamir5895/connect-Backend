package dbp.connect.MultimediaMensaje.Domain;

import dbp.connect.Mensaje.Domain.Mensaje;
import dbp.connect.Tipo;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
public class MultimediaMensaje {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "mensaje_id")
    private Mensaje mensaje;
    private String url;
    private Tipo tipo;
    private ZonedDateTime fecha;
}
