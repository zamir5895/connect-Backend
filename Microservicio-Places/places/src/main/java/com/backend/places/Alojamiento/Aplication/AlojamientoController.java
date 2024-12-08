package com.backend.places.Alojamiento.Aplication;


import com.backend.places.Alojamiento.DTOS.*;
import com.backend.places.Alojamiento.Domain.AlojamientoServicio;
import com.backend.places.Alojamiento.Excepciones.AlojamientoNotFound;
import com.backend.places.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import com.backend.places.AlojamientoMultimedia.Domain.AlojamientoMultimediaServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;
@RestController
@RequestMapping("/api/alojamiento")
public class AlojamientoController {
    @Autowired
    AlojamientoServicio alojamientoServicio;
    @Autowired
    AlojamientoMultimediaServicio alojamientoMultimediaServicio;

    @GetMapping("/multimedia/{alojamientoId}")
    public ResponseEntity<List<ResponseMultimediaDTO>> getMultimedia(@PathVariable Long alojamientoId) {
        try {
            System.out.println("prueba");
            List<ResponseMultimediaDTO> multimediaDTO = alojamientoMultimediaServicio.obtenerMultimediaPorPublicacionid(alojamientoId);
            return ResponseEntity.ok().body(multimediaDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/alojamientos/{propietarioId}")
    public ResponseEntity<Page<ResponseAlojamientoDTO>> getAlojamientos(@PathVariable Long propietarioId,
                                                                        @RequestParam int page,
                                                                        @RequestParam int size) {
        try {
            Page<ResponseAlojamientoDTO> alojamientos = alojamientoServicio.obtenerAlojamientoPaginacion(propietarioId, page, size);
            return ResponseEntity.ok(alojamientos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/eliminar/{alojamientoId}")
    public ResponseEntity<Void> eliminarAlojamiento(@PathVariable Long alojamientoId) {
        try {
            alojamientoServicio.eliminarById(alojamientoId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/alojamientos/{alojamientoId}")
    public ResponseEntity<Void> actualizarPrecio(@PathVariable Long alojamientoId, @Valid @RequestBody PriceDTO precio) {
        try {
            alojamientoServicio.modificarPrecio(alojamientoId, precio);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



    @PatchMapping("/alojamientos/descripcion/{alojamientoId}")
    public ResponseEntity<Void> actualizarDescripcion(@PathVariable Long alojamientoId, @Valid @RequestBody ContenidoDTO contenidoDTO) {
        try {
            alojamientoServicio.actualizarDescripcionAlojamiento(alojamientoId, contenidoDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/alojamientos/ubicacion/{alojamientoId}")
    public ResponseEntity<Void> actualizarUbicacion(@PathVariable Long alojamientoId, @Valid @RequestBody UbicacionDTO ubicacionDTO) {
        try {
            alojamientoServicio.actualizarUbicacionAlojamiento(alojamientoId, ubicacionDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/{alojamientoId}", consumes = "multipart/form-data")
    public ResponseEntity<ResponseAlojamientoDTO> actualizarAlojamiento(@PathVariable Long alojamientoId,
                                                                        @Valid @RequestPart("data") AlojamientoRequest alojamientoRequest,
                                                                        @RequestPart(value = "file", required = false) List<MultipartFile> multimedia) {
        try {
            ResponseAlojamientoDTO response = alojamientoServicio.actualizarAlojamiento(alojamientoId, alojamientoRequest, multimedia);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/eliminar/imagen/{alojamientoId}/{imagenId}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long alojamientoId, @PathVariable String imagenId) {
        try {
            alojamientoMultimediaServicio.eliminarArchivo(alojamientoId, imagenId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

}
