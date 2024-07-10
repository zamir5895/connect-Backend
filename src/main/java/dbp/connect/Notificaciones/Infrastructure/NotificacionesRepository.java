package dbp.connect.Notificaciones.Infrastructure;

import dbp.connect.Notificaciones.Domain.Notificaciones;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface NotificacionesRepository extends JpaRepository<Notificaciones, Long> {
    Page<Notificaciones> findByUsuarioId(Long usuarioId, Pageable pageable);

    List<Notificaciones> findByUsuarioIdAndDateBetween(Long usuarioId, ZonedDateTime startDate, ZonedDateTime endDate);
    @Query("SELECT n FROM Notificaciones n WHERE n.usuario.id = :usuarioId AND n.message LIKE %:query%")
    List<Notificaciones> findByUsuarioIdAndQuery(@Param("usuarioId") Long usuarioId, @Param("query") String query);}

