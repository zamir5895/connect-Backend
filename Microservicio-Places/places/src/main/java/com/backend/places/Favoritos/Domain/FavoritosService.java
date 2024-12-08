package com.backend.places.Favoritos.Domain;

import com.backend.places.Favoritos.DTOS.ResponseFavoritosDTO;
import com.backend.places.Favoritos.Infrastructure.FavoritosRepositorio;
import com.backend.places.PublicacionAlojamiento.Domain.PublicacionAlojamiento;
import com.backend.places.PublicacionAlojamiento.Infrastructure.PublicacionAlojamientoRespositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FavoritosService {
    @Autowired
    FavoritosRepositorio favoritosRepositorio;
    @Autowired
    private PublicacionAlojamientoRespositorio publicacionAlojamientoRespositorio;

    public void postearFavoritos(Long usuarioId, Long publicacionId) {
        Favoritos favoritos = new Favoritos();
        PublicacionAlojamiento publi = publicacionAlojamientoRespositorio.findById(publicacionId).orElseThrow(() -> new EntityNotFoundException("No se encontro la publicacion"));
        favoritos.setPublicacionAlojamiento(publi);
        favoritos.setUsuarioId(usuarioId);
        favoritosRepositorio.save(favoritos);
    }

    public Page<ResponseFavoritosDTO> getAllMyFavorites(Long usuarioId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Favoritos> favoritos = favoritosRepositorio.findByUsuarioId(usuarioId, pageable);
        List<ResponseFavoritosDTO> favoritosDTO = favoritos.getContent().
                stream().
                map(favorito -> {
                    try {
                        return mapear(favorito.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new PageImpl<> (favoritosDTO, pageable,favoritos.getTotalElements() );
    }

    public void deleteFromMyFavorites(Long usuarioId, Long publicacionId) {
        favoritosRepositorio.deleteById(publicacionId);
    }
    public ResponseFavoritosDTO getFavorito(Long usuarioId, Long publicacionId) {
        Favoritos favorito = favoritosRepositorio.findByUsuarioIdAndPublicacionId(usuarioId, publicacionId);
        return mapear(favorito.getId());
    }
    public Long countFavoritos(Long usuarioId) {
        return favoritosRepositorio.countByUsuarioId(usuarioId);
    }



    private ResponseFavoritosDTO mapear(Long favoritoId){
        Favoritos favoritos = favoritosRepositorio.findById(favoritoId).orElseThrow(()-> new EntityNotFoundException("No se encontro la publicacion"));
        ResponseFavoritosDTO response = new ResponseFavoritosDTO();
        response.setId(favoritos.getId());
        response.setUsuarioId(favoritos.getUsuarioId());
        response.setPublicacionId(favoritos.getPublicacionAlojamiento().getId());
        return response;
    }
}
