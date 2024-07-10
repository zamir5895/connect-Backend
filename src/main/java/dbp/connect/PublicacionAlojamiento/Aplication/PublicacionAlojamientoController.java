package dbp.connect.PublicacionAlojamiento.Aplication;


import dbp.connect.PublicacionAlojamiento.DTOS.PostPublicacionAlojamientoDTO;
import dbp.connect.PublicacionAlojamiento.DTOS.ResponsePublicacionAlojamiento;
import dbp.connect.PublicacionAlojamiento.Domain.PublicacionAlojamientoServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/publicacionAlojamiento")
public class PublicacionAlojamientoController {
    @Autowired
    private PublicacionAlojamientoServicio publicacionAlojamientoServicio;
    @PreAuthorize("hasRole('ROLE_HOST') ")
    @PostMapping()
    public ResponseEntity<ResponsePublicacionAlojamiento> crearPublicacionAlojamiento(@Valid @RequestBody PostPublicacionAlojamientoDTO publicacionAlojamientoDTO) {
        ResponsePublicacionAlojamiento createdPublicacionAlojamiento = publicacionAlojamientoServicio.guardarPublicacionAlojamiento(publicacionAlojamientoDTO);
        return ResponseEntity.created(URI.create("/alojamiento/"+createdPublicacionAlojamiento.getId())).body(createdPublicacionAlojamiento);
    }
    @GetMapping("/{publicacionId}")
    public ResponseEntity<ResponsePublicacionAlojamiento> consultarPublicacionAlojamiento(@PathVariable Long publicacionId) {
        return ResponseEntity.ok(publicacionAlojamientoServicio.getPublicacionId(publicacionId));
    }

    @GetMapping("/id/{apartmentID}")
    public ResponseEntity<ResponsePublicacionAlojamiento> getApartmentoPost(@PathVariable Long apartmentID) {
        System.out.println(publicacionAlojamientoServicio.getApartmentoPost(apartmentID));
        return ResponseEntity.ok(publicacionAlojamientoServicio.getApartmentoPost(apartmentID));
    }

   /* @GetMapping("{userId}")
    public ResponseEntity<Page<ResponsePublicacionAlojamiento>> consultarPorPublicacionParaUsuario(@PathVariable Long userId,
                                                                                           @RequestParam int page, int size) {

        return ResponseEntity.ok(publicacionAlojamientoServicio.getPublicacionRecomendadas(userId,page,size));
    }
    @GetMapping("/{propietarioId}")
    public ResponseEntity<Page<ResponsePublicacionAlojamiento>> consultarPorPublicacionParaPropietario(@PathVariable Long propietarioId,
                                                                                           @RequestParam int page, int size) {

        return ResponseEntity.ok(publicacionAlojamientoServicio.getPublicacionRecomendadas(userId,page,size));
        }
     @GetMapping("/ubicacion/{propietarioId}")
    public ResponseEntity<Page<ResponsePublicacionAlojamiento>> consultarPorPublicacionParaPropietario(@PathVariable Long propietarioId, @RequestParam double latitude, @RequestParam Double longitud,
                                                                                           @RequestParam int page, int size) {

        return ResponseEntity.ok(publicacionAlojamientoServicio.getPublicacionRecomendadas(userId,page,size));
        }
    */
   @PreAuthorize("hasRole('ROLE_HOST') ")
    @PatchMapping("/publicacionId")
    public ResponseEntity<Void> actualizarTItulo(@PathVariable Long publicacionId, @RequestBody  String titulo){
        publicacionAlojamientoServicio.actualizarTituloAlojamiento(publicacionId, titulo);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('ROLE_HOST') ")
    @DeleteMapping("/{publicacionId}")
    public ResponseEntity<Void> eliminarPublicacionAlojamiento(@PathVariable Long publicacionId) {
        publicacionAlojamientoServicio.eliminarPublicacion(publicacionId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/publicaciones/rango-calificacion")
    public ResponseEntity<List<ResponsePublicacionAlojamiento>> obtenerPublicacionesPorRangoDeCalificacion(@RequestParam Integer minRating, @RequestParam Integer maxRating) {
        List<ResponsePublicacionAlojamiento> publicacionDTOs = publicacionAlojamientoServicio.obtenerPublicacionesPorRangoDeCalificacion(minRating, maxRating);
        return ResponseEntity.ok(publicacionDTOs);
    }/*
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
    /*
    @PreAuthorize("hasRole('ROLE_HOST')")
    @PatchMapping("/{publicacionId}/actualizar")
    public ResponseEntity<Void> actualizarPublicacion(@PathVariable Long publicacionId, @RequestBody ActualizarPublicacionDTO actualizarPublicacionDTO) {
        publicacionAlojamientoServicio.actualizarPublicacion(publicacionId, actualizarPublicacionDTO);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/recientes")
    public ResponseEntity<List<ResponsePublicacionAlojamiento>> listarRecientes() {
        List<ResponsePublicacionAlojamiento> publicacionesRecientes = publicacionAlojamientoServicio.listarRecientes();
        return ResponseEntity.ok(publicacionesRecientes);
    }

    @GetMapping("/buscarPorPalabrasClave")
    public ResponseEntity<List<ResponsePublicacionAlojamiento>> buscarPorPalabrasClave(@RequestParam String palabrasClave) {
        List<ResponsePublicacionAlojamiento> publicaciones = publicacionAlojamientoServicio.buscarPorPalabrasClave(palabrasClave);
        return ResponseEntity.ok(publicaciones);
    }*/
}
