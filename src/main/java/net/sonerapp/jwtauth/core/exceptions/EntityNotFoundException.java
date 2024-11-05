package net.sonerapp.jwtauth.core.exceptions;

public class EntityNotFoundException extends IllegalArgumentException {

    public EntityNotFoundException(String message) {
        super(message);
    }

}
