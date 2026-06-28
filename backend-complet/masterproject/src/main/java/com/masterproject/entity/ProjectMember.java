package com.masterproject.entity;

import com.masterproject.entity.enums.RoleProjet;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entite ProjectMember : c'est la table mtaa LIAISON entre User et Project.
 *
 relation many too many
 * Un utilisateur peut participer a plusieurs projets, et un projet peut
 * contenir plusieurs utilisateurs (relation "plusieurs-a-plusieurs").
 * Mais en plus, on veut savoir quel ROLE chaque personne a DANS chaque projet
 * (RESPONSABLE ou MEMBRE). C'est exactement ce que stocke cette entite.
 *
 * Exemple : Ahmed peut etre RESPONSABLE du projet A et simple MEMBRE du projet B.
 *
 * La contrainte d'unicite (user_id, project_id) empeche d'ajouter deux fois
 * la meme personne dans le meme projet.
 */
@Entity
@Table(
    name = "project_members",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "project_id"})
)
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleProjet roleProjet;

    /** Date a laquelle l'utilisateur a ete ajoute au projet. */
    @Column(nullable = false)
    private LocalDateTime dateAjout;

    // --- Constructeurs ---

    public ProjectMember() {
    }

    public ProjectMember(User user, Project project, RoleProjet roleProjet) {
        this.user = user;
        this.project = project;
        this.roleProjet = roleProjet;
        this.dateAjout = LocalDateTime.now();
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public RoleProjet getRoleProjet() {
        return roleProjet;
    }

    public void setRoleProjet(RoleProjet roleProjet) {
        this.roleProjet = roleProjet;
    }

    public LocalDateTime getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }
}
