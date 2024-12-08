package com.backend.places.Alojamiento.Domain;

import com.backend.places.AlojamientoMultimedia.Domain.AlojamientoMultimedia;
import com.backend.places.Meneces.Meneces;
import com.backend.places.PublicacionAlojamiento.Domain.PublicacionAlojamiento;
import com.backend.places.TipoMoneda;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Alojamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "estado")
    private Estado estado;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitud")
    private Double longitude;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "fechaPublicacion")
    private LocalDateTime fechaPublicacion;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "tipoMoneda")
    private TipoMoneda tipoMoneda;

    @Column(name = "capacidad")
    private Integer capacidad;
    @Column(name = "cantidad_habitaciones")
    private Integer cantidadHabitaciones = 0;

    @Column(name = "cantidad_camas")
    private Integer cantidadCamas = 0;

    @Column(name = "cantidad_banios")
    private Integer cantidadBanios = 0;

    @OneToMany(mappedBy = "alojamiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlojamientoMultimedia> alojamientoMultimedia = new ArrayList<>();

    @OneToOne(mappedBy = "alojamientoP")
    private PublicacionAlojamiento publicacionAlojamiento;
    @Column(name = "propietarioId")
    private Long propietarioId;

    @Column(name="tipo")
    private TipoH tipo;

    @Column(name="descripcion_larga")
    private String descripcionLarga;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meneces> meneces = new ArrayList<>();

}
