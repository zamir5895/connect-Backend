package com.backend.publicaciones.PublicacionInicio.Exceptions;

public class UsuarioNoCoincideId extends RuntimeException {
    public UsuarioNoCoincideId(String message){
        super(message);
    }
}
