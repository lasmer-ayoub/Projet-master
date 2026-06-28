package com.masterproject.dto;

import java.util.Map;

/**
 * DTO renvoye pour le tableau de bord d'un projet (GET /api/projects/{id}/stats).
 * - totalTickets       : nombre total de tickets du projet
 * - ticketsParStatut   : nombre de tickets pour chaque statut (A_FAIRE, EN_COURS...)
 * - pourcentageTermine : pourcentage global d'avancement (tickets TERMINE / total)
 */
public record ProjectStatsDTO(
        long totalTickets,
        Map<String, Long> ticketsParStatut,
        double pourcentageTermine
) {
}
