// Page : detail d'un ticket avec ses commentaires et son historique.
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketService } from '../../core/services/ticket.service';
import { CommentService } from '../../core/services/comment.service';
import { Ticket, History } from '../../core/models/ticket.model';
import { Comment } from '../../core/models/comment.model';
import { LIBELLE_STATUT, LIBELLE_PRIORITE, LIBELLE_TYPE } from '../../core/models/enums';

@Component({
  selector: 'app-ticket-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ticket-detail.component.html',
  styleUrl: './ticket-detail.component.css',
})
export class TicketDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ticketService = inject(TicketService);
  private commentService = inject(CommentService);

  idTicket!: number;
  ticket?: Ticket;
  commentaires: Comment[] = [];
  historique: History[] = [];
  nouveauCommentaire = '';

  libelleStatut = LIBELLE_STATUT;
  libellePriorite = LIBELLE_PRIORITE;
  libelleType = LIBELLE_TYPE;

  ngOnInit(): void {
    this.idTicket = Number(this.route.snapshot.paramMap.get('id'));
    this.ticketService.getTicket(this.idTicket).subscribe({ next: (t) => this.ticket = t });
    this.chargerCommentaires();
    this.ticketService.getHistorique(this.idTicket).subscribe({ next: (h) => this.historique = h });
  }

  chargerCommentaires(): void {
    this.commentService.listerCommentaires(this.idTicket).subscribe({ next: (c) => this.commentaires = c });
  }

  ajouterCommentaire(): void {
    if (!this.nouveauCommentaire.trim()) return;
    this.commentService.ajouterCommentaire(this.idTicket, this.nouveauCommentaire).subscribe({
      next: () => { this.nouveauCommentaire = ''; this.chargerCommentaires(); },
    });
  }

  // Traduit le nom d'un champ technique en libelle lisible pour l'historique
  libelleChamp(champ: string): string {
    const m: Record<string, string> = {
      titre: 'Titre', description: 'Description', type: 'Type',
      priorite: 'Priorité', statut: 'Statut', responsable: 'Responsable',
    };
    return m[champ] || champ;
  }

  retour(): void {
    if (this.ticket) this.router.navigate(['/projets', this.ticket.projectId, 'tableau']);
    else this.router.navigate(['/projets']);
  }
}
