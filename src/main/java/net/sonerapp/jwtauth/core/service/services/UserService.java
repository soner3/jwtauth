package net.sonerapp.jwtauth.core.service.services;

import java.util.List;

import net.sonerapp.jwtauth.core.model.User;

public interface UserService {
    User createUser(String username, String email, String password);

    User createAdminUser(String username, String email, String password);

    User getUser(long pk);

    User updateUser(long pk);

    User deleteUser(long pk);

    List<User> getUserList();

}
