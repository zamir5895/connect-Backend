package com.backend.places.Review.Exceptions;

public class ReviewNotFoundException extends RuntimeException{
    public ReviewNotFoundException(String mensaje ){
        super(mensaje);
    }
}
