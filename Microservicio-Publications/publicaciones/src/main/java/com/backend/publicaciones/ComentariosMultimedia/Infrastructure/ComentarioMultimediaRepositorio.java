package com.backend.publicaciones.ComentariosMultimedia.Infrastructure;

import com.backend.publicaciones.ComentariosMultimedia.Domain.ComentarioMultimedia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComentarioMultimediaRepositorio extends CrudRepository<ComentarioMultimedia, String> {


    @Query("SELECT c FROM ComentarioMultimedia c WHERE c.comentario.id = :comentarioId")
    Optional<ComentarioMultimedia> findByComentarioId(@Param("comentarioId") Long comentarioId);

}
