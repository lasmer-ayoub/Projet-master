package com.masterproject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entite Notification : un message destine a un utilisateur.
 * Exemples : "Vous avez ete invite au projet X",
 *            "Le ticket Y vous a ete attribue".
 *
 * Chaque notification appartient a UN utilisateur (le destinataire) et
 * possede un indicateur "lue" (false = non lue, affichee dans la cloche).
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le texte affiche a l'utilisateur. */
    @Column(nullable = false, length = 500)
    private String message;

    /** false = pas encore lue (comptee dans la cloche) ; true = lue. */
    @Column(nullable = false)
    private boolean lue = false;

    /** Date de creation de la notification. */
    @Column(nullable = false)
    private LocalDateTime dateCreation;

    /** Le destinataire de la notification. */
    @ManyToOne
    @JoinColumn(name = "destinataire_id", nullable = false)
    private User destinataire;

    // --- Constructeurs ---

    public Notification() {
    }

    public Notification(String message, User destinataire) {
        this.message = message;
        this.destinataire = destinataire;
        this.lue = false;
        this.dateCreation = LocalDateTime.now();
    }

    // --- Getters / Setters ---

    public Long getId() { return id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isLue() { return lue; }
    public void setLue(boolean lue) { this.lue = lue; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public User getDestinataire() { return destinataire; }
    public void setDestinataire(User destinataire) { this.destinataire = destinataire; }
}
