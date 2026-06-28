package com.masterproject.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO recu pour creer un projet (POST /api/projects). */
public record CreateProjectRequest(

        @NotBlank(message = "Le nom du projet est obligatoire")
        String nom,

        String description
) {
}
