package com.backend.backenddbp.Friendship.DTO;

import lombok.Data;

@Data
public class AmigoPersonalizado {
    private Long amistadId;
    private String imagenUrl;
    private Long usuarioId;
    private String userName;
}
