package com.backend.backenddbp.Security.Auth;

import com.backend.backenddbp.Security.Auth.DTOS.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthJwtResponse> login(@RequestBody AuthLoginRequest authLoginRequest) {
        AuthJwtResponse authJwtResponse = authService.login(authLoginRequest);
        System.out.println("Login successful for user: " + authJwtResponse.getToken());
        return ResponseEntity.ok(authJwtResponse);
    }
//
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody AuthRegisterRequest authRegisterRequest) {
        try {
            System.out.println("Datos recibidos: " + authRegisterRequest);

            return ResponseEntity.ok(authService.register(authRegisterRequest));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authService.logout(authorizationHeader)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(400).body("Authorization header missing or invalid");
        }
    }

    @GetMapping("/authentication")
    public ResponseEntity<AuthenticationResponseDTO> autentificar(@RequestHeader("Authorization") String authorizationHeader) {
        System.out.println("Token recibido: " + authorizationHeader);
        return ResponseEntity.ok(authService.autentificar(authorizationHeader));

    }

    @PatchMapping("/personalInformation/{userId}")
    public ResponseEntity<Void> registrarPersonalInformation(@PathVariable Long userId,
                                                             @RequestBody PersonalInformationDTO dto) {
        authService.registerPersonalInformation(userId, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/profile/{userId}")
    public ResponseEntity<AuthenticationResponseDTO> updateProfile(@PathVariable Long userId,
                                                                   @RequestPart("descripcion") String descripcion,
                                                                   @RequestPart("file") MultipartFile file) throws Exception {
        System.out.println("id recibido: " + userId);
        System.out.println("Descripción recibida: " + descripcion);
        System.out.println("Archivo recibido: " + file.getOriginalFilename()); // Verifica el nombre del archivo recibido

        // Verifica el tamaño y tipo del archivo
        System.out.println("Tamaño del archivo: " + file.getSize());
        System.out.println("Tipo del archivo: " + file.getContentType());

        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setDescripcion(descripcion);

        return ResponseEntity.ok(authService.registrarProfile(userId, dto, file));
    }



}
