package net.sonerapp.jwtauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletRequest;
import net.sonerapp.jwtauth.infrastructure.security.jwt.JwtUtils;

@SpringBootTest
public class JwtUtilsTests {

    @Test
    void when_keys_are_valid_then_keypair_initialized() {
        JwtUtils jwtUtils = new JwtUtils();
        assertNotNull(jwtUtils.getKeyPair());
        assertNotNull(jwtUtils.getKeyPair().getPrivate());
        assertNotNull(jwtUtils.getKeyPair().getPublic());
    }

    @Test
    void when_keypath_wrong_then_keypair_initialization_failed() {
        assertThrows(IOException.class, () -> {
            new JwtUtils();
        });
    }

    @Test
    void when_generate_tokens_then_tokens_are_valid() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        JwtUtils jwtUtils = new JwtUtils();
        String refresh = jwtUtils.generateRefreshToken(userDetails);

        assertNotNull(refresh);
        assertTrue(jwtUtils.validateRefreshToken(refresh));

        String access = jwtUtils.generateAccessTokenFromRefreshToken(userDetails.getUsername(), refresh);
        assertTrue(jwtUtils.validateAccessToken(access));
    }

    @Test
    void token_expirations_successfull_setted() {
        JwtUtils jwtUtils = new JwtUtils();
        assertEquals(172800000, jwtUtils.getRefreshTokenExpiration());
        assertEquals(600000, jwtUtils.getAccessTokenExpiration());
    }

    @Test
    void when_authorization_header_is_valid_then_jwt_is_extracted() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");

        JwtUtils jwtUtils = new JwtUtils();
        String headerValue = jwtUtils.getJwtFromHeader(request);
        assertEquals(headerValue, "validtoken");
    }

}
