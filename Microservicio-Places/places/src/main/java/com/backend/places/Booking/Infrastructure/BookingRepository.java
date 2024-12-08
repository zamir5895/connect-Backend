package com.backend.places.Booking.Infrastructure;

import com.backend.places.Booking.Domain.Booking;
import lombok.Data;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.alojamientoId = :alojamientoId")
    Page<Booking> findByAlojamientoId(@Param("alojamientoId") Long alojamientoId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId")
    Page<Booking> findByUserIdPage(@Param("userId") Long userId, Pageable pageable);

}