package com.masterproject.controller;

import com.masterproject.dto.CreateTicketRequest;
import com.masterproject.dto.HistoryDTO;
import com.masterproject.dto.TicketDTO;
import com.masterproject.dto.UpdateTicketRequest;
import com.masterproject.entity.enums.Priorite;
import com.masterproject.entity.enums.Statut;
import com.masterproject.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TicketController : endpoints lies aux tickets.
 *
 * Remarque sur les URLs :
 *  - la creation et la liste des tickets passent par le projet
 *    (/api/projects/{projectId}/tickets), car un ticket appartient a un projet ;
 *  - les actions sur un ticket precis passent par /api/tickets/{id}.
 */
@RestController
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /** Cree un ticket dans un projet (reserve au responsable du projet). */
    @PostMapping("/projects/{projectId}/tickets")
    public TicketDTO creer(@PathVariable Long projectId,
                           @Valid @RequestBody CreateTicketRequest demande) {
        return ticketService.creerTicket(projectId, demande);
    }

    /**
     * Liste les tickets d'un projet, avec des filtres OPTIONNELS.
     * @RequestParam(required = false) : les filtres ne sont pas obligatoires.
     * Exemple d'appel : /api/projects/1/tickets?statut=EN_COURS&priorite=HAUTE
     */
    @GetMapping("/projects/{projectId}/tickets")
    public List<TicketDTO> lister(
            @PathVariable Long projectId,
            @RequestParam(required = false) Statut statut,
            @RequestParam(required = false) Priorite priorite,
            @RequestParam(required = false) String responsable) {
        return ticketService.listerTickets(projectId, statut, priorite, responsable);
    }

    /** Detail d'un ticket. */
    @GetMapping("/tickets/{id}")
    public TicketDTO detail(@PathVariable Long id) {
        return ticketService.getTicket(id);
    }

    /** Modifie un ticket (champs optionnels ; chaque changement est historise). */
    @PutMapping("/tickets/{id}")
    public TicketDTO modifier(@PathVariable Long id,
                              @RequestBody UpdateTicketRequest demande) {
        return ticketService.modifierTicket(id, demande);
    }

    /**
     * Change uniquement le statut d'un ticket (deplacement Kanban).
     * Exemple : PATCH /api/tickets/1/statut?valeur=EN_COURS
     */
    @PatchMapping("/tickets/{id}/statut")
    public TicketDTO changerStatut(@PathVariable Long id,
                                   @RequestParam Statut valeur) {
        return ticketService.changerStatut(id, valeur);
    }

    /** Supprime un ticket (reserve au responsable du projet). */
    @DeleteMapping("/tickets/{id}")
    public void supprimer(@PathVariable Long id) {
        ticketService.supprimerTicket(id);
    }

    /** Historique des modifications d'un ticket. */
    @GetMapping("/tickets/{id}/historique")
    public List<HistoryDTO> historique(@PathVariable Long id) {
        return ticketService.listerHistorique(id);
    }
}
