package com.backend.publicaciones.Comentarios.Excepciones;

public class PublicacionNoEncontradoException extends RuntimeException {
    public PublicacionNoEncontradoException(String message) {
        super(message);
    }
}
