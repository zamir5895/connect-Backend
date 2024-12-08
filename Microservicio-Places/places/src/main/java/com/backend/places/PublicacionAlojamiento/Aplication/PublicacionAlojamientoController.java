package com.backend.places.PublicacionAlojamiento.Aplication;


import com.backend.places.Alojamiento.Excepciones.AlojamientoNotFound;
import com.backend.places.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import com.backend.places.PublicacionAlojamiento.DTOS.PostPublicacionAlojamientoDTO;
import com.backend.places.PublicacionAlojamiento.DTOS.ResponseFilterDTO;
import com.backend.places.PublicacionAlojamiento.DTOS.ResponsePublicacionAlojamiento;
import com.backend.places.PublicacionAlojamiento.Domain.PublicacionAlojamientoServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;
@RestController
@RequestMapping("/api/publicacionAlojamiento")
public class PublicacionAlojamientoController {
    @Autowired
    private PublicacionAlojamientoServicio publicacionAlojamientoServicio;

    @PostMapping
    public ResponseEntity<ResponsePublicacionAlojamiento> crearPublicacionAlojamiento(@RequestBody PostPublicacionAlojamientoDTO publicacionAlojamientoDTO) {
        try {
            if (publicacionAlojamientoDTO.getAlojamiento() == null) {
                throw new IllegalArgumentException("Alojamiento data is missing");
            }
            System.out.println(publicacionAlojamientoDTO.getAlojamiento());
            ResponsePublicacionAlojamiento createdPublicacionAlojamiento =
                    publicacionAlojamientoServicio.guardarPublicacionAlojamiento(publicacionAlojamientoDTO);

            return ResponseEntity.created(URI.create("/alojamiento/" + createdPublicacionAlojamiento.getPublicacionId()))
                    .body(createdPublicacionAlojamiento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{publicacionId}/files")
    public ResponseEntity<List<ResponseMultimediaDTO>> publicarMultimedia(@PathVariable Long publicacionId, @RequestParam("files") List<MultipartFile> files) {
        try {
            return ResponseEntity.ok(publicacionAlojamientoServicio.subirArchivos(files, publicacionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{publicacionId}")
    public ResponseEntity<ResponsePublicacionAlojamiento> consultarPublicacionAlojamiento(@PathVariable Long publicacionId) {
        try {
            return ResponseEntity.ok(publicacionAlojamientoServicio.getPublicacionId(publicacionId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/individual/{apartmentID}")
    public ResponseEntity<ResponsePublicacionAlojamiento> getApartmentoPost(@PathVariable Long apartmentID) {
        try {
            return ResponseEntity.ok(publicacionAlojamientoServicio.getApartmentoPost(apartmentID));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<ResponsePublicacionAlojamiento>> consultarPublicacionesAlojamiento(@RequestParam int page, int size) {
        try {
            return ResponseEntity.ok(publicacionAlojamientoServicio.getPublicacionesAlojamiento(page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/mispublicaciones/{propietarioId}")
    public ResponseEntity<Page<ResponsePublicacionAlojamiento>> consultarPorPublicacionParaPropietario(@PathVariable Long propietarioId, @RequestParam int page, int size) {
        try {
            return ResponseEntity.ok(publicacionAlojamientoServicio.getMisPublicaciones(propietarioId, page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{publicacionId}")
    public ResponseEntity<Void> eliminarPublicacionAlojamiento(@PathVariable Long publicacionId) {
        try {
            publicacionAlojamientoServicio.eliminarPublicacion(publicacionId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/publicaciones/rango-calificacion")
    public ResponseEntity<List<ResponsePublicacionAlojamiento>> obtenerPublicacionesPorRangoDeCalificacion(@RequestParam Integer minRating, @RequestParam Integer maxRating) {
        try {
            return ResponseEntity.ok(publicacionAlojamientoServicio.obtenerPublicacionesPorRangoDeCalificacion(minRating, maxRating));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/publicaciones")
    public ResponseEntity<?> getPublicaciones(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<ResponsePublicacionAlojamiento> publicaciones = publicacionAlojamientoServicio.getAllPublicaciones(page, size);
            return ResponseEntity.ok(publicaciones);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error en los parámetros: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al obtener las publicaciones: " + e.getMessage());
        }
    }



    @GetMapping("/recientes")
    public ResponseEntity<Page<ResponsePublicacionAlojamiento>> getPublicacionesRecientes(@RequestParam int page, @RequestParam int size) {
        try {
            return ResponseEntity.ok(publicacionAlojamientoServicio.getPublicacionesRecientes(page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{publicacionId}/actualizar")
    public ResponseEntity<Void> actualizarTitulo(@PathVariable Long publicacionId, @RequestBody String titulo) {
        try {
            publicacionAlojamientoServicio.actualizarTituloAlojamiento(publicacionId, titulo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ResponsePublicacionAlojamiento>> buscarPorPalabrasClave(@RequestParam String keyword) {
        try {
            return ResponseEntity.ok(publicacionAlojamientoServicio.buscarPorPalabrasClave(keyword));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Page<ResponseFilterDTO>> getAlojamientos(@RequestParam int page,
                                                                   @RequestParam int size,
                                                                   @RequestParam Double distancia,
                                                                   @RequestParam Double maxPrecio,
                                                                   @RequestParam Double minPrecio,
                                                                   @RequestParam String tipoMoneda,
                                                                   @RequestParam Double latitude,
                                                                   @RequestParam Double longuitude) {
        try {
            return ResponseEntity.ok(publicacionAlojamientoServicio.obtenerPorFiltrosPublicacionesAloj(
                    page, size, distancia, maxPrecio, minPrecio, tipoMoneda, latitude, longuitude));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
