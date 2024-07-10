package dbp.connect.Mensaje.Aplication;

import dbp.connect.Chat.Exceptions.ChatNotFound;
import dbp.connect.Mensaje.DTOS.ContentDTO;
import dbp.connect.Mensaje.DTOS.DTOMensajePost;
import dbp.connect.Mensaje.DTOS.MensajeResponseDTO;
import dbp.connect.Mensaje.Domain.MensajeServicio;

import dbp.connect.MultimediaMensaje.DTO.MensajeMultimediaDTO;
import dbp.connect.MultimediaMensaje.Domain.MultimediaMensajeServicio;
import dbp.connect.User.DTO.UserProfileDTO;
import dbp.connect.User.Domain.UserService;
import dbp.connect.User.Exceptions.BadCredentialException;
import dbp.connect.User.Exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@RestController
@RequestMapping("/api/messages")
public class MensajeController {
    @Autowired
    private MensajeServicio mensajeServicio;
    @Autowired
    private UserService userService;
    @Autowired
    private MultimediaMensajeServicio multimediaMensajeServicio;

    @PostMapping("/create")
    public ResponseEntity<MensajeResponseDTO> createMensaje(@RequestBody DTOMensajePost mensaje,
                                                            @RequestHeader ("Authorization") String token)
            throws URISyntaxException, BadCredentialException, UserException {
        UserProfileDTO profileDTO = userService.finddUserProfile(token);
        mensaje.setUserId(profileDTO.getId());
        MensajeResponseDTO result = mensajeServicio.sendMessage(mensaje);
        return ResponseEntity.created(new URI("/api/mensajes/" + result.getId())).build();
    }

    @PatchMapping("/{chatId}/modificar")
    public ResponseEntity<Void> updateMensaje(@PathVariable Long chatId, @RequestBody ContentDTO contenido) throws URISyntaxException {

        mensajeServicio.modificarMensajeContenido(chatId, contenido);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{chatId}/mensajes/{mensajeId}")
    public ResponseEntity<Void> deleteMensaje(@PathVariable Long chatId, @PathVariable Long mensajeId) {
        mensajeServicio.deleteMensajeById(chatId,mensajeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{charId}/mensajes")
    public ResponseEntity<Page<MensajeResponseDTO>> getAllMensajes(@PathVariable Long chatId,
                                              @RequestParam int page,
                                              @RequestParam int size) {
        return ResponseEntity.ok(mensajeServicio.obtenerTodosLosMensajesDeUnChat(chatId,page,size));

    }

    @GetMapping("/{charId}/{mensajeId}")
    public ResponseEntity<MensajeResponseDTO> getMensaje(@PathVariable Long charId,
                                                         @PathVariable Long mensajeId) {

        return ResponseEntity.ok(mensajeServicio.obtenerMensajePorchatIdYMensajeId(charId,mensajeId));
    }
    @DeleteMapping("/{chatId}/mensajes/{userId}/{mensajeId}")
    public ResponseEntity<Void> deleteMensaje(@PathVariable Long chatId, @PathVariable Long userId, @PathVariable Long mensajeId) {
        mensajeServicio.deleteMessageById(chatId,userId,mensajeId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{chatId}/mensajes/{mensajeId}")
    public ResponseEntity<MensajeResponseDTO> updateMensaje(@PathVariable Long chatId,
                                                            @PathVariable Long mensajeId) {
        MensajeResponseDTO mensajeResponseDTO = mensajeServicio.updateStatus(chatId,mensajeId);
        return ResponseEntity.ok(mensajeResponseDTO);
    }
    @GetMapping("/{mensajeId}")
    public ResponseEntity<MensajeResponseDTO> getMensaje(@PathVariable Long mensajeId) {
        return ResponseEntity.ok(mensajeServicio.findMessageById(mensajeId));
    }
    @PatchMapping("/{chatId}/mensajes/{mensajeId}/read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long chatId,
                                                  @PathVariable Long mensajeId) throws ChatNotFound {
        mensajeServicio.markMessageAsRead(chatId,mensajeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/{chatId}/mensajes/unread")
    public ResponseEntity<List<MensajeResponseDTO>> getUnreadMessages(@PathVariable Long chatId,
                                                                      @RequestHeader("Authorization") String token) throws BadCredentialException, UserException {
        List<MensajeResponseDTO> mensajes = mensajeServicio.getUnreadMessages(chatId,token);
        return ResponseEntity.ok(mensajes);
    }
    @GetMapping("/{chatId}/search")
    public ResponseEntity<List<MensajeResponseDTO>> searchMessages(@PathVariable Long chatId,
                                                                   @RequestParam String query) {
        List<MensajeResponseDTO> mensajes = mensajeServicio.searchMessages(chatId,query);
        return ResponseEntity.ok(mensajes);
    }
    @DeleteMapping("/{chatId}/mensajes/{multimediaId}")
    public ResponseEntity<Void> deleteMultimedia(@PathVariable Long chatId,
                                                 @PathVariable Long mensajeId,
                                                 @PathVariable String multimediaId) {
        multimediaMensajeServicio.eliminarArchivo(chatId,mensajeId,multimediaId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{chatId}/mensajes/{multimediaId}")
    public ResponseEntity<Void> updateMultimedia(@PathVariable Long chatId,
                                                 @PathVariable Long mensajeId,
                                                 @PathVariable String multimediaId,
                                                 @RequestParam MultipartFile archivo) throws Exception {
        multimediaMensajeServicio.modificarArchivo(chatId, mensajeId,multimediaId,archivo);
        return ResponseEntity.accepted().build();
    }
    @GetMapping("/{mensajeId}/multimedia/{multimediaId}")
    public ResponseEntity<MensajeMultimediaDTO> getMultimedia(@PathVariable Long mensajeId,
                                                              @PathVariable String multimediaId) {
        return ResponseEntity.ok(multimediaMensajeServicio.obtenerMultimedia(mensajeId,multimediaId));
    }

}
