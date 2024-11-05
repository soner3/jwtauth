package net.sonerapp.jwtauth.core.exceptions.UserController;

public class EmailExistsException extends RuntimeException {

    public EmailExistsException(String message) {
        super(message);
    }

}
