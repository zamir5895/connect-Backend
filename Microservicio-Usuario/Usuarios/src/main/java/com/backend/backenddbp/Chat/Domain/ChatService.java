package com.backend.backenddbp.Chat.Domain;

import com.backend.backenddbp.Chat.DTO.*;
import com.backend.backenddbp.Chat.Exceptions.ChatNotFound;
import com.backend.backenddbp.Chat.Exceptions.NotAllowedPermissionChat;
import com.backend.backenddbp.Chat.Infrastructure.ChatRepository;

import com.backend.backenddbp.Friendship.Domain.FriendshipServicio;
import com.backend.backenddbp.Friendship.Exceptions.NotFriendException;
import com.backend.backenddbp.Mensaje.Domain.Mensaje;
import com.backend.backenddbp.Mensaje.Domain.MensajeServicio;
import com.backend.backenddbp.Mensaje.Infrastructure.MensajeRepository;
import com.backend.backenddbp.MultimediaMensaje.Domain.MultimediaMensajeServicio;
import com.backend.backenddbp.S3.StorageService;
import com.backend.backenddbp.Security.Auth.AuthService;
import com.backend.backenddbp.Security.JWT.JwtService;
import com.backend.backenddbp.Security.Utils.AuthorizationUtils;
import com.backend.backenddbp.User.Domain.User;
import com.backend.backenddbp.User.Domain.UserService;

import com.backend.backenddbp.User.Exceptions.UserException;
import com.backend.backenddbp.User.Infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorageService storageService;
    private static final AtomicLong counter = new AtomicLong(1);

    @Autowired
    private AuthorizationUtils authorizationUtils;
    @Autowired
    private AuthService authService;
    @Autowired
    private MensajeServicio mensajeServicio;




    public SIngleChatResponseDTO createChat(Long reqUser, Long targUser ) throws UserException, NotFriendException {
        User user = userRepository.findById(reqUser).orElseThrow(() -> new UserException("Usuario no encontrado"));
        User targetUser = userRepository.findById(targUser).orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado"));


        Chat ischatExist = chatRepository.findSingleChatByUsersIds(user,targetUser);
        if(ischatExist != null){
            return convertSiglesto(ischatExist.getId());
        }
        Chat chat = new Chat();
        chat.setId(generateId());
        chat.setCreatedBy(user);
        chat.getUsers().add(user);
        chat.getUsers().add(targetUser);
        chat.set_group(false);
        chat.setChat_image("");
        chat.setUpdatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
        chat.setFecha_creacion(ZonedDateTime.now(ZoneId.systemDefault()));
        chatRepository.save(chat);
        return convertSiglesto(chat.getId());
    }

    public ChatResponseDTO getChat(Long chatId, String token){
        String email = authService.autentificarByToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("Usuario no encontrado"));
        return convertchat(chatId, user.getId());
    }



    public List<ChatResponseDTO> findAllChatByUserId(String token) throws UserException {
        String email = authService.autentificarByToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() ->new EntityNotFoundException("Usuario no encontrado") );
        List<Chat> chats = chatRepository.findChatByUserId(user.getId());
        List<ChatResponseDTO> chatResponseDTOS = new ArrayList<>();
        for(Chat chat : chats){
            chatResponseDTOS.add(convertchat(chat.getId(), user.getId()));

        }
        return chatResponseDTOS;
    }


    public void deleteChat(Long chatId, String token) throws NotAllowedPermissionChat, UserException, ChatNotFound {

        String email = authService.autentificarByToken(token);
        Optional<Chat> opt = chatRepository.findById(chatId);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (opt.isPresent()) {
            Chat chat = opt.get();
            System.out.println("user entrante id " + user.getId());
            System.out.println("usuario creado id " + chat.getCreatedBy().getId());

            if (chat.getCreatedBy().getId().equals(user.getId()) && chat.is_group()) {
                chatRepository.deleteById(chat.getId());
                return;
            } else {
                throw new NotAllowedPermissionChat("No tienes permisos para eliminar el chat");
            }
        }

        throw new ChatNotFound("Chat no encontrado");
    }




    public GroupChatResponseDTO createChatGroupData(String token, GroupChatRequestDTO dtoRequest) throws Exception {
        String email = authService.autentificarByToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("Usuario no encontrado"));

        Chat group = new Chat();
        group.setCreatedBy(user);
        group.setChat_name(dtoRequest.getChatName());
        group.setFecha_creacion(ZonedDateTime.now(ZoneId.systemDefault()));
        group.setDeleted(false);
        group.setUpdatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
        group.set_group(true);
        group.getAdmins().add(user);

        for (Long userid : dtoRequest.getUsersId()) {
            User userL = userService.findUserById(userid);
            group.getUsers().add(userL);
        }
        group.getUsers().add(user);
        chatRepository.save(group);


        return convertGroupChat(group.getId());
    }

    public GroupChatResponseDTO addGroupImage(String token, Long chatId, MultipartFile imagen) throws Exception {
        String email = authService.autentificarByToken(token);
        Chat group = chatRepository.findChatById(chatId);
        if (imagen != null) {
            String key = storageService.subirAlS3File(imagen, serializarChatId(group.getId()));
            String url = storageService.obtenerURL(key);
            if (url != null) {
                group.setChat_image(url);
                chatRepository.save(group);

            }
        }

        return convertGroupChat(group.getId());
    }

    public GroupChatResponseDTO addUserToChat(Long chatId, Long userId, String token) throws NotAllowedPermissionChat {
        try {
            String email = authService.autentificarByToken(token);
            User admi = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException("Chat no encontrado"));
            User userRequest = userService.findUserById(userId);

            if (chat.getAdmins().contains(admi)) {
                chat.getUsers().add(userRequest);
                chatRepository.save(chat);
                return convertGroupChat(chat.getId());
            } else {
                throw new NotAllowedPermissionChat("No tienes permisos para agregar a un usuario al chat");
            }
        } catch (EntityNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
            throw new EntityNotFoundException(e.getMessage());
        } catch (NotAllowedPermissionChat e) {
            System.err.println("Error de permiso: " + e.getMessage());
            throw new NotAllowedPermissionChat(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException("No se pudo agregar al usuario al chat debido a un error inesperado");
        }
    }




    public GroupChatResponseDTO renameGroup(Long chatId, String name, String token) throws NotAllowedPermissionChat, UserException {
        Optional<Chat> opt = chatRepository.findById(chatId);
        String email = authService.autentificarByToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (opt.isPresent()) {
            Chat chat = opt.get();

            String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8);

            for (User us : chat.getAdmins()) {
                if (us.getId().equals(user.getId())) {
                    chat.setChat_name(decodedName);
                    chatRepository.save(chat);
                    return convertGroupChat(chat.getId());
                }
            }

            throw new NotAllowedPermissionChat("No tienes permisos para renombrar el chat");
        }

        throw new EntityNotFoundException("Chat no encontrado");
    }


    public GroupChatResponseDTO removeFromGroup(Long chatId, Long userId, Long userAdminId) throws NotAllowedPermissionChat, UserException, ChatNotFound {
        Optional<Chat> opt = chatRepository.findById(chatId);
        User user = userService.findUserById(userId);
        User userAdmin = userService.findUserById(userAdminId);
        if(opt.isPresent()) {
            Chat chat = opt.get();
            if (chat.getAdmins().contains(userAdmin)) {
                chat.getUsers().remove(user);
                 chatRepository.save(chat);
                 return convertGroupChat(chat.getId());
            }
            else if(chat.getUsers().contains(userAdmin)){
                if(user.getId().equals(userAdmin.getId())){
                    chat.getUsers().remove(user);
                     chatRepository.save(chat);
                     return convertGroupChat(chat.getId());
                }
                else{
                    throw new NotAllowedPermissionChat("No tienes permisos para remover al usuario del chat");
                }
            }

            throw new NotAllowedPermissionChat("No tienes permisos para remover al usuario del chat");

        }
        throw new ChatNotFound("Chat no encontrado");
    }




    public Set<ChatResponseDTO> searchChatsByName(String name, String token) {
        String normalizedQuery = name.trim().toLowerCase();

        String email = authService.autentificarByToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        List<Chat> chats = chatRepository.findChatsByNameAndUserId(normalizedQuery, user.getId());
        if(chats.size()>0){
            System.out.println("se encontro chats " + chats.size());
        }
        return chats.stream()
                .map(chat -> convertchat(chat.getId(), user.getId()))
                .collect(Collectors.toSet());
    }

    public ChatResponseDTO convertchat(Long chatId, Long userId) {
        Chat chat = chatRepository.findChatById(chatId);
        if (chat == null) {
            throw new EntityNotFoundException("Chat no encontrado");
        }

        ChatResponseDTO chatResponseDTO = new ChatResponseDTO();
        chatResponseDTO.setChatId(chatId);
        chatResponseDTO.setFechaCreacion(chat.getFecha_creacion());
        chatResponseDTO.setIsgroup(chat.is_group());
        chatResponseDTO.setUltimoMensaje(chat.getUpdatedAt());

        if (!chat.is_group()) {
            Long targetUserId = chat.getUsers().stream()
                    .filter(user -> !user.getId().equals(userId))
                    .map(User::getId)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            User targetUser = userRepository.findById(targetUserId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            chatResponseDTO.setChatName(targetUser.getPrimerNombre() + " " + targetUser.getPrimerApellido() + " " + targetUser.getSegundoApellido());
            chatResponseDTO.setChatImage(targetUser.getFotoUrl());

        } else {
            chatResponseDTO.setChatName(chat.getChat_name());
            chatResponseDTO.setChatImage(chat.getChat_image());
            chatResponseDTO.setCreatedBy(authService.convetDto(chat.getCreatedBy()));
        }

        chatResponseDTO.setUsuarios(
                chat.getUsers().stream()
                        .map(authService::convetDto)
                        .collect(Collectors.toList())
        );

        if (!chat.getMensajes().isEmpty()) {
            chatResponseDTO.setMensajes(
                    chat.getMensajes().stream()
                            .map(mensajeServicio::toDTOResponse)
                            .collect(Collectors.toList())
            );
        }

        return chatResponseDTO;
    }


    public Set<ChatMembersDTO> getChatMembers(Long chatId, String token) {
        String email = authService.autentificarByToken(token);
        User usuario = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));
        Set<ChatMembersDTO> members = new HashSet<>();

        Set<Long> adminIds = chat.getAdmins().stream().map(User::getId).collect(Collectors.toSet());

        if (!chat.is_group()) {
            for (User us : chat.getUsers()) {
                if (!us.getId().equals(usuario.getId())) {
                    ChatMembersDTO chatMembersDTO = new ChatMembersDTO();
                    chatMembersDTO.setUserFullName(us.getPrimerNombre() + " " + us.getPrimerApellido() + " " + us.getSegundoApellido());
                    chatMembersDTO.setAdmin(false);
                    chatMembersDTO.setUserId(us.getId());
                    members.add(chatMembersDTO);
                    break;
                }
            }
        } else {
            for (User us : chat.getUsers()) {
                ChatMembersDTO chatMembersDTO = new ChatMembersDTO();
                chatMembersDTO.setUserFullName(us.getPrimerNombre() + " " + us.getPrimerApellido() + " " + us.getSegundoApellido());
                chatMembersDTO.setUserId(us.getId());
                if(us.getId().equals(chat.getCreatedBy().getId())){
                    chatMembersDTO.setCreator(true);
                }else{
                    chatMembersDTO.setCreator(false);
                }
                chatMembersDTO.setAdmin(adminIds.contains(us.getId()));
                members.add(chatMembersDTO);

            }
        }

        return members;
    }


    public GroupChatResponseDTO updateChatImage(Long chatId, MultipartFile image) throws Exception {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));
        String token = storageService.subirAlS3File(image, serializarChatId(chatId));
        String url = storageService.obtenerURL(token);
        chat.setChat_image(url);
        chatRepository.save(chat);
        return convertGroupChat(chat.getId());
    }
    public void leaveChat(Long chatId, String token) {
        String email = authorizationUtils.authenticateUser();
        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new EntityNotFoundException("Usuario no encontrado"));
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));
        for(User us: chat.getAdmins()) {
            if(us.getId().equals(user.getId()) && chat.getAdmins().size()==1) {
                chatRepository.deleteById(chatId);
            }
        }
        chat.getUsers().remove(user);
        chatRepository.save(chat);
    }

    private String serializarChatId(Long imagenId){
        return "chatImage" + imagenId;
    }


    public Long generateId() {
        return counter.getAndIncrement();
    }


    public SIngleChatResponseDTO convertSiglesto (Long chatId){
        Chat chat = chatRepository.findById(chatId).orElseThrow(()->new EntityNotFoundException("Chat no encontrado"));
        SIngleChatResponseDTO responseDTO = new SIngleChatResponseDTO();
        responseDTO.setChatId(chatId);
        responseDTO.setFechaCreacion(chat.getFecha_creacion());
        responseDTO.setIsgroup(false);
        responseDTO.setUltimoMensaje(chat.getUpdatedAt());
        for(User user : chat.getUsers()){
            responseDTO.getUsuarios().add(authService.convetDto(user));
        }
        if(!chat.getMensajes().isEmpty()){
            for(Mensaje mensaje : chat.getMensajes()){
                responseDTO.getMensajes().add(mensajeServicio.toDTOResponse(mensaje));
            }
        }

        return responseDTO;
    }

    public GroupChatResponseDTO convertGroupChat(Long chatId){
        Chat chat = chatRepository.findById(chatId).orElseThrow(()->new EntityNotFoundException("Chat no encontrado"));
        GroupChatResponseDTO responseDTO = new GroupChatResponseDTO();
        responseDTO.setChatName(chat.getChat_name());
        responseDTO.setChatImage(chat.getChat_image());
        responseDTO.setCreatedBy(authService.convetDto(chat.getCreatedBy()));
        responseDTO.setIsgroup(true);
        responseDTO.setFechaCreacion(chat.getFecha_creacion());
        responseDTO.setUltimoMensaje(chat.getUpdatedAt());
        responseDTO.setChatId(chat.getId());
        for(User user : chat.getUsers()){
            responseDTO.getUsuarios().add(authService.convetDto(user));
        }
        if(!chat.getMensajes().isEmpty()){
            for(Mensaje mensaje : chat.getMensajes()){
                responseDTO.getMensajes().add(mensajeServicio.toDTOResponse(mensaje));
            }
        }
        responseDTO.setCreatedBy(authService.convetDto(chat.getCreatedBy()));
        return responseDTO;
    }

        public void updateLastMessagr(Long chatId){
            Chat chat = chatRepository.findById(chatId).orElseThrow(()->new EntityNotFoundException("Chat no encontrado"));
            chat.setUpdatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
            chatRepository.save(chat);

        }

}