package com.backend.backenddbp.Security.Utils;

import com.backend.backenddbp.Security.JWT.JwtService;
import com.backend.backenddbp.User.Domain.User;
import com.backend.backenddbp.User.Infrastructure.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@Component
public class AuthorizationUtils {

    @Autowired
    UserRepository usuarioRepository;
    @Autowired
    PasswordEncoder encoder;


    public String authenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        catch (ClassCastException e) {
            return null;
        }
    }


}