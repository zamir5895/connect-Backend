package dbp.connect.Mensaje.DTOS;

import dbp.connect.Mensaje.Domain.StatusMensaje;
import dbp.connect.MultimediaMensaje.DTO.MensajeMultimediaDTO;
import dbp.connect.MultimediaMensaje.Domain.MultimediaMensaje;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.time.ZonedDateTime;
import java.util.List;

@Data
public class MensajeResponseDTO {
    @Id
    private Long id;
    @NotNull
    private String username;
    @NotNull
    private Long chatId;
    @NotNull
    @Size(min = 2, max=1000)
    private String contenido;
    private StatusMensaje statusMensaje;
    private String userImage;
    private ZonedDateTime fecha;
    private List<MensajeMultimediaDTO> multimedia;
}
