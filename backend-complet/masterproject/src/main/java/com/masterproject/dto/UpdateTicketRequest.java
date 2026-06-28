package com.masterproject.dto;

import com.masterproject.entity.enums.Priorite;
import com.masterproject.entity.enums.Statut;
import com.masterproject.entity.enums.TypeTicket;
import java.time.LocalDate;

/**
 * DTO recu pour modifier un ticket (PUT /api/tickets/{id}).
 * Tous les champs sont optionnels (peuvent etre null) : on ne met a jour
 * que ceux qui sont fournis. Chaque changement est enregistre dans l'historique.
 */
public record UpdateTicketRequest(
        String titre,
        String description,
        TypeTicket type,
        Priorite priorite,
        Statut statut,
        LocalDate dateLimite,
        String responsableEmail
) {
}
