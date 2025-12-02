package com.Gestion.Evenements.service.UserService;

import com.Gestion.Evenements.models.User;
import com.Gestion.Evenements.repo.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    public UserService(UserRepository repo) { this.userRepository = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var authorities = u.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.name())).collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), authorities);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Supprime d'abord les rôles liés
        user.getRoles().clear();
        userRepository.delete(user);
    }
}
