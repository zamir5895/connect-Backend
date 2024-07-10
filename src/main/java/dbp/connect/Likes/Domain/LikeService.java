package dbp.connect.Likes.Domain;

import dbp.connect.Likes.DTOS.LikeResponseDTO;
import dbp.connect.Likes.DTOS.LikesOfUserDTO;
import dbp.connect.Likes.Infrastructure.LikeRepositorio;
import dbp.connect.Notificaciones.Domain.NotificacionesService;
import dbp.connect.PublicacionInicio.Domain.PublicacionInicio;
import dbp.connect.PublicacionInicio.Infrastructure.PublicacionInicioRepositorio;
import dbp.connect.User.Domain.User;
import dbp.connect.User.Infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class LikeService {
    @Autowired
    private LikeRepositorio likeRepositorio;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PublicacionInicioRepositorio publicacionInicioRepositorio;
    @Autowired
    private NotificacionesService notificacionesService;

    @Async
    public void processLikeAsync(Long publicacionInicioId, Long usuarioLikeId) {

        User userLike = userRepository.findById(usuarioLikeId).orElseThrow(()
                -> new IllegalArgumentException("User not found"));
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionInicioId).orElseThrow(()
                -> new IllegalArgumentException("PublicacionInicio not found"));
        publicacionInicio.setCantidadLikes(publicacionInicio.getCantidadLikes() + 1);
        Like like = new Like();
        like.setPublicacionInicio(publicacionInicio);
        like.setUsuarioLike(userLike);
        like.setFechaLike(ZonedDateTime.now(ZoneId.systemDefault()));
        likeRepositorio.save(like);
        publicacionInicio.getLikes().add(like);
        publicacionInicioRepositorio.save(publicacionInicio);
        notificacionesService.crearNotificacionPorLike(publicacionInicio.getAutorP().getId(), publicacionInicioId,
                "A " + userLike.getUsername() + " le ha gustado tu publicacion");
        System.out.println("Procesando like asincrono para publicacionInicioId: " + publicacionInicioId + " y usuarioLikeId: " + usuarioLikeId);
    }
    public LikeResponseDTO findLikeById(Long likeId) {
        Like like = likeRepositorio.findById(likeId).orElseThrow(()
                -> new IllegalArgumentException("Like not found"));
        LikeResponseDTO likeResponseDTO = new LikeResponseDTO();
        likeResponseDTO.setId(like.getId());
        likeResponseDTO.setPublicacionInicioId(like.getPublicacionInicio().getId());
        likeResponseDTO.setFechaLike(like.getFechaLike());
        likeResponseDTO.setUsuarioLikeId(like.getUsuarioLike().getId());
        likeResponseDTO.setUsuarioLikeUsername(like.getUsuarioLike().getUsername());
        likeResponseDTO.setUsuarioFotoPerfil(like.getUsuarioLike().getFotoUrl());
        return likeResponseDTO;
    }
    @Async
    public void deleteLikeByIdAsync(Long likeId) {
        Like like = likeRepositorio.findById(likeId).orElseThrow(()
                -> new IllegalArgumentException("Like not found"));
        PublicacionInicio publicacionInicio = like.getPublicacionInicio();
        publicacionInicio.setCantidadLikes(publicacionInicio.getCantidadLikes() - 1);
        likeRepositorio.deleteById(likeId);
        publicacionInicio.getLikes().remove(like);
        publicacionInicioRepositorio.save(publicacionInicio);

        System.out.println("Procesando like asincrono para likeId: " + likeId);
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
                    dto.setUsuarioLikeId(like.getUsuarioLike().getId());
                    dto.setUsuarioLikeUsername(like.getUsuarioLike().getUsername());
                    dto.setUsuarioFotoPerfil(like.getUsuarioLike().getFotoUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    public List<LikesOfUserDTO> findLikesByUsuarioLikeId(Long usuarioLikeId) {
        User user = userRepository.findById(usuarioLikeId).orElseThrow(()
                -> new IllegalArgumentException("User not found"));

        return user.getLikes().stream()
                .map(like -> {
                    LikesOfUserDTO dto = new LikesOfUserDTO();
                    dto.setLikeId(like.getId());
                    dto.setFechaLike(like.getFechaLike());
                    dto.setPublicacionInicioId(like.getPublicacionInicio().getId());
                    dto.setDescripcion(like.getPublicacionInicio().getCuerpo());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    public Integer findLikesCountByPublicacionInicioId(Long publicacionInicioId) {
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionInicioId)
                .orElseThrow(() -> new IllegalArgumentException("PublicacionInicio not found"));
        Integer likes = publicacionInicio.getLikes().size();
        return likes;
    }
    public List<LikeResponseDTO> findLikesByDateRange(ZonedDateTime inicio, ZonedDateTime fin) {
        List<Like> likes = likeRepositorio.findByFechaLikeBetween(inicio, fin);
        return likes.stream()
                .map(like -> {
                    LikeResponseDTO dto = new LikeResponseDTO();
                    dto.setId(like.getId());
                    dto.setFechaLike(like.getFechaLike());
                    dto.setPublicacionInicioId(like.getPublicacionInicio().getId());
                    dto.setUsuarioLikeId(like.getUsuarioLike().getId());
                    dto.setUsuarioLikeUsername(like.getUsuarioLike().getUsername());
                    dto.setUsuarioFotoPerfil(like.getUsuarioLike().getFotoUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    public List<LikeResponseDTO> findRecentLikesByUsuario(Long usuarioLikeId, int limit) {
        User user = userRepository.findById(usuarioLikeId).orElseThrow(()
                -> new IllegalArgumentException("User not found"));
        List<Like> likes = user.getLikes().stream()
                .sorted((l1, l2) -> l2.getFechaLike().compareTo(l1.getFechaLike()))
                .limit(limit)
                .collect(Collectors.toList());
        return likes.stream().map(like -> {
            LikeResponseDTO dto = new LikeResponseDTO();
            dto.setId(like.getId());
            dto.setFechaLike(like.getFechaLike());
            dto.setPublicacionInicioId(like.getPublicacionInicio().getId());
            dto.setUsuarioLikeId(like.getUsuarioLike().getId());
            dto.setUsuarioLikeUsername(like.getUsuarioLike().getUsername());
            dto.setUsuarioFotoPerfil(like.getUsuarioLike().getFotoUrl());
            return dto;
        }).collect(Collectors.toList());
    }
    public Integer totalLikes(Long publicacionId){

        return likeRepositorio.countByPublicacionInicioId(publicacionId);
    }

}
