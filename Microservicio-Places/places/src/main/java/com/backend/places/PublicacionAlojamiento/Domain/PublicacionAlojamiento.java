package com.backend.places.PublicacionAlojamiento.Domain;

import com.backend.places.Alojamiento.Domain.Alojamiento;
import com.backend.places.Review.Domain.Review;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Setter
@Getter
@Entity
public class PublicacionAlojamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "alojamientop_id")
    private Alojamiento alojamientoP;
    @Column(name = "fecha")
    private ZonedDateTime fecha;
    @Column(name="titulo")
    private String titulo;
    @Column
    private Double promedioRating;
    @Column
    private Integer cantidadReseñas;
    @Column
    private Long idUsuario;
    @OneToMany(mappedBy = "publicacionAlojamiento",cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Review> reviews;

}
