package com.backend.places.Alojamiento.DTOS;

import com.backend.places.Alojamiento.Domain.TipoH;
import com.backend.places.TipoMoneda;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Data
public class AlojamientoRequest {
    @NotNull
    private Long propietarioId;
    private Double latitud;
    private Double longitud;
    private String ubicacion;

    @Size(min = 1, max = 255)
    private String descripcionCorta;
    private String descripcionLarga;
    @NotNull
    private Double precio;
    @NotNull
    private TipoMoneda tipoMoneda;
    private Integer cantidadHabitaciones;
    private Integer cantidadCamas;
    private Integer cantidadBanios;
    private Integer capacidad;
    private TipoH tipo;
    private List<String> meneces;
}
