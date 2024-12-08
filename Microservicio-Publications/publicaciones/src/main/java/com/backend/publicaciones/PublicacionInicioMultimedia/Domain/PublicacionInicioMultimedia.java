package com.backend.publicaciones.PublicacionInicioMultimedia.Domain;

import com.backend.publicaciones.PublicacionInicio.Domain.PublicacionInicio;
import com.backend.publicaciones.Tipo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class PublicacionInicioMultimedia {
    @Id
    private String id;
    private String contenidoUrl;
    private Tipo tipo;
    private ZonedDateTime fechaCreacion;
    @ManyToOne
    @JoinColumn(name="publicacionInicio_id")
    private PublicacionInicio publicacionInicio;



}
