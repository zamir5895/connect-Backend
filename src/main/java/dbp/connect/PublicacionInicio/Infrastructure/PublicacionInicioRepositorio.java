package dbp.connect.PublicacionInicio.Infrastructure;

import dbp.connect.PublicacionInicio.Domain.PublicacionInicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionInicioRepositorio extends JpaRepository<PublicacionInicio, Long> {

    @Query("SELECT p FROM PublicacionInicio p WHERE LOWER(p.cuerpo) LIKE LOWER(concat('%', :palabraClave, '%'))")
        Page<PublicacionInicio> findByCuerpoContaining(@Param("palabraClave") String palabraClave, Pageable pageable);

    Page<PublicacionInicio> findByAutorP_Id(Long usuarioId, Pageable pageable);
    Page<PublicacionInicio> findAllByOrderByFechaPublicacionDesc(Pageable pageable);


}
