package net.sonerapp.jwtauth.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sonerapp.jwtauth.core.model.AuthRoles;
import net.sonerapp.jwtauth.core.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRolename(AuthRoles rolename);

}
