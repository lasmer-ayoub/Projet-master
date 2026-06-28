package com.masterproject.repository;

import com.masterproject.entity.Comment;
import com.masterproject.entity.Ticket;
import com.masterproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Repository pour l'entite Comment. */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** Tous les commentaires d'un ticket, du plus ancien au plus recent. */
    List<Comment> findByTicketOrderByDateCreationAsc(Ticket ticket);

    /** Tous les commentaires ecrits par un utilisateur donne. */
    List<Comment> findByAuteur(User auteur);
}
