package dbp.connect.Lugares.Domain;

import dbp.connect.User.Domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@Entity
public class Lugares {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String tittle;
    private String description;
    private Integer rating;
    private Double latitude;
    private Double longitude;
    private ZonedDateTime createdAt;
    @ManyToOne
    private User user;

}
