package net.sonerapp.jwtauth.core.exceptions.UserController;

public class PasswordsDoNotMatchException extends RuntimeException {

    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}
