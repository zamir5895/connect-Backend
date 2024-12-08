package com.backend.backenddbp.User.Domain;

import com.backend.backenddbp.S3.StorageService;
import com.backend.backenddbp.Security.Auth.AuthService;
import com.backend.backenddbp.User.DTO.*;
import com.backend.backenddbp.User.Exceptions.BadCredentialException;
import com.backend.backenddbp.User.Exceptions.UserException;
import com.backend.backenddbp.User.Infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private StorageService storageService;

/*
    @Transactional
    public void addFriend(Long userId, Long  friendId) {
        User user = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();
        user.addFriend(friend);
        userRepository.save(user);
        userRepository.save(friend); // Guardar explícitamente el amigo también
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();
        user.removeFriend(friend);
        userRepository.save(user);
        userRepository.save(fri end); // Guardar explícitamente el amigo también
    }
    public List<UserSearchDTO> searchuser(Query query) {
    }

*/


    @Autowired
    private AuthService authService;

    public UserProfileDTO findUserProfileById(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(user.getId());
        userProfileDTO.setUsername(user.getUsername());
        userProfileDTO.setPNombre(user.getPrimerNombre());
        userProfileDTO.setSNombre(user.getSegundoNombre());
        userProfileDTO.setPApellido(user.getPrimerApellido());
        userProfileDTO.setSApellido(user.getSegundoApellido());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setFotoUrl(user.getFotoUrl());
        userProfileDTO.setCiudad(user.getCiudad());
        userProfileDTO.setPais(user.getPais());
        userProfileDTO.setFechaNacimiento(user.getFechaNacimiento());
        userProfileDTO.setDireccion(user.getDireccion());
        userProfileDTO.setTelefono(user.getTelefono());
        userProfileDTO.setGenero(user.getGenero());
        userProfileDTO.setDescripcion(user.getDescripcion());
        userProfileDTO.setFechaCreacion(user.getCreatedAt());
        return userProfileDTO;
    }

    public UserDetailsService userDetailsService() {
        return username -> userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findUserById(Long id) throws UserException {
        Optional<User>  opt= userRepository.findById(id);
        if(opt.isPresent()){
            return opt.get();
        }
        throw new UserException("User not found"+id);
    }

    public UserProfileDTO finddUserProfile(String jwt) throws UserException, BadCredentialException {
         String email =  authService.autentificarByToken(jwt);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(user.getId());
        userProfileDTO.setUsername(user.getUsername());
        userProfileDTO.setPNombre(user.getPrimerNombre());
        userProfileDTO.setSNombre(user.getSegundoNombre());
        userProfileDTO.setPApellido(user.getPrimerApellido());
        userProfileDTO.setSApellido(user.getSegundoApellido());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setFotoUrl(user.getFotoUrl());
        userProfileDTO.setCiudad(user.getCiudad());
        userProfileDTO.setPais(user.getPais());
        userProfileDTO.setFechaNacimiento(user.getFechaNacimiento());
        userProfileDTO.setDireccion(user.getDireccion());
        userProfileDTO.setTelefono(user.getTelefono());
        userProfileDTO.setGenero(user.getGenero());
        userProfileDTO.setDescripcion(user.getDescripcion());
        userProfileDTO.setFechaCreacion(user.getCreatedAt());
        userProfileDTO.setRol(user.getRole());
        return userProfileDTO;
    }
    public void UpdateUser(Long userId, UpdateUserNameAndProfileDTO update, MultipartFile foto) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setUsername(update.getUserName());
        if(foto != null) {
            String objectKey = storageService.subirAlS3File(foto, userId.toString());
            String fotoUrl = storageService.obtenerURL(objectKey);
            user.setFotoUrl(fotoUrl);
        }
        user.setDireccion(update.getDireccion());
        user.setDescripcion(update.getDescripcion());
        user.setFechaNacimiento(update.getFechaNacimiento());
        userRepository.save(user);
    }
    public List<UserDto> searchUser(String query, String token) {
        String email =  authService.autentificarByToken(token);
        User current = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<User> users = userRepository.searchUser(query);
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : users) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setFotoUrl(user.getFotoUrl());
            userDto.setFullName(user.getPrimerNombre()
                    + " " + user.getPrimerApellido() + " " + user.getSegundoApellido());
            userDtoList.add(userDto);
        }

        return userDtoList;
    }
    public informacionDelusuario obtenerInformacionUsuario(Long  userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        informacionDelusuario informacionDelusuario = new informacionDelusuario();
        informacionDelusuario.setId(user.getId());
        informacionDelusuario.setUserFullName(user.getPrimerNombre() +" " + user.getPrimerApellido() + " " + user.getSegundoApellido());
        informacionDelusuario.setFotoPerfil(user.getFotoUrl());
        informacionDelusuario.setEmail(user.getEmail());
        informacionDelusuario.setRol(user.getRole());
        informacionDelusuario.setDescripcion(user.getDescripcion());
        return informacionDelusuario;
    }
    public void deleteUser(String jwt) throws Exception {
        String email =  authService.autentificarByToken(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.delete(user);
    }
    public void updateInformacionPersonal(String jwt, UpdateUserDTO update) throws Exception {
        String email =  authService.autentificarByToken(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setPrimerNombre(update.getPrimerNombre());
        user.setSegundoNombre(update.getSegundoNombre());
        user.setPrimerApellido(update.getPrimerApellido());
        user.setSegundoApellido(update.getSegundoApellido());

        userRepository.save(user);
    }

    public DIreccionUsuarioDTO obtenerDireccion(Long userId){
        User user = userRepository.findById(userId ).orElseThrow(() -> new EntityNotFoundException("User not found"));
        DIreccionUsuarioDTO dto = new DIreccionUsuarioDTO();
        dto.setDireccion(user.getDireccion());
        dto.setLatitud(user.getLatitud());
        dto.setLongitud(user.getLongitud());
        dto.setPais(user.getPais());
        dto.setCiudad(user.getCiudad());
        dto.setUsuarioId(user.getId());
        return dto;
    }

    public void updateUbicacion(String token, DIreccionUsuarioDTO request){
        String email =  authService.autentificarByToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if(!request.getCiudad().isEmpty()){
            user.setCiudad(request.getCiudad());
        }
        if(!request.getPais().isEmpty()){
            user.setPais(request.getPais());
        }
        if(request.getLatitud()!=0L){
            user.setLatitud(request.getLatitud());
        }
        if(request.getLongitud()!=0L){
            user.setLongitud(request.getLongitud());
        }
        if(!request.getDireccion().isEmpty()){
            user.setDireccion(request.getDireccion());
        }
        userRepository.save(user);
    }



}
