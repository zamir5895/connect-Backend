package com.backend.backenddbp.Security.Auth.DTOS;

import com.backend.backenddbp.User.Domain.Rol;
import lombok.Data;

@Data
public class AuthJwtResponse {
    private String token;
    private Long userId;
    private Rol role;
}
