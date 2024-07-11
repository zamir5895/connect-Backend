package dbp.connect.PublicacionInicio.Domain;

import dbp.connect.Comentarios.Excepciones.PublicacionNoEncontradoException;
import dbp.connect.Excepciones.NoEncontradoException;
import dbp.connect.Friendship.Domain.Friendship;
import dbp.connect.PublicacionInicio.DTOS.PostInicioDTO;
import dbp.connect.PublicacionInicio.DTOS.PublicacionInicioResponseDTO;
import dbp.connect.PublicacionInicio.Exceptions.UsuarioNoCoincideId;
import dbp.connect.PublicacionInicio.Infrastructure.PublicacionInicioRepositorio;
import dbp.connect.PublicacionInicioMultimedia.DTOS.MultimediaInicioDTO;
import dbp.connect.PublicacionInicioMultimedia.Domain.PublicacionInicioMultimedia;
import dbp.connect.PublicacionInicioMultimedia.Domain.PublicacionInicioMultimediaServicio;
import dbp.connect.PublicacionInicioMultimedia.Infrastructure.PublicacionInicioMultimediaRepositorio;
import dbp.connect.Security.Utils.AuthorizationUtils;
import dbp.connect.User.Domain.User;
import dbp.connect.User.Infrastructure.UserRepository;
import jakarta.persistence.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class PublicacionInicioServicio {
    @Autowired
    private PublicacionInicioRepositorio publicacionInicioRepositorio;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PublicacionInicioMultimediaServicio publicacionInicioMultimediaServicio;
    @Autowired
    private PublicacionInicioMultimediaRepositorio publicacionInicioMultimediaRepositorio;
    @Autowired
    private AuthorizationUtils authorizationUtils;

    public void createPostInicioDTO(PostInicioDTO postInicioDTO){
        User user = userRepository.findById(postInicioDTO.getAutorPId()).orElseThrow(()->new EntityNotFoundException("El usuario no existe"));
        PublicacionInicio publicacionInicio = new PublicacionInicio();
        publicacionInicio.setAutorP(user);
        publicacionInicio.setCantidadComentarios(0);
        publicacionInicio.setCantidadLikes(0);
        if(!postInicioDTO.getMultimediaList().isEmpty()){
            for(MultipartFile file : postInicioDTO.getMultimediaList()){
                PublicacionInicioMultimedia multimediaInicio = publicacionInicioMultimediaServicio.guardarArchivo(file);
                publicacionInicio.getPublicacionMultimedia().add(multimediaInicio);
                multimediaInicio.setPublicacionInicio(publicacionInicio);
                publicacionInicioMultimediaRepositorio.save(multimediaInicio);
            }
        }
        publicacionInicio.setCuerpo(postInicioDTO.getCuerpo());
        publicacionInicio.setFechaPublicacion(ZonedDateTime.now(ZoneId.systemDefault()));
        publicacionInicioRepositorio.save(publicacionInicio);
        user.getPublicacionInicio().add(publicacionInicio);
        userRepository.save(user);
        System.out.println("Publicacion creada");
    }
    public Page<PublicacionInicioResponseDTO> obtenerPublicacionesInicio(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<PublicacionInicio> publicaciones = publicacionInicioRepositorio.findAll(pageable);

        List<PublicacionInicioResponseDTO> publicacionesInicio = publicaciones.getContent().stream()
                .map(this::converToDto)
                .collect(Collectors.toList());

        while (publicacionesInicio.size() < size && !publicacionesInicio.isEmpty()) {
            PublicacionInicioResponseDTO defaultPublicacion = publicacionesInicio.get(publicacionesInicio.size() - 1);
            publicacionesInicio.add(defaultPublicacion);
        }

        return new PageImpl<>(publicacionesInicio, pageable, publicaciones.getTotalElements());
    }

    public PublicacionInicioResponseDTO obtenerPublciacionesInicio(Long publicacionId){
        PublicacionInicio inicio = publicacionInicioRepositorio.findById(publicacionId).
                orElseThrow(()-> new EntityNotFoundException("Publicacion no encontrada"));

        return converToDto(inicio);
    }
    public void eliminarPublicacionInicio(Long id){
        Optional<PublicacionInicio> inicio = publicacionInicioRepositorio.findById(id);
        if(inicio.isPresent()) {
            publicacionInicioRepositorio.deleteById(id);
        }
        else{
            throw new NoSuchElementException("No existe la publicacion");
        }
    }
    public Page<PublicacionInicioResponseDTO> obtenerPublicacionByUsuario(Long usuarioId, Integer page, Integer size){
        User usuario = userRepository.findById(usuarioId).orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado"));
        Pageable pageable = PageRequest.of(page, size);
        Page<PublicacionInicio> publicaciones = publicacionInicioRepositorio.findByAutorP_Id(usuarioId, pageable);
        if(publicaciones.isEmpty()){
            throw new RuntimeException(usuario.getUsername()+ "No tiene publicaciones");
        }
        List<PublicacionInicioResponseDTO> publicacionesInicio = publicaciones.getContent().stream()
                .map(this::converToDto)
                .collect(Collectors.toList());

        while (publicacionesInicio.size() < size && !publicacionesInicio.isEmpty()) {
            PublicacionInicioResponseDTO defaultPublicacion = publicacionesInicio.get(publicacionesInicio.size() - 1);
            publicacionesInicio.add(defaultPublicacion);
        }

        return new PageImpl<>(publicacionesInicio, pageable, publicaciones.getTotalElements());
    }

    public PublicacionInicioResponseDTO actualizarContenido(Long usuarioId, Long publicacionId, String contenido){
        User usuario = userRepository.findById(usuarioId).orElseThrow(()-> new EntityNotFoundException("Usuario no encontrado"));
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionId).orElseThrow(()->new EntityNotFoundException("Publicacion no encontrada"));
        if(publicacionInicio.getAutorP().getId()!=usuario.getId()){
            throw new UsuarioNoCoincideId("No es el autor de esta publicacion");
        }
        publicacionInicio.setCuerpo(contenido);
        publicacionInicioRepositorio.save(publicacionInicio);
        return converToDto(publicacionInicio);
    }
    public PublicacionInicioResponseDTO actualizarMultimedia(Long usuarioId, Long publicacionId, List<MultipartFile> multimedia) {
        PublicacionInicio publicacion = publicacionInicioRepositorio.findById(publicacionId)
                .orElseThrow(() -> new PublicacionNoEncontradoException("Publicacion no encontrada con id: " + publicacionId));

        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoCoincideId("Usuario no encontrado con id: " + usuarioId));

        if (!publicacion.getAutorP().getId().equals(usuarioId)) {
            throw new UsuarioNoCoincideId("Usuario no autorizado para modificar esta publicacion");
        }
        if(!multimedia.isEmpty()){
            for(MultipartFile file : multimedia){
                publicacion.getPublicacionMultimedia().removeAll(publicacion.getPublicacionMultimedia());
                PublicacionInicioMultimedia multimediaInicio = publicacionInicioMultimediaServicio.guardarArchivo(file);
                publicacion.getPublicacionMultimedia().add(multimediaInicio);
                multimediaInicio.setPublicacionInicio(publicacion);
                publicacionInicioMultimediaRepositorio.save(multimediaInicio);
            }
        }
        publicacionInicioRepositorio.save(publicacion);

        return converToDto(publicacion);
    }

    public Page<PublicacionInicioResponseDTO> buscarPorPalabraClave(String palabraClave, Pageable pageable){

        Page<PublicacionInicio> publicaciones = publicacionInicioRepositorio.findByCuerpoContaining(palabraClave, pageable);

        List<PublicacionInicioResponseDTO> publicacionesInicio = publicaciones.getContent().stream()
                .map(this::converToDto)
                .collect(Collectors.toList());

        while (publicacionesInicio.size() < pageable.getPageSize() && !publicacionesInicio.isEmpty()) {
            PublicacionInicioResponseDTO defaultPublicacion = publicacionesInicio.get(publicacionesInicio.size() - 1);
            publicacionesInicio.add(defaultPublicacion);
        }

        return new PageImpl<>(publicacionesInicio, pageable, publicaciones.getTotalElements());
    }
    public Page<PublicacionInicioResponseDTO> encontrarTodos(int page , int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<PublicacionInicio> publicaciones = publicacionInicioRepositorio.findAll(pageable);
        List<PublicacionInicioResponseDTO> publicacionesInicio = publicaciones.getContent().stream()
                .map(this::converToDto)
                .collect(Collectors.toList());

        while (publicacionesInicio.size() < size && !publicacionesInicio.isEmpty()) {
            PublicacionInicioResponseDTO defaultPublicacion = publicacionesInicio.get(publicacionesInicio.size() - 1);
            publicacionesInicio.add(defaultPublicacion);
        }

        return new PageImpl<>(publicacionesInicio, pageable, publicaciones.getTotalElements());
    }

    private PublicacionInicioResponseDTO converToDto(PublicacionInicio inicio){
        PublicacionInicioResponseDTO dto = new PublicacionInicioResponseDTO();
        dto.setId(inicio.getId());
        dto.setCantidadComentarios(inicio.getCantidadComentarios());
        dto.setCantidadLikes(inicio.getCantidadLikes());
        dto.setUsername(inicio.getAutorP().getUsername());
        dto.setContenido(inicio.getCuerpo());
        dto.setFechaPublicacion(inicio.getFechaPublicacion());
        if(inicio.getAutorP().getFotoUrl() != null){
            dto.setFotPerfilUrl(inicio.getAutorP().getFotoUrl());
        }
        else{
            dto.setFotPerfilUrl(null);
        }
        if(!inicio.getPublicacionMultimedia().isEmpty()){
            for(PublicacionInicioMultimedia multimedia : inicio.getPublicacionMultimedia()){
                dto.getMultimediaInicioDTO().add(converToDto(multimedia));
            }

        }
        return dto;
    }


    private MultimediaInicioDTO converToDto(PublicacionInicioMultimedia multimedia){
        MultimediaInicioDTO dto = new MultimediaInicioDTO();
        dto.setId(multimedia.getId());
        dto.setTipo(multimedia.getTipo());
        dto.setContenidoUrl(multimedia.getContenidoUrl());
        dto.setFechaCreacion(multimedia.getFechaCreacion());
        return dto;
    }
}
