package dbp.connect.PublicacionInicioMultimedia.Domain;


import dbp.connect.Comentarios.Excepciones.PublicacionNoEncontradoException;
import dbp.connect.PublicacionInicio.Domain.PublicacionInicio;
import dbp.connect.PublicacionInicio.Infrastructure.PublicacionInicioRepositorio;
import dbp.connect.PublicacionInicioMultimedia.DTOS.MultimediaInicioDTO;
import dbp.connect.PublicacionInicioMultimedia.Infrastructure.PublicacionInicioMultimediaRepositorio;
import dbp.connect.S3.StorageService;
import dbp.connect.Security.Utils.AuthorizationUtils;
import dbp.connect.Tipo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PublicacionInicioMultimediaServicio {
    @Autowired
    private PublicacionInicioMultimediaRepositorio publicacionInicioMultimediaRepositorio;
    @Autowired
    private PublicacionInicioRepositorio publicacionInicioRepositorio;
    @Autowired
    private StorageService storageService;
    private static Long idCounter =0L;

    @Autowired
    private AuthorizationUtils authorizationUtils;

    public PublicacionInicioMultimedia guardarArchivo(MultipartFile archivo) {
        try {
            PublicacionInicioMultimedia archivoMultimedia = new PublicacionInicioMultimedia();
            archivoMultimedia.setId(serializarId(generationId()));

            if (Objects.requireNonNull(archivo.getContentType()).startsWith("image/")) {
                archivoMultimedia.setTipo(Tipo.FOTO);
            } else if (Objects.requireNonNull(archivo.getContentType()).startsWith("video/")) {
                archivoMultimedia.setTipo(Tipo.VIDEO);
            } else {
                throw new IllegalArgumentException("Tipo de archivo no soportado");
            }
            archivoMultimedia.setFechaCreacion(ZonedDateTime.now(ZoneId.systemDefault()));

            String key = storageService.subiralS3File(archivo, archivoMultimedia.getId());
            archivoMultimedia.setContenidoUrl(storageService.obtenerURL(key));
            return archivoMultimedia;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo",e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void eliminarArchivo(Long publicacionId, String archivoId){
        Optional<PublicacionInicio> publicacionInicio = publicacionInicioRepositorio.findById(publicacionId);
        if (publicacionInicio.isPresent()) {
            PublicacionInicio publiInicial= publicacionInicio.get();
            for(PublicacionInicioMultimedia multimedia: publiInicial.getPublicacionMultimedia()){
                if(multimedia.getId().equals(archivoId)){
                    storageService.deleteFile(multimedia.getId());
                    publiInicial.getPublicacionMultimedia().remove(multimedia);
                    publicacionInicioMultimediaRepositorio.delete(multimedia);
                    publicacionInicioRepositorio.save(publiInicial);
                }
            }
        } else {
            throw new EntityNotFoundException("Alojamiento no encontrado con id: " + publicacionId);
        }
    }

    public void modificarArchivo(Long publicacionId, String imagenId, MultipartFile archivo) throws Exception {
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionId).
                orElseThrow(()->new PublicacionNoEncontradoException("No se encontro la publicacion"));
            for(PublicacionInicioMultimedia multimedia: publicacionInicio.getPublicacionMultimedia()){
                if(multimedia.getId().equals(imagenId)) {
                    if (Objects.requireNonNull(archivo.getContentType()).startsWith("image/")) {
                        multimedia.setTipo(Tipo.FOTO);
                    } else if (Objects.requireNonNull(archivo.getContentType()).startsWith("video/")) {
                        multimedia.setTipo(Tipo.VIDEO);
                    } else {
                        throw new IllegalArgumentException("Tipo de archivo no soportado");
                    }
                    String key = storageService.subiralS3File(archivo, multimedia.getId());
                    multimedia.setContenidoUrl(storageService.obtenerURL(key));
                    multimedia.setFechaCreacion(ZonedDateTime.now(ZoneId.systemDefault()));
                    publicacionInicioMultimediaRepositorio.save(multimedia);
                    publicacionInicioRepositorio.save(publicacionInicio);
                }
                else{
                    throw new EntityNotFoundException("La imagen no pertenece a la publicacion con id: " + publicacionId);
                }
            }

    }

    public List<MultimediaInicioDTO> obtenerMultimediaPorPublicacion(Long publicacionId) {
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionId).orElseThrow(
                () -> new EntityNotFoundException("No se encontró la publicación"));

        List<PublicacionInicioMultimedia> multimediaList = publicacionInicioMultimediaRepositorio.findByPublicacionInicio(publicacionId);

        if (multimediaList.isEmpty()) {
            throw new EntityNotFoundException("No se encontraron imágenes para el alojamiento con id: " + publicacionId);
        }

        List<MultimediaInicioDTO> multimediaDTOList = multimediaList.stream()
                .map(multimedia -> mapResponseMultimediaDTO(multimedia))
                .collect(Collectors.toList());

        return multimediaDTOList;
    }


    public MultimediaInicioDTO obtenerMultimedia(Long publicacionId, String archivoId) {
        Optional<PublicacionInicio> publicacionInicioOptional = publicacionInicioRepositorio.findById(publicacionId);
        if (publicacionInicioOptional.isPresent()) {
            PublicacionInicio publicacionInicio = publicacionInicioOptional.get();
            MultimediaInicioDTO multimediaDTO = new MultimediaInicioDTO();
            for (PublicacionInicioMultimedia multimedia : publicacionInicio.getPublicacionMultimedia()) {
                if (multimedia.getId().equals((archivoId))) {
                    multimediaDTO.setId(multimedia.getId());
                    multimediaDTO.setTipo(multimedia.getTipo());
                    multimediaDTO.setContenidoUrl(multimedia.getContenidoUrl());
                    multimediaDTO.setFechaCreacion(multimedia.getFechaCreacion());
                    return multimediaDTO;
                }
            }
            throw new EntityNotFoundException("No se encontró el archivo con id: " + archivoId);
        } else {
            throw new EntityNotFoundException("Publicacion Inicio no encontrado con id: " + publicacionId);
        }
    }
    private MultimediaInicioDTO mapResponseMultimediaDTO(PublicacionInicioMultimedia multimedia){
        MultimediaInicioDTO multimediaDTO = new MultimediaInicioDTO();
        multimediaDTO.setId(multimedia.getId());
        multimediaDTO.setTipo(multimedia.getTipo());
        multimediaDTO.setContenidoUrl(multimedia.getContenidoUrl());
        multimediaDTO.setFechaCreacion(multimedia.getFechaCreacion());
        return multimediaDTO;
    }

    private Long generationId() {
        return idCounter++;
    }
    private String serializarId(Long id) {
        return id.toString();
    }
}
