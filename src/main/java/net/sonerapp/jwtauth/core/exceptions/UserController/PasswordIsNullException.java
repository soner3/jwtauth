package net.sonerapp.jwtauth.core.exceptions.UserController;

public class PasswordIsNullException extends NullPointerException {

    public PasswordIsNullException(String message) {
        super(message);
    }
}
