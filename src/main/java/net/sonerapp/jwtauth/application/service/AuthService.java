package net.sonerapp.jwtauth.application.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import net.sonerapp.jwtauth.application.dto.AuthController.LoginRequestDto;
import net.sonerapp.jwtauth.application.dto.AuthController.LoginResponseDto;
import net.sonerapp.jwtauth.application.dto.AuthController.RefreshResponseDto;
import net.sonerapp.jwtauth.infrastructure.security.jwt.JwtUtils;

@Service
public class AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthService(JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<?> processLogin(LoginRequestDto loginData) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginData.username(), loginData.password()));
        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        String accessToken = jwtUtils.generateAccessTokenFromRefreshToken(userDetails.getUsername(), refreshToken);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.SET_COOKIE, generateHttpOnlyCookie(JwtUtils.COOKIE_ACCESS_NAME, accessToken,
                jwtUtils.getAccessTokenExpiration() / 1000).toString());
        header.add(HttpHeaders.SET_COOKIE, generateHttpOnlyCookie(JwtUtils.COOKIE_REFRESH_NAME, refreshToken,
                jwtUtils.getRefreshTokenExpiration() / 1000).toString());

        LoginResponseDto loginResponse = new LoginResponseDto(userDetails.getUsername(), accessToken, refreshToken);

        return ResponseEntity.ok().headers(header).body(loginResponse);
    }

    public ResponseEntity<?> processReAuthorization(String refreshToken) {
        if (jwtUtils.validateRefreshToken(refreshToken)) {
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            String newAccessToken = jwtUtils.generateAccessTokenFromRefreshToken(username, refreshToken);

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.SET_COOKIE, generateHttpOnlyCookie(JwtUtils.COOKIE_ACCESS_NAME, newAccessToken,
                    jwtUtils.getAccessTokenExpiration() / 1000).toString());
            RefreshResponseDto response = new RefreshResponseDto(newAccessToken);
            return ResponseEntity.ok().headers(header).body(response);
        } else {
            Map<String, Object> errorMessage = new HashMap<>();
            errorMessage.put("message", "Invalid refresh token");
            errorMessage.put("status", HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseCookie generateHttpOnlyCookie(String key, String value, int expiration) {
        return ResponseCookie.from(key, value)
                .path("/")
                .secure(false)
                .httpOnly(true)
                .sameSite("Strict")
                .maxAge(expiration)
                .build();

    }

}
