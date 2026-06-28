// Page : liste de mes projets + creation d'un nouveau projet.
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProjectService } from '../../core/services/project.service';
import { Project } from '../../core/models/project.model';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css',
})
export class ProjectListComponent implements OnInit {
  private projetService = inject(ProjectService);
  private router = inject(Router);

  projets: Project[] = [];
  chargement = true;

  // Formulaire de creation
  afficherFormulaire = false;
  nouveauNom = '';
  nouvelleDescription = '';

  ngOnInit(): void {
    this.chargerProjets();
  }

  chargerProjets(): void {
    this.chargement = true;
    this.projetService.listerMesProjets().subscribe({
      next: (data) => { this.projets = data; this.chargement = false; },
      error: () => { this.chargement = false; },
    });
  }

  creerProjet(): void {
    if (!this.nouveauNom.trim()) return;
    this.projetService.creerProjet(this.nouveauNom, this.nouvelleDescription).subscribe({
      next: () => {
        this.nouveauNom = '';
        this.nouvelleDescription = '';
        this.afficherFormulaire = false;
        this.chargerProjets();
      },
    });
  }

  ouvrirProjet(p: Project): void {
    this.router.navigate(['/projets', p.id]);
  }
}
