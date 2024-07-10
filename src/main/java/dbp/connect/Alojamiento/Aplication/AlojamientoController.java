package dbp.connect.Alojamiento.Aplication;


import dbp.connect.Alojamiento.DTOS.*;
import dbp.connect.Alojamiento.Domain.Alojamiento;
import dbp.connect.Alojamiento.Domain.AlojamientoServicio;
import dbp.connect.Alojamiento.Excepciones.AlojamientoNotFound;
import dbp.connect.AlojamientoMultimedia.DTOS.ResponseMultimediaDTO;
import dbp.connect.AlojamientoMultimedia.Domain.AlojamientoMultimediaServicio;
import dbp.connect.TipoMoneda;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/alojamiento")
public class AlojamientoController {
    @Autowired
    AlojamientoServicio alojamientoServicio;
    @Autowired
    AlojamientoMultimediaServicio alojamientoMultimediaServicio;
    @Autowired
    private StringHttpMessageConverter stringHttpMessageConverter;

    @PreAuthorize("hasRole('ROLE_HOST') ")
    @PostMapping()
    public ResponseEntity<ResponseAlojamientoDTO> crearAlojamiento(@Valid @RequestBody AlojamientoRequest alojamientoRequest) throws AlojamientoNotFound, AccessDeniedException {
         ResponseAlojamientoDTO createdAlojamiento =alojamientoServicio.guardarAlojamiento(alojamientoRequest);
        return ResponseEntity.created(URI.create("/alojamiento/"+createdAlojamiento.getId())).body(createdAlojamiento);
    }
    @GetMapping("/{alojamientoId}")
    public ResponseEntity<ResponseAlojamientoDTO> getAlojamiento(@PathVariable Long alojamientoId) throws AlojamientoNotFound {
        ResponseAlojamientoDTO alojamiento= alojamientoServicio.obtenerAlojamiento(alojamientoId);
        return ResponseEntity.ok().body(alojamiento);
    }
    @GetMapping("/multimedia/{alojamientoId}/{imagenId}")
    public ResponseEntity<ResponseMultimediaDTO> getMultimedia(@PathVariable Long alojamientoId, @PathVariable String imagenId) throws AccessDeniedException {
        ResponseMultimediaDTO multimediaDTO= alojamientoMultimediaServicio.obtenerMultimedia(alojamientoId, imagenId);
        return ResponseEntity.ok().body(multimediaDTO);
    }
    @GetMapping("/{alojamientoId}/multimedia")
    public ResponseEntity<Page<ResponseMultimediaDTO>> getMultimedia(@PathVariable Long alojamientoId,
                                                                     @RequestParam int page,
                                                                     @RequestParam int size) throws AccessDeniedException {
        alojamientoMultimediaServicio.obtenerMultimediaPaginacion(alojamientoId, page, size);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/alojamientos/{propietarioId}")
    public ResponseEntity<Page<ResponseAlojamientoDTO>> getAlojamientos(@PathVariable Long propietarioId,
                                                                        @RequestParam int page,
                                                                        @RequestParam int size) {
        alojamientoServicio.obtenerAlojamientoPaginacion(propietarioId, page, size);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('ROLE_HOST') ")
    @PatchMapping("/imagen/{alojamientoId}/{imagenId}")
    public ResponseEntity<Void> actualizarImagen(@PathVariable Long alojamientoId, @PathVariable String imagenId, @RequestBody MultipartFile imagen) throws Exception {
        alojamientoMultimediaServicio.modificarArchivo(alojamientoId, imagenId, imagen);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('ROLE_HOST') ")
    @DeleteMapping("/eliminar/{alojamientoId}")
    public ResponseEntity<Void> eliminarAlojamiento(@PathVariable Long alojamientoId) throws AlojamientoNotFound {
        alojamientoServicio.eliminarById(alojamientoId);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('ROLE_HOST') ")
    @PatchMapping("/alojamientos/{alojamientoId}")
    public ResponseEntity<Void> actualizarPrecio(@PathVariable Long alojamientoId,
                                                 @Valid @RequestBody PriceDTO precio) throws AlojamientoNotFound {
        alojamientoServicio.modificarPrecio(alojamientoId,precio);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('ROLE_HOST') ")
    @PatchMapping("/disponibilidad/{alojamientoId}")
    public ResponseEntity<Void> actualizarEstado(@PathVariable Long alojamientoId) throws AlojamientoNotFound {
        alojamientoServicio.actualizarEstadoAlojamiento(alojamientoId);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('ROLE_HOST') ")
    @PatchMapping("/alojamientos/descripcion/{alojamientoId}")
    public ResponseEntity<Void> actualizarDescripcion(@PathVariable Long alojamientoId,
                                                      @Valid @RequestBody ContenidoDTO contenidoDTO) throws AlojamientoNotFound {
        alojamientoServicio.actualizarDescripcionAlojamiento(alojamientoId, contenidoDTO);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('ROLE_HOST') ")
    @PatchMapping("/alojamientos/ubicacion/{alojamientoId}")
    public ResponseEntity<Void> actualizarUbicacion(@PathVariable Long alojamientoId,
                                                      @Valid @RequestBody UbicacionDTO ubicacionDTO) throws AlojamientoNotFound {
        alojamientoServicio.actualizarUbicacionAlojamiento(alojamientoId, ubicacionDTO);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('ROLE_HOST') ")
    @PutMapping("/{alojamientoId}")
    public ResponseEntity<ResponseAlojamientoDTO> actualizarAlojamiento(@PathVariable Long alojamientoId,
                                                                        @Valid @RequestBody AlojamientoRequest alojamientoRequest) throws AlojamientoNotFound {
        alojamientoServicio.actualizarAlojamiento(alojamientoId, alojamientoRequest);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('ROLE_HOST') ")
    @DeleteMapping("/eliminar/imagen/{alojamientoId}/{imagenId}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long alojamientoId, @PathVariable String imagenId) throws AccessDeniedException {
        alojamientoMultimediaServicio.eliminarArchivo(alojamientoId, imagenId);
        return ResponseEntity.noContent().build();
    }


    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/dashboard")
    public ResponseEntity<Page<ResponseAlojamientoDTO>> getAlojamientos(@RequestParam int page,
                                                                        @RequestParam int size,
                                                                        @RequestParam Double distancia,
                                                                        @RequestParam Double maxPrecio,
                                                                        @RequestParam Double minPrecio,
                                                                        @RequestParam String tipoMoneda,
                                                                        @RequestParam Double latitude,
                                                                        @RequestParam Double longuitude) {
        return ResponseEntity.ok(alojamientoServicio.obtenerAlojamientosDashboard(page,size,distancia,maxPrecio,minPrecio,tipoMoneda, latitude, longuitude));
    }

    @GetMapping("/dashboard/all")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<Page<ResponseAlojamientoDTO>> getAlojamientos(@RequestParam int page,
                                                                        @RequestParam int size) {
        return ResponseEntity.ok(alojamientoServicio.obtenerTodosAlojamientosDashboard(page,size));
    }


}
