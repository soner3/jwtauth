package net.sonerapp.jwtauth.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sonerapp.jwtauth.core.model.Role;
import net.sonerapp.jwtauth.core.model.model_enums.AppRoles;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    boolean existsByRolename(AppRoles rolename);

    Optional<Role> findByRolename(AppRoles rolename);

}
