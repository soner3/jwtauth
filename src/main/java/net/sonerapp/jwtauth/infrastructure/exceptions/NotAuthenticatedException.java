package net.sonerapp.jwtauth.infrastructure.exceptions;

public class NotAuthenticatedException extends RuntimeException {

    public NotAuthenticatedException(String message) {
        super(message);
    }

}
