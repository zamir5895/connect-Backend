package com.backend.backenddbp.Mensaje.Aplication;

import com.backend.backenddbp.Chat.Exceptions.ChatNotFound;
import com.backend.backenddbp.Mensaje.DTOS.ContentDTO;
import com.backend.backenddbp.Mensaje.DTOS.DTOMensajePost;
import com.backend.backenddbp.Mensaje.DTOS.MensajeResponseDTO;
import com.backend.backenddbp.Mensaje.Domain.MensajeServicio;

import com.backend.backenddbp.MultimediaMensaje.DTO.MensajeMultimediaDTO;
import com.backend.backenddbp.MultimediaMensaje.Domain.MultimediaMensajeServicio;
import com.backend.backenddbp.User.DTO.UserProfileDTO;
import com.backend.backenddbp.User.Domain.UserService;
import com.backend.backenddbp.User.Exceptions.BadCredentialException;
import com.backend.backenddbp.User.Exceptions.UserException;
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

    @PostMapping(value = "/create")
    public ResponseEntity<?> createMensaje(@RequestBody DTOMensajePost mensaje) {
        try {

            MensajeResponseDTO result = mensajeServicio.sendMessage(mensaje);
            return ResponseEntity.created(new URI("/api/mensajes/" + result.getId())).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el mensaje");
        }
    }

    @PatchMapping("/{chatId}/modificar")
    public ResponseEntity<?> updateMensaje(@PathVariable Long chatId, @RequestBody ContentDTO contenido) {
        try {
            mensajeServicio.modificarMensajeContenido(chatId, contenido);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar el mensaje");
        }
    }

    @DeleteMapping("/{chatId}/mensajes/{mensajeId}")
    public ResponseEntity<?> deleteMensaje(@PathVariable Long chatId, @PathVariable Long mensajeId) {
        try {
            mensajeServicio.deleteMensajeById(chatId, mensajeId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el mensaje");
        }
    }

    @GetMapping("/{chatId}/mensajes")
    public ResponseEntity<?> getAllMensajes(@PathVariable Long chatId, @RequestParam int page, @RequestParam int size) {
        try {
            Page<MensajeResponseDTO> mensajes = mensajeServicio.obtenerTodosLosMensajesDeUnChat(chatId, page, size);
            return ResponseEntity.ok(mensajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener los mensajes del chat");
        }
    }

    @GetMapping("/{chatId}/{mensajeId}")
    public ResponseEntity<?> getMensaje(@PathVariable Long chatId, @PathVariable Long mensajeId) {
        try {
            MensajeResponseDTO mensaje = mensajeServicio.obtenerMensajePorchatIdYMensajeId(chatId, mensajeId);
            return ResponseEntity.ok(mensaje);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el mensaje");
        }
    }

    @DeleteMapping("/{chatId}/mensajes/{userId}/{mensajeId}")
    public ResponseEntity<?> deleteMensaje(@PathVariable Long chatId, @PathVariable Long userId, @PathVariable Long mensajeId) {
        try {
            mensajeServicio.deleteMessageById(chatId, userId, mensajeId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el mensaje del usuario");
        }
    }

    @PatchMapping("/{chatId}/mensajes/{mensajeId}")
    public ResponseEntity<?> updateMensaje(@PathVariable Long chatId, @PathVariable Long mensajeId) {
        try {
            MensajeResponseDTO mensajeResponseDTO = mensajeServicio.updateStatus(chatId, mensajeId);
            return ResponseEntity.ok(mensajeResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el estado del mensaje");
        }
    }

    @GetMapping("/{mensajeId}")
    public ResponseEntity<?> getMensaje(@PathVariable Long mensajeId) {
        try {
            MensajeResponseDTO mensaje = mensajeServicio.findMessageById(mensajeId);
            return ResponseEntity.ok(mensaje);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el mensaje");
        }
    }

    @PatchMapping("/{chatId}/mensajes/{mensajeId}/read")
    public ResponseEntity<?> markMessageAsRead(@PathVariable Long chatId, @PathVariable Long mensajeId) {
        try {
            mensajeServicio.markMessageAsRead(chatId, mensajeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al marcar el mensaje como leído");
        }
    }

    @GetMapping("/{chatId}/mensajes/unread")
    public ResponseEntity<?> getUnreadMessages(@PathVariable Long chatId, @RequestHeader("Authorization") String token) {
        try {
            List<MensajeResponseDTO> mensajes = mensajeServicio.getUnreadMessages(chatId, token);
            return ResponseEntity.ok(mensajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener mensajes no leídos");
        }
    }

    @GetMapping("/{chatId}/search")
    public ResponseEntity<?> searchMessages(@PathVariable Long chatId, @RequestParam String query) {
        try {
            List<MensajeResponseDTO> mensajes = mensajeServicio.searchMessages(chatId, query);
            return ResponseEntity.ok(mensajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar mensajes en el chat");
        }
    }

    @DeleteMapping("/{chatId}/{multimediaId}")
    public ResponseEntity<?> deleteMultimedia(@PathVariable Long chatId, @PathVariable String multimediaId) {
        try {
            multimediaMensajeServicio.eliminarArchivo(chatId, multimediaId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el archivo multimedia");
        }
    }

    @PutMapping(value = "/{chatId}/{multimediaId}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateMultimedia(@PathVariable Long chatId,
                                              @PathVariable String multimediaId,
                                              @RequestPart MultipartFile archivo) {
        try {
            multimediaMensajeServicio.modificarArchivo(chatId, multimediaId, archivo);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el archivo multimedia");
        }
    }

    @GetMapping("/{mensajeId}/multimedia/{multimediaId}")
    public ResponseEntity<?> getMultimedia(@PathVariable Long mensajeId, @PathVariable String multimediaId) {
        try {
            MensajeMultimediaDTO multimedia = multimediaMensajeServicio.obtenerMultimedia(mensajeId, multimediaId);
            return ResponseEntity.ok(multimedia);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el archivo multimedia");
        }
    }


}
