package com.masterproject.dto;

import java.time.LocalDateTime;

/** DTO renvoye pour representer une ligne d'historique d'un ticket. */
public record HistoryDTO(
        Long id,
        String champModifie,
        String ancienneValeur,
        String nouvelleValeur,
        LocalDateTime dateModification,
        String auteurNom
) {
}
