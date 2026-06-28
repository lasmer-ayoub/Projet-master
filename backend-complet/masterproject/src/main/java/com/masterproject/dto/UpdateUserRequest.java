package com.masterproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Donnees envoyees par l'admin pour MODIFIER un utilisateur.
 * (Le mot de passe n'est pas modifie ici, pour rester simple.)
 */
public record UpdateUserRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @Email(message = "L'email n'est pas valide")
        @NotBlank(message = "L'email est obligatoire")
        String email,

        @NotBlank(message = "Le role est obligatoire")
        String role   // "ADMIN" ou "USER"
) {}
