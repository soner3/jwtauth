package net.sonerapp.jwtauth.application.service;

import java.util.HashMap;
import java.util.List;
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

import net.sonerapp.jwtauth.application.dto.LoginRequestDto;
import net.sonerapp.jwtauth.application.dto.LoginResponseDto;
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
            map.put("status", false);
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        String accessToken = jwtUtils.generateAccessTokenFromRefreshToken(userDetails, refreshToken);

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(role -> role.getAuthority())
                .toList();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, generateHttpOnlyCookie(jwtUtils.getCOOKIE_ACCESS_NAME(), accessToken,
                jwtUtils.getAccessTokenExpiration() / 1000).toString());
        headers.add(HttpHeaders.SET_COOKIE, generateHttpOnlyCookie(jwtUtils.getCOOKIE_REFRESH_NAME(), refreshToken,
                jwtUtils.getRefreshTokenExpiration() / 1000).toString());

        LoginResponseDto loginResponse = new LoginResponseDto(userDetails.getUsername(), accessToken, refreshToken,
                roles);

        return ResponseEntity.ok().headers(headers).body(loginResponse);
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
