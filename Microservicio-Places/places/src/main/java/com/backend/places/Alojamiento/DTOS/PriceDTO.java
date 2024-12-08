package com.backend.places.Alojamiento.DTOS;

import com.backend.places.TipoMoneda;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
public class PriceDTO {
    @NotNull
    private double precio;
    @NotNull
    private TipoMoneda tipoMoneda;
}
