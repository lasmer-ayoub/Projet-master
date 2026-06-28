// Barre de navigation affichee en haut de l'application.
// Montre le nom de l'utilisateur connecte, un lien Administration
// (uniquement pour l'admin) et un bouton de deconnexion.
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  get connecte(): boolean {
    return this.auth.estConnecte();
  }

  // Vrai si l'utilisateur connecte est administrateur (pour afficher le lien Admin)
  get estAdmin(): boolean {
    return this.auth.estAdmin();
  }

  get nomUtilisateur(): string {
    return this.auth.utilisateurCourant()?.nom ?? '';
  }

  deconnexion(): void {
    this.auth.deconnexion();
    this.router.navigate(['/login']);
  }
}
