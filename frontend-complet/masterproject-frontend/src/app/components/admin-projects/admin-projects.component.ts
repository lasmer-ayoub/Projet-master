// Onglet "Projets" de l'espace admin :
// liste de TOUS les projets (responsable + membres) et suppression.
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../core/services/admin.service';
import { AdminProject } from '../../core/models/admin.model';

@Component({
  selector: 'app-admin-projects',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-projects.component.html',
  styleUrl: './admin-projects.component.css',
})
export class AdminProjectsComponent implements OnInit {
  private adminService = inject(AdminService);

  projets: AdminProject[] = [];
  chargement = true;

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement = true;
    this.adminService.listerProjets().subscribe({
      next: (data) => { this.projets = data; this.chargement = false; },
      error: () => { this.chargement = false; },
    });
  }

  supprimer(p: AdminProject): void {
    if (!confirm(`Supprimer le projet "${p.nom}" et tout son contenu (tickets, commentaires) ?`)) return;
    this.adminService.supprimerProjet(p.id).subscribe({
      next: () => this.charger(),
      error: (e) => alert(e?.error?.message || "Impossible de supprimer ce projet."),
    });
  }
}
