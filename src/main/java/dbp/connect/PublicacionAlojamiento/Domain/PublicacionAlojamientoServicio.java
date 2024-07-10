package dbp.connect.PublicacionAlojamiento.Domain;

import com.uber.h3core.H3Core;
import dbp.connect.Alojamiento.Domain.Alojamiento;
import dbp.connect.Alojamiento.Infrastructure.AlojamientoRepositorio;
import dbp.connect.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import dbp.connect.AlojamientoMultimedia.Domain.AlojamientoMultimedia;
import dbp.connect.PublicacionAlojamiento.DTOS.PostPublicacionAlojamientoDTO;
import dbp.connect.PublicacionAlojamiento.DTOS.ResponsePublicacionAlojamiento;
import dbp.connect.PublicacionAlojamiento.Exceptions.PublicacionAlojamientoNotFoundException;
import dbp.connect.PublicacionAlojamiento.Infrastructure.PublicacionAlojamientoRespositorio;
import dbp.connect.PublicacionInicioMultimedia.DTOS.MultimediaInicioDTO;
import dbp.connect.PublicacionInicioMultimedia.Domain.PublicacionInicioMultimedia;
import dbp.connect.Review.Domain.ReviewServicio;
import dbp.connect.User.Infrastructure.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    public ResponsePublicacionAlojamiento guardarPublicacionAlojamiento(PostPublicacionAlojamientoDTO publicacionAlojamientoDTO){

        Optional<PublicacionAlojamiento> publicacionAlojamiento = publicacionAlojamientoRepositorio.findById(publicacionAlojamientoDTO.getId());
        if(publicacionAlojamiento.isPresent()) {
            throw new EntityExistsException("La publicacion ya existe");
        }

        Optional<Alojamiento> alojamiento = alojamientoRepositorio.findById(publicacionAlojamientoDTO.getAlojamientoId());
        if(alojamiento.isEmpty()) {
            throw new EntityExistsException("El alojamiento no existe");
        }
        Alojamiento alojamientoResponse = alojamientoRepositorio.save(alojamiento.get());
        PublicacionAlojamiento nuevaPublicacion = new PublicacionAlojamiento();

        nuevaPublicacion.setAlojamientoP(alojamientoResponse);
        nuevaPublicacion.setId(alojamientoResponse.getId());
        nuevaPublicacion.setFecha(ZonedDateTime.now(ZoneId.systemDefault()));
        nuevaPublicacion.setCantidadReseñas(0);
        nuevaPublicacion.setTitulo(publicacionAlojamientoDTO.getTitulo());
        nuevaPublicacion.setPromedioRating(0.0);
        publicacionAlojamientoRepositorio.save(nuevaPublicacion);
        PublicacionAlojamiento createdPublicacionAlojamiento = publicacionAlojamientoRepositorio.save(nuevaPublicacion);

        ResponsePublicacionAlojamiento response = converToDTO(createdPublicacionAlojamiento);

        return response;
    }
    public ResponsePublicacionAlojamiento getPublicacionId(Long publicacionId) {
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
        return publicaciones.stream()
                .map(this::converToDTO) // Assuming there's a method `convertToDTO`
                .collect(Collectors.toList());
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

    private ResponsePublicacionAlojamiento converToDTO(PublicacionAlojamiento publicacionAlojamiento){
        ResponsePublicacionAlojamiento response = new ResponsePublicacionAlojamiento();
        response.setId(publicacionAlojamiento.getId());
        response.setTitulo(publicacionAlojamiento.getTitulo());
        response.setDescripcion(publicacionAlojamiento.getAlojamientoP().getDescripcion());
        response.setLatitude(publicacionAlojamiento.getAlojamientoP().getLatitude());
        response.setLongitud(publicacionAlojamiento.getAlojamientoP().getLongitude());
        response.setCantidadReviews(publicacionAlojamiento.getCantidadReseñas());
        response.setPromedioRating(publicacionAlojamiento.getPromedioRating());
        response.setAutorFullName(publicacionAlojamiento.getAlojamientoP().getPropietario().getUsername());
        response.setFechaPublicacion(publicacionAlojamiento.getFecha());
        response.setPrice(publicacionAlojamiento.getAlojamientoP().getPrecio());
        response.setReviews(reviewServicio.obtenerReviewsRecientes(publicacionAlojamiento.getId()));
        response.setTipoMoneda(publicacionAlojamiento.getAlojamientoP().getTipoMoneda());
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


    private ResponseMultimediaDTO converToDto(AlojamientoMultimedia multimedia){
        ResponseMultimediaDTO dto = new ResponseMultimediaDTO();
        dto.setId(multimedia.getId());
        dto.setTipo(multimedia.getTipo());
        dto.setUrl_contenido(multimedia.getUrlContenido());
        return dto;
    }

    public ResponsePublicacionAlojamiento getApartmentoPost(Long apartmentID) {
        Optional<PublicacionAlojamiento> publicacionOpt = publicacionAlojamientoRepositorio.findByAlojamientoP_Id(apartmentID);
        if (publicacionOpt.isPresent()) {
            PublicacionAlojamiento publicacion = publicacionOpt.get();
            return converToDTO(publicacion);
        } else {
            throw new PublicacionAlojamientoNotFoundException("No se encontro publicacion del alojamiento/departamento con ID: " + apartmentID);
        }
    }
}
