package dbp.connect.Review.Domain;

import dbp.connect.Notificaciones.Domain.NotificacionesService;
import dbp.connect.PublicacionAlojamiento.Domain.PublicacionAlojamiento;
import dbp.connect.PublicacionAlojamiento.Domain.PublicacionAlojamientoServicio;
import dbp.connect.PublicacionAlojamiento.Exceptions.PublicacionAlojamientoNotFoundException;
import dbp.connect.PublicacionAlojamiento.Infrastructure.PublicacionAlojamientoRespositorio;
import dbp.connect.Review.DTOS.ResponseReviewDTO;
import dbp.connect.Review.DTOS.ReviewRequest;
import dbp.connect.Review.Exceptions.ReviewNotFoundException;
import dbp.connect.Review.Infrastructure.ReviewRepository;
import dbp.connect.User.Domain.User;
import dbp.connect.User.Infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServicio {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PublicacionAlojamientoRespositorio publicacionAlojamientoRespositorio;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificacionesService notificacionesService;

    public Long createReview( ReviewRequest reviewRequest) {
        Optional<PublicacionAlojamiento> publicacionAlojamiento = publicacionAlojamientoRespositorio.findById(reviewRequest.getPublicacionId());
        if (publicacionAlojamiento.isEmpty()) {
            throw new PublicacionAlojamientoNotFoundException("Publicacion no encontrada");
        }
        PublicacionAlojamiento publicacion = publicacionAlojamiento.get();
        User autorReview = userRepository.findById(reviewRequest.getAutorId()).
                orElseThrow(()->new EntityNotFoundException("Autor no encontrado"));

        Review review = new Review();
        review.setCalificacion(reviewRequest.getRating());
        review.setAutorR(autorReview);
        review.setComentario(reviewRequest.getContent());
        review.setPublicacionAlojamiento(publicacion);
        review.setFecha(LocalDateTime.now(ZoneId.systemDefault()));
        reviewRepository.save(review);
        publicacion.getReviews().add(review);
        publicacion.setCantidadReseñas(publicacion.getCantidadReseñas()+1);
        int sum = 0;
        for(Review r : publicacion.getReviews()) {
            sum += r.getCalificacion();
        }

        double promedio = (double) sum / publicacion.getCantidadReseñas();
        DecimalFormat df = new DecimalFormat("#.##");
        String roundedPromedio = df.format(promedio);
        promedio = Double.parseDouble(roundedPromedio);

        publicacion.setPromedioRating(promedio);
        notificacionesService.crearNotificacionPorReview(publicacion.getAlojamientoP().getPropietario().
                getId(),publicacion.getId(),"Se ha creado una nueva reseña en tu publicación");
        publicacionAlojamientoRespositorio.save(publicacion);
        return review.getId();
    }

    public ResponseReviewDTO getReview(Long publicacionAiD, Long reviewId){
        PublicacionAlojamiento alojamiento = publicacionAlojamientoRespositorio.findById(publicacionAiD).
                orElseThrow(()->new EntityNotFoundException("Publicacion de alojamiento no encontrado"));
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new EntityNotFoundException("Review no encontrada"));
        for(Review r: alojamiento.getReviews()){
            if(!r.getId().equals(review.getId())){
                throw new IllegalArgumentException("El review no pertenece al alojamiento");
            }
        }
        return mapToResponseDTO(review);
    }

    public Page<ResponseReviewDTO> obtenerReviewsPorPublicacionId(Long publicacionAId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
        reviewRepository.findById(publicacionAId).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        Page<Review> reviews = reviewRepository.findByPublicacionAlojamientoId(publicacionAId, pageable);
        List<ResponseReviewDTO> reviewsContent = new ArrayList<>(reviews.map(this::mapToResponseDTO).getContent());

        while (reviewsContent.size() < size) {
            ResponseReviewDTO defaultReview = reviewsContent.get(reviewsContent.size() - 1);
            reviewsContent.add(defaultReview);
        }

        return new PageImpl<>(reviewsContent, pageable, reviews.getTotalElements());
    }
    public void eliminarReseña(Long id){
        reviewRepository.deleteById(id);
    }
    public void actualizarContenido(Long publicacionAlojamientoId, Long reviewId,String contenido){
        Optional<PublicacionAlojamiento> publicacionAlojamientoOptional = publicacionAlojamientoRespositorio.findById(publicacionAlojamientoId);
        if (publicacionAlojamientoOptional.isEmpty()) {
            throw new PublicacionAlojamientoNotFoundException("Publicación no encontrada");
        }

        PublicacionAlojamiento publicacionAlojamiento = publicacionAlojamientoOptional.get();
        List<Review> reviews = publicacionAlojamiento.getReviews();

        Optional<Review> reviewOptional = reviews.stream()
                .filter(review -> review.getId().equals(reviewId))
                .findAny();

        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setComentario(contenido);
            reviewRepository.save(review);
        } else {
            throw new ReviewNotFoundException("Revisión no encontrada en esta publicación");
        }
    }

    public void actualizarRating(Long publicacionAlojamientoId, Long reviewId,Integer rating){
        Optional<PublicacionAlojamiento> publicacionAlojamientoOptional = publicacionAlojamientoRespositorio.findById(publicacionAlojamientoId);
        if (publicacionAlojamientoOptional.isEmpty()) {
            throw new PublicacionAlojamientoNotFoundException("Publicación no encontrada");
        }

        PublicacionAlojamiento publicacionAlojamiento = publicacionAlojamientoOptional.get();
        List<Review> reviews = publicacionAlojamiento.getReviews();

        Optional<Review> reviewOptional = reviews.stream()
                .filter(review -> review.getId().equals(reviewId))
                .findAny();
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            if(rating<5 && rating>1){
                review.setCalificacion(rating);
                reviewRepository.save(review);
                int sum = 0;
                for(Review r : publicacionAlojamiento.getReviews()) {
                    sum += r.getCalificacion();
                }
                double promedio = (double) sum / publicacionAlojamiento.getReviews().size();

                DecimalFormat df = new DecimalFormat("#.##");
                String roundedPromedio = df.format(promedio);
                promedio = Double.parseDouble(roundedPromedio);

                publicacionAlojamiento.setPromedioRating(promedio);

                publicacionAlojamientoRespositorio.save(publicacionAlojamiento);

            }
            else{
                throw new IllegalArgumentException("Rating no valido");
            }
        } else {
            throw new ReviewNotFoundException("Revisión no encontrada en esta publicación");
        }
    }
    public List<ResponseReviewDTO> obtenerReviewsPorAutorId(Long autorId){
        User autor = userRepository.findById(autorId).orElseThrow(()->new EntityNotFoundException("Autor no encontrado"));
        List<Review> reviews = reviewRepository.findByAutorR(autor);
        List<ResponseReviewDTO> responseReviewDTOS = new ArrayList<>();
        for(Review review: reviews){
            responseReviewDTOS.add(mapToResponseDTO(review));
        }
        return responseReviewDTOS;
    }
    public List<ResponseReviewDTO> obtenerReviewsRecientes(Long publicacionAlojamientoId){
        Pageable topFive = PageRequest.of(0, 5);

        List<Review> reviews = reviewRepository.findTop5ByPublicacionAlojamientoIdOrderByFechaDesc(publicacionAlojamientoId, topFive).getContent();
        List<ResponseReviewDTO> responseReviewDTOS = new ArrayList<>();
        for(Review review: reviews){
            responseReviewDTOS.add(mapToResponseDTO(review));
        }
        return responseReviewDTOS;
    }
    public List<ResponseReviewDTO> obtenerReviewsAlojIdPorCalificacion(Long publicacionId, Integer calificacion){
        List<Review> reviews = reviewRepository.findByPublicacionAlojamientoIdAndCalificacion(publicacionId,calificacion);
        List<ResponseReviewDTO> responseReviewDTOS = new ArrayList<>();
        for(Review review: reviews){
            responseReviewDTOS.add(mapToResponseDTO(review));
        }
        return responseReviewDTOS;
    }
    public List<ResponseReviewDTO> obtenerReviewsPorCalificacion(Integer calificacion){
        List<Review> reviews = reviewRepository.findByCalificacion(calificacion);
        List<ResponseReviewDTO> responseReviewDTOS = new ArrayList<>();
        for(Review review: reviews){
            responseReviewDTOS.add(mapToResponseDTO(review));
        }
        return responseReviewDTOS;
    }
    public Double obtenerPromedioCalificacion(Long publicacionId){
        PublicacionAlojamiento publicacionAlojamiento = publicacionAlojamientoRespositorio.findById(publicacionId).
                orElseThrow(()->new EntityNotFoundException("Publicacion no encontrada"));
        return publicacionAlojamiento.getPromedioRating();
    }

    private ResponseReviewDTO mapToResponseDTO(Review review) {
        ResponseReviewDTO dto = new ResponseReviewDTO();
        dto.setAutorFullname(review.getAutorR().getUsername());
        dto.setContenido(review.getComentario());
        dto.setCalificacion(review.getCalificacion());
        if (!review.getAutorR().getFotoUrl().isEmpty()) {
            dto.setAutorFotoUrl(review.getAutorR().getFotoUrl());
        } else {
            dto.setAutorFotoUrl(null);
        }
        dto.setDateTime(review.getFecha().atZone(ZoneId.systemDefault()));
        return dto;
    }

}
