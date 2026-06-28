package com.masterproject.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** DTO renvoye pour representer un ticket. */
public record TicketDTO(
        Long id,
        String titre,
        String description,
        String type,
        String priorite,
        String statut,
        LocalDateTime dateCreation,
        LocalDate dateLimite,
        Long projectId,
        String responsableNom,
        String responsableEmail
) {
}
