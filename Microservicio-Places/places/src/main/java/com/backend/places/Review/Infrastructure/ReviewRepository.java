package com.backend.places.Review.Infrastructure;

import com.backend.places.Review.Domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByPublicacionAlojamientoId(Long publicacionAId, Pageable pageable);
    @Query("SELECT r FROM Review r WHERE r.autorR = :autor")
    List<Review> findByAutorR(@Param("autor") Long autor);
    @Query("SELECT r FROM Review r WHERE r.publicacionAlojamiento.id = :publicacionAlojamientoId ORDER BY r.fecha DESC")
    Page<Review> findTop5ByPublicacionAlojamientoIdOrderByFechaDesc(@Param("publicacionAlojamientoId") Long publicacionAlojamientoId, Pageable pageable);
    @Query("SELECT r FROM Review r WHERE r.publicacionAlojamiento.id = :publicacionAlojamientoId AND r.calificacion = :calificacion")
    List<Review> findByPublicacionAlojamientoIdAndCalificacion(@Param("publicacionAlojamientoId") Long publicacionAlojamientoId, @Param("calificacion") Integer calificacion);
    List<Review> findByCalificacion(Integer calificacion);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.publicacionAlojamiento.id = :publicacionId")
    Integer countByPublicacionAlojamientoId(@Param("publicacionId") Long publicacionId);


}
