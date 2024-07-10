package dbp.connect.AlojamientoMultimedia.Domain;

import dbp.connect.Alojamiento.Domain.Alojamiento;
import dbp.connect.Tipo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
public class AlojamientoMultimedia {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "alojamiento_Id")
    private Alojamiento alojamiento;
    private String urlContenido;
    private Tipo tipo;
    private ZonedDateTime fechaCreacion;
}
