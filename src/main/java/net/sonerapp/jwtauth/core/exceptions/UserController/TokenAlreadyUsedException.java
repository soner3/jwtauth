package net.sonerapp.jwtauth.core.exceptions.UserController;

public class TokenAlreadyUsedException extends RuntimeException {

    public TokenAlreadyUsedException(String message) {
        super(message);
    }

}
