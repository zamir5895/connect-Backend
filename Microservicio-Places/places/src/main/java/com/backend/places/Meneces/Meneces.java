package com.backend.places.Meneces;

import com.backend.places.Alojamiento.Domain.Alojamiento;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Meneces {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "alojamiento_id")
    private Alojamiento alojamiento;
}
