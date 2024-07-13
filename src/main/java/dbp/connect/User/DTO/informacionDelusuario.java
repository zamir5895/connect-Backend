package dbp.connect.User.DTO;

import dbp.connect.User.Domain.Rol;
import lombok.Data;

@Data
public class informacionDelusuario {
    private Long id;
    private String userName;
    private String fotoPerfil;
    private String email;
    private Rol rol;
}
