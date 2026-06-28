package com.masterproject.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom complet de l'utilisateur. */
    @Column(nullable = false)
    private String nom;

    /**
     * unique est ce que fama compte bel email hedha wala lé
     */
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    /**
     * @ManyToOne : plusieurs utilisateurs peuvent partager le meme role.
     */
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /**
     *  - false : le compte vient d'etre cree, il est en attente.
     *  - true  : le compte est actif, la connexion est autorisee.
     *
     * Par defaut, un nouvel inscrit est NON valide (false).
     */
    @Column(nullable = false)
    private boolean valide = false;

    // --- Constructeurs ---

    public User() {
    }

    public User(String nom, String email, String motDePasse, Role role) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }
}
