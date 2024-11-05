package net.sonerapp.jwtauth.core.exceptions.UserController;

public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String message) {
        super(message);
    }

}
