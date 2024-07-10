package dbp.connect.Likes.Domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dbp.connect.PublicacionInicio.Domain.PublicacionInicio;
import dbp.connect.User.Domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.security.SecureRandomParameters;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "likes")
public class Like implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "fecha_like")
    private ZonedDateTime fechaLike;
    @ManyToOne
    @JsonIgnoreProperties("likes")
    private PublicacionInicio publicacionInicio;
    @ManyToOne
    @JsonIgnoreProperties("usuarioLikes")
    private User usuarioLike;
}
