package com.masterproject.dto;

import java.time.LocalDateTime;

/** DTO renvoye pour representer un commentaire. */
public record CommentDTO(
        Long id,
        String contenu,
        LocalDateTime dateCreation,
        String auteurNom
) {
}
