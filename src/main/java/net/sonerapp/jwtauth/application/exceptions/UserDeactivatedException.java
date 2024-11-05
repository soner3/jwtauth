package net.sonerapp.jwtauth.application.exceptions;

public class UserDeactivatedException extends IllegalArgumentException {
    public UserDeactivatedException(String message) {
        super(message);
    }
}
