package net.sonerapp.jwtauth.application.service.impl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import net.sonerapp.jwtauth.application.dto.OkDto;
import net.sonerapp.jwtauth.application.dto.AuthControllerDto.LoginResponseDto;
import net.sonerapp.jwtauth.application.exceptions.UserDeactivatedException;
import net.sonerapp.jwtauth.application.service.JwtService;
import net.sonerapp.jwtauth.core.service.UserService;
import net.sonerapp.jwtauth.infrastructure.security.jwt.JwtUtils;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public JwtServiceImpl(JwtUtils jwtUtils, AuthenticationManager authenticationManager,
            UserService userService) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<LoginResponseDto> processLogin(String username, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        String accessToken = jwtUtils.generateAccessTokenFromRefreshToken(userDetails.getUsername(), refreshToken);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.SET_COOKIE, generateHttpOnlyCookie(JwtUtils.REFRESH_COOKIE_KEY, refreshToken,
                jwtUtils.getRefreshExpiryTime() / 1000).toString());
        header.add(HttpHeaders.SET_COOKIE, generateHttpOnlyCookie(JwtUtils.ACCESS_COOKIE_KEY, accessToken,
                jwtUtils.getAccessExpiryTime() / 1000).toString());

        LoginResponseDto response = new LoginResponseDto(userDetails.getUsername());

        return ResponseEntity.ok().headers(header).body(response);
    }

    @Override
    public ResponseEntity<OkDto> processReAuthorization(String refreshToken) {
        if (jwtUtils.validateRefreshToken(refreshToken)) {
            String username = jwtUtils.getUsernameFromToken(refreshToken);

            if (!userService.isUserEnabled(username)) {
                throw new UserDeactivatedException("User is not enabled");
            }
            String newAccessToken = jwtUtils.generateAccessTokenFromRefreshToken(username, refreshToken);

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.SET_COOKIE, generateHttpOnlyCookie(JwtUtils.ACCESS_COOKIE_KEY, newAccessToken,
                    jwtUtils.getAccessExpiryTime() / 1000).toString());

            return ResponseEntity.ok().headers(header).body(new OkDto("Reauthorization successfull"));
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    }

    private ResponseCookie generateHttpOnlyCookie(String key, String value, int expiration) {
        return ResponseCookie
                .from(key, value)
                .path("/")
                .secure(false)
                .httpOnly(true)
                .sameSite("Strict")
                .maxAge(expiration)
                .build();

    }

}
