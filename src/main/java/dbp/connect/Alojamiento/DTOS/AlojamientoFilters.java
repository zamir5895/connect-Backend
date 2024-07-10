package dbp.connect.Alojamiento.DTOS;

import dbp.connect.TipoMoneda;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlojamientoFilters {
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @NotNull
    private Double MaxDistance;
    @NotNull
    private Double MaxPrecio;
    @NotNull
    private Double MinPrecio;
    @NotNull
    private TipoMoneda tipoMoneda;
}
