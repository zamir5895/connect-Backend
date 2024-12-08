package com.backend.backenddbp.MultimediaMensaje.Aplication;


import com.backend.backenddbp.MultimediaMensaje.DTO.MensajeMultimediaDTO;
import com.backend.backenddbp.MultimediaMensaje.Domain.MultimediaMensajeServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/archivos")
public class MultimediaController {

    @Autowired
    MultimediaMensajeServicio multimediaMensajeServicio;

    @DeleteMapping("/{chatId}/{mensajeId}/{multimediaId}")
    public ResponseEntity<?> deleteMultimedia(@PathVariable Long chatId, @PathVariable Long mensajeId, @PathVariable String multimediaId) {
        try {
            multimediaMensajeServicio.eliminarArchivo(chatId, multimediaId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el archivo multimedia");
        }
    }

    @PutMapping(value = "/{chatId}/mensajes/{multimediaId}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateMultimedia(@PathVariable Long chatId,
                                              @PathVariable Long mensajeId,
                                              @PathVariable String multimediaId,
                                              @RequestPart MultipartFile archivo) {
        try {
            multimediaMensajeServicio.modificarArchivo(chatId, multimediaId, archivo);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el archivo multimedia");
        }
    }

    @GetMapping("/{mensajeId}/multimedia/{multimediaId}")
    public ResponseEntity<?> getMultimedia(@PathVariable Long mensajeId, @PathVariable String multimediaId) {
        try {
            MensajeMultimediaDTO multimedia = multimediaMensajeServicio.obtenerMultimedia(mensajeId, multimediaId);
            return ResponseEntity.ok(multimedia);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el archivo multimedia");
        }
    }

    @PostMapping("/{chatId}/{mendajeId}/subir")
        public  ResponseEntity<?> uploadMultimedia(@PathVariable Long chatId, @PathVariable Long mendajeId, @RequestPart("file") MultipartFile archivo) {
        try{
            MensajeMultimediaDTO multimediaDTO = multimediaMensajeServicio.saveMultimedia(chatId, mendajeId, archivo);
            return ResponseEntity.ok(multimediaDTO);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el archivo multimedia");
        }
    }
    @GetMapping("/{mensajeId}")
    public ResponseEntity<?> getMultimedia(@PathVariable Long mensajeId) {
        try{
            List<MensajeMultimediaDTO> resultado = multimediaMensajeServicio.getallmultimediaByMensajeId(mensajeId);
            return ResponseEntity.ok(resultado);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al obtener el archivo multimedia");
        }
    }

}
