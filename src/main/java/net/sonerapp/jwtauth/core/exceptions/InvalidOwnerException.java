package net.sonerapp.jwtauth.core.exceptions;

public class InvalidOwnerException extends RuntimeException {

    public InvalidOwnerException(String message) {
        super(message);
    }
}
