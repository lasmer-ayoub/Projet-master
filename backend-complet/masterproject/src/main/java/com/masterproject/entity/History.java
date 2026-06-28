package com.masterproject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entite History : l'historique des modifications d'un ticket.
 *
 * A chaque fois qu'un champ important d'un ticket change (par exemple le statut
 * qui passe de "A faire" a "En cours"), on enregistre une ligne dans cette table :
 * quel champ a change, son ancienne valeur, sa nouvelle valeur, qui l'a modifie
 * et quand. Cela correspond a la fonctionnalite "historique des changements"
 * du cahier des charges et permet une tracabilite complete.
 */
@Entity
@Table(name = "histories")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le nom du champ modifie (ex : "statut", "priorite", "responsable"). */
    @Column(nullable = false)
    private String champModifie;

    /** L'ancienne valeur avant la modification. */
    private String ancienneValeur;

    /** La nouvelle valeur apres la modification. */
    private String nouvelleValeur;

    /** Date et heure de la modification. */
    @Column(nullable = false)
    private LocalDateTime dateModification;

    /** Le ticket concerne par la modification. */
    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    /** L'utilisateur qui a effectue la modification. */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- Constructeurs ---

    public History() {
    }

    public History(String champModifie, String ancienneValeur, String nouvelleValeur,
                   Ticket ticket, User user) {
        this.champModifie = champModifie;
        this.ancienneValeur = ancienneValeur;
        this.nouvelleValeur = nouvelleValeur;
        this.ticket = ticket;
        this.user = user;
        this.dateModification = LocalDateTime.now();
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChampModifie() {
        return champModifie;
    }

    public void setChampModifie(String champModifie) {
        this.champModifie = champModifie;
    }

    public String getAncienneValeur() {
        return ancienneValeur;
    }

    public void setAncienneValeur(String ancienneValeur) {
        this.ancienneValeur = ancienneValeur;
    }

    public String getNouvelleValeur() {
        return nouvelleValeur;
    }

    public void setNouvelleValeur(String nouvelleValeur) {
        this.nouvelleValeur = nouvelleValeur;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
