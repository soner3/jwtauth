package net.sonerapp.jwtauth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import jakarta.servlet.http.HttpServletRequest;
import net.sonerapp.jwtauth.infrastructure.security.jwt.JwtUtils;

@SpringBootTest(classes = { JwtUtils.class })
@TestPropertySource(locations = "classpath:test.properties")
public class JwtUtilsTests {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void when_keys_are_valid_then_keypair_initialized() {

        assertNotNull(jwtUtils.getKeyPair());
        assertNotNull(jwtUtils.getKeyPair().getPrivate());
        assertNotNull(jwtUtils.getKeyPair().getPublic());
    }

    @Test
    void when_generate_tokens_then_tokens_are_valid() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        String refresh = jwtUtils.generateRefreshToken(userDetails);

        assertNotNull(refresh);
        assertTrue(jwtUtils.validateRefreshToken(refresh));

        String access = jwtUtils.generateAccessTokenFromRefreshToken(userDetails.getUsername(), refresh);
        assertTrue(jwtUtils.validateAccessToken(access));
    }

    @Test
    void token_expirations_successfull_setted() {
        assertEquals(172800000, jwtUtils.getRefreshExpiryTime());
        assertEquals(600000, jwtUtils.getAccessExpiryTime());
    }

    @Test
    void when_authorization_header_is_valid_then_jwt_is_extracted() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");

        String headerValue = jwtUtils.getJwtFromHeader(request);
        assertEquals(headerValue, "validtoken");
    }

}
