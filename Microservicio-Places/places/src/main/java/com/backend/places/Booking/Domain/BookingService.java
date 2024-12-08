package com.backend.places.Booking.Domain;


import com.backend.places.Alojamiento.Domain.Alojamiento;
import com.backend.places.Alojamiento.Domain.AlojamientoServicio;
import com.backend.places.Alojamiento.Excepciones.AlojamientoNotFound;
import com.backend.places.Alojamiento.Infrastructure.AlojamientoRepositorio;
import com.backend.places.BlockedDates.BlockedDate;
import com.backend.places.BlockedDates.BlockedDateRepository;
import com.backend.places.Booking.DTOS.AlojamientoReservasResponseDTO;
import com.backend.places.Booking.DTOS.BookingResponseDTO;
import com.backend.places.Booking.Infrastructure.BookingRepository;
import com.backend.places.UserInfo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BlockedDateRepository blockedDateRepository;
    @Autowired
    private AlojamientoServicio alojamientoServicio;
    @Autowired
    private AlojamientoRepositorio alojamientoRepositorio;

    public boolean verificarDisponibilidad(Long accommodationId, Date startDate, Date endDate) {
        List<BlockedDate> fechasBloqueadas = blockedDateRepository.findByAlojamientoIdAndDateBetween(accommodationId, startDate, endDate);
        return fechasBloqueadas.isEmpty();
    }



    public Booking crearReserva(Booking booking) {
        if (!verificarDisponibilidad(booking.getAlojamientoId(), booking.getStartDate(), booking.getEndDate())) {
            throw new IllegalArgumentException("Fechas no disponibles para reserva.");
        }

        Booking nuevaReserva = bookingRepository.save(booking);

        List<BlockedDate> fechasABloquear = new ArrayList<>();
        Date fechaActual = booking.getStartDate();
        Calendar calendar = Calendar.getInstance();

        while (!fechaActual.after(booking.getEndDate())) {
            BlockedDate blockedDate = new BlockedDate();
            blockedDate.setAlojamientoId(booking.getAlojamientoId());
            blockedDate.setDate(fechaActual);
            fechasABloquear.add(blockedDate);

            calendar.setTime(fechaActual);
            calendar.add(Calendar.DATE, 1);
            fechaActual = calendar.getTime();
        }

        blockedDateRepository.saveAll(fechasABloquear);

        return nuevaReserva;
    }


    public void cancelarReserva(Long bookingId) {
        Booking reserva = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada."));
        bookingRepository.delete(reserva);

        blockedDateRepository.deleteByAlojamientoIdAndDateBetween(
                reserva.getAlojamientoId(),
                reserva.getStartDate(),
                reserva.getEndDate()
        );
    }

    public Page<BookingResponseDTO> obtenerReservasPorUsuario(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Booking> bookings =  bookingRepository.findByUserIdPage(userId, pageable);
        return bookings.map(booking -> {
            try {
                return converToDto(booking);
            }catch (Exception e){
                throw new IllegalArgumentException("Fechas no disponibles para reserva.");
            }

        });
    }
    public BookingResponseDTO converToDto(Booking book) throws AlojamientoNotFound {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(book.getId());
        dto.setResponseAlojamientoDTO(alojamientoServicio.mapResponseAlojamientoDTO(book.getAlojamientoId()));
        dto.setStartDate(book.getStartDate());
        dto.setEndDate(book.getEndDate());
        dto.setUserId(book.getUserId());
        Alojamiento al = alojamientoRepositorio.findById(book.getAlojamientoId()).orElseThrow(()->new EntityNotFoundException("Alojamiento no encontrado"));
        dto.setPublicacionId(al.getPublicacionAlojamiento().getId());
        dto.setTitulo(al.getPublicacionAlojamiento().getTitulo());
        return dto;

    }
    public List<BlockedDate> verfechasNoDisponibles(Long accommodationId) {
        return blockedDateRepository.findByAlojamientoId(accommodationId);
    }

    public  Page<AlojamientoReservasResponseDTO> obtenerReservasByAlojamiento(Long alojamientoId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Booking> bookings = bookingRepository.findByAlojamientoId(alojamientoId, pageable);

        return bookings.map(booking -> {
            try {
                return convertAlojamiento(booking);
            }catch (Exception e){
                throw new IllegalArgumentException("Fechas no disponibles para reserva.");
            }

        });
    }


    private AlojamientoReservasResponseDTO convertAlojamiento(Booking book) throws AlojamientoNotFound {
        AlojamientoReservasResponseDTO dto =new AlojamientoReservasResponseDTO();
        dto.setBookingId(book.getId());
        dto.setStartDate(book.getStartDate());
        dto.setEndDate(book.getEndDate());
        dto.setUserId(book.getUserId());
        UserInfo user = alojamientoServicio.obtenerUsuarioById(book.getUserId());
        dto.setEmail(user.getEmail());
        dto.setNombre(user.getUserFullName());
        dto.setFotoUrl(user.getFotoPerfil());
        dto.setUserId(book.getUserId());
        return dto;

    }
}
