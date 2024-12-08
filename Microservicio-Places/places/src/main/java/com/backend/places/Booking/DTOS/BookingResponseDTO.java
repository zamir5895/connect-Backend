package com.backend.places.Booking.DTOS;

import com.backend.places.Alojamiento.DTOS.ResponseAlojamientoDTO;
import lombok.Data;

import java.util.Date;

@Data
public class BookingResponseDTO {
    private Long bookingId;
    private Date startDate;
    private Date endDate;
    private Long userId;
    private ResponseAlojamientoDTO responseAlojamientoDTO;
    private Long publicacionId;
    private String titulo;



}
