// Composant : gestion des membres d'un projet (lister + inviter).
// Il recoit l'id du projet et si l'utilisateur est responsable (via @Input).
import { Component, inject, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../core/services/project.service';
import { Member } from '../../core/models/project.model';

@Component({
  selector: 'app-members',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './members.component.html',
  styleUrl: './members.component.css',
})
export class MembersComponent implements OnInit {
  @Input() idProjet!: number;        // id du projet (donne par le parent)
  @Input() estResponsable = false;   // l'utilisateur peut-il inviter ?

  private projetService = inject(ProjectService);

  membres: Member[] = [];
  emailInvite = '';
  erreur = '';

  ngOnInit(): void {
    this.chargerMembres();
  }

  chargerMembres(): void {
    this.projetService.listerMembres(this.idProjet).subscribe({
      next: (data) => this.membres = data,
    });
  }

  inviter(): void {
    this.erreur = '';
    if (!this.emailInvite.trim()) return;
    this.projetService.ajouterMembre(this.idProjet, this.emailInvite).subscribe({
      next: () => { this.emailInvite = ''; this.chargerMembres(); },
      error: (e) => this.erreur = e?.error?.message || "Impossible d'ajouter ce membre (email introuvable ?).",
    });
  }
}
