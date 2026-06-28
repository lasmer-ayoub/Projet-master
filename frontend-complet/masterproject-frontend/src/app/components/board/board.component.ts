// Page : le tableau Kanban d'un projet.
// Affiche une colonne par statut, avec les tickets correspondants.
// On peut deplacer un ticket d'une colonne a l'autre (glisser-deposer),
// et le responsable peut creer / modifier des tickets.
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../../core/services/project.service';
import { TicketService, DonneesTicket } from '../../core/services/ticket.service';
import { AuthService } from '../../core/services/auth.service';
import { Ticket } from '../../core/models/ticket.model';
import { Project, Member } from '../../core/models/project.model';
import { STATUTS, LIBELLE_STATUT, PRIORITES, LIBELLE_PRIORITE } from '../../core/models/enums';
import { TicketCardComponent } from '../ticket-card/ticket-card.component';
import { TicketDialogComponent } from '../ticket-dialog/ticket-dialog.component';

@Component({
  selector: 'app-board',
  standalone: true,
  imports: [CommonModule, FormsModule, TicketCardComponent, TicketDialogComponent],
  templateUrl: './board.component.html',
  styleUrl: './board.component.css',
})
export class BoardComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private projetService = inject(ProjectService);
  private ticketService = inject(TicketService);
  private auth = inject(AuthService);

  idProjet!: number;
  projet?: Project;
  membres: Member[] = [];
  tickets: Ticket[] = [];

  statuts = STATUTS;
  libelleStatut = LIBELLE_STATUT;
  priorites = PRIORITES;
  libellePriorite = LIBELLE_PRIORITE;

  // Filtres
  filtrePriorite = '';

  // Gestion de la fenetre de ticket
  dialogueOuvert = false;
  ticketEnEdition: Ticket | null = null;

  // Ticket en cours de glisser-deposer
  private ticketGlisse: Ticket | null = null;

  ngOnInit(): void {
    this.idProjet = Number(this.route.snapshot.paramMap.get('id'));
    this.projetService.getProjet(this.idProjet).subscribe({ next: (p) => this.projet = p });
    this.projetService.listerMembres(this.idProjet).subscribe({ next: (m) => this.membres = m });
    this.chargerTickets();
  }

  get estResponsable(): boolean {
    return this.projet?.monRoleProjet === 'RESPONSABLE';
  }

  // Email de l'utilisateur connecte (pour savoir quels tickets sont les siens)
  private get monEmail(): string {
    return this.auth.utilisateurCourant()?.email ?? '';
  }

  /**
   * Determine si l'utilisateur peut MODIFIER / DEPLACER un ticket donne.
   *  - le responsable du projet peut tout toucher,
   *  - un membre ne peut toucher que les tickets qui lui sont attribues.
   * (La lecture reste possible pour tous : on peut toujours ouvrir le ticket.)
   */
  peutModifierTicket(ticket: Ticket): boolean {
    if (this.estResponsable) return true;
    return ticket.responsableEmail === this.monEmail;
  }

  chargerTickets(): void {
    const filtres = this.filtrePriorite ? { priorite: this.filtrePriorite } : undefined;
    this.ticketService.listerTickets(this.idProjet, filtres).subscribe({
      next: (t) => this.tickets = t,
    });
  }

  // Renvoie les tickets d'une colonne (d'un statut donne)
  ticketsParStatut(statut: string): Ticket[] {
    return this.tickets.filter((t) => t.statut === statut);
  }

  // --- Glisser-deposer ---
  debutGlisser(ticket: Ticket): void {
    // On n'autorise le glisser que si l'utilisateur a le droit de modifier ce ticket.
    if (!this.peutModifierTicket(ticket)) {
      this.ticketGlisse = null;
      return;
    }
    this.ticketGlisse = ticket;
  }
  autoriserDepot(evenement: DragEvent): void {
    evenement.preventDefault(); // necessaire pour autoriser le depot
  }
  deposer(statutCible: string): void {
    if (this.ticketGlisse && this.ticketGlisse.statut !== statutCible) {
      const ticket = this.ticketGlisse;
      this.ticketService.changerStatut(ticket.id, statutCible).subscribe({
        next: (maj) => {
          ticket.statut = maj.statut; // mise a jour locale immediate
        },
      });
    }
    this.ticketGlisse = null;
  }

  // --- Fenetre de creation / modification ---
  ouvrirCreation(): void {
    this.ticketEnEdition = null;
    this.dialogueOuvert = true;
  }
  ouvrirModification(ticket: Ticket): void {
    this.ticketEnEdition = ticket;
    this.dialogueOuvert = true;
  }
  fermerDialogue(): void {
    this.dialogueOuvert = false;
    this.ticketEnEdition = null;
  }

  enregistrerTicket(donnees: DonneesTicket): void {
    if (this.ticketEnEdition) {
      // Modification
      this.ticketService.modifierTicket(this.ticketEnEdition.id, donnees).subscribe({
        next: () => { this.fermerDialogue(); this.chargerTickets(); },
      });
    } else {
      // Creation
      this.ticketService.creerTicket(this.idProjet, donnees).subscribe({
        next: () => { this.fermerDialogue(); this.chargerTickets(); },
      });
    }
  }

  // Ouvrir le detail d'un ticket (commentaires + historique)
  ouvrirDetail(ticket: Ticket): void {
    this.router.navigate(['/tickets', ticket.id]);
  }

  retour(): void {
    this.router.navigate(['/projets', this.idProjet]);
  }
}
