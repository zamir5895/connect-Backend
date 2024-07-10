package dbp.connect.ComentariosMultimedia.Infrastructure;

import dbp.connect.ComentariosMultimedia.Domain.ComentarioMultimedia;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioMultimediaRepositorio extends CrudRepository<ComentarioMultimedia, String> {
}
