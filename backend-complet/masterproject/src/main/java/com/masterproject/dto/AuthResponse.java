package com.masterproject.dto;

/**
 * DTO renvoye apres une connexion ou une inscription reussie.
 * Contient le token JWT (a renvoyer dans les requetes suivantes) et
 * les informations de l'utilisateur connecte.
 */
public record AuthResponse(
        String token,
        String type,      // toujours "Bearer"
        UserDTO utilisateur
) {
}
