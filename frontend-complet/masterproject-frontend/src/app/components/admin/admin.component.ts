// Page principale de l'espace administrateur.
// Onglets : Tableau de bord, Comptes en attente, Utilisateurs, Projets.
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminDashboardComponent } from '../admin-dashboard/admin-dashboard.component';
import { AdminPendingComponent } from '../admin-pending/admin-pending.component';
import { AdminUsersComponent } from '../admin-users/admin-users.component';
import { AdminProjectsComponent } from '../admin-projects/admin-projects.component';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, AdminDashboardComponent, AdminPendingComponent, AdminUsersComponent, AdminProjectsComponent],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent {
  // Onglet actif
  ongletActif: 'dashboard' | 'pending' | 'users' | 'projects' = 'dashboard';
}
