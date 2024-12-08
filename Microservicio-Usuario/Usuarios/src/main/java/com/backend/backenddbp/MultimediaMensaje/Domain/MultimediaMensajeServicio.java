package com.backend.backenddbp.MultimediaMensaje.Domain;

import com.backend.backenddbp.Chat.Domain.Chat;
import com.backend.backenddbp.Chat.Infrastructure.ChatRepository;
import com.backend.backenddbp.Mensaje.Domain.Mensaje;
import com.backend.backenddbp.Mensaje.Infrastructure.MensajeRepository;
import com.backend.backenddbp.MultimediaMensaje.DTO.MensajeMultimediaDTO;
import com.backend.backenddbp.MultimediaMensaje.Infrastructure.MultimediaMensajeRepositorio;
import com.backend.backenddbp.S3.StorageService;
import com.backend.backenddbp.Tipo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class MultimediaMensajeServicio {
    @Autowired
    private MultimediaMensajeRepositorio multimediaMensajeRepositorio;
    @Autowired
    private StorageService storageService;
    @Autowired
    private MensajeRepository mensajeRepository;

    private static Long idCounter =0L;
    @Autowired
    private ChatRepository chatRepository;


    public MensajeMultimediaDTO saveMultimedia(Long chatId, Long mensajeId, MultipartFile file) {
        try {
            Chat chat = chatRepository.findById(chatId).orElseThrow(
                    () -> new EntityNotFoundException("Chat no encontrado"));

            MultimediaMensaje archivoMultimedia = new MultimediaMensaje();
            archivoMultimedia.setId(serializarId(generationId()));
            archivoMultimedia.setMensajeId(mensajeId);
            if (Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
                archivoMultimedia.setTipo(Tipo.FOTO);
            } else if (Objects.requireNonNull(file.getContentType()).startsWith("video/")) {
                archivoMultimedia.setTipo(Tipo.VIDEO);
            } else {
                throw new IllegalArgumentException("Tipo de archivo no soportado");
            }
            archivoMultimedia.setFecha(ZonedDateTime.now(ZoneId.systemDefault()));
            String key = storageService.subirAlS3File(file, archivoMultimedia.getId());
            archivoMultimedia.setUrl(storageService.obtenerURL(key));
            multimediaMensajeRepositorio.save(archivoMultimedia);

            MensajeMultimediaDTO dto = new MensajeMultimediaDTO();
            dto.setId(archivoMultimedia.getId());
            dto.setFecha(archivoMultimedia.getFecha());
            dto.setTipo(archivoMultimedia.getTipo());
            dto.setMensajeId(archivoMultimedia.getMensajeId());
            dto.setId(archivoMultimedia.getId());
            dto.setUrl(archivoMultimedia.getUrl());
            return dto;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo",e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarArchivo(Long chatId, String imagenId) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));

            multimediaMensajeRepositorio.deleteById(imagenId);

    }

    public void modificarArchivo(Long chatId, String imagenId, MultipartFile archivo) throws Exception {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));
        MultimediaMensaje archivoMultimedia = multimediaMensajeRepositorio.findById(imagenId).orElseThrow(()->new EntityNotFoundException("Multimedia no encontrado"));
        String newwkey = storageService.subirAlS3File(archivo, archivoMultimedia.getId());
        archivoMultimedia.setUrl(storageService.obtenerURL(newwkey));



    }

    public MensajeMultimediaDTO obtenerMultimedia(Long mensajeId, String imagenId) {
        MultimediaMensaje resultados = multimediaMensajeRepositorio.findByMensajeIdAndImagenId(mensajeId, imagenId).orElseThrow(()->new EntityNotFoundException("Multimedia no encontrado"));
        MensajeMultimediaDTO dto = new MensajeMultimediaDTO();
        dto.setId(imagenId);
        dto.setFecha(ZonedDateTime.now(ZoneId.systemDefault()));
        dto.setTipo(resultados.getTipo());
        dto.setMensajeId(resultados.getMensajeId());
        dto.setUrl(resultados.getUrl());
        return dto;
    }

    public List<MensajeMultimediaDTO> getallmultimediaByMensajeId(Long mensajeId) {
        List<MultimediaMensaje> resultados = multimediaMensajeRepositorio.findAllByMensajeId(mensajeId);
        List<MensajeMultimediaDTO> dtos = new ArrayList<>();

        if (resultados.isEmpty()) {
            return dtos;
        } else {
            for (MultimediaMensaje multimedia : resultados) {
                MensajeMultimediaDTO dto = new MensajeMultimediaDTO();
                dto.setId(multimedia.getId());
                dto.setMensajeId(multimedia.getMensajeId());
                dto.setUrl(multimedia.getUrl());
                dto.setTipo(multimedia.getTipo());
                dto.setFecha(multimedia.getFecha());
                dtos.add(dto);
            }
            return dtos;
        }
    }


    private String serializarId(Long imagenId){
        return "imagen-" + imagenId;
    }
    public Long generationId() {
        return ++idCounter;
    }
}
