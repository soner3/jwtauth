package net.sonerapp.jwtauth.core.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.sonerapp.jwtauth.core.model.AuthRoles;
import net.sonerapp.jwtauth.core.model.Role;
import net.sonerapp.jwtauth.core.model.User;
import net.sonerapp.jwtauth.core.service.services.UserService;
import net.sonerapp.jwtauth.infrastructure.repository.RoleRepository;
import net.sonerapp.jwtauth.infrastructure.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, RoleRepository roleRepository,
            UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String username, String email, String password) {
        Role userRole = roleRepository.findByRolename(AuthRoles.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(AuthRoles.ROLE_USER)));

        User user = new User(username, email, passwordEncoder.encode(password));
        user.setRole(userRole);

        return userRepository.save(user);
    }

    @Override
    public User createAdminUser(String username, String email, String password) {
        Role adminRole = roleRepository.findByRolename(AuthRoles.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(AuthRoles.ROLE_ADMIN)));

        User user = new User(username, email, passwordEncoder.encode(password));
        user.setRole(adminRole);

        return userRepository.save(user);
    }

    @Override
    public User getUser(long pk) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public User updateUser(long pk) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatUser'");
    }

    @Override
    public User deleteUser(long pk) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public List<User> getUserList() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUsersList'");
    }

}
