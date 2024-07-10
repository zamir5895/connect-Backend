package dbp.connect.Notificaciones.Aplication;

import dbp.connect.Notificaciones.DTOS.NotificacionResponse;
import dbp.connect.Notificaciones.Domain.Notificaciones;
import dbp.connect.Notificaciones.Domain.NotificacionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionesController {

    @Autowired
    private NotificacionesService notificacionesService;
    @PostMapping
    public ResponseEntity<Notificaciones> createNotificacion(@RequestBody Notificaciones notificacion) {
        Notificaciones createdNotificacion = notificacionesService.createNotificacion(notificacion);
        return ResponseEntity.ok(createdNotificacion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponse> getNotificacionById(@PathVariable Long id) {
        NotificacionResponse notificacion = notificacionesService.getNotificacionById(id);
        return ResponseEntity.ok(notificacion);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificacion(@PathVariable Long id) {
        notificacionesService.deleteNotificacion(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<NotificacionResponse>> getNotificacionesByUsuarioId(@PathVariable Long usuarioId, Pageable pageable) {
        Page<NotificacionResponse> notificaciones = notificacionesService.getNotificacionesByUsuarioId(usuarioId, pageable);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/{usuarioId}/fecha")
    public ResponseEntity<List<NotificacionResponse>> getNotificacionesByDateBetween(@PathVariable Long usuarioId, @RequestParam ZonedDateTime startDate, @RequestParam ZonedDateTime endDate) {
        List<NotificacionResponse> notificaciones = notificacionesService.getNotificacionesByDateBetween(usuarioId, startDate, endDate);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/{usuarioId}/buscar")
    public ResponseEntity<List<NotificacionResponse>> getNotificacionesByQuery(@PathVariable Long usuarioId, @RequestParam String query) {
        List<NotificacionResponse> notificaciones = notificacionesService.getNotificacionesByQuery(usuarioId, query);
        return ResponseEntity.ok(notificaciones);
    }
}
