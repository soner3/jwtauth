package net.sonerapp.jwtauth.application.dto;

public record UnauthorizedDto(String path, String error, String message, int status) {

}
