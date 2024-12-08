package com.backend.places;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private String userFullName;
    private String descripcion;
    private String fotoPerfil;
    private String email;
    private String  rol;
}