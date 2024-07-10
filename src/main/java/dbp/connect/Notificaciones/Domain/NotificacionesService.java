package dbp.connect.Notificaciones.Domain;

import dbp.connect.Notificaciones.DTOS.NotificacionResponse;
import dbp.connect.Notificaciones.Infrastructure.NotificacionesRepository;
import dbp.connect.User.Domain.User;
import dbp.connect.User.Infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificacionesService {

    @Autowired
    private NotificacionesRepository notificacionesRepository;
    @Autowired
    private UserRepository userRepository;

    @Async
    public void crearNotificacionPorComentario(Long usuarioId, Long publicacionId, String mensaje) {
        User user = userRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Notificaciones notificacion = new Notificaciones();
        notificacion.setType(NotificationType.COMMENT);
        notificacion.setMessage(mensaje);
        notificacion.setRelatedEntityId(publicacionId);
        notificacion.setDate(ZonedDateTime.now(ZoneId.systemDefault()));
        notificacion.setUsuario(user);
        notificacionesRepository.save(notificacion);
        user.addNotificacion(notificacion);
        userRepository.save(user);
    }
    @Async
    public void crearNotificacionPorRespuesta(Long usuarioId, Long publicacionId, String mensaje) {
        User user = userRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Notificaciones notificacion = new Notificaciones();
        notificacion.setType(NotificationType.REPLY);
        notificacion.setMessage(mensaje);
        notificacion.setRelatedEntityId(publicacionId);
        notificacion.setDate(ZonedDateTime.now(ZoneId.systemDefault()));
        notificacion.setUsuario(user);
        notificacionesRepository.save(notificacion);
        user.addNotificacion(notificacion);
        userRepository.save(user);
    }
    @Async
    public void crearNotificacionPorReview(Long usuarioId, Long publicacionId, String mensaje) {
        User user = userRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Notificaciones notificacion = new Notificaciones();
        notificacion.setType(NotificationType.REVIEW);
        notificacion.setMessage(mensaje);
        notificacion.setRelatedEntityId(publicacionId);
        notificacion.setDate(ZonedDateTime.now(ZoneId.systemDefault()));
        notificacion.setUsuario(user);
        notificacionesRepository.save(notificacion);
        user.addNotificacion(notificacion);
        userRepository.save(user);
    }


    @Async
    public void crearNotificacionPorLike(Long usuarioId, Long publicacionId, String mensaje){
        User user = userRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Notificaciones notificacion = new Notificaciones();
        notificacion.setType(NotificationType.LIKE);
        notificacion.setRelatedEntityId(publicacionId);
        notificacion.setDate(ZonedDateTime.now(ZoneId.systemDefault()));
        notificacion.setUsuario(user);
        notificacion.setMessage(mensaje);
        notificacionesRepository.save(notificacion);
        user.addNotificacion(notificacion);
        userRepository.save(user);
    }

    public Notificaciones createNotificacion(Notificaciones notificacion) {
        return notificacionesRepository.save(notificacion);
    }

    public NotificacionResponse getNotificacionById(Long id) {
        Optional<Notificaciones> notificacion = notificacionesRepository.findById(id);
        if(notificacion.isPresent()) {
            Notificaciones notificacionFound = notificacion.get();
            NotificacionResponse notificacionResponse = new NotificacionResponse();
            notificacionResponse.setId(notificacionFound.getId());
            notificacionResponse.setType(notificacionFound.getType());
            notificacionResponse.setMessage(notificacionFound.getMessage());
            notificacionResponse.setDate(notificacionFound.getDate());
            notificacionResponse.setUsuarioId(notificacionFound.getUsuario().getId());
            return notificacionResponse;

        } else {
            throw new RuntimeException("Notificacion not found");
        }
    }

    public void deleteNotificacion(Long id) {
        notificacionesRepository.deleteById(id);
    }

    public Page<NotificacionResponse> getNotificacionesByUsuarioId(Long usuarioId, Pageable pageable) {
        User user = userRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return notificacionesRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::convertToNotificacionResponse);
    }

    public List<NotificacionResponse> getNotificacionesByDateBetween(Long usuarioId, ZonedDateTime startDate, ZonedDateTime endDate) {
        return notificacionesRepository.findByUsuarioIdAndDateBetween(usuarioId, startDate, endDate)
                .stream()
                .map(this::convertToNotificacionResponse)
                .collect(Collectors.toList());
    }

    public List<NotificacionResponse> getNotificacionesByQuery(Long usuarioId, String query) {
        return notificacionesRepository.findByUsuarioIdAndQuery(usuarioId, query)
                .stream()
                .map(this::convertToNotificacionResponse)
                .collect(Collectors.toList());
    }

    private NotificacionResponse convertToNotificacionResponse(Notificaciones notificacion) {
        NotificacionResponse response = new NotificacionResponse();
        response.setId(notificacion.getId());
        response.setType(notificacion.getType());
        response.setMessage(notificacion.getMessage());
        response.setDate(notificacion.getDate());
        response.setUsuarioId(notificacion.getUsuario().getId());
        response.setRelatedEntityId(notificacion.getRelatedEntityId());
        return response;
    }



}
