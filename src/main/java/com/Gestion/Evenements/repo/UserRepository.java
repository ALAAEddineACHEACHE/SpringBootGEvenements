package com.Gestion.Evenements.repo;
import com.Gestion.Evenements.models.User;
import com.Gestion.Evenements.models.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(@Param("email") String email);

}
