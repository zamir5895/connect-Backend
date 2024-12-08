package com.backend.publicaciones.Comentarios.Infrastructure;

import com.backend.publicaciones.Comentarios.Domain.Comentario;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Transactional
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    Page<Comentario> findByPublicacionId(Long publicacionId, Pageable pageable);
    Page<Comentario> findByParentId(Long parentId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comentario c WHERE c.publicacion.id = :publicacionId")
    Integer countByPublicacionId(@Param("publicacionId") Long publicacionId);

}
