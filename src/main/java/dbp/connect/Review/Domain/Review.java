package dbp.connect.Review.Domain;

import dbp.connect.PublicacionAlojamiento.Domain.PublicacionAlojamiento;
import dbp.connect.User.Domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "autorR_id")
    private User autorR;

    @ManyToOne
    @JoinColumn(name = "publicacionAlojamiento_id")
    private PublicacionAlojamiento publicacionAlojamiento;

    @Column(name="calificacion")
    private Integer calificacion;
    @Column(name="comentario")
    private String comentario;
    @Column(name="fecha")
    private LocalDateTime fecha;
}
