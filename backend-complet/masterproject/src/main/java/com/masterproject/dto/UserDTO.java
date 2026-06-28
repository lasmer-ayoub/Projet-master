package com.masterproject.dto;

/**
 * DTO renvoye pour representer un utilisateur.
 *
 * Tres important : on NE met PAS le mot de passe ici. C'est tout l'interet
 * du DTO : on choisit precisement les champs exposes au client.
 *
 * Le champ "valide" indique si le compte a ete approuve par l'administrateur.
 */
public record UserDTO(
        Long id,
        String nom,
        String email,
        String role,
        boolean valide
) {
}
