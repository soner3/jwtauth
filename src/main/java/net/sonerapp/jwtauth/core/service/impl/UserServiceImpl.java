package net.sonerapp.jwtauth.core.service.impl;

import java.time.Instant;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sonerapp.jwtauth.core.event.user.PasswordResetRequestEvent;
import net.sonerapp.jwtauth.core.event.user.ResendActivationMailEvent;
import net.sonerapp.jwtauth.core.event.user.UserCreatedEvent;
import net.sonerapp.jwtauth.core.exceptions.OutOfBoundsException;
import net.sonerapp.jwtauth.core.exceptions.UserController.EmailDoesNotExistException;
import net.sonerapp.jwtauth.core.exceptions.UserController.EmailExistsException;
import net.sonerapp.jwtauth.core.exceptions.UserController.InvalidUserTokenTypeException;
import net.sonerapp.jwtauth.core.exceptions.UserController.NoStrongPasswordException;
import net.sonerapp.jwtauth.core.exceptions.UserController.PasswordIsNullException;
import net.sonerapp.jwtauth.core.exceptions.UserController.PasswordsDoNotMatchException;
import net.sonerapp.jwtauth.core.exceptions.UserController.TokenAlreadyUsedException;
import net.sonerapp.jwtauth.core.exceptions.UserController.TokenExpiredException;
import net.sonerapp.jwtauth.core.exceptions.UserController.UnknownTokenException;
import net.sonerapp.jwtauth.core.exceptions.UserController.UserEnabledException;
import net.sonerapp.jwtauth.core.exceptions.UserController.UsernameExistsException;
import net.sonerapp.jwtauth.core.model.Role;
import net.sonerapp.jwtauth.core.model.User;
import net.sonerapp.jwtauth.core.model.UserToken;
import net.sonerapp.jwtauth.core.model.model_enums.AppRoles;
import net.sonerapp.jwtauth.core.model.model_enums.UserTokenType;
import net.sonerapp.jwtauth.core.repository.RoleRepository;
import net.sonerapp.jwtauth.core.repository.UserRepository;
import net.sonerapp.jwtauth.core.repository.UserTokenRepository;
import net.sonerapp.jwtauth.core.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher publisher;

    private final UserTokenRepository userTokenRepository;

    public UserServiceImpl(RoleRepository roleRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder, ApplicationEventPublisher publisher,
            UserTokenRepository userTokenRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.publisher = publisher;
        this.userTokenRepository = userTokenRepository;
    }

    @Override
    public User createUser(String username, String email, String password, String rePassword, String firstname,
            String lastname) {

        validateRegistrationCredentials(username, email, password, rePassword);

        Role userRole = roleRepository.findByRolename(AppRoles.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(AppRoles.ROLE_USER)));

        User user = new User(username, email, passwordEncoder.encode(password), firstname, lastname);

        user.setRole(userRole);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(false);

        User createdUser = userRepository.save(user);

        publisher.publishEvent(new UserCreatedEvent(createdUser));

        return createdUser;
    }

    @Override
    public User createAdminUser(String username, String email, String password, String rePassword, String firstname,
            String lastname) {

        validateRegistrationCredentials(username, email, password, rePassword);

        Role adminRole = roleRepository.findByRolename(AppRoles.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(AppRoles.ROLE_ADMIN)));

        User adminUser = new User(username, email, passwordEncoder.encode(password), firstname, lastname);

        adminUser.setRole(adminRole);
        adminUser.setAccountNonExpired(true);
        adminUser.setAccountNonLocked(true);
        adminUser.setCredentialsNonExpired(true);
        adminUser.setEnabled(true);

        User createdUser = userRepository.save(adminUser);

        return createdUser;
    }

    @Override
    public void activateUser(String token) {
        UserToken userToken = getValidToken(token, UserTokenType.USER_ACTIVATION_TOKEN);

        User user = userToken.getUser();

        user.setEnabled(true);
        userToken.setUsed(true);

        userRepository.save(user);
        userTokenRepository.save(userToken);

    }

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new OutOfBoundsException("No user found with the id: " + username));
    }

    @Override
    public Stream<User> getUserPage(Pageable pageable) {
        return userRepository.findAll(pageable).get();
    }

    private boolean isPasswordStrong(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.matches(passwordPattern, password);
    }

    @Override
    @Transactional
    public void deactivateUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        user.setEnabled(false);
        userRepository.save(user);

    }

    @Override
    public void processResetPasswordRequest(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailDoesNotExistException("No user found with the given mail: " + email));

        publisher.publishEvent(new PasswordResetRequestEvent(user));

    }

    @Override
    @Transactional
    public void resetPassword(String token, String password, String rePassword) {
        UserToken userToken = getValidToken(token, UserTokenType.PASSWORD_RESET_TOKEN);

        if (password == null || rePassword == null) {
            throw new PasswordIsNullException("The password or the rePassword is Null");
        }

        if (!password.equals(rePassword)) {
            throw new PasswordsDoNotMatchException("RePassword and Password do not match");
        }

        if (!isPasswordStrong(password)) {
            throw new NoStrongPasswordException(
                    "your password must contain at least 8 characters and has uppercase, lowercase, digits and special characters.");
        }

        User user = userToken.getUser();

        user.setPassword(passwordEncoder.encode(password));

        userToken.setUsed(true);

        userRepository.save(user);
        userTokenRepository.save(userToken);

    }

    @Override
    public void resendActivationMail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailDoesNotExistException("No user found with the given mail: " + email));
        if (user.isEnabled()) {
            throw new UserEnabledException("User is already enabled");
        }

        publisher.publishEvent(new ResendActivationMailEvent(user));
    }

    @Override
    public boolean isUserEnabled(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No User found with the name: " + username));
        return user.isEnabled();
    }

    private UserToken getValidToken(String token, UserTokenType tokenType) {
        UserToken userToken = userTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnknownTokenException("Token is unknown to the system"));

        if (userToken.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenExpiredException("Token is already expired");
        }

        if (userToken.isUsed()) {
            throw new TokenAlreadyUsedException("Token has already been used");
        }

        if (!userToken.getTokenType().equals(tokenType)) {
            throw new InvalidUserTokenTypeException("Invalid token type");
        }

        return userToken;
    }

    private void validateRegistrationCredentials(String username, String email, String password, String rePassword) {

        if (password == null || rePassword == null) {
            throw new PasswordIsNullException("The password or the rePassword is Null");
        }

        if (!password.equals(rePassword)) {
            throw new PasswordsDoNotMatchException("RePassword and Password do not match");
        }

        if (userRepository.existsByUsername(username)) {
            throw new UsernameExistsException("The given username already exists.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new EmailExistsException("The given email already exists.");
        }

        if (!isPasswordStrong(password)) {
            throw new NoStrongPasswordException(
                    "your password must contain at least 8 characters and has uppercase, lowercase, digits and special characters.");
        }
    }

}
