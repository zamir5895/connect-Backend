package dbp.connect.AlojamientoMultimedia.Infrastructure;

import dbp.connect.Alojamiento.Domain.Alojamiento;
import dbp.connect.AlojamientoMultimedia.Domain.AlojamientoMultimedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlojamientoMultimediaRepositorio extends JpaRepository<AlojamientoMultimedia, String> {
    Page<AlojamientoMultimedia> findByAlojamiento_Id(Long propietarioId, Pageable pageable);
    Optional<AlojamientoMultimedia> findById(String id);
}
