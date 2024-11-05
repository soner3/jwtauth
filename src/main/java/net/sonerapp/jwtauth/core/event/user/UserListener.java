package net.sonerapp.jwtauth.core.event.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import net.sonerapp.jwtauth.core.email.EmailService;
import net.sonerapp.jwtauth.core.model.Profile;
import net.sonerapp.jwtauth.core.model.User;
import net.sonerapp.jwtauth.core.model.UserToken;
import net.sonerapp.jwtauth.core.model.model_enums.UserTokenType;
import net.sonerapp.jwtauth.core.repository.ProfileRepository;
import net.sonerapp.jwtauth.core.repository.UserTokenRepository;

@Service
public class UserListener {

    private final ProfileRepository profileRepository;

    private final EmailService emailService;

    private final UserTokenRepository userTokenRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    public UserListener(ProfileRepository profileRepository, EmailService emailService,
            UserTokenRepository userTokenRepository) {
        this.profileRepository = profileRepository;
        this.emailService = emailService;
        this.userTokenRepository = userTokenRepository;
    }

    @EventListener
    @Async
    public void createProfileFromUser(UserCreatedEvent event) {
        User user = event.user();
        Profile newProfile = new Profile(user);

        profileRepository.save(newProfile);

        String activateUrl = getMailUrlFromUser(event.user(), UserTokenType.USER_ACTIVATION_TOKEN);

        emailService.sendActivateUserMail(user.getEmail(), activateUrl);

    }

    @EventListener
    @Async
    public void sendResetPasswordMail(PasswordResetRequestEvent event) {
        User user = event.user();

        String resetUrl = getMailUrlFromUser(user, UserTokenType.PASSWORD_RESET_TOKEN);

        emailService.sendPasswordResetMail(user.getEmail(), resetUrl);

    }

    @EventListener
    @Async
    public void resendUserActivationMail(ResendActivationMailEvent event) {
        User user = event.user();
        String activateUrl = getMailUrlFromUser(user, UserTokenType.USER_ACTIVATION_TOKEN);

        emailService.sendActivateUserMail(user.getEmail(), activateUrl);

    }

    private String getMailUrlFromUser(User user, UserTokenType tokenType) {
        String token = UUID.randomUUID().toString();

        UserToken userToken = new UserToken(token, user, tokenType);

        UserToken createdToken = userTokenRepository.save(userToken);

        return frontendUrl + "/" + createdToken.getToken();

    }

}
