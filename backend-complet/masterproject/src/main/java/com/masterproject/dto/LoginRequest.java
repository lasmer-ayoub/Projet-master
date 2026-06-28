package com.masterproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO recu lors de la connexion (POST /api/auth/login).
 *
 * On utilise un "record" Java : c'est une classe immuable et tres concise,
 * parfaite pour transporter des donnees (pas besoin d'ecrire les getters).
 *
 * Les annotations @NotBlank / @Email servent a VALIDER les donnees envoyees :
 * si l'email est vide ou mal forme, la requete est rejetee automatiquement.
 */
public record LoginRequest(

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email n'est pas valide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        String motDePasse
) {
}
