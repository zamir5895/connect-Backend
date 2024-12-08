package com.backend.places.Alojamiento.DTOS;

import com.backend.places.Alojamiento.Domain.Estado;
import com.backend.places.Alojamiento.Domain.TipoH;
import com.backend.places.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import com.backend.places.TipoMoneda;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import javax.lang.model.util.ElementScanner6;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseAlojamientoDTO {
    @NotNull
    private Long id;
    @NotNull
    private Long propietarioId;
    private String nombre;
    private String foto;
    private Double latitude;
    private Double longitude;
    @NotNull
    private String ubicacion;
    @NotNull
    private String descripcionCorta;
    private String descripcionLarga;
    private TipoH tipo;
    @NotNull
    private TipoMoneda tipoMoneda;
    @NotNull
    private Double precio;
    private Integer capacidad;
    private Integer cantidadHabitaciones;
    private Integer cantidadCamas;
    private Integer cantidadBanios;
    private Estado estado;
    private List<ResponseMultimediaDTO> multimedia = new ArrayList<>();
    private List<String> meneces = new ArrayList<>();
}
