package net.sonerapp.jwtauth.core.exceptions.UserController;

public class InvalidUserTokenTypeException extends RuntimeException {

    public InvalidUserTokenTypeException(String message) {
        super(message);
    }

}
