package net.sonerapp.jwtauth.infrastructure.exceptions;

public class JwtExpiredException extends RuntimeException {

    public JwtExpiredException(String message) {
        super(message);
    }

}
