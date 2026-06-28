package com.masterproject.controller;

import com.masterproject.dto.NotificationDTO;
import com.masterproject.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * NotificationController : routes des notifications de l'utilisateur connecte.
 * Toutes ces routes necessitent d'etre connecte (gere par Spring Security).
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** Liste mes notifications . */
    @GetMapping
    public List<NotificationDTO> mesNotifications() {
        return notificationService.listerMesNotifications();
    }

    /** Nombre de notifications non lues . */
    @GetMapping("/count")
    public Map<String, Long> compterNonLues() {
        return Map.of("nonLues",
                notificationService.compterNonLues());
    }

    /** Marquer toutes mes notifications comme lues. */
    @PutMapping("/read-all")
    public void toutMarquerLues() {
        notificationService.marquerToutesLues();
    }

    /** Marquer une notification precise comme lue. */
    @PutMapping("/{id}/read")
    public void marquerLue(@PathVariable Long id) {
        notificationService.marquerLue(id);
    }
}
