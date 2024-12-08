package com.backend.places.Booking.Aplication;


import com.backend.places.BlockedDates.BlockedDate;
import com.backend.places.Booking.DTOS.AlojamientoReservasResponseDTO;
import com.backend.places.Booking.DTOS.BookingResponseDTO;
import com.backend.places.Booking.Domain.Booking;
import com.backend.places.Booking.Domain.BookingService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<String> crearReserva(@RequestBody Booking booking) {
        try {
            boolean disponible = bookingService.verificarDisponibilidad(
                    booking.getAlojamientoId(),
                    booking.getStartDate(),
                    booking.getEndDate()
            );

            if (!disponible) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El alojamiento no está disponible en esas fechas.");
            }

            bookingService.crearReserva(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body("Reserva creada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la reserva.");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<BookingResponseDTO>> obtenerReservas(@PathVariable Long userId, @RequestParam Integer page,
                                                                    @RequestParam Integer size) {
        try {
            System.out.println(userId);
            Page<BookingResponseDTO> reservas = bookingService.obtenerReservasPorUsuario(userId, page,size);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> cancelarReserva(@PathVariable Long bookingId) {
        try {
            bookingService.cancelarReserva(bookingId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Reserva cancelada exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cancelar la reserva.");
        }
    }
    @GetMapping("{alojamientoId}")
    public  ResponseEntity<Page<AlojamientoReservasResponseDTO>> obtenerReserva(@PathVariable Long alojamientoId,
                                                                                @RequestParam Integer page, @RequestParam Integer size) {
        try{
            Page<AlojamientoReservasResponseDTO> response = bookingService.obtenerReservasByAlojamiento(alojamientoId, page , size);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/reservaciones/{alojamientoId}")
    public ResponseEntity<List<BlockedDate>> obtenerBlockedDates(@PathVariable Long alojamientoId) {
        try{
            List<BlockedDate> response =bookingService.verfechasNoDisponibles(alojamientoId);
            return ResponseEntity.ok(response);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
