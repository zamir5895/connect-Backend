package com.backend.places.Alojamiento.DTOS;

import com.backend.places.Alojamiento.Domain.Estado;
import com.backend.places.Tipo;
import com.backend.places.TipoMoneda;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;


@Data
public class AlojamientoUpdateDTO {
    @NotNull
    private Long propietarioId;
    private Double latitude;
    private Double longitude;
    @NotNull
    private String ubicacion;
    @NotNull
    @Size(min = 1, max = 255)
    private String descripcion;
    @NotNull
    private double precio;
    @NotNull
    private Estado estado;
    @NotNull
    private TipoMoneda tipoMoneda;
    private List<MultipartFile> multimedia = new ArrayList<>();
}
