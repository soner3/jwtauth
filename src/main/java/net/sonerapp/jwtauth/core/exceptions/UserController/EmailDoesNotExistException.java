package net.sonerapp.jwtauth.core.exceptions.UserController;

public class EmailDoesNotExistException extends RuntimeException {

    public EmailDoesNotExistException(String message) {
        super(message);
    }

}
