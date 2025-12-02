package com.Gestion.Evenements.repo;
import com.Gestion.Evenements.models.User;
import com.Gestion.Evenements.models.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    static User findFirstByEmail(String email) {
        return null;
    }

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    // Méthode pour récupérer tous les utilisateurs ayant un rôle spécifique
    List<User> findAllByRolesContaining(Role role);
    Optional<User> findByEmail(String email);

}
