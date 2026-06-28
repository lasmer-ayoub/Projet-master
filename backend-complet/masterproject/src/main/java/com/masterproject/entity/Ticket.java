package com.masterproject.entity;

import com.masterproject.entity.enums.Priorite;
import com.masterproject.entity.enums.Statut;
import com.masterproject.entity.enums.TypeTicket;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;



@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre court du ticket  */
    @Column(nullable = false)
    private String titre;

    /** Description detaillee de la tache. */
    @Column(length = 2000)
    private String description;

    /** Type du ticket : TACHE, BUG, AMELIORATION, DEMANDE, URGENCE. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTicket type;

    /** Priorite : FAIBLE, MOYENNE, HAUTE, CRITIQUE. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priorite priorite;

    /** Statut : determine la colonne Kanban (A_FAIRE, EN_COURS, ...). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut;

    /** Date de creation (remplie automatiquement). */
    @Column(nullable = false)
    private LocalDateTime dateCreation;

    /** Date limite a laquelle le ticket doit etre termine (optionnelle). */
    private LocalDate dateLimite;

    /**
     * Le projet auquel appartient ce ticket.
     * Un ticket appartient TOUJOURS a un seul projet.
     */
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * L'utilisateur a qui le ticket est affecte (le "responsable" du ticket).
     * Peut etre null si le ticket n'est encore affecte a personne.
     */
    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private User responsable;

    // --- Constructeurs ---

    public Ticket() {
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeTicket getType() {
        return type;
    }

    public void setType(TypeTicket type) {
        this.type = type;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDate getDateLimite() {
        return dateLimite;
    }

    public void setDateLimite(LocalDate dateLimite) {
        this.dateLimite = dateLimite;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getResponsable() {
        return responsable;
    }

    public void setResponsable(User responsable) {
        this.responsable = responsable;
    }
}
