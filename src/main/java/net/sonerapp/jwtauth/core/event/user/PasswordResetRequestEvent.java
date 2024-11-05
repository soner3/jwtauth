package net.sonerapp.jwtauth.core.event.user;

import net.sonerapp.jwtauth.core.model.User;

public record PasswordResetRequestEvent(User user) {

}
