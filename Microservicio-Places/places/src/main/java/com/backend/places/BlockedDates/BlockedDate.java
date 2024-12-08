package com.backend.places.BlockedDates;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
@Data
@Entity
@Table(name = "blocked_dates")
public class BlockedDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alojamiento_id", nullable = false)
    private Long alojamientoId;

    @Column(name = "date", nullable = false)
    private Date date;
}
