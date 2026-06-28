package com.masterproject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entite Project : represente un projet (ou espace de travail).
 *
 * Un projet est cree par un utilisateur (le "createur"), qui en devient
 * automatiquement le RESPONSABLE (voir ProjectService.creerProjet).
 * Un projet contient plusieurs tickets et regroupe plusieurs membres.
 */
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom du projet (ex : "Organisation d'un forum etudiant"). */
    @Column(nullable = false)
    private String nom;

    /** Description libre du projet. */
    @Column(length = 1000)
    private String description;

    /** Date et heure de creation du projet (remplie automatiquement). */
    @Column(nullable = false)
    private LocalDateTime dateCreation;

    /**
     * L'utilisateur qui a cree le projet.
     * C'est lui le responsable initial.
     */
    @ManyToOne
    @JoinColumn(name = "createur_id", nullable = false)
    private User createur;

    // --- Constructeurs ---

    public Project() {
    }

    public Project(String nom, String description, User createur) {
        this.nom = nom;
        this.description = description;
        this.createur = createur;
        this.dateCreation = LocalDateTime.now();
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public User getCreateur() {
        return createur;
    }

    public void setCreateur(User createur) {
        this.createur = createur;
    }
}
