package com.backend.backenddbp.MultimediaMensaje.Domain;

import com.backend.backenddbp.Tipo;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
public class MultimediaMensaje {
    @Id
    private String id;
    private Long mensajeId;
    @Column(name="url", columnDefinition = "TEXT")
    private String url;
    private Tipo tipo;
    private ZonedDateTime fecha;
}
