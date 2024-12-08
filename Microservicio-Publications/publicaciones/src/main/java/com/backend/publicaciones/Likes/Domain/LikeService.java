package com.backend.publicaciones.Likes.Domain;

import com.backend.publicaciones.Likes.DTOS.LikeResponseDTO;
import com.backend.publicaciones.Likes.Infrastructure.LikeRepositorio;
import com.backend.publicaciones.PublicacionInicio.Domain.PublicacionInicio;
import com.backend.publicaciones.PublicacionInicio.Infrastructure.PublicacionInicioRepositorio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class LikeService {
    @Autowired
    private LikeRepositorio likeRepositorio;

    @Autowired
    private PublicacionInicioRepositorio publicacionInicioRepositorio;


    @Transactional
    public boolean processLikeSync(Long publicacionInicioId, Long usuarioLikeId) {
        try {
            boolean exists = likeRepositorio.existsByPublicacionIdAndUsuarioId(publicacionInicioId, usuarioLikeId);
            if (exists) {
                System.out.println("El like ya existe para publicacionInicioId: " + publicacionInicioId + " y usuarioLikeId: " + usuarioLikeId);
                return false;
            }

            PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionInicioId)
                    .orElseThrow(() -> new IllegalArgumentException("PublicacionInicio not found"));

            Like like = new Like();
            like.setPublicacionInicio(publicacionInicio);
            like.setUsuarioLike(usuarioLikeId);
            like.setFechaLike(ZonedDateTime.now(ZoneId.systemDefault()));
            likeRepositorio.save(like);

            publicacionInicio.setCantidadLikes(publicacionInicio.getCantidadLikes() + 1);
            publicacionInicio.getLikes().add(like);
            publicacionInicioRepositorio.save(publicacionInicio);

            System.out.println("Like creado exitosamente para publicacionInicioId: " + publicacionInicioId + " y usuarioLikeId: " + usuarioLikeId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Indica que ocurrió un error
        }
    }

    public LikeResponseDTO findLikeById(Long likeId) {
        Like like = likeRepositorio.findById(likeId).orElseThrow(()
                -> new IllegalArgumentException("Like not found"));
        LikeResponseDTO likeResponseDTO = new LikeResponseDTO();
        likeResponseDTO.setId(like.getId());
        likeResponseDTO.setPublicacionInicioId(like.getPublicacionInicio().getId());
        likeResponseDTO.setFechaLike(like.getFechaLike());
        likeResponseDTO.setUsuarioLikeId(like.getUsuarioLike());
        return likeResponseDTO;
    }

    @Transactional
    public boolean deleteLikesByPublicacionIdAndUsuarioId(Long publicacionId, Long usuarioId) {
        List<Long> likeIds = likeRepositorio.findIdsByPublicacionIdAndUsuarioId(publicacionId, usuarioId);
        PublicacionInicio inicio = publicacionInicioRepositorio.findById(publicacionId).orElseThrow(()-> new EntityNotFoundException("La publicacion no existe "));
        if (!likeIds.isEmpty()) {
            likeIds.forEach(likeRepositorio::deleteById);
            return true;
        } else {
            return false;
        }
    }


    public List<LikeResponseDTO> findLikesByPublicacionInicioId(Long publicacionInicioId) {
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionInicioId)
                .orElseThrow(() -> new IllegalArgumentException("PublicacionInicio not found"));

        return publicacionInicio.getLikes().stream()
                .map(like -> {
                    LikeResponseDTO dto = new LikeResponseDTO();
                    dto.setId(like.getId());
                    dto.setFechaLike(like.getFechaLike());
                    dto.setPublicacionInicioId(like.getPublicacionInicio().getId());
                    dto.setUsuarioLikeId(like.getUsuarioLike());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public Integer findLikesCountByPublicacionInicioId(Long publicacionInicioId) {
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionInicioId)
                .orElseThrow(() -> new IllegalArgumentException("PublicacionInicio not found"));
        return publicacionInicio.getLikes().size();
    }

    public List<LikeResponseDTO> findLikesByDateRange(ZonedDateTime inicio, ZonedDateTime fin) {
        List<Like> likes = likeRepositorio.findByFechaLikeBetween(inicio, fin);
        return likes.stream()
                .map(like -> {
                    LikeResponseDTO dto = new LikeResponseDTO();
                    dto.setId(like.getId());
                    dto.setFechaLike(like.getFechaLike());
                    dto.setPublicacionInicioId(like.getPublicacionInicio().getId());
                    dto.setUsuarioLikeId(like.getUsuarioLike());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Boolean existsLike(Long publicacionId, Long userId){
         return likeRepositorio.existsByPublicacionIdAndUsuarioId(publicacionId, userId);
    }


}
