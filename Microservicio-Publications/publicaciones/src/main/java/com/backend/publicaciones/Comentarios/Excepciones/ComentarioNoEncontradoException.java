package com.backend.publicaciones.Comentarios.Excepciones;

public class ComentarioNoEncontradoException extends RuntimeException {
    public ComentarioNoEncontradoException(String message) {
        super(message);
    }
}
