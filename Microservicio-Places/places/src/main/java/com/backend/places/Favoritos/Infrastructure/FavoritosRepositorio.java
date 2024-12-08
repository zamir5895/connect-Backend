package com.backend.places.Favoritos.Infrastructure;

import com.backend.places.Favoritos.Domain.Favoritos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoritosRepositorio extends JpaRepository <Favoritos, Long> {
    @Query("SELECT f FROM Favoritos f WHERE f.usuarioId = :idUsuario")
    Page<Favoritos> findByUsuarioId(@Param ("idUsuario")Long idUsuario, Pageable pageable);
    @Query("SELECT f FROM Favoritos f WHERE f.usuarioId =:idUsuario AND f.publicacionAlojamiento.id = :publicacionId")
    Favoritos findByUsuarioIdAndPublicacionId(@Param("idUsuario") Long idUsuario, @Param("publicacionId") Long publicacionId);
    Long countByUsuarioId(Long usuarioId);
}
