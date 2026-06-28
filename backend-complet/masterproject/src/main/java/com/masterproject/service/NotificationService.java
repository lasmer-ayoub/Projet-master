package com.masterproject.service;

import com.masterproject.dto.NotificationDTO;
import com.masterproject.entity.Notification;
import com.masterproject.entity.User;
import com.masterproject.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * NotificationService : gestion des notifications des utilisateurs.
 *
 * Les autres services (ProjectService, TicketService) appellent
 * creerNotification(...) pour prevenir un utilisateur d'un evenement
 * (invitation a un projet, ticket attribue, ...).
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(NotificationRepository notificationRepository,
                               UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    /** Cree une notification pour un utilisateur donne. */
    public void creerNotification(User destinataire, String message) {
        notificationRepository.save(new Notification(message, destinataire));
    }

    /** Liste les notifications de l'utilisateur CONNECTE (plus recentes d'abord). */
    public List<NotificationDTO> listerMesNotifications() {
        User moi = userService.getUtilisateurConnecte();
        return notificationRepository.findByDestinataireOrderByDateCreationDesc(moi).stream()
                .map(this::versDTO)
                .toList();
    }

    /** Nombre de notifications non lues de l'utilisateur connecte (compteur cloche). */
    public long compterNonLues() {
        User moi = userService.getUtilisateurConnecte();
        return notificationRepository.countByDestinataireAndLueFalse(moi);
    }

    /** Marque TOUTES mes notifications comme lues. */
    public void marquerToutesLues() {
        User moi = userService.getUtilisateurConnecte();
        notificationRepository.findByDestinataireOrderByDateCreationDesc(moi).forEach(n -> {
            if (!n.isLue()) {
                n.setLue(true);
                notificationRepository.save(n);
            }
        });
    }

    /** Marque UNE notification comme lue (en verifiant qu'elle m'appartient). */
    public void marquerLue(Long id) {
        User moi = userService.getUtilisateurConnecte();
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification introuvable"));
        if (!n.getDestinataire().getId().equals(moi.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cette notification ne vous appartient pas");
        }
        n.setLue(true);
        notificationRepository.save(n);
    }

    /** Conversion entite -> DTO. */
    private NotificationDTO versDTO(Notification n) {
        return new NotificationDTO(n.getId(), n.getMessage(), n.isLue(), n.getDateCreation());
    }
}
