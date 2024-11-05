package net.sonerapp.jwtauth.infrastructure.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import net.sonerapp.jwtauth.core.email.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromMail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetMail(String to, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromMail);
        message.setSubject("Password Reset Request");
        message.setText("Click the Link to reset your password: " + resetUrl);
        mailSender.send(message);
    }

    @Override
    public void sendActivateUserMail(String to, String activateUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromMail);
        message.setSubject("Activate Account");
        message.setText("Click the link to activate your account: " + activateUrl);
        mailSender.send(message);
    }

}
