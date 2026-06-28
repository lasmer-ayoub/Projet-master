package com.masterproject.repository;

import com.masterproject.entity.Notification;
import com.masterproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Repository pour l'entite Notification. */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** Toutes les notifications d'un utilisateur, de la plus recente a la plus ancienne. */
    List<Notification> findByDestinataireOrderByDateCreationDesc(User destinataire);

    /** Nombre de notifications NON LUES d'un utilisateur (pour le compteur de la cloche). */
    long countByDestinataireAndLueFalse(User destinataire);
}
