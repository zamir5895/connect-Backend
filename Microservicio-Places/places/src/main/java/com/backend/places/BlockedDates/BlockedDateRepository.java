package com.backend.places.BlockedDates;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface BlockedDateRepository extends JpaRepository<BlockedDate, Long> {
    @Query("SELECT bd FROM BlockedDate bd WHERE bd.alojamientoId = :alojamientoId AND bd.date BETWEEN :startDate AND :endDate")
    List<BlockedDate> findByAlojamientoIdAndDateBetween(
            @Param("alojamientoId") Long alojamientoId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM BlockedDate bd WHERE bd.alojamientoId = :alojamientoId AND bd.date BETWEEN :startDate AND :endDate")
    void deleteByAlojamientoIdAndDateBetween(
            @Param("alojamientoId") Long alojamientoId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );


    @Query("SELECT bd FROM BlockedDate bd WHERE bd.alojamientoId = :alojamientoId")
    List<BlockedDate> findByAlojamientoId( @Param("alojamientoId") Long alojamientoId);
}