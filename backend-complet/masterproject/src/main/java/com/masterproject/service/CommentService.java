package com.masterproject.service;

import com.masterproject.dto.CommentDTO;
import com.masterproject.dto.CreateCommentRequest;
import com.masterproject.entity.Comment;
import com.masterproject.entity.Ticket;
import com.masterproject.entity.User;
import com.masterproject.repository.CommentRepository;
import com.masterproject.repository.TicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * CommentService : logique metier des commentaires.
 * Tout membre d'un projet peut ajouter et consulter les commentaires
 * des tickets de ce projet.
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository,
                          TicketRepository ticketRepository,
                          ProjectService projectService,
                          UserService userService) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.projectService = projectService;
        this.userService = userService;
    }

    /** Ajoute un commentaire a un ticket (reserve aux membres du projet). */
    public CommentDTO ajouterCommentaire(Long ticketId, CreateCommentRequest demande) {
        Ticket ticket = getTicketOuErreur(ticketId);
        // L'auteur doit etre membre du projet du ticket.
        projectService.verifierMembre(ticket.getProject());

        User auteur = userService.getUtilisateurConnecte();
        Comment commentaire = new Comment(demande.contenu(), ticket, auteur);
        commentRepository.save(commentaire);

        return versDTO(commentaire);
    }

    /** Liste les commentaires d'un ticket (reserve aux membres du projet). */
    public List<CommentDTO> listerCommentaires(Long ticketId) {
        Ticket ticket = getTicketOuErreur(ticketId);
        projectService.verifierMembre(ticket.getProject());

        return commentRepository.findByTicketOrderByDateCreationAsc(ticket).stream()
                .map(this::versDTO)
                .toList();
    }

    // --- Utilitaires ---

    private Ticket getTicketOuErreur(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket introuvable"));
    }

    private CommentDTO versDTO(Comment commentaire) {
        return new CommentDTO(
                commentaire.getId(),
                commentaire.getContenu(),
                commentaire.getDateCreation(),
                commentaire.getAuteur().getNom()
        );
    }
}
