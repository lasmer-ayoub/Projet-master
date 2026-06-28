package com.masterproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO recu lors de l'inscription (POST /api/auth/register). */
public record RegisterRequest(

        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email n'est pas valide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caracteres")
        String motDePasse
) {
}
