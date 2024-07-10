package dbp.connect.Comentarios.Domain;

import dbp.connect.ComentariosMultimedia.Domain.ComentarioMultimedia;
import dbp.connect.PublicacionInicio.Domain.PublicacionInicio;
import dbp.connect.User.Domain.User;
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


    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH})
    @JoinColumn(name ="autorComentario_id")
    private User autorComentario;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comentario parent;

    @OneToMany(mappedBy = "parent", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH})
    private List<Comentario> replies;

    public void addCommentReplies(Comentario comment) {
        if(replies == null) {
            replies = new ArrayList<>();
        }
        replies.add(comment);
        comment.setParent(this);  //establecer el parent del comentario hijo

    }
    @ManyToOne
    @JoinColumn(name="publicacion_id",nullable = false)
    private PublicacionInicio publicacion;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comentario_multimedia_id")
    private ComentarioMultimedia comentarioMultimedia;

}
