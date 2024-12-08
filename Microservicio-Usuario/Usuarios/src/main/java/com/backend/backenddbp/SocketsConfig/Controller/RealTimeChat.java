package com.backend.backenddbp.SocketsConfig.Controller;


import com.backend.backenddbp.Chat.Domain.Chat;
import com.backend.backenddbp.Chat.Domain.ChatService;
import com.backend.backenddbp.Chat.Infrastructure.ChatRepository;
import com.backend.backenddbp.Mensaje.DTOS.DTOMensajePost;
import com.backend.backenddbp.Mensaje.DTOS.MensajeResponseDTO;
import com.backend.backenddbp.Mensaje.Domain.Mensaje;
import com.backend.backenddbp.Mensaje.Domain.MensajeServicio;
import com.backend.backenddbp.Mensaje.Infrastructure.MensajeRepository;
import com.backend.backenddbp.MultimediaMensaje.DTO.MensajeMultimediaDTO;
import com.backend.backenddbp.MultimediaMensaje.Domain.MultimediaMensaje;
import com.backend.backenddbp.Security.Auth.AuthService;
import com.backend.backenddbp.SocketsConfig.DTO.DTOMessagePost;
import com.backend.backenddbp.SocketsConfig.DTO.DTOMessageResponse;
import com.backend.backenddbp.User.Domain.User;
import com.backend.backenddbp.User.Domain.UserService;
import com.backend.backenddbp.User.Exceptions.BadCredentialException;
import com.backend.backenddbp.User.Exceptions.UserException;
import com.backend.backenddbp.User.Infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Iterator;
import java.util.List;


@RestController
public class RealTimeChat {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private MensajeServicio messageService;

    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MensajeRepository mensajeRepository;

    @MessageMapping("/message")
    @SendTo("/group/public")
    public DTOMessageResponse receiveMessage(@Payload DTOMessagePost mensaje ) {

        System.out.println("receive message in public ---------- ");
        DTOMessageResponse message = convertDTO(mensaje);

        simpMessagingTemplate.convertAndSend("/group/"+mensaje.getChatId().toString(), message);

        return message;
    }

    @MessageMapping("/chat/{groupId}")
    public DTOMessageResponse sendToUser(@Payload DTOMessagePost mensaje,
                              @Header("Authorization") String jwt, @DestinationVariable String groupId) throws UserException, BadCredentialException, UserException {
        System.out.println("recived private message - - - - - "+ mensaje.getContenido());

        String email = authService.autentificarByToken(jwt);
        User user = userRepository.findByEmail(email).orElseThrow(()->new UserException("User not found"));
        System.out.println("userr private message - - - - - - "+user);
        mensaje.setUserId(user.getId());

        Chat chat=chatRepository.findChatById(mensaje.getChatId());

        DTOMessageResponse createdMessage = convertDTO(mensaje);

        User reciverUser=reciver(chat, user);

        simpMessagingTemplate.convertAndSendToUser(groupId, "/private", createdMessage);

        return createdMessage;
    }

    public User reciver(Chat chat,User reqUser) {
        Iterator<User> iterator = chat.getUsers().iterator();

        User user1 = iterator.next();
        User user2 = iterator.next();

        if(user1.getId().equals(reqUser.getId())){
            return user2;
        }
        return user1;
    }
    private DTOMessageResponse convertDTO(DTOMessagePost dto){
        DTOMessageResponse response = new DTOMessageResponse();
        Mensaje mensaje = mensajeRepository.findById(dto.getMessageId()).orElseThrow(()->new EntityNotFoundException("Mensaje no encontrado"));
        response.setMensajeId(dto.getMessageId());
        response.setContenido(dto.getContenido());
        response.setChatId(mensaje.getChat().getId());
        response.setFullName(mensaje.getAutor().getPrimerNombre() + " " + mensaje.getAutor().getPrimerApellido() + " " + mensaje.getAutor().getSegundoApellido());
        response.setFecha(mensaje.getFecha_mensaje());
        response.setStatusMensaje(mensaje.getStatus());
        response.setUserId(mensaje.getAutor().getId());

        return response;
    }





}