package dbp.connect.User.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateUserNameAndProfileDTO {
    @NotNull
    private String userName;
    @NotNull
    private MultipartFile profilePicture;
}
