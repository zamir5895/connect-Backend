package com.backend.publicaciones;

import com.backend.publicaciones.Comentarios.Excepciones.ComentarioNoEncontradoException;
import com.backend.publicaciones.Comentarios.Excepciones.PublicacionNoEncontradoException;
import com.backend.publicaciones.PublicacionInicio.Exceptions.UsuarioNoCoincideId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ComentarioNoEncontradoException.class)
    public ResponseEntity<String> exception(ComentarioNoEncontradoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(PublicacionNoEncontradoException.class)
    public ResponseEntity<String> exception(PublicacionNoEncontradoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioNoCoincideId.class)
    public ResponseEntity<String> handleRecursoNoEncontradoException(UsuarioNoCoincideId ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
}
