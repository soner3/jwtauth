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

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtils {

    private KeyPair keyPair;

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

}
