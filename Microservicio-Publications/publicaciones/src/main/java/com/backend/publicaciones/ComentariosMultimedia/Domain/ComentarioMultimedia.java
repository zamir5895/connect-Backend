package com.backend.publicaciones.ComentariosMultimedia.Domain;

import com.backend.publicaciones.Comentarios.Domain.Comentario;
import com.backend.publicaciones.Tipo;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@Entity
@EqualsAndHashCode
public class ComentarioMultimedia {
    @Id
    private String id;
    private String urlContenido;
    private Tipo tipo;
    private ZonedDateTime fechaCreacion;
    @ManyToOne
    @JoinColumn(name = "comentario_id")
    private Comentario comentario;

}
