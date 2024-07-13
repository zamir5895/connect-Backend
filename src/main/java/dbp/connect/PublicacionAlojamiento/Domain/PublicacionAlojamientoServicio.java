package dbp.connect.PublicacionAlojamiento.Domain;

import com.uber.h3core.H3Core;
import dbp.connect.Alojamiento.DTOS.ResponseAlojamientoDTO;
import dbp.connect.Alojamiento.Domain.Alojamiento;
import dbp.connect.Alojamiento.Domain.AlojamientoServicio;
import dbp.connect.Alojamiento.Domain.Estado;
import dbp.connect.Alojamiento.Infrastructure.AlojamientoRepositorio;
import dbp.connect.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import dbp.connect.AlojamientoMultimedia.Domain.AlojamientoMultimedia;
import dbp.connect.AlojamientoMultimedia.Domain.AlojamientoMultimediaServicio;
import dbp.connect.AlojamientoMultimedia.Infrastructure.AlojamientoMultimediaRepositorio;
import dbp.connect.PublicacionAlojamiento.DTOS.PostPublicacionAlojamientoDTO;
import dbp.connect.PublicacionAlojamiento.DTOS.ResponseFilterDTO;
import dbp.connect.PublicacionAlojamiento.DTOS.ResponsePublicacionAlojamiento;
import dbp.connect.PublicacionAlojamiento.Exceptions.PublicacionAlojamientoNotFoundException;
import dbp.connect.PublicacionAlojamiento.Infrastructure.PublicacionAlojamientoRespositorio;
import dbp.connect.PublicacionInicioMultimedia.DTOS.MultimediaInicioDTO;
import dbp.connect.PublicacionInicioMultimedia.Domain.PublicacionInicioMultimedia;
import dbp.connect.Review.DTOS.ResponseReviewDTO;
import dbp.connect.Review.Domain.Review;
import dbp.connect.Review.Domain.ReviewServicio;
import dbp.connect.Security.Utils.AuthorizationUtils;
import dbp.connect.User.Domain.User;
import dbp.connect.User.Infrastructure.UserRepository;
import jakarta.persistence.EntityExistsException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private UserRepository userRepository;

    @Autowired
    private ReviewServicio reviewServicio;
    @Autowired
    private AlojamientoMultimediaServicio alojamientoMultimediaServicio;
    @Autowired
    private AlojamientoMultimediaRepositorio alojamientoMultimediaRepositorio;
    @Autowired
    private AuthorizationUtils authorizationUtils;
    @Autowired
    private AlojamientoServicio alojamientoServicio;

    public ResponsePublicacionAlojamiento guardarPublicacionAlojamiento(PostPublicacionAlojamientoDTO publicacionAlojamientoDTO, List<MultipartFile> multi) throws AccessDeniedException {
        Alojamiento alojamiento = new Alojamiento();
        User currentPropietario = userRepository.findById(publicacionAlojamientoDTO.getAlojamiento().getPropietarioId())
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        alojamiento.setPropietario(currentPropietario);
        alojamiento.setEstado(Estado.DISPONIBLE);
        alojamiento.setFechaPublicacion(LocalDateTime.now(ZoneId.systemDefault()));
        alojamiento.setDescripcion(publicacionAlojamientoDTO.getAlojamiento().getDescripcion());
        alojamiento.setLongitude(publicacionAlojamientoDTO.getAlojamiento().getLongitude());
        alojamiento.setLatitude(publicacionAlojamientoDTO.getAlojamiento().getLatitude());
        alojamiento.setUbicacion(publicacionAlojamientoDTO.getAlojamiento().getUbicacion());
        alojamiento.setPrecio(publicacionAlojamientoDTO.getAlojamiento().getPrecio());
        alojamiento.setTipoMoneda(publicacionAlojamientoDTO.getAlojamiento().getTipoMoneda());

        alojamientoRepositorio.save(alojamiento);

        for (MultipartFile archivo : multi) {
            AlojamientoMultimedia multimedia = alojamientoMultimediaServicio.guardarArchivo(archivo);
            multimedia.setAlojamiento(alojamiento);
            alojamientoMultimediaRepositorio.save(multimedia);
            alojamiento.getAlojamientoMultimedia().add(multimedia);
        }

        PublicacionAlojamiento nuevaPublicacion = new PublicacionAlojamiento();
        nuevaPublicacion.setAlojamientoP(alojamiento);
        nuevaPublicacion.setFecha(ZonedDateTime.now(ZoneId.systemDefault()));
        nuevaPublicacion.setCantidadReseñas(0);
        nuevaPublicacion.setTitulo(publicacionAlojamientoDTO.getTitulo());
        nuevaPublicacion.setPromedioRating(0.0);

        PublicacionAlojamiento createdPublicacionAlojamiento = publicacionAlojamientoRepositorio.save(nuevaPublicacion);
        ResponsePublicacionAlojamiento response = converToDTO(createdPublicacionAlojamiento);

        return response;
    }

    public ResponsePublicacionAlojamiento getPublicacionId(Long publicacionId) throws AccessDeniedException {
        Optional<PublicacionAlojamiento> publicacionOpt = publicacionAlojamientoRepositorio.findById(publicacionId);
        if (publicacionOpt.isPresent()) {
            PublicacionAlojamiento publicacion = publicacionOpt.get();
            return converToDTO(publicacion);
        } else {
            throw new PublicacionAlojamientoNotFoundException("PublicacionAlojamiento not found with id " + publicacionId);
        }
    }

   /* public Page<ResponsePublicacionAlojamiento> getPublicacionRecomendadas(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PublicacionAlojamiento> publicaciones = publicacionAlojamientoRepositorio.findByUserId(u, );
        return publicaciones.map(this::converToDTO);
    }*/ //Mmmmm puede ser encontrar por publicaciones hechas por el autor del alojamiento?
    //tambien implementar un sistema de recomendaciones
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
                    }
                })
                .collect(Collectors.toList());
        List<ResponsePublicacionAlojamiento> collect1 = collect;
        return collect1;
    }

    /*public Page<ResponsePublicacionAlojamiento> buscarPorUbicacion(double latitud, double longitud, double radio, int page, int sz) throws IOException {
        Pageable pageable = PageRequest.of(page, sz);

        H3Core h3 = H3Core.newInstance();
        int resolucion = 12; // or any other resolution that fits your needs
        long indiceH3 = h3.geoToH3(latitud, longitud, resolucion);
        List<Long> indicesCercanos = h3.kRing(indiceH3, (int) radio); // Adjust the radio accordingly

        Page<PublicacionAlojamiento> publicaciones = publicacionAlojamientoRepositorio.findByH3IndexIn(indicesCercanos, pageable);
        return publicaciones.map(this::converToDTO);
    }*/

    public Page<ResponsePublicacionAlojamiento> getMisPublicaciones(Long propietarioId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<PublicacionAlojamiento> publicaciones = publicacionAlojamientoRepositorio.findByAlojamientoP_Propietario_Id(propietarioId, pageable);
        return publicaciones.map(publicacionAlojamiento -> {
            try {
                return converToDTO(publicacionAlojamiento);
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public Page<ResponsePublicacionAlojamiento> getPublicacionesAlojamiento(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<PublicacionAlojamiento> publicaciones = publicacionAlojamientoRepositorio.findAll(pageable);
        return publicaciones.map(publicacionAlojamiento -> {
            try {
                return converToDTO(publicacionAlojamiento);
            } catch (AccessDeniedException e) {
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
                                    .map(this::convertToDTO)
                                    .collect(Collectors.toList()));
                            dto.setFullName(p.getFullName());
                            dto.setAlojamiento(r);
                            return dto;
                        }))
                .collect(Collectors.toList());

        // Crear una página con los resultados
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(responseFilterDTOs, pageable, response.getTotalElements());
    }

    private ResponseReviewDTO converReview(Review review) {
        // Implementar la conversión de Review a ResponseReviewDTO
        ResponseReviewDTO dto = new ResponseReviewDTO();
        dto.setReviewId(review.getId());
        dto.setContenido(review.getComentario());
        dto.setCalificacion(review.getCalificacion());
        dto.setDateTime(review.getFecha());
        return dto;
    }

    private ResponsePublicacionAlojamiento converToDTO(PublicacionAlojamiento publicacionAlojamiento) throws AccessDeniedException {
        ResponsePublicacionAlojamiento response = new ResponsePublicacionAlojamiento();
        response.setPublicacionId(publicacionAlojamiento.getId());
        response.setTitulo(publicacionAlojamiento.getTitulo());
        response.setDescripcion(publicacionAlojamiento.getAlojamientoP().getDescripcion());
        response.setLatitude(publicacionAlojamiento.getAlojamientoP().getLatitude());
        response.setLongitud(publicacionAlojamiento.getAlojamientoP().getLongitude());
        response.setCantidadReviews(publicacionAlojamiento.getCantidadReseñas());
        response.setPromedioRating(publicacionAlojamiento.getPromedioRating());
        response.setAutorFullName(publicacionAlojamiento.getAlojamientoP().getPropietario().getPrimerNombre() + " " +
                publicacionAlojamiento.getAlojamientoP().getPropietario().getPrimerApellido() + " " + publicacionAlojamiento.getAlojamientoP().getPropietario().getSegundoApellido());
        response.setFechaPublicacion(publicacionAlojamiento.getFecha());
        response.setPrice(publicacionAlojamiento.getAlojamientoP().getPrecio());
        response.setReviews(reviewServicio.obtenerReviewsRecientes(publicacionAlojamiento.getId()));
        response.setTipoMoneda(publicacionAlojamiento.getAlojamientoP().getTipoMoneda());
        response.setAlojamientoId(publicacionAlojamiento.getAlojamientoP().getId());
        response.setAlojamientoId(publicacionAlojamiento.getAlojamientoP().getId());
        response.setEstado(publicacionAlojamiento.getAlojamientoP().getEstado());
        response.setPropietarioId(publicacionAlojamiento.getAlojamientoP().getPropietario().getId());
        if (publicacionAlojamiento.getAlojamientoP().getPropietario().getFotoUrl() != null) {
            response.setAutorPhotoUrl(publicacionAlojamiento.getAlojamientoP().getPropietario().getFotoUrl());
        } else {
            response.setAutorPhotoUrl(null);
        }

        for(AlojamientoMultimedia multimedia: publicacionAlojamiento.getAlojamientoP().getAlojamientoMultimedia()){
            response.getAlojamientoMultimedia().add(converToDto(multimedia));
        }

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
            }
        });

    }

    private ResponseMultimediaDTO converToDto(AlojamientoMultimedia multimedia){
        ResponseMultimediaDTO dto = new ResponseMultimediaDTO();
        dto.setId(multimedia.getId());
        dto.setTipo(multimedia.getTipo());
        dto.setUrl_contenido(multimedia.getUrlContenido());
        return dto;
    }

    public ResponsePublicacionAlojamiento getApartmentoPost(Long apartmentID) throws AccessDeniedException {
        Optional<PublicacionAlojamiento> publicacionOpt = publicacionAlojamientoRepositorio.findById(apartmentID);
        if (publicacionOpt.isPresent()) {
            PublicacionAlojamiento publicacion = publicacionOpt.get();
            return converToDTO(publicacion);
        } else {
            throw new PublicacionAlojamientoNotFoundException("No se encontro publicacion del alojamiento/departamento con ID: " + apartmentID);
        }
    }
}
