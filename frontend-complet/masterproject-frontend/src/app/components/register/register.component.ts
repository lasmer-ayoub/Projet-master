// Page d'inscription (creation d'un compte utilisateur).
// Le compte est cree EN ATTENTE : l'utilisateur doit etre valide par
// l'administrateur avant de pouvoir se connecter.
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  private auth = inject(AuthService);

  nom = '';
  email = '';
  motDePasse = '';
  erreur = '';
  chargement = false;
  succes = false; // passe a true quand le compte est cree (message d'attente)

  sInscrire(): void {
    this.erreur = '';
    this.chargement = true;
    this.auth.register(this.nom, this.email, this.motDePasse).subscribe({
      next: () => {
        // Compte cree : on affiche un message d'attente (pas de connexion auto).
        this.succes = true;
        this.chargement = false;
      },
      error: (e) => {
        this.erreur = e?.error?.message || "Impossible de créer le compte (email déjà utilisé ?).";
        this.chargement = false;
      },
    });
  }
}
