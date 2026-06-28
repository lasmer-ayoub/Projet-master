package com.masterproject.dto;

import java.time.LocalDateTime;

/** DTO d'une notification renvoyee au frontend. */
public record NotificationDTO(
        Long id,
        String message,
        boolean lue,
        LocalDateTime dateCreation
) {}
