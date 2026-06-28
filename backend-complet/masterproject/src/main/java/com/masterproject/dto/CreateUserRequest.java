package com.masterproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Donnees envoyees par l'admin pour CREER un utilisateur.
 * Les annotations @NotBlank / @Email valident automatiquement les champs.
 */
public record CreateUserRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @Email(message = "L'email n'est pas valide")
        @NotBlank(message = "L'email est obligatoire")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 4, message = "Le mot de passe doit faire au moins 4 caracteres")
        String motDePasse,

        @NotBlank(message = "Le role est obligatoire")
        String role   // "ADMIN" ou "USER"
) {}
