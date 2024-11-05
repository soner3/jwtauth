package net.sonerapp.jwtauth.core.exceptions.UserController;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message) {
        super(message);
    }

}
