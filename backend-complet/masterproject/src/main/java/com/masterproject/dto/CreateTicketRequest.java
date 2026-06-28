package com.masterproject.dto;

import com.masterproject.entity.enums.Priorite;
import com.masterproject.entity.enums.TypeTicket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO recu pour creer un ticket dans un projet
 * (POST /api/projects/{projectId}/tickets).
 * responsableEmail est optionnel : si fourni, le ticket est directement
 * affecte a cette personne (qui doit etre membre du projet).
 */
public record CreateTicketRequest(

        @NotBlank(message = "Le titre est obligatoire")
        String titre,

        String description,

        @NotNull(message = "Le type est obligatoire")
        TypeTicket type,

        @NotNull(message = "La priorite est obligatoire")
        Priorite priorite,

        LocalDate dateLimite,

        String responsableEmail
) {
}
