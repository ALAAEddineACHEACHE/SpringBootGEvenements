package com.Gestion.Evenements.utils;

import com.Gestion.Evenements.models.UserPrincipal;
import com.Gestion.Evenements.models.enums.Role;
import com.Gestion.Evenements.models.enums.TokenType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
// Centralisation de la logique de sécurité
    /**
     * Vérifie si l’utilisateur courant a le rôle USER et que son token est un REGISTER_TOKEN
     */
    @PreAuthorize("isAuthenticated()")
    public boolean isToVerifyAccountWithRegisterToken(UserPrincipal currentUser) {

        if (currentUser == null) return false;

        // Vérifier s'il a le rôle USER ou autre
        boolean hasUserRole =
                currentUser.getAuthorities()
                        .stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + Role.ROLE_USER.name()));

        // Vérifier type de token (REGISTER_TOKEN)
        boolean isRegisterToken = currentUser.getTokenType() == TokenType.REGISTER_TOKEN;

        return hasUserRole && isRegisterToken;
    }
}
