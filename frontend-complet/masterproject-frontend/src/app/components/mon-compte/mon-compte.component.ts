// Page "Mon compte" : voir et modifier son profil (nom, email),
// et changer son mot de passe.
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-mon-compte',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mon-compte.component.html',
  styleUrl: './mon-compte.component.css',
})
export class MonCompteComponent implements OnInit {
  private auth = inject(AuthService);

  // Profil
  nom = '';
  email = '';
  role = '';
  messageProfil = '';
  erreurProfil = '';

  // Mot de passe
  ancienMotDePasse = '';
  nouveauMotDePasse = '';
  messageMdp = '';
  erreurMdp = '';

  ngOnInit(): void {
    const u = this.auth.utilisateurCourant();
    if (u) {
      this.nom = u.nom;
      this.email = u.email;
      this.role = u.role;
    }
  }

  enregistrerProfil(): void {
    this.messageProfil = '';
    this.erreurProfil = '';
    this.auth.modifierMonProfil(this.nom, this.email).subscribe({
      next: () => this.messageProfil = 'Profil mis à jour avec succès.',
      error: (e) => this.erreurProfil = e?.error?.message || "Impossible de modifier le profil.",
    });
  }

  changerMdp(): void {
    this.messageMdp = '';
    this.erreurMdp = '';
    if (!this.ancienMotDePasse || !this.nouveauMotDePasse) return;
    this.auth.changerMotDePasse(this.ancienMotDePasse, this.nouveauMotDePasse).subscribe({
      next: () => {
        this.messageMdp = 'Mot de passe modifié avec succès.';
        this.ancienMotDePasse = '';
        this.nouveauMotDePasse = '';
      },
      error: (e) => this.erreurMdp = e?.error?.message || "Impossible de changer le mot de passe.",
    });
  }
}
