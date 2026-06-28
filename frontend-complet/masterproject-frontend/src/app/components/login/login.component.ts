// Page de connexion.
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  // Champs lies au formulaire
  email = '';
  motDePasse = '';
  erreur = '';
  chargement = false;

  seConnecter(): void {
    this.erreur = '';
    this.chargement = true;
    this.auth.login(this.email, this.motDePasse).subscribe({
      next: () => this.router.navigate(['/projets']),
      error: (e) => {
        // 403 = compte pas encore valide par l'admin ; sinon identifiants incorrects.
        if (e?.status === 403) {
          this.erreur = e?.error?.message
            || "Votre compte n'a pas encore été validé par l'administrateur.";
        } else {
          this.erreur = 'Email ou mot de passe incorrect.';
        }
        this.chargement = false;
      },
    });
  }
}
