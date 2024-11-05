package net.sonerapp.jwtauth.core.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sonerapp.jwtauth.core.model.model_enums.UserTokenType;

@Entity
@Data
@NoArgsConstructor
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String token;

    @Column(nullable = false, updatable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean used;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private UserTokenType tokenType;

    @ManyToOne
    @JoinColumn(name = "user_fk")
    private User user;

    public UserToken(String token, User user, UserTokenType tokenType) {
        this.token = token;
        this.user = user;
        this.tokenType = tokenType;
        this.used = false;
        this.expiryDate = Instant.now().plus(15, ChronoUnit.MINUTES);
    }

}
