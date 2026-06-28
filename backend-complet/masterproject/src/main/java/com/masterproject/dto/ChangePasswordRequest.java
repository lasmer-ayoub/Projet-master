package com.masterproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Donnees pour que l'utilisateur connecte change SON mot de passe. */
public record ChangePasswordRequest(
        @NotBlank(message = "Le mot de passe actuel est obligatoire") String ancienMotDePasse,
        @NotBlank(message = "Le nouveau mot de passe est obligatoire")
        @Size(min = 4, message = "Le nouveau mot de passe doit faire au moins 4 caracteres")
        String nouveauMotDePasse
) {}
