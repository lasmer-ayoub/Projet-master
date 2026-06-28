package com.masterproject.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     *  : "ADMIN" ou "USER".
     */
    @Column(unique = true, nullable = false)
    private String libelle;

    // --- Constructeurs ---

    public Role() {
    }

    public Role(String libelle) {
        this.libelle = libelle;
    }

    // --- Getters et Setters ---


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
