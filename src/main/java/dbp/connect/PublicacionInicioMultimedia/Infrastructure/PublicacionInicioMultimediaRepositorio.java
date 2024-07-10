package dbp.connect.PublicacionInicioMultimedia.Infrastructure;

import dbp.connect.PublicacionInicioMultimedia.Domain.PublicacionInicioMultimedia;
import dbp.connect.PublicacionInicioMultimedia.Domain.PublicacionInicioMultimediaServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionInicioMultimediaRepositorio extends JpaRepository<PublicacionInicioMultimedia, String> {
    @Query("SELECT m FROM PublicacionInicioMultimedia m WHERE m.publicacionInicio.id = :publicacionId")
    List<PublicacionInicioMultimedia> findByPublicacionInicio(@Param("publicacionId") Long publicacionId);
}
