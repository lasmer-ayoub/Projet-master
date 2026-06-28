package com.masterproject.dto;

import java.time.LocalDateTime;

/** DTO renvoye pour representer un membre d'un projet. */
public record MemberDTO(
        Long userId,
        String nom,
        String email,
        String roleProjet,
        LocalDateTime dateAjout
) {
}
