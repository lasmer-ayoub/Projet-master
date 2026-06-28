package com.masterproject.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Vue d'un projet pour l'ADMIN : en plus des infos du projet, on indique
 * qui est le responsable, combien de membres, leurs noms, et le nombre de tickets.
 */
public record AdminProjectDTO(
        Long id,
        String nom,
        String description,
        LocalDateTime dateCreation,
        String responsableNom,
        int nombreMembres,
        List<String> membresNoms,
        int nombreTickets
) {}
