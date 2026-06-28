// Page : detail d'un projet (infos, statistiques, membres, acces au tableau).
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../../core/services/project.service';
import { Project, ProjectStats } from '../../core/models/project.model';
import { LIBELLE_STATUT } from '../../core/models/enums';
import { MembersComponent } from '../members/members.component';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [CommonModule, MembersComponent],
  templateUrl: './project-detail.component.html',
  styleUrl: './project-detail.component.css',
})
export class ProjectDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private projetService = inject(ProjectService);

  idProjet!: number;
  projet?: Project;
  stats?: ProjectStats;
  libelleStatut = LIBELLE_STATUT;

  ngOnInit(): void {
    this.idProjet = Number(this.route.snapshot.paramMap.get('id'));
    this.projetService.getProjet(this.idProjet).subscribe({ next: (p) => this.projet = p });
    this.projetService.getStatistiques(this.idProjet).subscribe({ next: (s) => this.stats = s });
  }

  get estResponsable(): boolean {
    return this.projet?.monRoleProjet === 'RESPONSABLE';
  }

  // Transforme la map {statut: nombre} en liste pour l'affichage
  get statutsListe(): { statut: string; nombre: number }[] {
    if (!this.stats) return [];
    return Object.keys(this.stats.ticketsParStatut)
      .map((s) => ({ statut: s, nombre: this.stats!.ticketsParStatut[s] }));
  }

  ouvrirTableau(): void {
    this.router.navigate(['/projets', this.idProjet, 'tableau']);
  }
}
