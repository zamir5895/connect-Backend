package com.backend.places.Review.Domain;

import com.backend.places.Alojamiento.Domain.AlojamientoServicio;
import com.backend.places.PublicacionAlojamiento.Domain.PublicacionAlojamiento;
import com.backend.places.PublicacionAlojamiento.Exceptions.PublicacionAlojamientoNotFoundException;
import com.backend.places.PublicacionAlojamiento.Infrastructure.PublicacionAlojamientoRespositorio;
import com.backend.places.Review.DTOS.ResponseReviewDTO;
import com.backend.places.Review.DTOS.ReviewRequest;
import com.backend.places.Review.Exceptions.ReviewNotFoundException;
import com.backend.places.Review.Infrastructure.ReviewRepository;
import com.backend.places.UserInfo;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private AlojamientoServicio alojamientoServicio;

    public Long createReview( ReviewRequest reviewRequest) throws BadRequestException {
        Optional<PublicacionAlojamiento> publicacionAlojamiento = publicacionAlojamientoRespositorio.findById(reviewRequest.getPublicacionId());
        if (publicacionAlojamiento.isEmpty()) {
            throw new PublicacionAlojamientoNotFoundException("Publicacion no encontrada");
        }
        if(reviewRequest.getAutorId()==null || reviewRequest.getAutorId()==0){
            throw new BadRequestException("No existe el usuario");
        }
        try{
            UserInfo userInfo = alojamientoServicio.obtenerUsuarioById(reviewRequest.getAutorId());
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("No existe el usuario");
        }
        PublicacionAlojamiento publicacion = publicacionAlojamiento.get();

        Review review = new Review();
        review.setCalificacion(reviewRequest.getRating());
        review.setAutorR(reviewRequest.getAutorId());
        review.setComentario(reviewRequest.getContent());
        review.setPublicacionAlojamiento(publicacion);
        review.setFecha(ZonedDateTime.now(ZoneId.systemDefault()));
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
        Page<Review> reviews = reviewRepository.findByPublicacionAlojamientoId(publicacionAId, pageable);
        List<ResponseReviewDTO> reviewsContent = new ArrayList<>(reviews.
                map(this::mapToResponseDTO).getContent());

        return new PageImpl<>(reviewsContent, pageable, reviews.getTotalElements());
    }


    public void eliminarReseña(Long id){
        Review review = reviewRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Review no encontrada"));
        PublicacionAlojamiento publicacionAlojamiento = review.getPublicacionAlojamiento();
        publicacionAlojamiento.setCantidadReseñas(publicacionAlojamiento.getCantidadReseñas()-1);
        publicacionAlojamiento.getReviews().remove(review);
        int sum = 0;
        for(Review r : publicacionAlojamiento.getReviews()) {
            sum += r.getCalificacion();
        }
        double promedio = (double) sum / publicacionAlojamiento.getCantidadReseñas();
        DecimalFormat df = new DecimalFormat("#.##");
        String roundedPromedio = df.format(promedio);
        promedio = Double.parseDouble(roundedPromedio);
        publicacionAlojamiento.setPromedioRating(promedio);
        publicacionAlojamientoRespositorio.save(publicacionAlojamiento);
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
        List<Review> reviews = reviewRepository.findByAutorR(autorId);
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

    public Integer cantidadReviewsByPublicacionId(Long publicacionId){
        return reviewRepository.countByPublicacionAlojamientoId(publicacionId);
    }

    private ResponseReviewDTO mapToResponseDTO(Review review) {
        ResponseReviewDTO dto = new ResponseReviewDTO();
        dto.setReviewId(review.getId());
        dto.setContenido(review.getComentario());
        dto.setCalificacion(review.getCalificacion());
        dto.setPublicacionId(review.getPublicacionAlojamiento().getId());
        dto.setAutorId(review.getAutorR());

        dto.setDateTime(review.getFecha());
        System.out.println(review.getAutorR());
        UserInfo info = alojamientoServicio.obtenerUsuarioById(review.getAutorR());
        dto.setAutorFotoUrl(info.getFotoPerfil());
        dto.setAutorName(info.getUserFullName());

        return dto;
    }



}
