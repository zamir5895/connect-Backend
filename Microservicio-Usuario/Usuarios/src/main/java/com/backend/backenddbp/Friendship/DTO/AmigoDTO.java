package com.backend.backenddbp.Friendship.DTO;

import lombok.Data;

import java.util.Objects;

@Data
public class AmigoDTO {
    private Long amigoId;
    private String username;
    private String fotoUrl;
    private String fullName;
    private Long amistadId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AmigoDTO amigoDTO = (AmigoDTO) o;
        return Objects.equals(amigoId, amigoDTO.getAmigoId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(amigoId);
    }
}
