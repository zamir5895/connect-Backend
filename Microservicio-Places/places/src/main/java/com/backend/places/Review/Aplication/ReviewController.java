package com.backend.places.Review.Aplication;

import com.backend.places.Review.DTOS.ContenidoDTO;
import com.backend.places.Review.DTOS.ResponseReviewDTO;
import com.backend.places.Review.DTOS.ReviewRequest;
import com.backend.places.Review.Domain.ReviewServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/api/review")
public class ReviewController {
    @Autowired
    private ReviewServicio reviewServicio;

    @PostMapping()
    public ResponseEntity<Void> createReview(@RequestBody ReviewRequest reviewDTO) {
        try {
            Long reviewId = reviewServicio.createReview(reviewDTO);
            URI location = URI.create("/review/" + reviewId);
            return ResponseEntity.created(location).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{publicacionAlojamientoId}")
    public ResponseEntity<Page<ResponseReviewDTO>> obtenerReviews(
            @PathVariable Long publicacionAlojamientoId,
            @RequestParam Integer page,
            @RequestParam Integer size) {
        try {
            System.out.println(publicacionAlojamientoId);
            System.out.println("ENtrando 2");
            Page<ResponseReviewDTO> reviews = reviewServicio.obtenerReviewsPorPublicacionId(publicacionAlojamientoId, page, size);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("{publicacionAlojamientoId}/{reviewId}")
    public ResponseEntity<ResponseReviewDTO> obtenerReview(@PathVariable Long publicacionAlojamientoId, @PathVariable Long reviewId) {
        try {
            ResponseReviewDTO dto = reviewServicio.getReview(publicacionAlojamientoId, reviewId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> eliminarReviewById(@PathVariable Long reviewId) {
        try {
            reviewServicio.eliminarReseña(reviewId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("{publicacionAId}/{reviewId}")
    public ResponseEntity<Void> actualizarContenido(@PathVariable Long publicacionAId,
                                                    @PathVariable Long reviewId,
                                                    @RequestBody ContenidoDTO contenido) {
        try {
            reviewServicio.actualizarContenido(publicacionAId, reviewId, contenido.getContenido());
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{publicacionId}/{reviewId}/calificacion")
    public ResponseEntity<Void> actualizarCalificacion(@PathVariable Long reviewId,
                                                       @PathVariable Long publicacionId,
                                                       @RequestParam Integer calificacion) {
        try {
            reviewServicio.actualizarRating(reviewId, publicacionId, calificacion);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<ResponseReviewDTO>> obtenerReviewsPorAutor(@PathVariable Long autorId) {
        try {
            List<ResponseReviewDTO> reviews = reviewServicio.obtenerReviewsPorAutorId(autorId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{publicacionAlojId}/recientes")
    public ResponseEntity<List<ResponseReviewDTO>> obtenerReviewsRecientes(@PathVariable Long publicacionAlojId) {
        try {
            List<ResponseReviewDTO> reviews = reviewServicio.obtenerReviewsRecientes(publicacionAlojId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/calificacion")
    public ResponseEntity<List<ResponseReviewDTO>> obtenerReviewsPorCalificacion(@RequestParam Integer calificacion) {
        try {
            List<ResponseReviewDTO> reviews = reviewServicio.obtenerReviewsPorCalificacion(calificacion);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/calificacion/{publicacionId}")
    public ResponseEntity<List<ResponseReviewDTO>> obtenerMejoresReviews(@PathVariable Long publicacionId,
                                                                         @RequestParam Integer calificacion) {
        try {
            List<ResponseReviewDTO> responseReviewDTO = reviewServicio.obtenerReviewsAlojIdPorCalificacion(publicacionId, calificacion);
            return ResponseEntity.ok(responseReviewDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



}
