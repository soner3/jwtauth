package net.sonerapp.jwtauth.infrastructure.exceptions;

public class JwtClaimEmptyException extends IllegalArgumentException {

    public JwtClaimEmptyException(String message) {
        super(message);
    }
}
