package com.masterproject.controller;

import com.masterproject.dto.UserDTO;
import com.masterproject.dto.UpdateProfileRequest;
import com.masterproject.dto.ChangePasswordRequest;
import com.masterproject.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController : endpoints lies aux utilisateurs.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Liste tous les utilisateurs.
     *
     * @PreAuthorize("hasRole('ADMIN')") : c'est le controle du role SYSTEME.
     * Seul un utilisateur ayant le role ADMIN peut appeler cet endpoint ;
     * sinon Spring Security renvoie automatiquement une erreur 403 (interdit).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> listerTous() {
        return userService.listerTous();
    }

    /** Renvoie les informations de l'utilisateur actuellement connecte. */
    @GetMapping("/me")
    public UserDTO monProfil() {
        return userService.versDTO(userService.getUtilisateurConnecte());
    }

    /** Modifie mon profil (nom + email). */
    @PutMapping("/me")
    public UserDTO modifierMonProfil(@jakarta.validation.Valid @RequestBody UpdateProfileRequest demande) {
        return userService.modifierMonProfil(demande);
    }

    /** Change mon mot de passe (verifie l'ancien). */
    @PutMapping("/me/password")
    public void changerMonMotDePasse(@jakarta.validation.Valid @RequestBody ChangePasswordRequest demande) {
        userService.changerMonMotDePasse(demande);
    }
}
