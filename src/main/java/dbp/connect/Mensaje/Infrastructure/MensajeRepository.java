package dbp.connect.Mensaje.Infrastructure;

import dbp.connect.Mensaje.Domain.Mensaje;
import dbp.connect.Mensaje.Domain.StatusMensaje;
import dbp.connect.User.Domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    Page<Mensaje> findByChatId(Long chatId, Pageable pageable);
    Optional<Mensaje> findByAutor(User user);
    Optional<Mensaje> findByChatIdAndId(Long chatId, Long id);
    List<Mensaje> findByChatIdAndStatus(Long chatId, StatusMensaje status);
    List<Mensaje> findByChatIdAndCuerpoContainingIgnoreCase(Long chatId, String cuerpo);
}

