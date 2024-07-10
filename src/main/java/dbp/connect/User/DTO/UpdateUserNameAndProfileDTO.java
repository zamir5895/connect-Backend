package dbp.connect.User.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class UpdateUserNameAndProfileDTO {
    @NotNull
    private String userName;
    private MultipartFile profilePicture;
    private String descripcion;
    private LocalDate fechaNacimiento;
    private String  direccion;
}
