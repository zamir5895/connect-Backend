package com.backend.places.Favoritos.Domain;

import com.backend.places.PublicacionAlojamiento.Domain.PublicacionAlojamiento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Favoritos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private PublicacionAlojamiento publicacionAlojamiento;
    private Long usuarioId;
}
