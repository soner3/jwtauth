package net.sonerapp.jwtauth.application.service;

import org.springframework.http.ResponseEntity;

import net.sonerapp.jwtauth.application.dto.OkDto;
import net.sonerapp.jwtauth.application.dto.AuthControllerDto.LoginResponseDto;

public interface JwtService {
    ResponseEntity<LoginResponseDto> processLogin(String username, String password);

    ResponseEntity<OkDto> processReAuthorization(String refreshToken);

}
