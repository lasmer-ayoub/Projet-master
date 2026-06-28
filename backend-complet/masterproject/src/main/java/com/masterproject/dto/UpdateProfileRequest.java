package com.masterproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Donnees pour que l'utilisateur connecte modifie SON profil (nom + email). */
public record UpdateProfileRequest(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @Email(message = "L'email n'est pas valide") @NotBlank(message = "L'email est obligatoire") String email
) {}
