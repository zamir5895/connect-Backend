package com.backend.backenddbp.User.Aplication;
import com.backend.backenddbp.User.DTO.*;
import com.backend.backenddbp.User.Domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private  UserService userService;

    @GetMapping("/perfilMasInformacion")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            UserProfileDTO userProfileDTO = userService.finddUserProfile(token);
            return new ResponseEntity<>(userProfileDTO, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el perfil de usuario");
        }
    }

    @GetMapping("{query}")
    public ResponseEntity<?> searchUser(@RequestHeader("Authorization") String token, @PathVariable String query) {
        try {
            List<UserDto> userDtoList = userService.searchUser(query, token);
            return new ResponseEntity<>(userDtoList, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar usuarios");
        }
    }

    @GetMapping("/perfil/{userId}")
    public ResponseEntity<?> obtenerPequeñaInfo(@PathVariable  Long userId) {
        try {
            System.out.println(userId);
            informacionDelusuario informacion = userService.obtenerInformacionUsuario(userId);
            return new ResponseEntity<>(informacion, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            System.out.println("id del usuario "+userId);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener información del usuario");
        }
    }

    @GetMapping("/findById/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            UserProfileDTO userProfileDTO = userService.findUserProfileById(userId);
            return new ResponseEntity<>(userProfileDTO, HttpStatus.OK);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el usuario por ID");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
        try {
            userService.deleteUser(token);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el usuario");
        }
    }

    @PatchMapping("/profile/informacionPersonal")
    public ResponseEntity<?> updatePersonalInformation(@RequestHeader("Authorization") String token,
                                                       @RequestBody UpdateUserDTO update) {
        try {
            userService.updateInformacionPersonal(token, update);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la información personal");
        }
    }

    @GetMapping("/direccion/{userId}")
    public ResponseEntity<?> getDireccionInformation(@PathVariable Long userId) {
        try {
            DIreccionUsuarioDTO direccion = userService.obtenerDireccion(userId);
            return ResponseEntity.ok(direccion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener la dirección del usuario");
        }
    }

    @PatchMapping("/update/direccion/")
    public ResponseEntity<?> updateUserUbicacion(@RequestBody DIreccionUsuarioDTO direccion, @RequestHeader("Authorization") String token) {
        try{
            userService.updateUbicacion(token, direccion);
            return ResponseEntity.accepted().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener la dirección del usuario");

        }
    }


}
