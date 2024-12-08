package com.backend.places.Booking.DTOS;


import com.backend.places.Alojamiento.DTOS.ResponseAlojamientoDTO;
import com.backend.places.UserInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class AlojamientoReservasResponseDTO {
    private Long bookingId;
    private Date startDate;
    private Date endDate;
    private Long userId;
    private String nombre;
    private String email;
    private String fotoUrl;


}
