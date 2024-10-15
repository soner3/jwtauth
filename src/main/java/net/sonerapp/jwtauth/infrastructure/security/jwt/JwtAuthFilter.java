package net.sonerapp.jwtauth.infrastructure.security.jwt;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("Extracting access token from request URI: {}", request.getRequestURI());
        try {
            String accessToken = parseJwtToken(request);
            log.debug("Extracted token: {}", accessToken);
            if (accessToken != null && jwtUtils.validateAccessToken(accessToken)) {

                String username = jwtUtils.getUsernameFromToken(accessToken);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Roles from User: {}", userDetails.getAuthorities());

            }

        } catch (Exception e) {
            log.error("Could not set authentication for request", e.getMessage());
        }

    }

    public String parseJwtToken(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromHeader(request);
        if (jwt == null) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals(jwtUtils.getCOOKIE_ACCESS_NAME())) {
                    return cookie.getValue();
                }
            }
            return null;
        } else {
            return jwt;
        }

    }

}
