package com.masterproject.dto;

import java.time.LocalDateTime;

/**
 * DTO renvoye pour representer un projet.
 * "monRoleProjet" indique le role de l'utilisateur CONNECTE dans ce projet
 * (RESPONSABLE ou MEMBRE) : pratique pour que le front sache quoi afficher.
 */
public record ProjectDTO(
        Long id,
        String nom,
        String description,
        LocalDateTime dateCreation,
        String createurNom,
        String monRoleProjet
) {
}
