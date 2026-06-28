// Fenetre (modale) pour creer ou modifier un ticket.
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Ticket } from '../../core/models/ticket.model';
import { Member } from '../../core/models/project.model';
import { DonneesTicket } from '../../core/services/ticket.service';
import { TYPES_TICKET, PRIORITES, STATUTS, LIBELLE_STATUT, LIBELLE_PRIORITE, LIBELLE_TYPE } from '../../core/models/enums';

@Component({
  selector: 'app-ticket-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ticket-dialog.component.html',
  styleUrl: './ticket-dialog.component.css',
})
export class TicketDialogComponent implements OnInit {
  @Input() ticket: Ticket | null = null; // null = creation, sinon = modification
  @Input() membres: Member[] = [];        // pour choisir le responsable
  @Output() valider = new EventEmitter<DonneesTicket>();
  @Output() fermer = new EventEmitter<void>();

  types = TYPES_TICKET;
  priorites = PRIORITES;
  statuts = STATUTS;
  libelleStatut = LIBELLE_STATUT;
  libellePriorite = LIBELLE_PRIORITE;
  libelleType = LIBELLE_TYPE;

  // Champs du formulaire
  donnees: DonneesTicket = {
    titre: '', description: '', type: 'TACHE', priorite: 'MOYENNE',
    statut: 'A_FAIRE', dateLimite: null, responsableEmail: '',
  };

  get modeModification(): boolean { return this.ticket !== null; }

  ngOnInit(): void {
    // En modification, on pre-remplit le formulaire avec le ticket existant
    if (this.ticket) {
      this.donnees = {
        titre: this.ticket.titre,
        description: this.ticket.description,
        type: this.ticket.type,
        priorite: this.ticket.priorite,
        statut: this.ticket.statut,
        dateLimite: this.ticket.dateLimite ?? null,
        responsableEmail: this.ticket.responsableEmail ?? '',
      };
    }
  }

  soumettre(): void {
    if (!this.donnees.titre.trim()) return;
    this.valider.emit(this.donnees);
  }
}
