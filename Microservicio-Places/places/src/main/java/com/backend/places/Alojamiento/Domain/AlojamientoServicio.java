package com.backend.places.Alojamiento.Domain;

import com.backend.places.Alojamiento.DTOS.*;
import com.backend.places.Alojamiento.Excepciones.AlojamientoNotFound;
import com.backend.places.Alojamiento.Excepciones.DescripcionIgualException;
import com.backend.places.Alojamiento.Infrastructure.AlojamientoRepositorio;

import com.backend.places.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import com.backend.places.AlojamientoMultimedia.Domain.AlojamientoMultimedia;
import com.backend.places.AlojamientoMultimedia.Domain.AlojamientoMultimediaServicio;
import com.backend.places.AlojamientoMultimedia.Infrastructure.AlojamientoMultimediaRepositorio;
import com.backend.places.Meneces.Meneces;
import com.backend.places.Meneces.MenecesRepository;
import com.backend.places.TipoMoneda;
import com.backend.places.UserInfo;
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

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlojamientoServicio {
    @Autowired
    AlojamientoRepositorio alojamientoRepositorio;
    @Autowired
    AlojamientoMultimediaServicio alojamientoMultimediaServicio;
    @Autowired
    private AlojamientoMultimediaRepositorio alojamientoMultimediaRepositorio;
    @Autowired
    private MenecesRepository menecesRepository;
    @Autowired
    private RestTemplate restTemplate;


    public ResponseAlojamientoDTO guardarAlojamiento(AlojamientoRequest alojamiento) throws AlojamientoNotFound, AccessDeniedException {
        if (alojamiento.getDescripcionCorta() == null ||
                alojamiento.getLatitud() == null ||
                alojamiento.getLongitud() == null) {
            throw new IllegalArgumentException("Los argumentos descripción, longitud y latitud no deben ser nulos");
        }
        if (alojamiento.getLatitud() < -90 || alojamiento.getLongitud() > 90) {
            throw new IllegalArgumentException("Latitud debe estar entre -90 y 90");
        }
        if (alojamiento.getLatitud() < -180 || alojamiento.getLongitud() > 180) {
            throw new IllegalArgumentException("Longitud debe estar entre -180 y 180");
        }
        if(alojamiento.getPropietarioId() == null){
            throw new IllegalArgumentException("Propietario id no puede ser nulo");
        }
        try{
            UserInfo info = obtenerUsuarioById(alojamiento.getPropietarioId());
        }catch (EntityNotFoundException e){
            throw new IllegalArgumentException("Propietario no encontrado");
        }

        try {
            Alojamiento alojamientoAux = new Alojamiento();
            alojamientoAux.setPropietarioId(alojamiento.getPropietarioId());
            alojamientoAux.setFechaPublicacion(LocalDateTime.now(ZoneId.systemDefault()));
            alojamientoAux.setDescripcion(alojamiento.getDescripcionCorta());
            alojamientoAux.setLongitude(alojamiento.getLongitud());
            alojamientoAux.setLatitude(alojamiento.getLatitud());
            alojamientoAux.setUbicacion(alojamiento.getUbicacion());
            alojamientoAux.setEstado(Estado.DISPONIBLE);
            alojamientoAux.setTipoMoneda(alojamiento.getTipoMoneda());
            alojamientoAux.setPrecio(alojamiento.getPrecio());
            alojamientoAux.setDescripcionLarga(alojamiento.getDescripcionLarga());
            alojamientoAux.setCantidadBanios(alojamiento.getCantidadBanios());
            alojamientoAux.setCantidadCamas(alojamiento.getCantidadCamas());
            alojamientoAux.setCantidadHabitaciones(alojamiento.getCantidadHabitaciones());
            alojamientoAux.setTipo(alojamiento.getTipo());
            alojamientoAux.setCapacidad(alojamiento.getCapacidad());

            Alojamiento alojamientoGuardado = alojamientoRepositorio.save(alojamientoAux);

            if (alojamiento.getMeneces() != null && !alojamiento.getMeneces().isEmpty()) {
                for (String s : alojamiento.getMeneces()) {
                    Meneces meneces = new Meneces();
                    meneces.setName(s.trim());
                    meneces.setAlojamiento(alojamientoGuardado);
                    menecesRepository.save(meneces);
                }
            }
            System.out.println("guardado "+ alojamientoGuardado.getCantidadBanios());

            return mapResponseAlojamientoDTO(alojamientoGuardado.getId());

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el alojamiento: " + e.getMessage(), e);
        }
    }


    public List<ResponseMultimediaDTO> guardarArchivos(List<MultipartFile> files, Long alojamientoId ) throws AccessDeniedException {
        Alojamiento alojamientoAux = alojamientoRepositorio.findById(alojamientoId).orElseThrow(()->new EntityNotFoundException("Alojamiento no encontrado"));
        if(!files.isEmpty()){
            for(MultipartFile file : files){
                alojamientoMultimediaServicio.guardarArchivo(file, alojamientoAux);
            }
            alojamientoRepositorio.save(alojamientoAux);
            return alojamientoMultimediaServicio.obtenerMultimediaPorPublicacionid(alojamientoId);
        }else{
            return new ArrayList<>();
        }
    }




    public void eliminarById(Long alojamientoId) throws AlojamientoNotFound {
        if (alojamientoRepositorio.existsById(alojamientoId)) {
            alojamientoRepositorio.deleteById(alojamientoId);
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }

    public void modificarPrecio(Long alojamientoId, PriceDTO precio) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            Alojamiento alojamiento = alojamientoOptional.get();
            alojamiento.setPrecio(precio.getPrecio());
            alojamiento.setTipoMoneda(precio.getTipoMoneda());
            alojamientoRepositorio.save(alojamiento);
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }



    public void actualizarDescripcionAlojamiento(Long alojamientoId, ContenidoDTO contenidoDTO) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            Alojamiento alojamiento = alojamientoOptional.get();
            if (alojamiento.getDescripcion().equals(contenidoDTO.getDescripcion())) {
                throw new DescripcionIgualException("La descripcion debe de ser diferente a la proporcionada anteriormente.");
            } else {
                alojamiento.setDescripcion(contenidoDTO.getDescripcion());
                alojamientoRepositorio.save(alojamiento);
            }
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }

    public void actualizarUbicacionAlojamiento(Long id, UbicacionDTO ubicacionDTO) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(id);
        if (alojamientoOptional.isPresent()) {
            Alojamiento alojamiento = alojamientoOptional.get();
            if (ubicacionDTO.getLatitude() == null || ubicacionDTO.getLongitude() == null) {
                alojamiento.setLatitude(alojamiento.getLatitude());
                alojamiento.setLongitude(alojamiento.getLongitude());

            } else {
                alojamiento.setLatitude(ubicacionDTO.getLatitude());
                alojamiento.setLongitude(ubicacionDTO.getLongitude());
            }
            alojamiento.setUbicacion(ubicacionDTO.getUbicacion());
            alojamientoRepositorio.save(alojamiento);
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + id);
        }
    }

    public ResponseAlojamientoDTO actualizarAlojamiento(Long alojamientoId,
                                                        AlojamientoRequest alojamientoRequest, List<MultipartFile> multi) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoId);
        if (alojamientoOptional.isPresent()) {
            Alojamiento alojamiento = alojamientoOptional.get();
            alojamiento.setDescripcion(alojamientoRequest.getDescripcionCorta());
            alojamiento.setPrecio(alojamientoRequest.getPrecio());
            alojamiento.setTipoMoneda(alojamientoRequest.getTipoMoneda());
            alojamiento.setLatitude(alojamientoRequest.getLatitud());
            alojamiento.setLongitude(alojamientoRequest.getLongitud());
            alojamiento.setUbicacion(alojamientoRequest.getUbicacion());
            if(!multi.isEmpty()){
                for(AlojamientoMultimedia multimedia: alojamiento.getAlojamientoMultimedia()){
                    alojamientoMultimediaRepositorio.delete(multimedia);
                }
                for (MultipartFile archivo : multi) {
                    AlojamientoMultimedia multimedia = alojamientoMultimediaServicio.guardarArchivo(archivo, alojamiento);
                    multimedia.setAlojamiento(alojamiento);
                    alojamientoMultimediaRepositorio.save(multimedia);
                    alojamiento.getAlojamientoMultimedia().add(multimedia);
                }

            }
            alojamientoRepositorio.save(alojamiento);
            ResponseAlojamientoDTO responseAlojamientoDTO = mapResponseAlojamientoDTO(alojamientoId);
            return responseAlojamientoDTO;
        } else {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoId);
        }
    }

    public Page<ResponseAlojamientoDTO> obtenerAlojamientoPaginacion(Long propietarioId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Alojamiento> alojamientos = alojamientoRepositorio.findByPropietarioId(propietarioId, pageable);
        if(alojamientos.isEmpty()){
            throw new RuntimeException(propietarioId + "No tiene alojamientos");
        }
        List<ResponseAlojamientoDTO> alojamientoDTOList = alojamientos.getContent().stream()
                .map(alojamiento -> {
                    try {
                        return mapResponseAlojamientoDTO(alojamiento.getId());
                    } catch (AlojamientoNotFound e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        return new PageImpl<>(alojamientoDTOList, pageable, alojamientos.getTotalElements());
    }


    public Page<ResponseAlojamientoDTO> obtenerAlojamientosPaginacionDisponibles(Long propietarioid, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Alojamiento> alojamientos = alojamientoRepositorio.findByPropietarioIdAndEstado(propietarioid, Estado.DISPONIBLE, pageable);
        if(alojamientos.isEmpty()){
            throw new RuntimeException(propietarioid+ "No tiene alojamientos");
        }
        List<ResponseAlojamientoDTO> alojamientoDTOList = alojamientos.getContent().stream()
                .map(alojamiento -> {
                    try {
                        return mapResponseAlojamientoDTO(alojamiento.getId());
                    } catch (AlojamientoNotFound e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        return new PageImpl<>(alojamientoDTOList, pageable, alojamientos.getTotalElements());
    }

    public ResponseAlojamientoDTO mapResponseAlojamientoDTO(Long alojamientoid) throws AlojamientoNotFound {
        Optional<Alojamiento> alojamientoOptional = alojamientoRepositorio.findById(alojamientoid);
        if (!alojamientoOptional.isPresent()) {
            throw new AlojamientoNotFound("Alojamiento no encontrado con id: " + alojamientoid);
        }
        Alojamiento alojamiento = alojamientoOptional.get();
        System.out.println(alojamiento.getPropietarioId());
        ResponseAlojamientoDTO responseAlojamientoDTO = new ResponseAlojamientoDTO();
        responseAlojamientoDTO.setId(alojamiento.getId());
        responseAlojamientoDTO.setPropietarioId(alojamiento.getPropietarioId());
        responseAlojamientoDTO.setDescripcionCorta(alojamiento.getDescripcion());
        responseAlojamientoDTO.setDescripcionLarga(alojamiento.getDescripcionLarga());
        responseAlojamientoDTO.setTipo(alojamiento.getTipo());
        responseAlojamientoDTO.setPrecio(alojamiento.getPrecio());
        responseAlojamientoDTO.setTipoMoneda(alojamiento.getTipoMoneda());
        responseAlojamientoDTO.setLatitude(alojamiento.getLatitude());
        responseAlojamientoDTO.setLongitude(alojamiento.getLongitude());
        responseAlojamientoDTO.setUbicacion(alojamiento.getUbicacion());
        responseAlojamientoDTO.setTipoMoneda(alojamiento.getTipoMoneda());
        responseAlojamientoDTO.setCantidadBanios(alojamiento.getCantidadBanios());
        responseAlojamientoDTO.setCapacidad(alojamiento.getCapacidad());
        responseAlojamientoDTO.setCantidadCamas(alojamiento.getCantidadCamas());
        responseAlojamientoDTO.setCantidadHabitaciones(alojamiento.getCantidadHabitaciones());
        responseAlojamientoDTO.setEstado(alojamiento.getEstado());
        // Mapear la lista de multimedia
        List<ResponseMultimediaDTO> multimediaDTOList = new ArrayList<>();
        if (alojamiento.getAlojamientoMultimedia() != null && !alojamiento.getAlojamientoMultimedia().isEmpty()) {
            for (AlojamientoMultimedia multimedia : alojamiento.getAlojamientoMultimedia()) {
                ResponseMultimediaDTO multimediaDTO = new ResponseMultimediaDTO();
                multimediaDTO.setId(multimedia.getId());
                multimediaDTO.setTipo(multimedia.getTipo());
                multimediaDTO.setUrl_contenido(multimedia.getUrlContenido()); // Asignar el contenido correctamente
                multimediaDTO.setFechaCreacion(multimedia.getFechaCreacion());
                multimediaDTOList.add(multimediaDTO);
            }
        }
        responseAlojamientoDTO.setMultimedia(multimediaDTOList);

        List<String> meneces = new ArrayList<>();
        for(Meneces m : alojamiento.getMeneces()){
            meneces.add(m.getName());
        }
        responseAlojamientoDTO.setMeneces(meneces);
        UserInfo info = obtenerUsuarioById(alojamiento.getPropietarioId());
        responseAlojamientoDTO.setFoto(info.getFotoPerfil());
        responseAlojamientoDTO.setNombre(info.getUserFullName());
        return responseAlojamientoDTO;

    }



    public Page<ResponseAlojamientoDTO> obtenerAlojamientosDashboard(int page, int size, Double distancia ,
                                                                     Double maxPrecio, Double minPrec,
                                                                     String tipoMoneda,
                                                                     Double latitude, Double longuitude){
        Pageable pageable = PageRequest.of(page, size);
        Page<Alojamiento> alojamientos = alojamientoRepositorio.findAllByEstado(Estado.DISPONIBLE,pageable);
        TipoMoneda realTipoMoneda;
        if (tipoMoneda == "PEN"){
            realTipoMoneda = TipoMoneda.PEN;
        }
        else if(tipoMoneda == "EUR"){
            realTipoMoneda = TipoMoneda.EUR;
        }
        else{
            realTipoMoneda = TipoMoneda.USD;
        }

        AlojamientoFilters filters = new AlojamientoFilters(latitude,longuitude,distancia,maxPrecio,minPrec,realTipoMoneda);
        List<ResponseAlojamientoDTO> alojamientoDTOList = alojamientos.getContent().stream()
                .filter(alojamiento -> checkFilter(filters, alojamiento))
                .map(alojamiento -> {
                    try {
                        return mapResponseAlojamientoDTO(alojamiento.getId());
                    } catch (AlojamientoNotFound e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        return new PageImpl<>(alojamientoDTOList, pageable, alojamientos.getTotalElements());
    }


    private ResponseAlojamientoDTO mapBienResponseAlojamientoDTO(Alojamiento alojamiento) {
        ResponseAlojamientoDTO responseAlojamientoDTO = new ResponseAlojamientoDTO();
        responseAlojamientoDTO.setId(alojamiento.getId());
        responseAlojamientoDTO.setPropietarioId(alojamiento.getPropietarioId());
        responseAlojamientoDTO.setDescripcionCorta(alojamiento.getDescripcion());
        responseAlojamientoDTO.setDescripcionLarga(alojamiento.getDescripcionLarga());
        responseAlojamientoDTO.setTipo(alojamiento.getTipo());
        responseAlojamientoDTO.setPrecio(alojamiento.getPrecio());
        responseAlojamientoDTO.setTipoMoneda(alojamiento.getTipoMoneda());
        responseAlojamientoDTO.setLatitude(alojamiento.getLatitude());
        responseAlojamientoDTO.setLongitude(alojamiento.getLongitude());
        responseAlojamientoDTO.setUbicacion(alojamiento.getUbicacion());
        responseAlojamientoDTO.setCantidadBanios(alojamiento.getCantidadBanios());
        responseAlojamientoDTO.setCantidadCamas(alojamiento.getCantidadCamas());
        responseAlojamientoDTO.setCantidadHabitaciones(alojamiento.getCantidadHabitaciones());
        List<ResponseMultimediaDTO> multimediaDTOList = new ArrayList<>();
        if(!alojamiento.getAlojamientoMultimedia().isEmpty()){
            for(AlojamientoMultimedia multimedia: alojamiento.getAlojamientoMultimedia()) {
                ResponseMultimediaDTO multimediaDTO = new ResponseMultimediaDTO();
                multimediaDTO.setId(multimedia.getId());
                multimediaDTO.setTipo(multimedia.getTipo());
                multimedia.setUrlContenido(multimedia.getUrlContenido());
                multimediaDTOList.add(multimediaDTO);
        }}

        responseAlojamientoDTO.setMultimedia(multimediaDTOList);
        return responseAlojamientoDTO;
    }

    public boolean checkFilter(AlojamientoFilters filters, Alojamiento a){
            double distance = calculateDistance(filters.getLatitude(), filters.getLongitude(), a.getLatitude(), a.getLongitude());

            return distance <= filters.getMaxDistance()&&
                    a.getPrecio() <= filters.getMaxPrecio() && a.getPrecio() >= filters.getMinPrecio() &&
                    a.getTipoMoneda().equals(filters.getTipoMoneda());
    }

    private static final double R = 6371;

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public UserInfo obtenerUsuarioById(Long id) {
        String url = "http://localhost:8080/api/user/perfil/" + id;
        System.out.println("Id " + id);
        try {
            return restTemplate.getForObject(url, UserInfo.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener información del usuario", e);
        }
    }

}

