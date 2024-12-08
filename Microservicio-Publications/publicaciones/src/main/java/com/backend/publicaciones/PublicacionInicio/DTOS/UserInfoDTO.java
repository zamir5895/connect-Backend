package com.backend.publicaciones.PublicacionInicio.DTOS;

import lombok.Data;
@Data
public class UserInfoDTO {

    private Long id;
    private String userFullName;
    private String descripcion;
    private String fotoPerfil;
    private String email;
    private String  rol;
}