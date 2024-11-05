package net.sonerapp.jwtauth.core.exceptions.UserController;

public class UnknownTokenException extends RuntimeException {

    public UnknownTokenException(String message) {
        super(message);
    }

}
