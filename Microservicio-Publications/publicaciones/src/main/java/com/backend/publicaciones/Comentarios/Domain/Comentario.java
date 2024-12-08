package com.backend.publicaciones.Comentarios.Domain;

import com.backend.publicaciones.ComentariosMultimedia.Domain.ComentarioMultimedia;
import com.backend.publicaciones.PublicacionInicio.Domain.PublicacionInicio;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
public class Comentario {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "message")
    private String message;
    @Column(name="likes")
    private Integer likes;
    @Column(name = "date")
    private ZonedDateTime date;

    private Long autorId;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comentario parent;

    @OneToMany(mappedBy = "parent", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH})
    private List<Comentario> replies = new ArrayList<>();

    public void addCommentReplies(Comentario comment) {
        if(replies == null) {
            replies = new ArrayList<>();
        }
        replies.add(comment);
        comment.setParent(this);

    }
    @ManyToOne
    @JoinColumn(name="publicacion_id",nullable = false)
    private PublicacionInicio publicacion;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comentario_multimedia_id")
    private ComentarioMultimedia comentarioMultimedia;

}
