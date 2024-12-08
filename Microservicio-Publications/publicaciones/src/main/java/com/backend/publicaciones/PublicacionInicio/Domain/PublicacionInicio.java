package com.backend.publicaciones.PublicacionInicio.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.backend.publicaciones.Comentarios.Domain.Comentario;

import com.backend.publicaciones.Likes.Domain.Like;
import com.backend.publicaciones.PublicacionInicioMultimedia.Domain.PublicacionInicioMultimedia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PublicacionInicio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JoinColumn(name="fechaPublicacion")
    private ZonedDateTime fechaPublicacion;
    @JoinColumn(name = "cantidadLikes")
    private Integer cantidadLikes;
    @JoinColumn(name = "cantidadComentarios")
    private Integer cantidadComentarios;

    @OneToMany(mappedBy = "publicacionInicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Like> likes = new ArrayList<>();
    private Long autorP;
    @OneToMany(mappedBy = "publicacionInicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PublicacionInicioMultimedia> publicacionMultimedia = new ArrayList<>();
    private String cuerpo;
    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comentario> comentarios = new HashSet<>();

}
