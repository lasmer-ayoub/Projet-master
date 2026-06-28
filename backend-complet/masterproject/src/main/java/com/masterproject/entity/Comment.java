package com.masterproject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entite Comment : un commentaire ecrit par un utilisateur sur un ticket.
 * Permet la collaboration et les echanges autour d'une tache.
 */
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le texte du commentaire. */
    @Column(nullable = false, length = 2000)
    private String contenu;

    /** Date et heure du commentaire (remplie automatiquement). */
    @Column(nullable = false)
    private LocalDateTime dateCreation;

    /** Le ticket sur lequel porte le commentaire. */
    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    /** L'utilisateur qui a ecrit le commentaire. */
    @ManyToOne
    @JoinColumn(name = "auteur_id", nullable = false)
    private User auteur;

    // --- Constructeurs ---

    public Comment() {
    }

    public Comment(String contenu, Ticket ticket, User auteur) {
        this.contenu = contenu;
        this.ticket = ticket;
        this.auteur = auteur;
        this.dateCreation = LocalDateTime.now();
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getAuteur() {
        return auteur;
    }

    public void setAuteur(User auteur) {
        this.auteur = auteur;
    }
}
