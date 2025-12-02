package com.Gestion.Evenements.models;

import com.Gestion.Evenements.models.enums.TokenType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {

    private final User user;
    private TokenType tokenType;
    private String token;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public static UserPrincipal create(User user) {
        return new UserPrincipal(user);
    }

    public static UserPrincipal create(User user, TokenType tokenType, String token) {
        UserPrincipal principal = new UserPrincipal(user);
        principal.tokenType = tokenType;
        principal.token = token;
        return principal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // déjà hashé
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // utilisé pour JWT
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
