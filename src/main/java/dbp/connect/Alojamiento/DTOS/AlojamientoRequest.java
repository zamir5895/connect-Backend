package dbp.connect.Alojamiento.DTOS;

import dbp.connect.TipoMoneda;
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
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;
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
    private TipoMoneda tipoMoneda;
    private List<MultipartFile> multimedia = new ArrayList<>();

}
