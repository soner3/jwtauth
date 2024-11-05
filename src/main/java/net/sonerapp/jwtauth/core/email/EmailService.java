package net.sonerapp.jwtauth.core.email;

public interface EmailService {
    void sendPasswordResetMail(String to, String resetUrl);

    void sendActivateUserMail(String to, String activateUrl);
}
