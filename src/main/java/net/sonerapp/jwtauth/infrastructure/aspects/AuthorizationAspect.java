package net.sonerapp.jwtauth.infrastructure.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.sonerapp.jwtauth.core.model.model_enums.AppRoles;
import net.sonerapp.jwtauth.infrastructure.exceptions.AccessDeniedException;

@Aspect
@Component
@Order(1)
public class AuthorizationAspect {

    private final Logger log = LoggerFactory.getLogger(AuthorizationAspect.class);

    @Before("@annotation(net.sonerapp.jwtauth.infrastructure.annotations.RoleValidator)")
    public void validateAdminRole() {
        log.info("Validating Role");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !hasAdminRole(authentication)) {
            log.warn("Denied Access to this ressource by throwing: " + AccessDeniedException.class.toString());
            throw new AccessDeniedException("Access to this ressource is denied");
        }

    }

    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(
                        grantedAuthorities -> grantedAuthorities.getAuthority().equals(AppRoles.ROLE_ADMIN.toString()));
    }

}
