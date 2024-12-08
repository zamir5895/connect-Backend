package com.backend.places.Review.Domain;

import com.backend.places.PublicacionAlojamiento.Domain.PublicacionAlojamiento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "autoR", nullable = false)
    private Long autorR;

    @ManyToOne
    @JoinColumn(name = "publicacionAlojamiento_id")
    private PublicacionAlojamiento publicacionAlojamiento;

    @Column(name="calificacion")
    private Integer calificacion;
    @Column(name="comentario")
    private String comentario;
    @Column(name="fecha")
    private ZonedDateTime fecha;
}
