package com.masterproject.service;

import com.masterproject.dto.*;
import com.masterproject.entity.*;
import com.masterproject.entity.enums.Priorite;
import com.masterproject.entity.enums.RoleProjet;
import com.masterproject.entity.enums.Statut;
import com.masterproject.entity.enums.TypeTicket;
import com.masterproject.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TicketService : logique metier des tickets.
 *
 * Regles appliquees :
 *  - creer / supprimer un ticket : reserve au RESPONSABLE du projet,
 *  - consulter / modifier / changer le statut / commenter : tout MEMBRE du projet.
 *
 * A chaque modification importante, une ligne est ajoutee dans l'historique
 * (entite History) pour assurer la tracabilite.
 */
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final HistoryRepository historyRepository;
    private final CommentRepository commentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final NotificationService notificationService;

    public TicketService(TicketRepository ticketRepository,
                         HistoryRepository historyRepository,
                         CommentRepository commentRepository,
                         ProjectMemberRepository projectMemberRepository,
                         UserRepository userRepository,
                         ProjectService projectService,
                         UserService userService,
                         NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.historyRepository = historyRepository;
        this.commentRepository = commentRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * Cree un ticket dans un projet (reserve au RESPONSABLE).
     * Le ticket demarre au statut A_FAIRE. Si un email de responsable est
     * fourni, le ticket lui est directement affecte (il doit etre membre).
     */
    @Transactional
    public TicketDTO creerTicket(Long projectId, CreateTicketRequest demande) {
        Project projet = projectService.getProjetOuErreur(projectId);
        projectService.verifierResponsable(projet);

        Ticket ticket = new Ticket();
        ticket.setTitre(demande.titre());
        ticket.setDescription(demande.description());
        ticket.setType(demande.type());
        ticket.setPriorite(demande.priorite());
        ticket.setStatut(Statut.A_FAIRE);          // tout nouveau ticket est "A faire"
        ticket.setDateCreation(LocalDateTime.now());
        ticket.setDateLimite(demande.dateLimite());
        ticket.setProject(projet);

        // Affectation eventuelle a un membre du projet.
        if (demande.responsableEmail() != null && !demande.responsableEmail().isBlank()) {
            User responsable = trouverMembreParEmail(demande.responsableEmail(), projet);
            ticket.setResponsable(responsable);
        }

        ticketRepository.save(ticket);

        // On enregistre la creation dans l'historique.
        enregistrerHistorique(ticket, "creation", null, "Ticket cree");

        // Notification au membre a qui le ticket est attribue.
        if (ticket.getResponsable() != null) {
            notificationService.creerNotification(ticket.getResponsable(),
                    "Le ticket \"" + ticket.getTitre() + "\" vous a ete attribue (projet "
                    + projet.getNom() + ").");
        }

        return versDTO(ticket);
    }

    /**
     * Liste les tickets d'un projet (reserve aux membres).
     * Les trois filtres (statut, priorite, responsable) sont optionnels :
     * s'ils sont nuls, on ne filtre pas dessus.
     */
    public List<TicketDTO> listerTickets(Long projectId, Statut statut,
                                         Priorite priorite, String responsableEmail) {
        Project projet = projectService.getProjetOuErreur(projectId);
        projectService.verifierMembre(projet);

        return ticketRepository.findByProject(projet).stream()
                .filter(t -> statut == null || t.getStatut() == statut)
                .filter(t -> priorite == null || t.getPriorite() == priorite)
                .filter(t -> responsableEmail == null
                        || (t.getResponsable() != null
                            && t.getResponsable().getEmail().equalsIgnoreCase(responsableEmail)))
                .map(this::versDTO)
                .toList();
    }

    /** Recupere le detail d'un ticket (reserve aux membres du projet). */
    public TicketDTO getTicket(Long ticketId) {
        Ticket ticket = getTicketOuErreur(ticketId);
        projectService.verifierMembre(ticket.getProject());
        return versDTO(ticket);
    }

    /**
     * Modifie un ticket (reserve aux membres).
     * Seuls les champs fournis (non nuls) sont mis a jour, et chaque
     * changement reel est enregistre dans l'historique.
     */
    @Transactional
    public TicketDTO modifierTicket(Long ticketId, UpdateTicketRequest demande) {
        Ticket ticket = getTicketOuErreur(ticketId);
        Project projet = ticket.getProject();
        // Verifie que l'utilisateur est membre, ET qu'il a le droit de TOUCHER ce ticket.
        verifierPeutModifierTicket(ticket, projet);

        // Titre
        if (demande.titre() != null && !demande.titre().equals(ticket.getTitre())) {
            enregistrerHistorique(ticket, "titre", ticket.getTitre(), demande.titre());
            ticket.setTitre(demande.titre());
        }
        // Description
        if (demande.description() != null
                && !demande.description().equals(ticket.getDescription())) {
            ticket.setDescription(demande.description());
        }
        // Type
        if (demande.type() != null && demande.type() != ticket.getType()) {
            enregistrerHistorique(ticket, "type",
                    nomOuVide(ticket.getType()), demande.type().name());
            ticket.setType(demande.type());
        }
        // Priorite
        if (demande.priorite() != null && demande.priorite() != ticket.getPriorite()) {
            enregistrerHistorique(ticket, "priorite",
                    nomOuVide(ticket.getPriorite()), demande.priorite().name());
            ticket.setPriorite(demande.priorite());
        }
        // Statut (deplacement dans le Kanban)
        if (demande.statut() != null && demande.statut() != ticket.getStatut()) {
            enregistrerHistorique(ticket, "statut",
                    nomOuVide(ticket.getStatut()), demande.statut().name());
            ticket.setStatut(demande.statut());
        }
        // Date limite
        if (demande.dateLimite() != null
                && !demande.dateLimite().equals(ticket.getDateLimite())) {
            ticket.setDateLimite(demande.dateLimite());
        }
        // Responsable (affectation a un autre membre)
        if (demande.responsableEmail() != null && !demande.responsableEmail().isBlank()) {
            User nouveau = trouverMembreParEmail(demande.responsableEmail(), projet);
            String ancien = (ticket.getResponsable() == null)
                    ? null : ticket.getResponsable().getEmail();
            if (!Objects.equals(ancien, nouveau.getEmail())) {
                enregistrerHistorique(ticket, "responsable", ancien, nouveau.getEmail());
                ticket.setResponsable(nouveau);
                // Notification au nouveau responsable du ticket.
                notificationService.creerNotification(nouveau,
                        "Le ticket \"" + ticket.getTitre() + "\" vous a ete attribue (projet "
                        + projet.getNom() + ").");
            }
        }

        ticketRepository.save(ticket);
        return versDTO(ticket);
    }

    /**
     * Change uniquement le statut d'un ticket (pratique pour le glisser-deposer
     * dans le tableau Kanban). Reserve aux membres.
     */
    @Transactional

    public TicketDTO changerStatut(Long ticketId, Statut nouveauStatut) {
        Ticket ticket = getTicketOuErreur(ticketId);
        // Verifie que l'utilisateur peut deplacer CE ticket dans le Kanban.
        verifierPeutModifierTicket(ticket, ticket.getProject());

        if (nouveauStatut != ticket.getStatut()) {
            enregistrerHistorique(ticket, "statut",
                    ticket.getStatut().name(), nouveauStatut.name());
            ticket.setStatut(nouveauStatut);
            ticketRepository.save(ticket);
        }
        return versDTO(ticket);
    }

    /** Supprime un ticket (reserve au RESPONSABLE du projet). */
    @Transactional
    public void supprimerTicket(Long ticketId) {
        Ticket ticket = getTicketOuErreur(ticketId);
        projectService.verifierResponsable(ticket.getProject());

        // On supprime d'abord les commentaires et l'historique lies au ticket,
        // pour eviter les erreurs de cle etrangere dans la base.
        commentRepository.findByTicketOrderByDateCreationAsc(ticket)
                .forEach(commentRepository::delete);
        historyRepository.findByTicketOrderByDateModificationDesc(ticket)
                .forEach(historyRepository::delete);

        ticketRepository.delete(ticket);
    }

    /** Liste l'historique des modifications d'un ticket (reserve aux membres). */
    public List<HistoryDTO> listerHistorique(Long ticketId) {
        Ticket ticket = getTicketOuErreur(ticketId);
        projectService.verifierMembre(ticket.getProject());

        List<HistoryDTO> resultat = new ArrayList<>();
        for (History h : historyRepository.findByTicketOrderByDateModificationDesc(ticket)) {
            resultat.add(new HistoryDTO(
                    h.getId(),
                    h.getChampModifie(),
                    h.getAncienneValeur(),
                    h.getNouvelleValeur(),
                    h.getDateModification(),
                    h.getUser().getNom()
            ));
        }
        return resultat;
    }

    // ============================================================
    //   METHODES UTILITAIRES
    // ============================================================

    /** Recupere un ticket par son id, ou leve une erreur 404. */
    private Ticket getTicketOuErreur(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket introuvable"));
    }

    /**
     * Verifie que l'utilisateur connecte a le droit de MODIFIER / DEPLACER ce ticket.
     *
     * Regle :
     *  - le RESPONSABLE du projet peut toucher TOUS les tickets,
     *  - un simple MEMBRE ne peut toucher QUE les tickets qui lui sont attribues.
     *
     * La simple consultation (lecture) reste autorisee a tous les membres :
     * cette verification n'est appelee que pour les actions d'ecriture.
     */
    private void verifierPeutModifierTicket(Ticket ticket, Project projet) {
        // Doit d'abord etre membre du projet (sinon erreur 403).
        ProjectMember appartenance = projectService.verifierMembre(projet);

        // Le responsable du projet a tous les droits : on s'arrete la.
        if (appartenance.getRoleProjet() == RoleProjet.RESPONSABLE) {
            return;
        }

        // Sinon (simple membre) : le ticket doit lui etre attribue.
        User connecte = userService.getUtilisateurConnecte();
        boolean estSonTicket = ticket.getResponsable() != null
                && Objects.equals(ticket.getResponsable().getId(), connecte.getId());

        if (!estSonTicket) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ce ticket ne vous est pas attribue : vous pouvez le consulter, ");
        }
    }

    /**
     * Retrouve un utilisateur par email et verifie qu'il est bien membre
     * du projet (on ne peut pas affecter un ticket a quelqu'un d'exterieur).
     */
    private User trouverMembreParEmail(String email, Project projet) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aucun utilisateur avec cet email"));
        projectMemberRepository.findByUserAndProject(user, projet)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cet utilisateur n'est pas membre du projet"));
        return user;
    }

    /** Cree et enregistre une ligne d'historique pour le ticket. */
    private void enregistrerHistorique(Ticket ticket, String champ,
                                       String ancienne, String nouvelle) {
        User auteur = userService.getUtilisateurConnecte();
        History h = new History(champ, ancienne, nouvelle, ticket, auteur);
        historyRepository.save(h);
    }

    /** Renvoie le nom d'un enum, ou null s'il est nul (pour l'historique). */
    private String nomOuVide(Enum<?> valeur) {
        return (valeur == null) ? null : valeur.name();
    }

    /** Convertit une entite Ticket en TicketDTO. */
    private TicketDTO versDTO(Ticket ticket) {
        return new TicketDTO(
                ticket.getId(),
                ticket.getTitre(),
                ticket.getDescription(),
                ticket.getType().name(),
                ticket.getPriorite().name(),
                ticket.getStatut().name(),
                ticket.getDateCreation(),
                ticket.getDateLimite(),
                ticket.getProject().getId(),
                ticket.getResponsable() == null ? null : ticket.getResponsable().getNom(),
                ticket.getResponsable() == null ? null : ticket.getResponsable().getEmail()
        );
    }
}
