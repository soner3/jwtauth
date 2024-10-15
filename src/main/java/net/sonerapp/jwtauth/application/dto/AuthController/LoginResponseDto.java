package net.sonerapp.jwtauth.application.dto.AuthController;

public record LoginResponseDto(String username, String accessToken, String refreshToken) {

}
