package dbp.connect.Mensaje.Domain;

import dbp.connect.Chat.Domain.Chat;
import dbp.connect.Chat.Domain.ChatService;
import dbp.connect.Chat.Exceptions.ChatNotFound;
import dbp.connect.Chat.Infrastructure.ChatRepository;
import dbp.connect.Mensaje.DTOS.ContentDTO;
import dbp.connect.Mensaje.DTOS.DTOMensajePost;
import dbp.connect.Mensaje.DTOS.MensajeResponseDTO;
import dbp.connect.Mensaje.Infrastructure.MensajeRepository;
import dbp.connect.MultimediaMensaje.DTO.MensajeMultimediaDTO;
import dbp.connect.MultimediaMensaje.Domain.MultimediaMensaje;
import dbp.connect.MultimediaMensaje.Domain.MultimediaMensajeServicio;
import dbp.connect.MultimediaMensaje.Infrastructure.MultimediaMensajeRepositorio;
import dbp.connect.User.DTO.UserProfileDTO;
import dbp.connect.User.Domain.User;
import dbp.connect.User.Domain.UserService;
import dbp.connect.User.Exceptions.BadCredentialException;
import dbp.connect.User.Exceptions.UserException;
import dbp.connect.User.Infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MensajeServicio {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MensajeRepository mensajeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MultimediaMensajeServicio multimediaMensajeIndividualServicio;
    @Autowired
    private MultimediaMensajeRepositorio multimediaMensajeRepositorio;
    @Autowired
    private UserService userService;

    public MensajeResponseDTO sendMessage(DTOMensajePost mensaje) {
        User user = userRepository.findById(mensaje.getUserId()).orElseThrow(
                () -> new UsernameNotFoundException("Usuario no encontrado"));
        Chat chat = chatRepository.findById(mensaje.getChatId()).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));

        Mensaje newMessage = new Mensaje();
        newMessage.setAutor(user);
        newMessage.setChat(chat);
        newMessage.setCuerpo(mensaje.getContenido());
        newMessage.setStatus(StatusMensaje.ENVIADO);
        newMessage.setCuerpo(mensaje.getContenido());
        newMessage.setFecha_mensaje(ZonedDateTime.now(ZoneId.systemDefault()));
        for (MultipartFile file : mensaje.getMultimedia()) {
            MultimediaMensaje multimedia = multimediaMensajeIndividualServicio.saveMultimedia(file);
            multimedia.setMensaje(newMessage);
            multimediaMensajeRepositorio.save(multimedia);
            newMessage.getMultimediaMensaje().add(multimedia);
        }
        mensajeRepository.save(newMessage);
        return toDTOResponse(newMessage);
    }

    public void modificarMensajeContenido(Long chatId, ContentDTO contenido) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));

        for (Mensaje mensaje : chat.getMensajes()) {
            if (mensaje.getId().equals(contenido.getMensajeId())) {
                Mensaje mensajeI = mensajeRepository.findById(contenido.getMensajeId()).orElseThrow(
                        () -> new EntityNotFoundException("MensajeController no encontrado"));
                mensajeI.setCuerpo(contenido.getMensaje());
                mensajeRepository.save(mensajeI);
            }
        }
    }

    public void deleteMensajeById(Long chatId, Long id) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));

        for (Mensaje mensaje : chat.getMensajes()) {
            if (mensaje.getId().equals(id)) {
                mensajeRepository.deleteById(id);
                chat.removeMensaje(mensaje);
            }
        }
    }

    public Page<MensajeResponseDTO> obtenerTodosLosMensajesDeUnChat(Long chatId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                ()->new EntityNotFoundException("Chat no encontrado"));
        Page<Mensaje> mensajesPage = mensajeRepository.findByChatId(chatId, pageable);


        List<MensajeResponseDTO> mensajesDTO = mensajesPage.getContent().stream()
                .map(this::toDTOResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(mensajesDTO.stream().collect(Collectors.toList()), pageable, mensajesPage.getTotalElements());
    }

    public MensajeResponseDTO obtenerMensajePorchatIdYMensajeId(Long chatId, Long mensajeiD){

        Mensaje mensaje = mensajeRepository.findByChatIdAndId(chatId, mensajeiD)
                .orElseThrow(()->new EntityNotFoundException("No se encontro el mensaje para el chat especificado"));
        return toDTOResponse(mensaje);
    }
    public MensajeResponseDTO findMessageById(Long id){
        Mensaje mensaje = mensajeRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("Mensaje no encontrado"));
        return toDTOResponse(mensaje);
    }

    public void deleteMessageById(Long chatId, Long userId, Long id){
        Mensaje mensaje = mensajeRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("Mensaje no encontrado"));
        if(!mensaje.getChat().getId().equals(chatId)){
            throw new EntityNotFoundException("No se puede eliminar el mensaje");
        }
        if(!mensaje.getAutor().getId().equals(userId)){
            throw new EntityNotFoundException("No se puede eliminar el mensaje");
        }
        mensajeRepository.deleteById(id);
    }


    public MensajeResponseDTO updateStatus(Long chatId,Long MensajeId){
        Mensaje mensaje = mensajeRepository.findByChatIdAndId(chatId,MensajeId)
                .orElseThrow(()->new EntityNotFoundException("No se encontro el mensaje para el chat especificado"));
        mensaje.setStatus(StatusMensaje.VISTO);
        mensajeRepository.save(mensaje);
        return toDTOResponse(mensaje);
    }
    public void markMessageAsRead(Long chatId, Long mensajeId) throws ChatNotFound {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                ()->new EntityNotFoundException("Chat no encontrado"));
        if(!chat.getId().equals(chatId)){
            throw new ChatNotFound("Chat no encontrado");
        }
        Mensaje mensaje = mensajeRepository.findByChatIdAndId(chatId,mensajeId)
                .orElseThrow(()->
                        new EntityNotFoundException("No se encontro el mensaje para el chat especificado"));
        mensaje.setStatus(StatusMensaje.VISTO);
        mensajeRepository.save(mensaje);
    }


    public List<MensajeResponseDTO> getUnreadMessages(Long chatId, String token) throws BadCredentialException, UserException {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                ()->new EntityNotFoundException("Chat no encontrado"));
        UserProfileDTO userProfile = userService.finddUserProfile(token);
        User user = userRepository.findById(userProfile.getId()).orElseThrow(
                ()->new EntityNotFoundException("Usuario no encontrado"));
        if(!chat.getUsers().contains(user)){
            throw new EntityNotFoundException("Usuario no pertenece al chat");
        }
        List<Mensaje> mensajes = mensajeRepository.findByChatIdAndStatus(chatId, StatusMensaje.ENVIADO);
        return mensajes.stream()
                .map(this::toDTOResponse)
                .collect(Collectors.toList());
    }


    public List<MensajeResponseDTO> searchMessages(Long chatId, String query) {
        List<Mensaje> mensajes = mensajeRepository.findByChatIdAndCuerpoContainingIgnoreCase(chatId, query);
        return mensajes.stream()
                .map(this::toDTOResponse)
                .collect(Collectors.toList());
    }



    private MensajeResponseDTO toDTOResponse(Mensaje mensaje){
        MensajeResponseDTO mensajeResponseDTO = new MensajeResponseDTO();
        mensajeResponseDTO.setId(mensaje.getId());
        mensajeResponseDTO.setContenido(mensaje.getCuerpo());
        mensajeResponseDTO.setStatusMensaje(mensaje.getStatus());
        mensajeResponseDTO.setUsername(mensaje.getAutor().getUsername());
        mensajeResponseDTO.setChatId(mensaje.getChat().getId());
        mensajeResponseDTO.setFecha(mensaje.getFecha_mensaje());
        mensajeResponseDTO.setUserImage(mensaje.getAutor().getFotoUrl());
        for(MultimediaMensaje multimediaMensaje: mensaje.getMultimediaMensaje()){
            MensajeMultimediaDTO multimediaDTO = new MensajeMultimediaDTO();
            multimediaDTO.setId(multimediaMensaje.getId());
            multimediaDTO.setTipo(multimediaMensaje.getTipo());
            multimediaDTO.setUrl(multimediaMensaje.getUrl());
            mensajeResponseDTO.getMultimedia().add(multimediaDTO);
        }
        return mensajeResponseDTO;
    }



}
