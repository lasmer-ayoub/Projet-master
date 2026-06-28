package com.masterproject.dto;

import java.util.Map;

/**
 * Statistiques globales de l'application, pour les graphes de l'admin.
 *  - utilisateursParRole : combien d'ADMIN, combien d'USER
 *  - ticketsParStatut    : combien de tickets dans chaque statut
 */
public record AdminStatsDTO(
        long totalUtilisateurs,
        Map<String, Long> utilisateursParRole,
        long totalProjets,
        long totalTickets,
        Map<String, Long> ticketsParStatut
) {}
