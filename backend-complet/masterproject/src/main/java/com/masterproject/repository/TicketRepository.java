package com.masterproject.repository;

import com.masterproject.entity.Project;
import com.masterproject.entity.Ticket;
import com.masterproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Repository pour l'entite Ticket. */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /** Tous les tickets d'un projet donne. */
    List<Ticket> findByProject(Project project);

    /** Tous les tickets d'un projet, identifie par son id. */
    List<Ticket> findByProjectId(Long projectId);

    /** Tous les tickets dont un utilisateur est le responsable. */
    List<Ticket> findByResponsable(User responsable);
}
