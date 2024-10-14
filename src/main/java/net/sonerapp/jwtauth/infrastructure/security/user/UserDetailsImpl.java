package net.sonerapp.jwtauth.infrastructure.security.user;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.sonerapp.jwtauth.core.model.User;

@Service
public class UserDetailsImpl implements UserDetails {

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private boolean isEnabled;

    private boolean isCredentialsNonExpired;

    private boolean isAccountNonLocked;

    private boolean isAccountNonExpired;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl() {
    }

    public UserDetailsImpl(String username, String email, String password, boolean isEnabled,
            boolean isCredentialsNonExpired, boolean isAccountNonLocked, boolean isAccountNonExpired,
            Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isEnabled = isEnabled;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isAccountNonExpired = isAccountNonExpired;
        this.authorities = authorities;
    }

    public static UserDetails build(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());

        return new UserDetailsImpl(user.getUsername(), user.getEmail(), user.getPassword(), user.isEnabled(),
                user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.isAccountNonExpired(),
                List.of(authority));
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

}
