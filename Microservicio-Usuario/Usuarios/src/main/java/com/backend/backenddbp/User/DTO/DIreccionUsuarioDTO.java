package com.backend.backenddbp.User.DTO;

import lombok.Data;

@Data
public class    DIreccionUsuarioDTO {
    private String ciudad;
    private Long usuarioId;
    private String direccion;
    private String pais;
    private Double latitud;
    private Double longitud;

}
