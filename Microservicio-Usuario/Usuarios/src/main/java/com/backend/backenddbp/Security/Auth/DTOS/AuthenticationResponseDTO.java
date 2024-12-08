package com.backend.backenddbp.Security.Auth.DTOS;


import com.backend.backenddbp.User.Domain.Rol;
import lombok.Data;

@Data
public class AuthenticationResponseDTO {
    private  Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Rol role;
    private String fotoPerfil;
    private String userName;

}
