package com.masterproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO recu pour ajouter un membre a un projet
 * (POST /api/projects/{id}/members).
 * On identifie la personne a ajouter par son email.
 */
public record AddMemberRequest(

        @NotBlank(message = "L'email du membre est obligatoire")
        @Email(message = "L'email n'est pas valide")
        String email
) {
}
