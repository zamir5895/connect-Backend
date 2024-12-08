package com.backend.backenddbp.Security.JWT;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.backend.backenddbp.Security.Auth.Exceptions.UnauthorizedException;
import com.backend.backenddbp.User.Domain.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {   

    @Value("{JWT_SECRET}")
    private String secret;

    @Autowired
    UserService userService;

    public static String extractUsername(String token) {
        try {
            return JWT.decode(token).getSubject();
        } catch (JWTDecodeException e) {
            throw new UnauthorizedException("Invalid or expired token.");
        }
    }

    public String extractClaim(String token, String claim) {
        return JWT.decode(token).getClaim(claim).asString();
    }

    public String generateToken(UserDetails data){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000 * 60 * 60 * 10);

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withSubject(data.getUsername())
                .withClaim("role", data.getAuthorities().toArray()[0].toString())
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(algorithm);
    }

    public void validateToken(String token, String userEmail) throws AuthenticationException {

        JWT.require(Algorithm.HMAC256(secret)).build().verify(token);

        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                token,
                userDetails.getAuthorities()
        );

        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
    }
    public void invalidateToken() {
        SecurityContextHolder.clearContext();
    }

}