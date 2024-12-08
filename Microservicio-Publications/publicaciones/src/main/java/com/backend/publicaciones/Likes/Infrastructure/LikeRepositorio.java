package com.backend.publicaciones.Likes.Infrastructure;

import com.backend.publicaciones.Likes.Domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepositorio extends JpaRepository<Like, Long> {

        List<Like> findByFechaLikeBetween(ZonedDateTime inicio, ZonedDateTime fin);

        @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.publicacionInicio.id = :publicacionId AND l.usuarioLike = :usuarioId")
        boolean existsByPublicacionIdAndUsuarioId(@Param("publicacionId") Long publicacionId, @Param("usuarioId") Long usuarioId);

        @Query("SELECT l.id FROM Like l WHERE l.publicacionInicio.id = :publicacionId AND l.usuarioLike = :usuarioId")
        List<Long> findIdsByPublicacionIdAndUsuarioId(@Param("publicacionId") Long publicacionId, @Param("usuarioId") Long usuarioId);
}
