package com.backend.publicaciones.PublicacionInicio.Domain;

import com.backend.publicaciones.Comentarios.Excepciones.PublicacionNoEncontradoException;

import com.backend.publicaciones.Comentarios.Infrastructure.ComentarioRepository;
import com.backend.publicaciones.Likes.Domain.LikeService;
import com.backend.publicaciones.PublicacionInicio.DTOS.*;
import com.backend.publicaciones.PublicacionInicio.Exceptions.UsuarioNoCoincideId;
import com.backend.publicaciones.PublicacionInicio.Infrastructure.PublicacionInicioRepositorio;
import com.backend.publicaciones.PublicacionInicioMultimedia.DTOS.MultimediaInicioDTO;
import com.backend.publicaciones.PublicacionInicioMultimedia.Domain.PublicacionInicioMultimedia;
import com.backend.publicaciones.PublicacionInicioMultimedia.Domain.PublicacionInicioMultimediaServicio;
import com.backend.publicaciones.PublicacionInicioMultimedia.Infrastructure.PublicacionInicioMultimediaRepositorio;
import jakarta.persistence.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PublicacionInicioServicio {
    @Autowired
    private PublicacionInicioRepositorio publicacionInicioRepositorio;
    @Autowired
    private PublicacionInicioMultimediaServicio publicacionInicioMultimediaServicio;
    @Autowired
    private PublicacionInicioMultimediaRepositorio publicacionInicioMultimediaRepositorio;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ComentarioRepository comentarioRepository;

    public PublicacionInicioResponseDTO createPostInicioDTO(PostInicioDTO postInicioDTO) {
        if (postInicioDTO.getAutorPId() == null || postInicioDTO.getCuerpo() == null) {
            throw new IllegalArgumentException("PostInicioDTO o sus atributos no pueden ser nulos");
        }
        try{
            obtenerUsuarioById(postInicioDTO.getAutorPId());

        }catch (Exception e ){
            throw new EntityNotFoundException("usuario no existe");
        }
        PublicacionInicio publicacionInicio = new PublicacionInicio();
        publicacionInicio.setAutorP(postInicioDTO.getAutorPId());
        publicacionInicio.setCantidadComentarios(0);
        publicacionInicio.setCantidadLikes(0);
        publicacionInicio.setCuerpo(postInicioDTO.getCuerpo());
        publicacionInicio.setFechaPublicacion(ZonedDateTime.now(ZoneId.systemDefault()));

        PublicacionInicio saved = publicacionInicioRepositorio.save(publicacionInicio);
        PublicacionInicioResponseDTO publicacionInicioResponseDTO = new PublicacionInicioResponseDTO();
        publicacionInicioResponseDTO.setId(saved.getId());
        publicacionInicioResponseDTO.setAutorId(saved.getAutorP());
        UserInfoDTO info = obtenerUsuarioById(postInicioDTO.getAutorPId());
        publicacionInicioResponseDTO.setAutorNombre(info.getUserFullName());
        publicacionInicioResponseDTO.setAutorFotoUrl(info.getFotoPerfil());
        publicacionInicioResponseDTO.setContenido(saved.getCuerpo());
        publicacionInicioResponseDTO.setFechaPublicacion(saved.getFechaPublicacion());
        List<MultimediaInicioDTO> dto = new ArrayList<>();
        publicacionInicioResponseDTO.setMultimediaInicioDTO(dto);
        publicacionInicioResponseDTO.setCantidadComentarios(0);
        publicacionInicioResponseDTO.setCantidadLikes(0);
        return publicacionInicioResponseDTO;
    }

    public DTOcreacionFiles addFilesToPost(Long publicacionId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("La lista de archivos no puede ser nula o vacía");
        }

        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionId)
                .orElseThrow(() -> new EntityNotFoundException("Publicación no existe"));

        List<PublicacionInicioMultimedia> multimediaList = files.stream()
                .map(file -> publicacionInicioMultimediaServicio.guardarArchivo(file, publicacionInicio))
                .collect(Collectors.toList());

        publicacionInicioRepositorio.save(publicacionInicio);

        DTOcreacionFiles dto = new DTOcreacionFiles();
        dto.setPublicacionId(publicacionInicio.getId());
        for (PublicacionInicioMultimedia multimedia : publicacionInicio.getPublicacionMultimedia()) {
            MultimediaInicioDTO multimediaDTO = new MultimediaInicioDTO();
            multimediaDTO.setId(multimedia.getId());
            multimediaDTO.setContenidoUrl(multimedia.getContenidoUrl());
            multimediaDTO.setTipo(multimedia.getTipo());
            multimediaDTO.setFechaCreacion(multimedia.getFechaCreacion());
            dto.getMultimediaInicioDTO().add(multimediaDTO);
        }
        return dto;
    }

    public PublicacionInicioResponseDTO obtenerPublciacionesInicio(Long publicacionId) {
        PublicacionInicio inicio = publicacionInicioRepositorio.findById(publicacionId).
                orElseThrow(() -> new EntityNotFoundException("Publicacion no encontrada"));
        UserInfoDTO dto = obtenerUsuarioById(inicio.getAutorP());

        return converToDtoIntegrated(inicio, dto);
    }

    public void eliminarPublicacionInicio(Long id) {
        Optional<PublicacionInicio> inicio = publicacionInicioRepositorio.findById(id);
        if (inicio.isPresent()) {
            publicacionInicioRepositorio.deleteById(id);
        } else {
            throw new NoSuchElementException("No existe la publicacion");
        }
    }


    public Page<PublicacionInicioResponseDTO> obtenerPublicacionByUsuario(Long usuarioId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PublicacionInicio> publicaciones = publicacionInicioRepositorio.findByAutorP(usuarioId, pageable);
        if (publicaciones.isEmpty()) {
            throw new RuntimeException(usuarioId + "No tiene publicaciones");
        }
        List<PublicacionInicioResponseDTO> publicacionesInicio = publicaciones.getContent().stream()
                .map(publicacion ->{
                    Long userId = publicacion.getAutorP();
                    UserInfoDTO dto = obtenerUsuarioById(userId);
                    return converToDtoIntegrated(publicacion, dto);

                }).collect(Collectors.toList());

        return new PageImpl<>(publicacionesInicio, pageable, publicaciones.getTotalElements());
    }

    public PublicacionInicioResponseDTO actualizarContenido(Long usuarioId, Long publicacionId, String contenido) {
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionId).orElseThrow(() -> new EntityNotFoundException("Publicacion no encontrada"));
        if (publicacionInicio.getAutorP() != usuarioId) {
            throw new UsuarioNoCoincideId("No es el autor de esta publicacion");
        }
        publicacionInicio.setCuerpo(contenido);
        publicacionInicioRepositorio.save(publicacionInicio);
        UserInfoDTO dto = obtenerUsuarioById(usuarioId);

        return converToDtoIntegrated(publicacionInicio, dto);
    }

    public PublicacionInicioResponseDTO actualizarMultimedia(Long usuarioId, Long publicacionId, List<MultipartFile> multimedia) {
        PublicacionInicio publicacion = publicacionInicioRepositorio.findById(publicacionId)
                .orElseThrow(() -> new PublicacionNoEncontradoException("Publicacion no encontrada con id: " + publicacionId));

        if (!publicacion.getAutorP().equals(usuarioId)) {
            throw new UsuarioNoCoincideId("Usuario no autorizado para modificar esta publicacion");
        }

        List<PublicacionInicioMultimedia> existingMultimedia = publicacion.getPublicacionMultimedia();
        publicacionInicioMultimediaRepositorio.deleteAll(existingMultimedia);
        publicacion.getPublicacionMultimedia().clear();

        if (!multimedia.isEmpty()) {
            List<PublicacionInicioMultimedia> newMultimediaList = multimedia.stream()
                    .map(file -> publicacionInicioMultimediaServicio.guardarArchivo(file, publicacion))
                    .collect(Collectors.toList());
        }

        publicacionInicioRepositorio.save(publicacion);

        UserInfoDTO dto = obtenerUsuarioById(usuarioId);

        return converToDtoIntegrated(publicacion, dto);
    }

    public Page<PublicacionInicioResponseDTO> buscarPorPalabraClave(String palabraClave, Pageable pageable) {

        Page<PublicacionInicio> publicaciones = publicacionInicioRepositorio.findByCuerpoContaining(palabraClave, pageable);

        List<PublicacionInicioResponseDTO> publicacionesInicio = publicaciones.getContent().stream()
                .map(publicacion ->{
                    Long userId = publicacion.getAutorP();
                    UserInfoDTO dto = obtenerUsuarioById(userId);
                    return converToDtoIntegrated(publicacion, dto);

                }).collect(Collectors.toList());
        return new PageImpl<>(publicacionesInicio, pageable, publicaciones.getTotalElements());
    }

    public Page<PublicacionInicioResponseDTO> encontrarTodos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PublicacionInicio> publicaciones = publicacionInicioRepositorio.findAllByOrderByFechaPublicacionDesc(pageable);
        List<PublicacionInicioResponseDTO> publicacionesInicio = publicaciones.getContent().stream()
                .map(publicacion ->{
                    Long userId = publicacion.getAutorP();
                    UserInfoDTO dto = obtenerUsuarioById(userId);
                    return converToDtoIntegrated(publicacion, dto);
                }).collect(Collectors.toList());

        return new PageImpl<>(publicacionesInicio, pageable, publicaciones.getTotalElements());
    }

    public Page<PublicacionInicioResponseDTO> encontrarPublicacionAMigos(AmigosDTO amigosDTO, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        List<PublicacionInicio> todasLasPublicaciones = publicacionInicioRepositorio.findPublicacionesPorAmigos(amigosDTO.getUsersid());

        Collections.shuffle(todasLasPublicaciones);

        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + size, todasLasPublicaciones.size());

        if (fromIndex > todasLasPublicaciones.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, todasLasPublicaciones.size());
        }

        List<PublicacionInicioResponseDTO> publicacionesDto = todasLasPublicaciones.subList(fromIndex, toIndex)
                .stream()
                .map(this::converToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(publicacionesDto, pageable, todasLasPublicaciones.size());
    }

    private PublicacionInicioResponseDTO converToDto(PublicacionInicio inicio) {
        PublicacionInicioResponseDTO dto = new PublicacionInicioResponseDTO();
        dto.setId(inicio.getId());
        dto.setCantidadComentarios(inicio.getCantidadComentarios());
        dto.setCantidadLikes(inicio.getCantidadLikes());
        dto.setContenido(inicio.getCuerpo());
        dto.setAutorId(inicio.getAutorP());
        dto.setFechaPublicacion(inicio.getFechaPublicacion());
        if (!inicio.getPublicacionMultimedia().isEmpty()) {
            for (PublicacionInicioMultimedia multimedia : inicio.getPublicacionMultimedia()) {
                dto.getMultimediaInicioDTO().add(converToDto(multimedia));
            }

        }
        return dto;
    }

    private UserInfoDTO obtenerUsuarioById(Long id) {
        String url = "http://localhost:8080/api/user/perfil/" + id;
        try {
            return restTemplate.getForObject(url, UserInfoDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener información del usuario", e);
        }
    }

    private PublicacionInicioResponseDTO converToDtoIntegrated(PublicacionInicio inicio, UserInfoDTO usuarioInfo) {
        PublicacionInicioResponseDTO dto = new PublicacionInicioResponseDTO();
        dto.setId(inicio.getId());
        dto.setCantidadComentarios(comentarioRepository.countByPublicacionId(inicio.getId()));
        dto.setCantidadLikes(likeService.findLikesCountByPublicacionInicioId(inicio.getId()));
        dto.setContenido(inicio.getCuerpo());
        dto.setAutorId(inicio.getAutorP());
        dto.setFechaPublicacion(inicio.getFechaPublicacion());
        if (!inicio.getPublicacionMultimedia().isEmpty()) {
            for (PublicacionInicioMultimedia multimedia : inicio.getPublicacionMultimedia()) {
                dto.getMultimediaInicioDTO().add(converToDto(multimedia));
            }

        }
        dto.setAutorFotoUrl(usuarioInfo.getFotoPerfil());
        dto.setAutorNombre(usuarioInfo.getUserFullName());
        return dto;
    }

    private MultimediaInicioDTO converToDto(PublicacionInicioMultimedia multimedia) {
        MultimediaInicioDTO dto = new MultimediaInicioDTO();
        dto.setId(multimedia.getId());
        dto.setTipo(multimedia.getTipo());
        dto.setContenidoUrl(multimedia.getContenidoUrl());
        dto.setFechaCreacion(multimedia.getFechaCreacion());
        return dto;
    }
}


