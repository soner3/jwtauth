package net.sonerapp.jwtauth.core.service;

import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;

import net.sonerapp.jwtauth.core.model.User;

public interface UserService {

        User createUser(String username, String email, String password, String rePassword, String firstname,
                        String lastname);

        User createAdminUser(String username, String email, String password, String rePassword, String firstname,
                        String lastname);

        void activateUser(String token);

        User getUser(String username);

        Stream<User> getUserPage(Pageable pageable);

        void deactivateUser(String username);

        boolean isUserEnabled(String username);

        void processResetPasswordRequest(String email);

        void resetPassword(String token, String password, String rePassword);

        void resendActivationMail(String email);

}
