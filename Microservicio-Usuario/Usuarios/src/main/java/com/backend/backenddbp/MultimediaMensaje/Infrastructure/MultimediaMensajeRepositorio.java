package com.backend.backenddbp.MultimediaMensaje.Infrastructure;

import com.backend.backenddbp.MultimediaMensaje.Domain.MultimediaMensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MultimediaMensajeRepositorio extends JpaRepository<MultimediaMensaje, String> {

    @Query(value = "SELECT * FROM multimedia_mensaje WHERE mensaje_id = :mensajeId AND id = :imagenId", nativeQuery = true)
    Optional<MultimediaMensaje> findByMensajeIdAndImagenId(@Param("mensajeId") Long mensajeId, @Param("imagenId") String imagenId);

    @Query(value = "SELECT * FROM multimedia_mensaje WHERE mensaje_id = :mensajeId", nativeQuery = true)
    List<MultimediaMensaje> findAllByMensajeId(@Param("mensajeId") Long mensajeId);
}
