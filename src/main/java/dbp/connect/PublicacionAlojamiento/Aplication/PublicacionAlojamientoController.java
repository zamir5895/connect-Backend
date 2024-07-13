package dbp.connect.PublicacionAlojamiento.Aplication;


import dbp.connect.Alojamiento.DTOS.ResponseAlojamientoDTO;
import dbp.connect.PublicacionAlojamiento.DTOS.PostPublicacionAlojamientoDTO;
import dbp.connect.PublicacionAlojamiento.DTOS.ResponseFilterDTO;
import dbp.connect.PublicacionAlojamiento.DTOS.ResponsePublicacionAlojamiento;
import dbp.connect.PublicacionAlojamiento.Domain.PublicacionAlojamientoServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/publicacionAlojamiento")
public class PublicacionAlojamientoController {
    @Autowired
    private PublicacionAlojamientoServicio publicacionAlojamientoServicio;
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponsePublicacionAlojamiento> crearPublicacionAlojamiento(@RequestPart  PostPublicacionAlojamientoDTO publicacionAlojamientoDTO,
                                                                                      @RequestPart(name = "multimedia", required = false) List<MultipartFile> multimedia
    ) throws AccessDeniedException {
        if (publicacionAlojamientoDTO.getAlojamiento() == null) {
            throw new IllegalArgumentException("Alojamiento data is missing");
        }

        ResponsePublicacionAlojamiento createdPublicacionAlojamiento = publicacionAlojamientoServicio.guardarPublicacionAlojamiento(publicacionAlojamientoDTO, multimedia);

        return ResponseEntity.created(URI.create("/alojamiento/"+createdPublicacionAlojamiento.getPublicacionId())).body(createdPublicacionAlojamiento);
    }
    @GetMapping("/{publicacionId}")
    public ResponseEntity<ResponsePublicacionAlojamiento> consultarPublicacionAlojamiento(@PathVariable Long publicacionId) throws AccessDeniedException {
        return ResponseEntity.ok(publicacionAlojamientoServicio.getPublicacionId(publicacionId));
    }

    @GetMapping("/individual/{apartmentID}")
    public ResponseEntity<ResponsePublicacionAlojamiento> getApartmentoPost(@PathVariable Long apartmentID) throws AccessDeniedException {
        System.out.println(publicacionAlojamientoServicio.getApartmentoPost(apartmentID));
        return ResponseEntity.ok(publicacionAlojamientoServicio.getApartmentoPost(apartmentID));
    }
    @GetMapping()
    public ResponseEntity<Page<ResponsePublicacionAlojamiento>> consultarPublicacionesAlojamiento(@RequestParam int page, int size) {
        return ResponseEntity.ok(publicacionAlojamientoServicio.getPublicacionesAlojamiento(page, size));
    }

    @GetMapping("/mispublicacion/{propietarioId}")
    public ResponseEntity<Page<ResponsePublicacionAlojamiento>> consultarPorPublicacionParaPropietario(@PathVariable Long propietarioId,
                                                                                           @RequestParam int page, int size) {

        return ResponseEntity.ok(publicacionAlojamientoServicio.getMisPublicaciones(propietarioId,page,size));
    }


    @PatchMapping("/{publicacionId}")
    public ResponseEntity<Void> actualizarTItulo(@PathVariable Long publicacionId, @RequestBody  String titulo){
        publicacionAlojamientoServicio.actualizarTituloAlojamiento(publicacionId, titulo);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{publicacionId}")
    public ResponseEntity<Void> eliminarPublicacionAlojamiento(@PathVariable Long publicacionId) {
        publicacionAlojamientoServicio.eliminarPublicacion(publicacionId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/publicaciones/rango-calificacion")
    public ResponseEntity<List<ResponsePublicacionAlojamiento>> obtenerPublicacionesPorRangoDeCalificacion(@RequestParam Integer minRating, @RequestParam Integer maxRating) {
        List<ResponsePublicacionAlojamiento> publicacionDTOs = publicacionAlojamientoServicio.obtenerPublicacionesPorRangoDeCalificacion(minRating, maxRating);
        return ResponseEntity.ok(publicacionDTOs);
    }
        @GetMapping("/recientes")
    public ResponseEntity<Page<ResponsePublicacionAlojamiento>> getPublicacionesRecientes(@RequestParam int page, @RequestParam int size) {
        Page<ResponsePublicacionAlojamiento> publicaciones = publicacionAlojamientoServicio.getPublicacionesRecientes(page, size);
        return ResponseEntity.ok(publicaciones);
    }

    @PatchMapping("/{publicacionId}/actualizar")
    public ResponseEntity<Void> actualizarDescripcion(@PathVariable Long publicacionId, @RequestBody String descripcion) {
        publicacionAlojamientoServicio.actualizarDescripcion(publicacionId, descripcion);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/buscar")
    public ResponseEntity<List<ResponsePublicacionAlojamiento>> buscarPorPalabrasClave(@RequestParam String keyword) {
        List<ResponsePublicacionAlojamiento> publicaciones = publicacionAlojamientoServicio.buscarPorPalabrasClave(keyword);
        return ResponseEntity.ok(publicaciones);
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
        return ResponseEntity.ok(publicacionAlojamientoServicio.obtenerPorFiltrosPublicacionesAloj
                (page,size,distancia,maxPrecio,minPrecio,tipoMoneda, latitude, longuitude));
    }
    /*
    @GetMapping("/buscarPorUbicacion")
    public ResponseEntity<Page<ResponsePublicacionAlojamiento>> buscarPorUbicacion(
            @RequestParam double latitud,
            @RequestParam double longitud,
            @RequestParam double radio,
            @RequestParam int page,
            @RequestParam int size) throws IOException {
        Page<ResponsePublicacionAlojamiento> publicaciones = publicacionAlojamientoServicio.buscarPorUbicacion(latitud, longitud, radio, page, size);
        return ResponseEntity.ok(publicaciones);
    }
*/
}
