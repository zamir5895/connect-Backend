package com.backend.places.PublicacionAlojamiento.Domain;

import com.backend.places.Alojamiento.DTOS.ResponseAlojamientoDTO;
import com.backend.places.Alojamiento.Domain.Alojamiento;
import com.backend.places.Alojamiento.Domain.AlojamientoServicio;
import com.backend.places.Alojamiento.Domain.Estado;
import com.backend.places.Alojamiento.Excepciones.AlojamientoNotFound;
import com.backend.places.Alojamiento.Infrastructure.AlojamientoRepositorio;
import com.backend.places.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import com.backend.places.AlojamientoMultimedia.Domain.AlojamientoMultimedia;
import com.backend.places.AlojamientoMultimedia.Domain.AlojamientoMultimediaServicio;
import com.backend.places.AlojamientoMultimedia.Infrastructure.AlojamientoMultimediaRepositorio;
import com.backend.places.PublicacionAlojamiento.DTOS.PostPublicacionAlojamientoDTO;
import com.backend.places.PublicacionAlojamiento.DTOS.ResponseFilterDTO;
import com.backend.places.PublicacionAlojamiento.DTOS.ResponsePublicacionAlojamiento;
import com.backend.places.PublicacionAlojamiento.Exceptions.PublicacionAlojamientoNotFoundException;
import com.backend.places.PublicacionAlojamiento.Infrastructure.PublicacionAlojamientoRespositorio;
import com.backend.places.Review.DTOS.ResponseReviewDTO;
import com.backend.places.Review.Domain.Review;
import com.backend.places.Review.Domain.ReviewServicio;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PublicacionAlojamientoServicio {
    @Autowired
    private PublicacionAlojamientoRespositorio publicacionAlojamientoRepositorio;

    @Autowired
    private AlojamientoRepositorio alojamientoRepositorio;

    @Autowired
    private ReviewServicio reviewServicio;
    @Autowired
    private AlojamientoMultimediaServicio alojamientoMultimediaServicio;
    @Autowired
    private AlojamientoMultimediaRepositorio alojamientoMultimediaRepositorio;

    @Autowired
    private AlojamientoServicio alojamientoServicio;

    public ResponsePublicacionAlojamiento guardarPublicacionAlojamiento(PostPublicacionAlojamientoDTO publicacionAlojamientoDTO) throws AccessDeniedException, AlojamientoNotFound {
        ResponseAlojamientoDTO dto = alojamientoServicio.guardarAlojamiento(publicacionAlojamientoDTO.getAlojamiento());

        Alojamiento alojamiento = alojamientoRepositorio.findById(dto.getId())
                .orElseThrow(() -> new AlojamientoNotFound("Alojamiento no encontrado después de guardarlo"));

        PublicacionAlojamiento nuevaPublicacion = new PublicacionAlojamiento();
        nuevaPublicacion.setAlojamientoP(alojamiento);
        nuevaPublicacion.setFecha(ZonedDateTime.now(ZoneId.systemDefault()));
        nuevaPublicacion.setCantidadReseñas(0);
        nuevaPublicacion.setTitulo(publicacionAlojamientoDTO.getTitulo());
        nuevaPublicacion.setPromedioRating(0.0);

        PublicacionAlojamiento createdPublicacionAlojamiento = publicacionAlojamientoRepositorio.save(nuevaPublicacion);
        ResponsePublicacionAlojamiento response = converToDTO(createdPublicacionAlojamiento);
        System.out.println("prueba "+ dto);
        response.setResponseAlojamientoDTO(dto);
        response.setFechaPublicacion(ZonedDateTime.now(ZoneId.systemDefault()));

        return response;
    }

    public List<ResponseMultimediaDTO> subirArchivos(List<MultipartFile> files, Long publicacionId) throws AccessDeniedException {
        PublicacionAlojamiento p = publicacionAlojamientoRepositorio.findById(publicacionId).orElseThrow(()->new EntityNotFoundException("Publicaacion no existe"));
        return alojamientoServicio.guardarArchivos(files,p.getAlojamientoP().getId());
    }

    public ResponsePublicacionAlojamiento getPublicacionId(Long publicacionId) throws AccessDeniedException, AlojamientoNotFound {
        Optional<PublicacionAlojamiento> publicacionOpt = publicacionAlojamientoRepositorio.findById(publicacionId);
        if (publicacionOpt.isPresent()) {
            PublicacionAlojamiento publicacion = publicacionOpt.get();
            return converToDTO(publicacion);
        } else {
            throw new PublicacionAlojamientoNotFoundException("PublicacionAlojamiento not found with id " + publicacionId);
        }
    }


    public void actualizarTituloAlojamiento(Long publicacionId, String titulo){
        Optional<PublicacionAlojamiento> p = publicacionAlojamientoRepositorio.findById(publicacionId);
        if(p.isEmpty()) {
            throw new PublicacionAlojamientoNotFoundException("Publicacion no existe");
        }
        PublicacionAlojamiento publicacion = p.get();
        publicacion.setTitulo(titulo);
        publicacionAlojamientoRepositorio.save(publicacion);

    }
    public void eliminarPublicacion(Long publicacionId) {
        Optional<PublicacionAlojamiento> publi = publicacionAlojamientoRepositorio.findById(publicacionId);
        if(publi.isPresent()) {
            publicacionAlojamientoRepositorio.delete(publi.get());
        }
        else{
            throw new PublicacionAlojamientoNotFoundException("Publicacion no existe");
        }
    }


    public List<ResponsePublicacionAlojamiento> obtenerPublicacionesPorRangoDeCalificacion(Integer minRating, Integer maxRating) {
        List<PublicacionAlojamiento> publicaciones = publicacionAlojamientoRepositorio.findByCalificacionBetween(minRating, maxRating);
        List<ResponsePublicacionAlojamiento> collect = publicaciones.stream()
                .map(publicacionAlojamiento -> {
                    try {
                        return converToDTO(publicacionAlojamiento);
                    } catch (AccessDeniedException e) {
                        throw new RuntimeException(e);
                    } catch (AlojamientoNotFound e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        List<ResponsePublicacionAlojamiento> collect1 = collect;
        return collect1;
    }



    public Page<ResponsePublicacionAlojamiento> getMisPublicaciones(Long propietarioId, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
        Page<PublicacionAlojamiento> publicaciones = publicacionAlojamientoRepositorio.findByAlojamientoP_Propietario_Id(propietarioId, pageable);
        return publicaciones.map(publicacionAlojamiento -> {
            try {
                return converToDTO(publicacionAlojamiento);
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            } catch (AlojamientoNotFound e) {
                throw new RuntimeException(e);
            }
        });
    }


    public Page<ResponsePublicacionAlojamiento> getPublicacionesAlojamiento(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
        Page<PublicacionAlojamiento> publicaciones = publicacionAlojamientoRepositorio.findAll(pageable);
        return publicaciones.map(publicacionAlojamiento -> {
            try {
                return converToDTO(publicacionAlojamiento);
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            } catch (AlojamientoNotFound e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void actualizarDescripcion(Long publicacionId, String descripcion){
        Optional<PublicacionAlojamiento> p = publicacionAlojamientoRepositorio.findById(publicacionId);
        if(p.isEmpty()) {
            throw new PublicacionAlojamientoNotFoundException("Publicacion no existe");
        }
        PublicacionAlojamiento publicacion = p.get();
        publicacion.getAlojamientoP().setDescripcion(descripcion);
        publicacionAlojamientoRepositorio.save(publicacion);
    }

    public List <ResponsePublicacionAlojamiento> buscarPorPalabrasClave(String keyword){
        List<PublicacionAlojamiento> publicaciones = publicacionAlojamientoRepositorio.findByPalabrasClave(keyword);
        List<ResponsePublicacionAlojamiento> collect = publicaciones.stream()
                .map(publicacionAlojamiento -> {
                    try {
                        return converToDTO(publicacionAlojamiento);
                    } catch (AccessDeniedException e) {
                        throw new RuntimeException(e);
                    } catch (AlojamientoNotFound e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        return collect;
    }
    public Page<ResponseFilterDTO> obtenerPorFiltrosPublicacionesAloj(int page, int size, Double distancia,
                                                                      Double maxPrecio, Double minPrecio,
                                                                      String tipoMoneda, Double latitude,
                                                                      Double longuitude) {
        Page<ResponseAlojamientoDTO> response = alojamientoServicio.obtenerAlojamientosDashboard(page, size,
                distancia, maxPrecio, minPrecio, tipoMoneda, latitude, longuitude);
        List<PublicacionAlojamiento> publicaciones = publicacionAlojamientoRepositorio.findAll();

        List<ResponseFilterDTO> responseFilterDTOs = publicaciones.stream()
                .flatMap(p -> response.getContent().stream()
                        .filter(r -> p.getAlojamientoP().getId().equals(r.getId()))
                        .map(r -> {
                            ResponseFilterDTO dto = new ResponseFilterDTO();
                            dto.setPublicacionId(p.getId());
                            dto.setTitulo(p.getTitulo());
                            dto.setPromedioRating(p.getPromedioRating());
                            dto.setCantidadReviews(p.getCantidadReseñas());
                            dto.setReviews(p.getReviews().stream()
                                    .map(this::converReview)
                                    .collect(Collectors.toList()));

                            dto.setAlojamiento(r);
                            return dto;
                        }))
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(responseFilterDTOs, pageable, response.getTotalElements());
    }

    private ResponseReviewDTO converReview(Review review) {
        ResponseReviewDTO dto = new ResponseReviewDTO();
        dto.setReviewId(review.getId());
        dto.setContenido(review.getComentario());
        dto.setCalificacion(review.getCalificacion());
        dto.setDateTime(review.getFecha());
        dto.setAutorId(review.getAutorR());
        return dto;
    }

    public Page<ResponsePublicacionAlojamiento> getAllPublicaciones(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<PublicacionAlojamiento> publicaciones = publicacionAlojamientoRepositorio.findAllPageable(pageable);

        Page<ResponsePublicacionAlojamiento> response = publicaciones.map(publicacion -> {
            try {
                return converToDTO(publicacion);
            } catch (AccessDeniedException | AlojamientoNotFound e) {
                throw new RuntimeException("Error al mapear la publicación: " + e.getMessage(), e);
            }
        });

        return response;
    }

    private ResponsePublicacionAlojamiento converToDTO(PublicacionAlojamiento publicacionAlojamiento) throws AccessDeniedException, AlojamientoNotFound {
        ResponsePublicacionAlojamiento response = new ResponsePublicacionAlojamiento();
        response.setPublicacionId(publicacionAlojamiento.getId());
        response.setTitulo(publicacionAlojamiento.getTitulo());
        response.setFechaPublicacion(publicacionAlojamiento.getFecha());
        response.setCantidadReviews(reviewServicio.cantidadReviewsByPublicacionId(publicacionAlojamiento.getId()));
        response.setPromedioRating(reviewServicio.obtenerPromedioCalificacion(publicacionAlojamiento.getId()));
        response.setResponseAlojamientoDTO(alojamientoServicio.mapResponseAlojamientoDTO(publicacionAlojamiento.getAlojamientoP().getId()));

        return response;
    }
    public Page<ResponsePublicacionAlojamiento> getPublicacionesRecientes(int page, int size ) {
        Pageable pageable = PageRequest.of(page, size);
        ZonedDateTime fechaActual = ZonedDateTime.now();
        Timestamp timestamp = Timestamp.from(fechaActual.toInstant());

        Page<PublicacionAlojamiento> publicaciones = publicacionAlojamientoRepositorio.findByFechaReciente(timestamp, pageable);
        return publicaciones.map(publicacionAlojamiento -> {
            try {
                return converToDTO(publicacionAlojamiento);
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            } catch (AlojamientoNotFound e) {
                throw new RuntimeException(e);
            }
        });

    }



    public ResponsePublicacionAlojamiento getApartmentoPost(Long apartmentID) throws AccessDeniedException, AlojamientoNotFound {
        Optional<PublicacionAlojamiento> publicacionOpt = publicacionAlojamientoRepositorio.findById(apartmentID);
        if (publicacionOpt.isPresent()) {
            PublicacionAlojamiento publicacion = publicacionOpt.get();
            return converToDTO(publicacion);
        } else {
            throw new PublicacionAlojamientoNotFoundException("No se encontro publicacion del alojamiento/departamento con ID: " + apartmentID);
        }
    }
}
