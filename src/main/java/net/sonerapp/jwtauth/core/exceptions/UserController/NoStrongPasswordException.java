package net.sonerapp.jwtauth.core.exceptions.UserController;

public class NoStrongPasswordException extends RuntimeException {

    public NoStrongPasswordException(String message) {
        super(message);
    }

}
