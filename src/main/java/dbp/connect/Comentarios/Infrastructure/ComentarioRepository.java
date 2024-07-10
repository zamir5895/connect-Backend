package dbp.connect.Comentarios.Infrastructure;

import dbp.connect.Comentarios.Domain.Comentario;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Transactional
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    Page<Comentario> findByPublicacionId(Long publicacionId, Pageable pageable);
    Page<Comentario> findByParentId(Long parentId, Pageable pageable);
}
