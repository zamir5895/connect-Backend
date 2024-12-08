package com.backend.backenddbp.Mensaje.Domain;

import com.backend.backenddbp.Chat.Domain.Chat;
import com.backend.backenddbp.MultimediaMensaje.Domain.MultimediaMensaje;
import com.backend.backenddbp.User.Domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="chat_id")
    private Chat chat;
    @ManyToOne
    @JoinColumn(name="autor_id")
    private User autor;
    @Column(name="cuerpo")
    private String cuerpo;
    @Column(name="status")
    private StatusMensaje status;
    @Column(name="fecha_mensaje")
    private ZonedDateTime fecha_mensaje;


}
