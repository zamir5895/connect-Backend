package com.backend.places.PublicacionAlojamiento.Infrastructure;

import com.backend.places.PublicacionAlojamiento.Domain.PublicacionAlojamiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PublicacionAlojamientoRespositorio extends JpaRepository<PublicacionAlojamiento, Long> {
    @Query("SELECT p FROM PublicacionAlojamiento p WHERE p.promedioRating BETWEEN :minRating AND :maxRating")
    List<PublicacionAlojamiento> findByCalificacionBetween(@Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating);

    @Query(value = "SELECT * FROM publicacion_alojamiento WHERE alojamientop_id IN (SELECT id FROM alojamiento WHERE propietario_id = :propietarioId)",
            countQuery = "SELECT count(*) FROM publicacion_alojamiento WHERE alojamientop_id IN (SELECT id FROM alojamiento WHERE propietario_id = :propietarioId)",
            nativeQuery = true)
    Page<PublicacionAlojamiento> findByAlojamientoP_Propietario_Id(@Param("propietarioId") Long propietarioId, Pageable pageable);

    @Query("SELECT p FROM PublicacionAlojamiento p JOIN " +
            "p.alojamientoP a WHERE LOWER(p.titulo)" +
            " LIKE LOWER(concat('%', :keyword, '%')) OR " +
            "LOWER(a.descripcion) LIKE LOWER(concat('%', :keyword, '%'))")
    List<PublicacionAlojamiento> findByPalabrasClave(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM publicacion_alojamiento WHERE fecha > CAST(:fecha AS timestamp) - INTERVAL '2 weeks'",
            nativeQuery = true)
    Page<PublicacionAlojamiento> findByFechaReciente(@Param("fecha") Timestamp fecha, Pageable pageable);


    @Query("SELECT p FROM PublicacionAlojamiento p")
    Page<PublicacionAlojamiento> findAllPageable(Pageable pageable);

    Optional<PublicacionAlojamiento> findByAlojamientoP_Id(Long id);
    /*Page<PublicacionAlojamiento> findByH3IndexIn(List<Long> h3Indices, Pageable pageable);
*/}
