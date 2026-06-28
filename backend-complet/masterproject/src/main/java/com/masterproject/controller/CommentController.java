package com.masterproject.controller;

import com.masterproject.dto.CommentDTO;
import com.masterproject.dto.CreateCommentRequest;
import com.masterproject.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CommentController : endpoints lies aux commentaires des tickets.
 */
@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** Ajoute un commentaire au ticket. */
    @PostMapping
    public CommentDTO ajouter(@PathVariable Long ticketId,
                              @Valid @RequestBody CreateCommentRequest demande) {
        return commentService.ajouterCommentaire(ticketId, demande);
    }

    /** Liste les commentaires du ticket (du plus ancien au plus recent). */
    @GetMapping
    public List<CommentDTO> lister(@PathVariable Long ticketId) {
        return commentService.listerCommentaires(ticketId);
    }
}
