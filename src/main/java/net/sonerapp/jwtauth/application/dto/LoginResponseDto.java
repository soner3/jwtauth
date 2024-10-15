package net.sonerapp.jwtauth.application.dto;

import java.util.List;

public record LoginResponseDto(String username, String accessToken, String refreshToken, List<String> roles) {

}
