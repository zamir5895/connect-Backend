package com.backend.backenddbp.Mensaje.DTOS;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@Data
public class DTOMensajePost {
    private Long userId;
    @NotNull
    private Long chatId;
    @NotNull
    @Size(min = 2, max=1000)
    private String contenido;
}
