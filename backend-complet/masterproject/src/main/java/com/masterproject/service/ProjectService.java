package com.masterproject.service;

import com.masterproject.dto.*;
import com.masterproject.entity.*;
import com.masterproject.entity.enums.RoleProjet;
import com.masterproject.entity.enums.Statut;
import com.masterproject.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProjectService : logique metier des projets.
 *
 * C'est ici qu'on applique la regle cle de l'application :
 *  - celui qui CREE un projet en devient automatiquement le RESPONSABLE,
 *  - le RESPONSABLE peut inviter des membres,
 *  - seuls les MEMBRES d'un projet peuvent le consulter.
 *
 * Les methodes de verification (verifierMembre / verifierResponsable) sont
 * aussi reutilisees par TicketService et CommentService.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository projectMemberRepository,
                          UserRepository userRepository,
                          TicketRepository ticketRepository,
                          UserService userService,
                          NotificationService notificationService) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * Cree un projet. L'utilisateur connecte devient le createur ET le
     * RESPONSABLE du projet (on cree automatiquement la ligne ProjectMember).
     * @Transactional : si une etape echoue, tout est annule (coherence des donnees).
     */
    @Transactional
    public ProjectDTO creerProjet(CreateProjectRequest demande) {
        User createur = userService.getUtilisateurConnecte();

        // 1. On cree et enregistre le projet.
        Project projet = new Project(demande.nom(), demande.description(), createur);
        projectRepository.save(projet);

        // 2. On inscrit le createur comme RESPONSABLE de ce projet.
        ProjectMember appartenance =
                new ProjectMember(createur, projet, RoleProjet.RESPONSABLE);
        projectMemberRepository.save(appartenance);

        return versDTO(projet, RoleProjet.RESPONSABLE);
    }

    /** Liste les projets auxquels l'utilisateur connecte participe. */
    public List<ProjectDTO> listerMesProjets() {
        User utilisateur = userService.getUtilisateurConnecte();
        return projectMemberRepository.findByUser(utilisateur).stream()
                .map(appartenance ->
                        versDTO(appartenance.getProject(), appartenance.getRoleProjet()))
                .toList();
    }

    /** Recupere le detail d'un projet (l'utilisateur doit en etre membre). */
    public ProjectDTO getProjet(Long projectId) {
        Project projet = getProjetOuErreur(projectId);
        ProjectMember appartenance = verifierMembre(projet);
        return versDTO(projet, appartenance.getRoleProjet());
    }

    /**
     * Ajoute un membre a un projet.
     * Seul le RESPONSABLE du projet peut le faire. Le nouvel utilisateur
     * est ajoute avec le role MEMBRE.
     */
    @Transactional
    public MemberDTO ajouterMembre(Long projectId, AddMemberRequest demande) {
        Project projet = getProjetOuErreur(projectId);

        // Verification : la personne connectee doit etre RESPONSABLE du projet.
        verifierResponsable(projet);

        // On retrouve l'utilisateur a ajouter grace a son email.
        User nouvelUtilisateur = userRepository.findByEmail(demande.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aucun utilisateur avec cet email"));

        // On verifie qu'il n'est pas deja membre.
        if (projectMemberRepository.findByUserAndProject(nouvelUtilisateur, projet).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cet utilisateur est deja membre du projet");
        }

        // On l'ajoute comme MEMBRE.
        ProjectMember appartenance =
                new ProjectMember(nouvelUtilisateur, projet, RoleProjet.MEMBRE);
        projectMemberRepository.save(appartenance);

        // Notification au membre invite.
        notificationService.creerNotification(nouvelUtilisateur,
                "Vous avez ete invite au projet \"" + projet.getNom() + "\".");

        return versMemberDTO(appartenance);
    }

    /** Liste les membres d'un projet (l'utilisateur doit en etre membre). */
    public List<MemberDTO> listerMembres(Long projectId) {
        Project projet = getProjetOuErreur(projectId);
        verifierMembre(projet);
        return projectMemberRepository.findByProject(projet).stream()
                .map(this::versMemberDTO)
                .toList();
    }

    /**
     * Calcule les statistiques d'un projet pour le tableau de bord :
     * nombre total de tickets, repartition par statut, et pourcentage
     * d'avancement (tickets TERMINE / total). Correspond a la fonctionnalite
     * "tableau de bord avec statistiques" du cahier des charges.
     */
    public ProjectStatsDTO getStatistiques(Long projectId) {
        Project projet = getProjetOuErreur(projectId);
        verifierMembre(projet);

        List<Ticket> tickets = ticketRepository.findByProject(projet);
        long total = tickets.size();

        // On compte le nombre de tickets pour chaque statut.
        Map<String, Long> parStatut = new HashMap<>();
        for (Ticket ticket : tickets) {
            String statut = ticket.getStatut().name();
            parStatut.put(statut, parStatut.getOrDefault(statut, 0L) + 1);
        }

        // Pourcentage d'avancement = part des tickets au statut TERMINE.
        long nbTermines = parStatut.getOrDefault(Statut.TERMINE.name(), 0L);
        double pourcentage = (total == 0) ? 0.0 : (nbTermines * 100.0) / total;

        return new ProjectStatsDTO(total, parStatut, pourcentage);
    }

    // ============================================================
    //   METHODES UTILITAIRES (reutilisees par les autres services)
    // ============================================================

    /** Recupere un projet par son id, ou leve une erreur 404 s'il n'existe pas. */
    public Project getProjetOuErreur(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Projet introuvable"));
    }

    /**
     * Verifie que l'utilisateur connecte est bien MEMBRE du projet.
     * Renvoie sa ligne d'appartenance (qui contient son role projet),
     * ou leve une erreur 403 (acces interdit) s'il n'est pas membre.
     */
    public ProjectMember verifierMembre(Project projet) {
        User utilisateur = userService.getUtilisateurConnecte();
        return projectMemberRepository.findByUserAndProject(utilisateur, projet)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Vous n'etes pas membre de ce projet"));
    }

    /**
     * Verifie que l'utilisateur connecte est le RESPONSABLE du projet.
     * Leve une erreur 403 si ce n'est pas le cas.
     */
    public void verifierResponsable(Project projet) {
        ProjectMember appartenance = verifierMembre(projet);
        if (appartenance.getRoleProjet() != RoleProjet.RESPONSABLE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Action reservee au responsable du projet");
        }
    }

    // --- Conversions entite -> DTO ---

    private ProjectDTO versDTO(Project projet, RoleProjet monRole) {
        return new ProjectDTO(
                projet.getId(),
                projet.getNom(),
                projet.getDescription(),
                projet.getDateCreation(),
                projet.getCreateur().getNom(),
                monRole.name()
        );
    }

    private MemberDTO versMemberDTO(ProjectMember appartenance) {
        User u = appartenance.getUser();
        return new MemberDTO(
                u.getId(),
                u.getNom(),
                u.getEmail(),
                appartenance.getRoleProjet().name(),
                appartenance.getDateAjout()
        );
    }
}
