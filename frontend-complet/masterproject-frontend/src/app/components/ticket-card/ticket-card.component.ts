// Carte d'un ticket, affichee dans une colonne du tableau Kanban.
// Composant "presentation" : il recoit un ticket et emet des evenements.
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ticket } from '../../core/models/ticket.model';
import { LIBELLE_PRIORITE, LIBELLE_TYPE } from '../../core/models/enums';

@Component({
  selector: 'app-ticket-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ticket-card.component.html',
  styleUrl: './ticket-card.component.css',
})
export class TicketCardComponent {
  @Input() ticket!: Ticket;
  @Input() peutModifier = false;       // affiche le bouton modifier si responsable
  @Output() ouvrir = new EventEmitter<Ticket>();
  @Output() modifier = new EventEmitter<Ticket>();

  libellePriorite = LIBELLE_PRIORITE;
  libelleType = LIBELLE_TYPE;

  // Classe CSS de couleur selon la priorite
  classePriorite(): string {
    return 'prio-' + this.ticket.priorite.toLowerCase();
  }
  classeType(): string {
    return 'type-' + this.ticket.type.toLowerCase();
  }

  // Vrai si le ticket a une date limite
  get aDeadline(): boolean {
    return !!this.ticket.dateLimite;
  }

  // Vrai si la date limite est depassee (et le ticket n'est pas termine)
  get deadlineDepassee(): boolean {
    if (!this.ticket.dateLimite) return false;
    if (this.ticket.statut === 'TERMINE') return false;
    const aujourdhui = new Date(new Date().toDateString());
    return new Date(this.ticket.dateLimite) < aujourdhui;
  }
}
