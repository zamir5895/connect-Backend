package dbp.connect.MultimediaMensaje.Domain;

import dbp.connect.Chat.Domain.Chat;
import dbp.connect.Chat.Infrastructure.ChatRepository;
import dbp.connect.Mensaje.Domain.Mensaje;
import dbp.connect.Mensaje.Infrastructure.MensajeRepository;
import dbp.connect.MultimediaMensaje.DTO.MensajeMultimediaDTO;
import dbp.connect.MultimediaMensaje.Infrastructure.MultimediaMensajeRepositorio;
import dbp.connect.S3.StorageService;
import dbp.connect.Tipo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

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


    public MultimediaMensaje saveMultimedia(MultipartFile file) {
        try {
            MultimediaMensaje archivoMultimedia = new MultimediaMensaje();
            archivoMultimedia.setId(serializarId(generationId()));

            if (Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
                archivoMultimedia.setTipo(Tipo.FOTO);
            } else if (Objects.requireNonNull(file.getContentType()).startsWith("video/")) {
                archivoMultimedia.setTipo(Tipo.VIDEO);
            } else {
                throw new IllegalArgumentException("Tipo de archivo no soportado");
            }
            archivoMultimedia.setFecha(ZonedDateTime.now(ZoneId.systemDefault()));
            String key = storageService.subiralS3File(file, archivoMultimedia.getId());
            archivoMultimedia.setUrl(storageService.obtenerURL(key));
            return archivoMultimedia;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo",e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarArchivo(Long chatId, Long mensajeId, String imagenId) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));

        Optional<Mensaje> mensajeOptional = mensajeRepository.findById(mensajeId);
        if (mensajeOptional.isPresent()) {
            Mensaje mensaje= mensajeOptional.get();
            Iterator<MultimediaMensaje> iterator = mensaje.getMultimediaMensaje().iterator();
            while(iterator.hasNext()){
                MultimediaMensaje multimedia = iterator.next();
                if(multimedia.getId().equals(imagenId)){
                    storageService.deleteFile(multimedia.getId());
                    iterator.remove();
                    multimediaMensajeRepositorio.delete(multimedia);
                }
            }
            mensajeRepository.save(mensaje);
        } else {
            throw new EntityNotFoundException("Alojamiento no encontrado con id: " + mensajeId);
        }
    }

    public void modificarArchivo(Long chatId, Long mensajeId, String imagenId, MultipartFile archivo) throws Exception {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat no encontrado"));
        Optional<Mensaje> mensajeOptioanl = mensajeRepository.findById(mensajeId);
        if (mensajeOptioanl.isPresent()) {
            Mensaje mensaje= mensajeOptioanl.get();
            for(MultimediaMensaje multimedia: mensaje.getMultimediaMensaje()){
                if(multimedia.getId().equals(imagenId)){
                    if (Objects.requireNonNull(archivo.getContentType()).startsWith("image/")) {
                        multimedia.setTipo(Tipo.FOTO);
                    } else if (Objects.requireNonNull(archivo.getContentType()).startsWith("video/")) {
                        multimedia.setTipo(Tipo.VIDEO);
                    } else {
                        throw new IllegalArgumentException("Tipo de archivo no soportado");
                    }
                    String key = storageService.subiralS3File(archivo, multimedia.getId());
                    multimedia.setUrl(storageService.obtenerURL(key));
                    multimedia.setFecha(ZonedDateTime.now(ZoneId.systemDefault()));
                    multimediaMensajeRepositorio.save(multimedia);
                    mensajeRepository.save(mensaje);
                }
            }
        }
        else {
            throw new EntityNotFoundException("Alojamiento no encontrado con id: " + mensajeId);
        }
    }

    public MensajeMultimediaDTO obtenerMultimedia(Long mensajeId, String imagenId) {
        Optional<Mensaje> mensajeOptional = mensajeRepository.findById(mensajeId);
        if (mensajeOptional.isPresent()) {
            Mensaje mensaje = mensajeOptional.get();
            MensajeMultimediaDTO multimediaDTO = new MensajeMultimediaDTO();
            for (MultimediaMensaje multimedia : mensaje.getMultimediaMensaje()) {
                if (multimedia.getId().equals((imagenId))) {
                    multimediaDTO.setId(multimedia.getId());
                    multimediaDTO.setTipo(multimedia.getTipo());
                    multimediaDTO.setUrl(multimedia.getUrl());
                    return multimediaDTO;
                }
            }
            throw new EntityNotFoundException("No se encontró la imagen con id: " + imagenId);
        } else {
            throw new EntityNotFoundException("Alojamiento no encontrado con id: " + mensajeId);
        }
    }


    private String serializarId(Long imagenId){
        return "imagen-" + imagenId;
    }
    public Long generationId() {
        return ++idCounter;
    }
}
