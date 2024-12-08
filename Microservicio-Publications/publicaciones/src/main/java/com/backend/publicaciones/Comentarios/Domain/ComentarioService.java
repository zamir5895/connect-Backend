package com.backend.publicaciones.Comentarios.Domain;

import com.backend.publicaciones.Comentarios.DTOS.CambioContenidoDTO;
import com.backend.publicaciones.Comentarios.DTOS.ComentarioDto;
import com.backend.publicaciones.Comentarios.DTOS.ComentarioRespuestaDTO;
import com.backend.publicaciones.Comentarios.Excepciones.ComentarioNoEncontradoException;
import com.backend.publicaciones.Comentarios.Excepciones.PublicacionNoEncontradoException;
import com.backend.publicaciones.Comentarios.Infrastructure.ComentarioRepository;
import com.backend.publicaciones.ComentariosMultimedia.DTOS.ResponseComMultimediaDTO;
import com.backend.publicaciones.ComentariosMultimedia.Domain.ComentarioMultimedia;
import com.backend.publicaciones.ComentariosMultimedia.Domain.ComentarioMultimediaServicio;
import com.backend.publicaciones.ComentariosMultimedia.Infrastructure.ComentarioMultimediaRepositorio;
import com.backend.publicaciones.PublicacionInicio.DTOS.UserInfoDTO;
import com.backend.publicaciones.PublicacionInicio.Domain.PublicacionInicio;
import com.backend.publicaciones.PublicacionInicio.Infrastructure.PublicacionInicioRepositorio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ComentarioService {
    @Autowired
    private ComentarioRepository comentarioRepository;
    @Autowired
    private PublicacionInicioRepositorio publicacionInicioRepositorio;
    @Autowired
    private ComentarioMultimediaServicio comentarioMultimediaServicio;
    @Autowired
    private ComentarioMultimediaRepositorio comentarioMultimediaRepositorio;
    @Autowired
    private RestTemplate restTemplate;


    public ComentarioRespuestaDTO createNewComentario(Long publicacionID, ComentarioDto comentarioDTO) {
        PublicacionInicio publicacion = publicacionInicioRepositorio.findById(publicacionID)
                .orElseThrow(() -> new PublicacionNoEncontradoException("Publicacion no encontrada"));

        Comentario comentario = new Comentario();
        comentario.setMessage(comentarioDTO.getMessage());
        comentario.setAutorId(comentarioDTO.getAutorId());
        comentario.setPublicacion(publicacion);
        comentario.setLikes(0);
        comentario.setDate(ZonedDateTime.now(ZoneId.systemDefault()));

        Comentario response = comentarioRepository.save(comentario);

        publicacion.setCantidadComentarios(publicacion.getCantidadComentarios() + 1);
        publicacionInicioRepositorio.save(publicacion);

        return convertToDto(response);
    }


    public ComentarioRespuestaDTO createNewComentarioHijo(Long publicacionID, Long parentId, ComentarioDto comentarioDTO) {
        PublicacionInicio publicacion = publicacionInicioRepositorio.findById(publicacionID)
                .orElseThrow(() -> new PublicacionNoEncontradoException("Publicacion no encontrada"));

        Comentario parentComentario = comentarioRepository.findById(parentId)
                .orElseThrow(() -> new ComentarioNoEncontradoException("Comentario padre no encontrado"));

        Comentario comentario = new Comentario();
        comentario.setMessage(comentarioDTO.getMessage());
        comentario.setAutorId(comentarioDTO.getAutorId());
        comentario.setPublicacion(publicacion);
        comentario.setLikes(0);
        comentario.setDate(ZonedDateTime.now(ZoneId.systemDefault()));
        comentario.setParent(parentComentario);

        Comentario comment = comentarioRepository.save(comentario);

        publicacion.setCantidadComentarios(publicacion.getCantidadComentarios() + 1);
        publicacionInicioRepositorio.save(publicacion);

        return convertToDto(comment);
    }


    public ResponseComMultimediaDTO agregarMultimediaToComment( Long comentarioId, MultipartFile file){
        Comentario comentario = comentarioRepository.findById(comentarioId).orElseThrow(()-> new EntityNotFoundException("Comentario  no encontrado"));

        comentarioMultimediaServicio.guardarArchivo(file, comentario);

        ComentarioMultimedia multi = comentarioMultimediaRepositorio.findByComentarioId(comentarioId).orElseThrow(()->new EntityNotFoundException("Comentario no encontrado"));
        ResponseComMultimediaDTO dto = new ResponseComMultimediaDTO();
        dto.setId(multi.getId());
        dto.setTipo(multi.getTipo());
        dto.setUrl_contenido(multi.getUrlContenido());
        return dto;
    }



    public Page<ComentarioRespuestaDTO> getComentario(Long publicacionId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comentario> comentarios = comentarioRepository.findByPublicacionId(publicacionId, pageable);

        List<ComentarioRespuestaDTO> comentariosContent = comentarios.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());


        return new PageImpl<>(comentariosContent, pageable, comentarios.getTotalElements());
    }

    public Page<ComentarioRespuestaDTO> getResponseComentarios(Long publicacionId, Long parentId, Integer page, Integer size) {
        Optional<PublicacionInicio> publicacionInicio = publicacionInicioRepositorio.findById(publicacionId);
        if (publicacionInicio.isEmpty()) {
            throw new PublicacionNoEncontradoException("No se encontraron respuestas para este comentario");
        }
        Optional<Comentario> comentarioPadreOptional = comentarioRepository.findById(parentId);
        if (comentarioPadreOptional.isEmpty()) {
            throw new ComentarioNoEncontradoException("Comentario padre no encontrado con ID: " + parentId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Comentario> respuestasPage = comentarioRepository.findByParentId(parentId, pageable);


        List<ComentarioRespuestaDTO> respuestasDTOs = respuestasPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(respuestasDTOs, pageable, respuestasPage.getTotalElements());
    }

    public void deleteComentarioById(Long publicacionID,Long comentarioId){
        Optional<PublicacionInicio> publicacionInicio = publicacionInicioRepositorio.findById(publicacionID);
        if (publicacionInicio.isPresent()) {
            PublicacionInicio publicacion = publicacionInicio.get();
            publicacion.setCantidadComentarios(publicacion.getCantidadComentarios() - 1);
            Optional<Comentario> comentario = comentarioRepository.findById(comentarioId);
            if (comentario.isPresent()) {
                Comentario comentarioInicio = comentario.get();
                if(comentarioInicio.getComentarioMultimedia() != null){
                    comentarioMultimediaServicio.eliminarArchivo(comentarioInicio.getId(), comentarioInicio.getComentarioMultimedia().getId());
                }
                publicacion.getComentarios().remove(comentarioInicio);
                comentarioRepository.deleteById(comentarioInicio.getId());
                publicacionInicioRepositorio.save(publicacion);
            }
            else{
                throw new ComentarioNoEncontradoException("Comentario no encontrado");
            }
        }
        else{
            throw new PublicacionNoEncontradoException("Publicacion no encontrado");
        }

    }

    public void deleteComentarioRespuestaById(Long publicacionID,Long parentId,Long comentarioId){
        Optional<PublicacionInicio> publicacionInicio = publicacionInicioRepositorio.findById(publicacionID);
        if (publicacionInicio.isPresent()) {
            PublicacionInicio publicacion = publicacionInicio.get();
            publicacion.setCantidadComentarios(publicacion.getCantidadComentarios() - 1);
            Optional<Comentario> comentarioPadreOptional = publicacion.getComentarios().stream()
                    .filter(comentario -> comentario.getId().equals(parentId))
                    .findFirst();

            if (comentarioPadreOptional.isPresent()) {
                Comentario comentarioPadre = comentarioPadreOptional.get();

                Optional<Comentario> respuestaOptional = comentarioPadre.getReplies().stream()
                        .filter(respuesta -> respuesta.getId().equals(comentarioId))
                        .findFirst();
                if (respuestaOptional.isPresent()) {
                    Comentario respuesta = respuestaOptional.get();
                    comentarioMultimediaServicio.eliminarArchivo(respuesta.getId(), respuesta.getComentarioMultimedia().getId());
                    comentarioPadre.getReplies().remove(respuesta);
                    comentarioRepository.deleteById(respuesta.getId());
                    publicacionInicioRepositorio.save(publicacion);
                    comentarioRepository.save(comentarioPadre);
                } else {
                    throw new ComentarioNoEncontradoException("Respuesta de comentario no encontrado");
                }
            } else {
                throw new ComentarioNoEncontradoException("Comentario no encontrado");
            }
        }
        else{
            throw new PublicacionNoEncontradoException("Publicacion no encontrado");
        }
    }

    public ComentarioRespuestaDTO actualizarComentario(Long publicacionId, Long comentarioId,
                                                       CambioContenidoDTO cambioContenidoDTO){
        Optional<PublicacionInicio> publicacionInicio= publicacionInicioRepositorio.findById(publicacionId);
        if (publicacionInicio.isPresent()) {
            PublicacionInicio publicacion = publicacionInicio.get();
            Optional<Comentario> comentario = comentarioRepository.findById(comentarioId);
            if (!comentario.isPresent()) {
                throw new ComentarioNoEncontradoException("Comentario no encontrado");
            }
            Comentario comentarioInicio = comentario.get();
            comentarioInicio.setMessage(cambioContenidoDTO.getContenido());
            comentarioRepository.save(comentarioInicio);
            publicacion.getComentarios().add(comentarioInicio);
            publicacionInicioRepositorio.save(publicacion);
            return convertToDto(comentarioInicio);
        }
        else{
            throw new PublicacionNoEncontradoException("Publicacion no encontrado");
        }
    }

    public ComentarioRespuestaDTO actualizarContenidoDeComentarioRespuesta(Long publicacionId, Long parentID,Long
            comentarioId,CambioContenidoDTO cambioContenidoDTO){
        Optional<PublicacionInicio> publicacionInicio = publicacionInicioRepositorio.findById(publicacionId);
        if (publicacionInicio.isEmpty()) {
            throw new PublicacionNoEncontradoException("Publicacion no encontrado");
        }
        Optional<Comentario> comentarioP = comentarioRepository.findById(parentID);
        if (comentarioP.isEmpty()) {
            throw new ComentarioNoEncontradoException("Comentario no encontrado");
        }
        PublicacionInicio publicacion = publicacionInicio.get();
        Comentario comentarioPadre = comentarioP.get();
        Optional<Comentario> comentarioHijoOptional = comentarioPadre.getReplies().stream()
                .filter(comentario -> comentario.getId().equals(comentarioId))
                .findFirst();
        if (comentarioHijoOptional.isPresent()) {
            Comentario comentarioHijo = comentarioHijoOptional.get();
            comentarioHijo.setMessage(cambioContenidoDTO.getContenido());
            comentarioRepository.save(comentarioHijo);
            publicacion.getComentarios().add(comentarioPadre);
            publicacionInicioRepositorio.save(publicacion);
            return convertToDto(comentarioHijo);
        } else {
            throw new ComentarioNoEncontradoException("Comentario hijo no encontrado con ID: " + comentarioId);
        }
    }

    private UserInfoDTO obtenerUsuarioById(Long id) {
        String url = "http://localhost:8080/api/user/perfil/" + id;
        try {
            return restTemplate.getForObject(url, UserInfoDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener información del usuario", e);
        }
    }

    private ComentarioRespuestaDTO convertToDto(Comentario comentario) {
        ComentarioRespuestaDTO dto = new ComentarioRespuestaDTO();

        dto.setId(comentario.getId());
        dto.setMessage(comentario.getMessage());
        dto.setAutorId(comentario.getAutorId());
        dto.setLikes(comentario.getLikes());
        dto.setFechaCreacion(comentario.getDate());

        // Manejo de multimedia
        if (comentario.getComentarioMultimedia() != null) {
            dto.setUrlMulimedia(comentario.getComentarioMultimedia().getUrlContenido());
            dto.setMultimediaId(comentario.getComentarioMultimedia().getId());
        } else {
            dto.setUrlMulimedia(null);
            dto.setMultimediaId(null);
        }

        // Manejo seguro de parentId
        dto.setParentId(comentario.getParent() != null ? comentario.getParent().getId() : null);

        // Obtención del usuario con manejo de excepciones
        try {
            UserInfoDTO user = obtenerUsuarioById(comentario.getAutorId());
            if (user != null) {
                dto.setFotoUrl(user.getFotoPerfil());
                dto.setNombre(user.getUserFullName());
            } else {
                throw new EntityNotFoundException("El usuario con ID " + comentario.getAutorId() + " no existe");
            }
        } catch (Exception e) {
            // Lanza una excepción específica si el usuario no se encuentra
            throw new EntityNotFoundException("Error al obtener el usuario con ID " + comentario.getAutorId() + ": " + e.getMessage(), e);
        }

        return dto;
    }

    @Transactional
    public void actualizarComentariolikes(Long publicacionId,Long comentarioId){
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionId)
                .orElseThrow(() -> new PublicacionNoEncontradoException("Publicación no encontrada"));

        Comentario comentarioInicio = publicacionInicio.getComentarios().stream()
                .filter(comentario -> comentario.getId().equals(comentarioId))
                .findFirst()
                .orElseThrow(() -> new ComentarioNoEncontradoException("Comentario no encontrado con ID: " + comentarioId));
        if (!comentarioInicio.getPublicacion().getId().equals(publicacionId)) {
            throw new ComentarioNoEncontradoException("El comentario no pertenece a la publicación especificada");
        }
        comentarioInicio.setLikes(comentarioInicio.getLikes()+1);
        publicacionInicio.getComentarios().add(comentarioInicio);
        publicacionInicioRepositorio.save(publicacionInicio);
        comentarioRepository.save(comentarioInicio);
    }

    @Transactional
    public void actualizarContenidoDeComentarioRespuestaLikes(Long publicacionId,Long parentID,Long comentarioId){
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionId)
                .orElseThrow(() -> new PublicacionNoEncontradoException("Publicación no encontrada"));

        Comentario comentarioPadre = publicacionInicio.getComentarios().stream()
                .filter(comentario -> comentario.getId().equals(parentID))
                .findFirst()
                .orElseThrow(() -> new ComentarioNoEncontradoException("Comentario no encontrado con ID: " + parentID));

        if (!comentarioPadre.getPublicacion().getId().equals(publicacionId)) {
            throw new ComentarioNoEncontradoException("El comentario no pertenece a la publicación especificada");
        }
        Comentario comentarioHijo = comentarioPadre.getReplies().stream()
                .filter(comentario -> comentario.getId().equals(comentarioId))
                .findFirst()
                .orElseThrow(() -> new ComentarioNoEncontradoException("Respuesta no encontrado con ID: " + comentarioId));

        comentarioHijo.setLikes(comentarioHijo.getLikes()+1);
        comentarioRepository.save(comentarioHijo);
        publicacionInicio.getComentarios().add(comentarioPadre);
        publicacionInicioRepositorio.save(publicacionInicio);
    }


    @Transactional
    public void actualizarComentarioDislikes(Long publicacionId,Long comentarioId){
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionId)
                .orElseThrow(() -> new PublicacionNoEncontradoException("Publicación no encontrada"));

        Comentario comentarioInicio = publicacionInicio.getComentarios().stream()
                .filter(comentario -> comentario.getId().equals(comentarioId))
                .findFirst()
                .orElseThrow(() -> new ComentarioNoEncontradoException("Comentario no encontrado con ID: " + comentarioId));
        if (!comentarioInicio.getPublicacion().getId().equals(publicacionId)) {
            throw new ComentarioNoEncontradoException("El comentario no pertenece a la publicación especificada");
        }
        comentarioInicio.setLikes(comentarioInicio.getLikes()-1);
        publicacionInicio.getComentarios().add(comentarioInicio);
        publicacionInicioRepositorio.save(publicacionInicio);
        comentarioRepository.save(comentarioInicio);
    }


    public void actualizarContenidoDeComentarioRespuestaDislikes(Long publicacionId, Long parentId, Long comentarioId){
        PublicacionInicio publicacionInicio = publicacionInicioRepositorio.findById(publicacionId)
                .orElseThrow(() -> new PublicacionNoEncontradoException("Publicación no encontrada"));

        Comentario comentarioPadre = publicacionInicio.getComentarios().stream()
                .filter(comentario -> comentario.getId().equals(parentId))
                .findFirst()
                .orElseThrow(() -> new ComentarioNoEncontradoException("Comentario no encontrado con ID: " + parentId));

        if (!comentarioPadre.getPublicacion().getId().equals(publicacionId)) {
            throw new ComentarioNoEncontradoException("El comentario no pertenece a la publicación especificada");
        }
        Comentario comentarioHijo = comentarioPadre.getReplies().stream()
                .filter(comentario -> comentario.getId().equals(comentarioId))
                .findFirst()
                .orElseThrow(() -> new ComentarioNoEncontradoException("Respuesta no encontrado con ID: " + comentarioId));

        comentarioHijo.setLikes(comentarioHijo.getLikes()-1);
        comentarioRepository.save(comentarioHijo);
        publicacionInicio.getComentarios().add(comentarioPadre);
        publicacionInicioRepositorio.save(publicacionInicio);
    }
}
