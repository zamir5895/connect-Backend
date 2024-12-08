package com.backend.places.AlojamientoMultimedia.Domain;

import com.backend.places.Alojamiento.Domain.Alojamiento;
import com.backend.places.Alojamiento.Infrastructure.AlojamientoRepositorio;
import com.backend.places.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import com.backend.places.AlojamientoMultimedia.Infrastructure.AlojamientoMultimediaRepositorio;
import com.backend.places.S3.StorageService;
import com.backend.places.Tipo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class AlojamientoMultimediaServicio {
    @Autowired
    private StorageService storageService;
    @Autowired
    private AlojamientoMultimediaRepositorio alojamientoMultimediaRepositorio;
    @Autowired
    private AlojamientoRepositorio alojamientoRepositorio;

    public AlojamientoMultimedia guardarArchivo(MultipartFile archivo, Alojamiento alojamiento) {
        try {
            AlojamientoMultimedia archivoMultimedia = new AlojamientoMultimedia();
            String id = generarNuevoId();
            archivoMultimedia.setId(id);

            if (Objects.requireNonNull(archivo.getContentType()).startsWith("image/")) {
                archivoMultimedia.setTipo(Tipo.FOTO);
            } else if (Objects.requireNonNull(archivo.getContentType()).startsWith("video/")) {
                archivoMultimedia.setTipo(Tipo.VIDEO);
            } else {
                throw new IllegalArgumentException("Tipo de archivo no soportado");
            }
            archivoMultimedia.setFechaCreacion(ZonedDateTime.now(ZoneId.systemDefault()));
            String key = storageService.subirAlS3File(archivo, id);
            archivoMultimedia.setUrlContenido(storageService.obtenerURL(key));
            archivoMultimedia.setAlojamiento(alojamiento);
            AlojamientoMultimedia m =  alojamientoMultimediaRepositorio.save(archivoMultimedia);
            return m;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo",e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarArchivo(Long alojamientoId, String imagenId) throws AccessDeniedException {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            Alojamiento aloj = alojamientoOptional.get();

            Iterator<AlojamientoMultimedia> iterator = aloj.getAlojamientoMultimedia().iterator();
            while (iterator.hasNext()) {
                AlojamientoMultimedia multimedia = iterator.next();
                if (multimedia.getId().equals(imagenId)) {
                    storageService.deleteFile(multimedia.getId());
                    iterator.remove(); 
                    alojamientoMultimediaRepositorio.delete(multimedia);
                }
            }

            alojamientoRepositorio.save(aloj);
        } else {
            throw new EntityNotFoundException("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }




    public List<ResponseMultimediaDTO> obtenerMultimediaPorPublicacionid(Long alojamientoId) throws AccessDeniedException {
        List<AlojamientoMultimedia> resultados = alojamientoMultimediaRepositorio.findByALojamientoId(alojamientoId);
        if (!resultados.isEmpty()) {
            List<ResponseMultimediaDTO> responseMultimediaDTOS = new ArrayList<>();
            for(AlojamientoMultimedia multimedia: resultados){
                ResponseMultimediaDTO multimediaDTO = new ResponseMultimediaDTO();
                multimediaDTO.setId(multimedia.getId());
                multimediaDTO.setTipo(multimedia.getTipo());
                multimediaDTO.setUrl_contenido(multimedia.getUrlContenido());
                responseMultimediaDTOS.add(multimediaDTO);
            }
            return responseMultimediaDTOS;

        }else{
            return new ArrayList<>();
        }
    }

    private String generarNuevoId() {
        return UUID.randomUUID().toString();
    }

}
