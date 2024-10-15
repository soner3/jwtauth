package net.sonerapp.jwtauth.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sonerapp.jwtauth.application.dto.AuthController.LoginRequestDto;
import net.sonerapp.jwtauth.application.dto.AuthController.RefreshRequestDto;
import net.sonerapp.jwtauth.application.service.AuthService;
import net.sonerapp.jwtauth.infrastructure.security.jwt.JwtUtils;

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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshRequestDto refreshData,
            @CookieValue(name = JwtUtils.COOKIE_REFRESH_NAME, required = true) String cookieRefreshToken) {
        return authService
                .processReAuthorization(cookieRefreshToken != null ? cookieRefreshToken : refreshData.refreshToken());
    }

}
