package com.backend.backenddbp.Chat.Aplication;

import com.backend.backenddbp.Chat.DTO.*;
import com.backend.backenddbp.Chat.Domain.Chat;
import com.backend.backenddbp.Chat.Domain.ChatService;
import com.backend.backenddbp.Chat.Exceptions.ChatNotFound;
import com.backend.backenddbp.Chat.Exceptions.NotAllowedPermissionChat;
import com.backend.backenddbp.Friendship.Exceptions.NotFriendException;
import com.backend.backenddbp.Security.Auth.AuthService;
import com.backend.backenddbp.Security.Auth.DTOS.AuthenticationResponseDTO;
import com.backend.backenddbp.User.DTO.UserProfileDTO;
import com.backend.backenddbp.User.Domain.UserService;
import com.backend.backenddbp.User.Exceptions.BadCredentialException;
import com.backend.backenddbp.User.Exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/single")
    public ResponseEntity<?> createChat(@RequestBody SingleChatRequestDTO singleChatRequestDTO,
                                        @RequestHeader("Authorization") String token) {
        try {
            AuthenticationResponseDTO usuario = authService.autentificar(token);
            SIngleChatResponseDTO chat = chatService.createChat(usuario.getUserId(), singleChatRequestDTO.getUserId());
            return new ResponseEntity<>(chat, HttpStatus.CREATED);
        } catch (UserException  | NotFriendException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear el chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/group/data")
    public ResponseEntity<?> createGroupData(
            @RequestHeader("Authorization") String token,
            @RequestBody GroupChatRequestDTO groupChatRequestDTO) {
        try {
            GroupChatResponseDTO chat = chatService.createChatGroupData(token, groupChatRequestDTO);
            return new ResponseEntity<>(chat, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al crear el grupo de chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/group/file/{groupId}")
    public ResponseEntity<?> uploadGroupImage(
            @RequestHeader("Authorization") String token,
            @PathVariable("groupId") Long groupId,
            @RequestPart("file") MultipartFile image) {
        try {
            GroupChatResponseDTO updatedChat = chatService.addGroupImage(token, groupId, image);
            return new ResponseEntity<>(updatedChat, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al subir la imagen del grupo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/{chatId}")
    public ResponseEntity<?> getChat(@PathVariable Long chatId, @RequestHeader("Authorization") String token) {
        try {
            ChatResponseDTO chat = chatService.getChat(chatId, token);
            return new ResponseEntity<>(chat, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener el chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> findAllChatsOfUser(@RequestHeader("Authorization") String token) {
        try {
            List<ChatResponseDTO> chats = chatService.findAllChatByUserId(token);
            return new ResponseEntity<>(chats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener los chats del usuario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{chatId}/add/{userId}")
    public ResponseEntity<?> addToGroup(@PathVariable Long chatId, @PathVariable Long userId, @RequestHeader("Authorization") String token) {
        try {
            GroupChatResponseDTO chat = chatService.addUserToChat(chatId, userId, token);
            return new ResponseEntity<>(chat, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al añadir usuario al grupo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{chatId}/remove/{userId}")
    public ResponseEntity<?> removeFromGroup(@PathVariable Long chatId, @PathVariable Long userId, @RequestHeader("Authorization") String token) {
        try {
            UserProfileDTO user = userService.finddUserProfile(token);
            GroupChatResponseDTO chat = chatService.removeFromGroup(chatId, userId, user.getId());
            return new ResponseEntity<>(chat, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al remover usuario del grupo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable Long chatId, @RequestHeader("Authorization") String token) {
        try {
            System.out.println("Entramos a eliminar");
            chatService.deleteChat(chatId, token);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al eliminar el chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/{chatId}")
    public ResponseEntity<?> updateChat(@PathVariable Long chatId, @RequestParam String newName, @RequestHeader("Authorization") String token) {
        try {
            chatService.renameGroup(chatId, newName, token);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar el nombre del chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchChatsByName(@RequestParam String query, @RequestHeader("Authorization") String token) {
        try {
            Set<ChatResponseDTO> chats = chatService.searchChatsByName(query, token);
            return new ResponseEntity<>(chats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar chats", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{chatId}/members")
    public ResponseEntity<?> getChatMembers(@RequestHeader("Authorization") String token,  @PathVariable Long chatId) {
        try {
            Set<ChatMembersDTO> members = chatService.getChatMembers(chatId, token);
            return new ResponseEntity<>(members, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener los miembros del chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{chatId}/updateImage")
    public ResponseEntity<?> updateChatImage(@PathVariable Long chatId, @RequestParam("image") MultipartFile newImage, @RequestHeader("Authorization") String token) {
        try {
            chatService.updateChatImage(chatId, newImage);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar la imagen del chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{chatId}/leave")
    public ResponseEntity<?> leaveChat(@PathVariable Long chatId, @RequestHeader("Authorization") String token) {
        try {
            chatService.leaveChat(chatId, token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al salir del chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/status/{chatId}")
    public ResponseEntity<?> updateLasMessage(@PathVariable Long chatId) {
        try {
            chatService.updateLastMessagr(chatId);  // Sin requerir `message`
            System.out.println("Actualizando solo la fecha");
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar los mensajes", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
