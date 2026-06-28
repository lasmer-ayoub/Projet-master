package com.masterproject.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO recu pour ajouter un commentaire (POST /api/tickets/{ticketId}/comments). */
public record CreateCommentRequest(

        @NotBlank(message = "Le commentaire ne peut pas etre vide")
        String contenu
) {
}
