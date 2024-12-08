package com.backend.places;

import com.backend.places.Alojamiento.Excepciones.AlojamientoNotFound;
import com.backend.places.Alojamiento.Excepciones.DescripcionIgualException;

import com.backend.places.Review.Exceptions.ReviewNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(DescripcionIgualException.class)
    public ResponseEntity<String> handleRecursoNoEncontradoException(DescripcionIgualException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<String> handleReviewNotFounException(ReviewNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
