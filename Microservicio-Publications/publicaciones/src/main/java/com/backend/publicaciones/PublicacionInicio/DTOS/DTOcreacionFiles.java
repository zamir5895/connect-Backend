package com.backend.publicaciones.PublicacionInicio.DTOS;

import com.backend.publicaciones.PublicacionInicioMultimedia.DTOS.MultimediaInicioDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class DTOcreacionFiles {
    private Long publicacionId;
    private List<MultimediaInicioDTO> multimediaInicioDTO = new ArrayList<>();

}
