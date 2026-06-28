// Barre laterale (sidebar) affichee a gauche, adaptee au role de l'utilisateur.
// Elle remplace l'ancienne barre de navigation du haut.
//  - Admin       : Administration, Mes projets, Mon compte, Deconnexion
//  - Responsable
//    / Membre    : Mes projets, Notifications (avec compteur), Mon compte, Deconnexion
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent implements OnInit {
  private auth = inject(AuthService);
  private notifService = inject(NotificationService);
  private router = inject(Router);

  nonLues = 0;

  ngOnInit(): void {
    // On charge le compteur de notifications non lues (si connecte et non admin).
    if (this.connecte && !this.estAdmin) {
      this.rafraichirCompteur();
    }
  }

  get connecte(): boolean { return this.auth.estConnecte(); }
  get estAdmin(): boolean { return this.auth.estAdmin(); }
  get nomUtilisateur(): string { return this.auth.utilisateurCourant()?.nom ?? ''; }
  get roleLisible(): string { return this.estAdmin ? 'Administrateur' : 'Utilisateur'; }

  rafraichirCompteur(): void {
    this.notifService.compterNonLues().subscribe({
      next: (r) => this.nonLues = r.nonLues,
      error: () => {},
    });
  }

  deconnexion(): void {
    this.auth.deconnexion();
    this.router.navigate(['/login']);
  }
}
