package com.masterproject.repository;

import com.masterproject.entity.History;
import com.masterproject.entity.Ticket;
import com.masterproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Repository pour l'entite History (historique des tickets). */
public interface HistoryRepository extends JpaRepository<History, Long> {

    /** Tout l'historique d'un ticket, du plus recent au plus ancien. */
    List<History> findByTicketOrderByDateModificationDesc(Ticket ticket);

    /** Tout l'historique des actions effectuees par un utilisateur donne. */
    List<History> findByUser(User user);
}
