package dbp.connect.Notificaciones.DTOS;

import dbp.connect.Notificaciones.Domain.NotificationType;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class NotificacionResponse {
    private Long id;
    private NotificationType type;
    private String message;
    private ZonedDateTime date;
    private Long usuarioId;
    private Long relatedEntityId;
}
