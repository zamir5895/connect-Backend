package dbp.connect.Chat.Domain;

import dbp.connect.Chat.DTO.ChatMembersDTO;
import dbp.connect.Chat.DTO.GroupChatRequestDTO;
import dbp.connect.Chat.Exceptions.ChatNotFound;
import dbp.connect.Chat.Exceptions.NotAllowedPermissionChat;
import dbp.connect.Chat.Infrastructure.ChatRepository;

import dbp.connect.Friendship.Domain.FriendshipServicio;
import dbp.connect.Friendship.Exceptions.NotFriendException;
import dbp.connect.Mensaje.Infrastructure.MensajeRepository;
import dbp.connect.MultimediaMensaje.Domain.MultimediaMensajeServicio;
import dbp.connect.S3.StorageService;
import dbp.connect.Security.Utils.AuthorizationUtils;
import dbp.connect.User.Domain.User;
import dbp.connect.User.Domain.UserService;

import dbp.connect.User.Exceptions.UserException;
import dbp.connect.User.Infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MensajeRepository mensajeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MultimediaMensajeServicio multimediaMensajeServicio;
    @Autowired
    private StorageService storageService;
    private static final AtomicLong counter = new AtomicLong(1);
    @Autowired
    private FriendshipServicio friendshipServicio;
    @Autowired
    private AuthorizationUtils authorizationUtils;


    public Chat createChat(Long reqUser,Long targUser ) throws UserException, NotFriendException {
        User user = userRepository.findById(reqUser).orElseThrow(() -> new UserException("Usuario no encontrado"));
        User targetUser = userRepository.findById(targUser).orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado"));
        if (!friendshipServicio.isFriend(reqUser, targUser)) {
            throw new NotFriendException("Los usuarios no son amigos");
        }

        Chat ischatExist = chatRepository.findSingleChatByUsersIds(user,targetUser);
        if(ischatExist != null){
            return ischatExist;
        }
        Chat chat = new Chat();
        chat.setId(generateId());
        chat.setCreatedBy(user);
        chat.getUsers().add(user);
        chat.getUsers().add(targetUser);
        chat.setGroup(false);
        return chat;
    }

    public Chat getChat(Long chatId){
        return chatRepository.findById(chatId).orElseThrow(()-> new EntityNotFoundException("Chat no encontrado"));
    }
    public List<Chat> findAllChatByUserId(Long userId) throws UserException {
        User user = userService.findUserById(userId);
        List<Chat> chats = chatRepository.findChatByUserId(userId);

        return chats;
    }
    public Chat createChatGroup(Long userId, GroupChatRequestDTO dtoRequest) throws Exception {
        User user = userService.findUserById(userId);
        Chat group = new Chat();
        group.setId(generateId());
        group.setCreatedBy(user);
        group.setChat_name(dtoRequest.getChatName());
        String token = storageService.subiralS3File(dtoRequest.getCharImage(),serializarChatId(group.getId()));
        String url = storageService.obtenerURL(token);
        group.setGroup(true);
        group.setChat_image(url);
        group.getAdmins().add(user);
        for(Long userid: dtoRequest.getUsersId()){
            User userL = userService.findUserById(userid);
            group.getUsers().add(userL);
        }
        return group;
    }
    public Chat addUserToChat(Long chatId, Long userId, Long reqUserId) throws Exception {
        Optional<Chat> opt = chatRepository.findById(chatId);
        User user = userService.findUserById(userId);
        User requestUser = userService.findUserById(reqUserId);
        if(opt.isPresent()){
            Chat chat = opt.get();
            if(chat.getAdmins().contains(requestUser)){
                chat.getUsers().add(user);
                return chat;
            }
            else{
                throw new NotAllowedPermissionChat("No tienes permisos para agregar a un usuario al chat");
            }
        }
        throw new Exception("No se pudo agregar al usuario al chat");
    }

    public void renameGroup(Long chatId, String name, Long userAdminId) throws NotAllowedPermissionChat, UserException {
        Optional<Chat> opt = chatRepository.findById(chatId);
        User user = userService.findUserById(userAdminId);
        if(opt.isPresent()){
            Chat chat = opt.get();
            if(chat.getAdmins().contains(user)){
                chat.setChat_name(name);
                chatRepository.save(chat);
            }
            else{
                throw new NotAllowedPermissionChat("No tienes permisos para renombrar el chat");
            }
        }
        throw new EntityNotFoundException("Chat no encontrado");
    }

    public Chat removeFromGroup(Long chatId, Long userId, Long userAdminId) throws NotAllowedPermissionChat, UserException, ChatNotFound {
        Optional<Chat> opt = chatRepository.findById(chatId);
        User user = userService.findUserById(userId);
        User userAdmin = userService.findUserById(userAdminId);
        if(opt.isPresent()) {
            Chat chat = opt.get();
            if (chat.getAdmins().contains(userAdmin)) {
                chat.getUsers().remove(user);
                return chatRepository.save(chat);
            }
            else if(chat.getUsers().contains(userAdmin)){
                if(user.getId().equals(userAdmin.getId())){
                    chat.getUsers().remove(user);
                    return chatRepository.save(chat);
                }
                else{
                    throw new NotAllowedPermissionChat("No tienes permisos para remover al usuario del chat");
                }
            }

            throw new NotAllowedPermissionChat("No tienes permisos para remover al usuario del chat");

        }
        throw new ChatNotFound("Chat no encontrado");
    }

    public void deleteChat(Long chatId, Long userId) throws NotAllowedPermissionChat, UserException, ChatNotFound {
        Optional<Chat> opt = chatRepository.findById(chatId);
        User user = userService.findUserById(userId);
        if(opt.isPresent()){
            Chat chat = opt.get();
            if(chat.getAdmins().contains(user)){
                chatRepository.delete(chat);
            }
            else{
                throw new NotAllowedPermissionChat("No tienes permisos para eliminar el chat");
            }
        }
        throw new ChatNotFound("Chat no encontrado");
    }
    public List<Chat> findAllChats(Long userId) {
        return chatRepository.findAllByUserId(userId);
    }
    public List<Chat> searchChatsByName(String name) {
        String email = authorizationUtils.authenticateUser();
        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new EntityNotFoundException("Usuario no encontrado"));
        return chatRepository.findChatsByNameAndUserId(name, user.getId());
    }
    public List<ChatMembersDTO> getChatMembers(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));
        List<ChatMembersDTO> members = new ArrayList<>();
        for (User user : chat.getUsers()) {
            ChatMembersDTO chatMembersDTO = new ChatMembersDTO();
            chatMembersDTO.setChatId(chatId);
            chatMembersDTO.setUserId(user.getId());
            if (chat.isGroup()) {
                chatMembersDTO.setAdmin(chat.getAdmins().contains(user));
                chatMembersDTO.setGroup(true);
                chatMembersDTO.setChatImage(chat.getChat_image());
            }
            chatMembersDTO.setDeleted(false);
            members.add(chatMembersDTO);
        }
        return members;
    }
    public void updateChatImage(Long chatId, MultipartFile image) throws Exception {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));
        String token = storageService.subiralS3File(image, serializarChatId(chatId));
        String url = storageService.obtenerURL(token);
        chat.setChat_image(url);
        chatRepository.save(chat);
    }
    public void leaveChat(Long chatId, String token) {
        String email = authorizationUtils.authenticateUser();
        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new EntityNotFoundException("Usuario no encontrado"));
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));
        chat.getUsers().remove(user);
        chatRepository.save(chat);
    }

    private String serializarChatId(Long imagenId){
        return "chat-" + imagenId;
    }


    public Long generateId() {
        return counter.getAndIncrement();
    }


}