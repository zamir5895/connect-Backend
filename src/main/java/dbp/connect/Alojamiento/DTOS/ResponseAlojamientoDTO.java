package dbp.connect.Alojamiento.DTOS;

import dbp.connect.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import dbp.connect.TipoMoneda;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseAlojamientoDTO {
    @NotNull
    private Long id;
    @NotNull
    private Long propietarioId;
    private double latitude;
    private double longitude;
    @NotNull
    private String ubicacion;
    @NotNull
    private String descripcion;
    @NotNull
    private TipoMoneda tipoMoneda;
    @NotNull
    private double precio;
    private List<ResponseMultimediaDTO> multimedia = new ArrayList<>();
}
