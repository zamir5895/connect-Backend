package com.backend.backenddbp.Friendship.DTO;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Objects;

@Data
public class AmigosDTO {
    private Long amigoId;
    private String nombreCompleto;
    private String apellidoCompleto;
    private String fotoPerfilUrl;
    private String userName;
    private ZonedDateTime fechaAmistad;
    private Long amistadId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AmigosDTO amigoDTO = (AmigosDTO) o;
        return Objects.equals(amigoId, amigoDTO.getAmigoId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(amigoId);
    }
}
