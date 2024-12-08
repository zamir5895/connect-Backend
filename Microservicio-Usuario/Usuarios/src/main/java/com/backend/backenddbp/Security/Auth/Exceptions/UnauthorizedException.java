package com.backend.backenddbp.Security.Auth.Exceptions;


public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
