package com.Gestion.Evenements.service.UserService;
import com.Gestion.Evenements.dto.AuthRequest;
import com.Gestion.Evenements.dto.AuthResponse;
import com.Gestion.Evenements.dto.RegisterRequest;
import com.Gestion.Evenements.models.User;

import java.util.List;

public interface IUserService {

    /** Inscription d'un utilisateur */
    User register(RegisterRequest request);

    /** Authentification + génération JWT */
    AuthResponse login(AuthRequest request);

    /** Récupérer un user par ID */
    User getUserById(Long id);

    /** Récupérer un user par email */
    User getUserByEmail(String email);

    /** Liste de tous les utilisateurs */
    List<User> getAllUsers();

    /** Mise à jour du profil utilisateur */
    User updateUser(Long id, User updated);

    /** Suppression d'un utilisateur */
    void deleteUser(Long id);

    /** Ajouter un rôle à un utilisateur */
    User addRoleToUser(Long userId, String roleName);

    /** Récupère l'utilisateur connecté à partir du token JWT */
    User getCurrentUser();

    /** Vérifier si email existe déjà */
    boolean emailExists(String email);
}
