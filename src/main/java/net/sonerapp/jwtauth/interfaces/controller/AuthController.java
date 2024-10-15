package net.sonerapp.jwtauth.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sonerapp.jwtauth.application.dto.LoginRequestDto;
import net.sonerapp.jwtauth.application.service.AuthService;

@RestController
@RequestMapping("/api/auth/jwt")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginData) {
        return authService.processLogin(loginData);
    }

}
