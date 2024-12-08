package com.backend.backenddbp.Chat.Domain;


import com.backend.backenddbp.Mensaje.Domain.Mensaje;
import com.backend.backenddbp.User.Domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Chat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long Id;

    @Column(name= "fecha_creacion")
    private ZonedDateTime fecha_creacion;

    @Column(name = "chat_name")
    private String chat_name;

    @Column(name = "chat_image", columnDefinition = "TEXT")
    private String chat_image;

    @Column(name = "is_group")
    private boolean is_group ;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @ManyToMany
    private Set<User> admins = new HashSet<>();

    @JoinColumn(name = "created_by")
    @ManyToOne
    private User createdBy;

    @ManyToMany
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Mensaje> mensajes = new ArrayList<>();


    public Chat addMensaje(Mensaje mensaje) {
        this.mensajes.add(mensaje);
        mensaje.setChat(this);
        return this;
    }

    public Chat removeMensaje(Mensaje mensaje) {
        this.mensajes.remove(mensaje);
        mensaje.setChat(null);
        return this;
    }


    public Chat addUsuario(User usuario) {
        this.users.add(usuario);
        usuario.getChats().add(this);
        return this;
    }

    public Chat removeUsuario(User usuario) {
        this.users.remove(usuario);
        usuario.getChats().remove(this);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Chat chat = (Chat) o;
        if (chat.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), chat.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Transient
    public int getMessageCount() {
        return this.mensajes.size();
    }

}
