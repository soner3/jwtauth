package net.sonerapp.jwtauth.infrastructure.security.jwt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Getter
public class JwtUtils {

    private KeyPair keyPair;

    private final String JWT_HEADER_KEY = "typ";
    private final String JWT_HEADER_VALUE = "JWT";

    private final String TOKEN_TYPE_KEY = "token_type";
    private final String REFRESH_TOKEN_TYPE = "refresh";
    private final String ACCESS_TOKEN_TYPE = "access";

    public static final String COOKIE_ACCESS_NAME = "access_jwt";
    public static final String COOKIE_REFRESH_NAME = "refresh_jwt";

    @Value("${jwt.refresh.expiration}")
    private int refreshTokenExpiration;

    @Value("${jwt.access.expiration}")
    private int accessTokenExpiration;

    public JwtUtils() {
        try {
            String privateKeyContent = new String(
                    Files.readAllBytes(Paths.get("src/main/resources/keys/private_key.pem")))
                    .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            String publicKeyContent = new String(
                    Files.readAllBytes(Paths.get("src/main/resources/keys/public_key.pem")))
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] privateKeyDecoded = Base64.getDecoder().decode(privateKeyContent);
            byte[] publicKeyDecoded = Base64.getDecoder().decode(publicKeyContent);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyDecoded);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyDecoded);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            this.keyPair = new KeyPair(publicKey, privateKey);

            if (keyPair != null && keyPair.getPrivate() != null && keyPair.getPublic() != null) {
                log.info("KeyPair successfully initialized.");
                log.info("Private Key: {}", keyPair.getPrivate().toString());
                log.info("Public Key: {}", keyPair.getPublic().toString());
            } else {
                log.error("KeyPair initialization failed.");
            }

        } catch (IOException e) {
            log.error("Could not read private and public key: {}", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.error("No RSA algorithm exists: {}", e.getMessage());
        } catch (InvalidKeySpecException e) {
            log.error("Invalid Key: {}", e.getMessage());

        }
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        } else {
            return null;
        }
    }

    private String generateJwtToken(String username, int expiration, String tokenType) {
        return Jwts.builder()
                .header()
                .add(JWT_HEADER_KEY, JWT_HEADER_VALUE)
                .and()
                .subject(username)
                .claim(TOKEN_TYPE_KEY, tokenType)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expiration))
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateJwtToken(userDetails.getUsername(), refreshTokenExpiration, REFRESH_TOKEN_TYPE);
    }

    public String getUsernameFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            String tokenType = Jwts
                    .parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload()
                    .get(TOKEN_TYPE_KEY, String.class);
            if (tokenType != null && tokenType.equals(REFRESH_TOKEN_TYPE)) {
                return true;
            } else {
                return false;
            }
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token is Expired", e.getMessage());

        } catch (UnsupportedJwtException e) {
            log.error("JWT token is not supported", e.getMessage());

        } catch (IllegalArgumentException e) {
            log.error("JWT Claims string is empty", e.getMessage());
        }

        return false;
    }

    public String generateAccessTokenFromRefreshToken(String username, String refreshToken) {
        if (refreshToken != null && validateRefreshToken(refreshToken)) {
            return generateJwtToken(username, accessTokenExpiration, ACCESS_TOKEN_TYPE);
        } else {
            return null;
        }

    }

    public boolean validateAccessToken(String accessToken) {
        try {
            String tokenType = Jwts
                    .parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload()
                    .get(TOKEN_TYPE_KEY, String.class);

            if (tokenType != null && tokenType.equals(ACCESS_TOKEN_TYPE)) {
                return true;
            } else {
                return false;
            }
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token is Expired", e.getMessage());

        } catch (UnsupportedJwtException e) {
            log.error("JWT token is not supported", e.getMessage());

        } catch (IllegalArgumentException e) {
            log.error("JWT Claims string is empty", e.getMessage());
        }

        return false;
    }

}
