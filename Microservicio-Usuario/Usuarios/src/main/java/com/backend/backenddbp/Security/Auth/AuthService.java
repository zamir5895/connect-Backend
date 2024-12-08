package com.backend.backenddbp.Security.Auth;

import com.backend.backenddbp.S3.StorageService;
import com.backend.backenddbp.Security.Auth.DTOS.*;
import com.backend.backenddbp.Security.Auth.Exceptions.UnauthorizedException;
import com.backend.backenddbp.Security.JWT.JwtService;
import com.backend.backenddbp.Security.Utils.AuthorizationUtils;
import com.backend.backenddbp.User.Domain.User;
import com.backend.backenddbp.User.Infrastructure.UserRepository;
import com.backend.backenddbp.User.Domain.Rol;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class AuthService {
    final UserRepository userRepository;
    final JwtService jwtService;
    final PasswordEncoder passwordEncoder;
    final ModelMapper modelMapper;
    final StorageService storageService;
    final AuthorizationUtils authorizationUtils;
    @Autowired
    @Lazy
    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder,
                       ModelMapper modelMapper, StorageService storageService,
                       AuthorizationUtils authorizationUtils) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.storageService = storageService;
        this.authorizationUtils = authorizationUtils;
    }
    public AuthJwtResponse login(AuthLoginRequest authLoginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(authLoginRequest.getEmail());
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        if(!passwordEncoder.matches(authLoginRequest.getPassword(), userOptional.get().getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        AuthJwtResponse response = new AuthJwtResponse();
        response.setToken(jwtService.generateToken(userOptional.get()));
        response.setUserId(userOptional.get().getId());
        response.setRole(userOptional.get().getRole());
        return response;
    }
    public  AuthenticationResponseDTO register(AuthRegisterRequest authRegisterRequest) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(authRegisterRequest.getEmail());
        if (userOptional.isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User newuser = mapear(authRegisterRequest);
        newuser.setPassword(passwordEncoder.encode(authRegisterRequest.getPassword()));
        newuser.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
        if(!authRegisterRequest.getRole().isEmpty()){
            if(authRegisterRequest.getRole().toUpperCase().equals("TRAVELER")){
                newuser.setRole(Rol.TRAVELER);}
            else if(authRegisterRequest.getRole().toUpperCase().equals("HOST")){
                newuser.setRole(Rol.HOST);}
        }
        newuser.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
        System.out.println(newuser);
        System.out.println(newuser.getRole());
        System.out.println(authRegisterRequest.getPrimerApellido());
        userRepository.save(newuser);
        User user = userRepository.findByEmail(newuser.getEmail()).get();
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO();
        responseDTO.setUserId(user.getId());
        responseDTO.setFotoPerfil(user.getFotoUrl());
        responseDTO.setFullName(user.getPrimerNombre() + " " + user.getPrimerApellido() + " " + user.getSegundoApellido());
        responseDTO.setUserName(user.getUsername());
        responseDTO.setEmail(newuser.getEmail());
        responseDTO.setRole(newuser.getRole());
        return responseDTO;


    }
    public boolean logout(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            throw new UnauthorizedException("Authorization header missing or invalid");
        }
        try{
            jwtService.invalidateToken();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public AuthenticationResponseDTO autentificar(String authorizationHeader) {
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        System.out.println("Token recibido depues de modificiar: " + token);
        String username = jwtService.extractUsername(token); // Lanza UnauthorizedException si el token es inválido

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized to perform this operation"));

        AuthenticationResponseDTO authenticationResponseDTO = new AuthenticationResponseDTO();
        authenticationResponseDTO.setFullName(user.getPrimerNombre() + " " + user.getSegundoNombre() +
                " " + user.getPrimerApellido() + " " + user.getSegundoApellido());
        authenticationResponseDTO.setFotoPerfil(user.getFotoUrl());
        authenticationResponseDTO.setUserId(user.getId());
        authenticationResponseDTO.setPhoneNumber(user.getTelefono());
        authenticationResponseDTO.setRole(user.getRole());
        authenticationResponseDTO.setEmail(user.getEmail());
        authenticationResponseDTO.setUserName(user.getUsername());

        return authenticationResponseDTO;
    }
    public String autentificarByToken(String authorizationHeader) {
        System.out.println("Token antes de modificaar: " + authorizationHeader);
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        System.out.println("Token recibido depues de modificiar: " + token);

        String username = jwtService.extractUsername(token);
        return username;
    }

    public void registerPersonalInformation(Long userId, PersonalInformationDTO dto){
        User user = userRepository.findById(userId).orElseThrow(()->new UnauthorizedException("No se existe tal usuario"));
        if(dto.getDireccion().isEmpty()){
            user.setDireccion("");
        }
        else{
            user.setDireccion(dto.getDireccion());
        }
        if(dto.getTelefono().isEmpty()){
            user.setTelefono("");
        }
        else{
            user.setTelefono(dto.getTelefono());
        }

        user.setFechaNacimiento(dto.getDateOfBirth());
        if(dto.getCiudad().isEmpty()){
            user.setCiudad("");
        }
        else{
            user.setCiudad(dto.getCiudad());
        }
        user.setPais(dto.getPais());
        user.setLatitud(dto.getLatitude());
        user.setLongitud(dto.getLongitude());
        user.setGenero(dto.getGender());
        userRepository.save(user);

    }

    public AuthenticationResponseDTO registrarProfile(Long userId, ProfileRequestDTO dto, MultipartFile photo) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("No se existe tal usuario"));

        if (photo != null) {
            try {
                String objectKey = storageService.subirAlS3File(photo, user.getId().toString());
                String fotoUrl = storageService.obtenerURL(objectKey);
                user.setFotoUrl(fotoUrl);
            } catch (Exception e) {
                throw new RuntimeException("Error al subir el archivo al S3: " + e.getMessage(), e);
            }
        }

        user.setDescripcion(dto.getDescripcion());
        userRepository.save(user);


        return convetDto(user);
    }

    public AuthenticationResponseDTO convetDto(User user){
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO();
        responseDTO.setFullName(user.getPrimerNombre() + " " + user.getPrimerApellido() + " " + user.getSegundoApellido());
        responseDTO.setUserId(user.getId());
        responseDTO.setFotoPerfil(user.getFotoUrl());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setRole(user.getRole());
        responseDTO.setPhoneNumber(user.getTelefono());
        responseDTO.setUserName(user.getUsername());
        return responseDTO;
    }




    public User mapear(AuthRegisterRequest authRegisterRequest) throws Exception {
        User user = new User();
        user.setPrimerNombre(authRegisterRequest.getPrimerNombre());
        if( authRegisterRequest.getSegundoNombre() == null ||authRegisterRequest.getSegundoNombre().isEmpty()){
            user.setSegundoNombre("");
        }else{
            user.setSegundoNombre(authRegisterRequest.getSegundoNombre());
        }
        System.out.println("segundo apellido " + authRegisterRequest.getPrimerApellido());
        user.setPrimerApellido(authRegisterRequest.getPrimerApellido());
        user.setSegundoApellido(authRegisterRequest.getSegundoApellido());
        user.setUsername(authRegisterRequest.getUserName());
        user.setEdad(authRegisterRequest.getEdad());
        user.setEmail(authRegisterRequest.getEmail());
        return user;
    }
}
