package dbp.connect.PublicacionInicio.DTOS;


import dbp.connect.User.Domain.User;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Getter
@Setter
@Data
public class PostInicioDTO {
    @Size(min=1, max=255)
    private String Cuerpo;
    private List<MultipartFile> multimediaList;
    @NotNull
    private Long autorPId;
}
 