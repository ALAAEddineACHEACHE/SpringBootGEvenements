package com.Gestion.Evenements.service;

import com.Gestion.Evenements.models.User;
import com.Gestion.Evenements.models.UserPrincipal;
import com.Gestion.Evenements.models.enums.TokenType;
import com.Gestion.Evenements.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Chargement par username (Spring Security utilise "username").
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + email));

        return UserPrincipal.create(user);
    }

    /**
     * Chargement utilisé quand tu veux valider un JWT avec TokenType.
     */
    public UserDetails loadUserByUsernameTokenType(String email, TokenType tokenType, String token) throws UsernameNotFoundException {
        System.out.println("DEBUG: Searching for user with email = '" + email + "'");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + email));

        System.out.println("DEBUG: Found user: " + user.getEmail());

        return UserPrincipal.create(user, tokenType, token);
    }


}
