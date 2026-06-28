// Onglet "Tableau de bord" de l'espace admin :
// chiffres cles + un camembert (utilisateurs par role)
// + un graphe en barres (tickets par statut).
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../core/services/admin.service';
import { AdminStats } from '../../core/models/admin.model';
import { LIBELLE_STATUT } from '../../core/models/enums';

// Un morceau de camembert, deja calcule pour l'affichage SVG
interface PartCamembert {
  libelle: string;
  valeur: number;
  pourcentage: number;
  couleur: string;
  chemin: string; // attribut "d" du <path> SVG
}

// Une barre du graphe en barres
interface Barre {
  libelle: string;
  valeur: number;
  hauteurPct: number;
  couleur: string;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css',
})
export class AdminDashboardComponent implements OnInit {
  private adminService = inject(AdminService);

  stats?: AdminStats;
  chargement = true;

  partsRole: PartCamembert[] = [];
  barresStatut: Barre[] = [];
  libelleStatut = LIBELLE_STATUT;

  // Couleurs (claires) par role et par statut
  private couleursRole: { [k: string]: string } = { ADMIN: '#6554c0', USER: '#4c9aff' };
  private couleursStatut: { [k: string]: string } = {
    A_FAIRE: '#97a0af', EN_COURS: '#4c9aff', EN_ATTENTE: '#ffab00',
    A_VALIDER: '#6554c0', BLOQUE: '#ff5630', ANNULE: '#b3bac5', TERMINE: '#36b37e',
  };

  ngOnInit(): void {
    this.adminService.getStatistiques().subscribe({
      next: (s) => {
        this.stats = s;
        this.partsRole = this.calculerCamembert(s.utilisateursParRole, this.couleursRole);
        this.barresStatut = this.calculerBarres(s.ticketsParStatut, this.couleursStatut);
        this.chargement = false;
      },
      error: () => { this.chargement = false; },
    });
  }

  // ----- Calcul des morceaux du camembert -----
  private calculerCamembert(data: { [k: string]: number }, couleurs: { [k: string]: string }): PartCamembert[] {
    const entrees = Object.keys(data).map((k) => ({ cle: k, val: data[k] }));
    const total = entrees.reduce((s, e) => s + e.val, 0);
    if (total === 0) return [];

    const cx = 100, cy = 100, r = 90;
    let angleDepart = -Math.PI / 2; // on commence en haut
    const parts: PartCamembert[] = [];

    for (const e of entrees) {
      const portion = e.val / total;
      const angleFin = angleDepart + portion * 2 * Math.PI;
      const x1 = cx + r * Math.cos(angleDepart);
      const y1 = cy + r * Math.sin(angleDepart);
      const x2 = cx + r * Math.cos(angleFin);
      const y2 = cy + r * Math.sin(angleFin);
      const grandArc = portion > 0.5 ? 1 : 0;
      // Si une seule catégorie (100%), on dessine un cercle complet
      const chemin = portion >= 0.999
        ? `M ${cx} ${cy - r} A ${r} ${r} 0 1 1 ${cx - 0.01} ${cy - r} Z`
        : `M ${cx} ${cy} L ${x1} ${y1} A ${r} ${r} 0 ${grandArc} 1 ${x2} ${y2} Z`;

      parts.push({
        libelle: e.cle,
        valeur: e.val,
        pourcentage: Math.round(portion * 100),
        couleur: couleurs[e.cle] || '#4c9aff',
        chemin,
      });
      angleDepart = angleFin;
    }
    return parts;
  }

  // ----- Calcul des barres -----
  private calculerBarres(data: { [k: string]: number }, couleurs: { [k: string]: string }): Barre[] {
    const entrees = Object.keys(data).map((k) => ({ cle: k, val: data[k] }));
    const max = Math.max(1, ...entrees.map((e) => e.val));
    return entrees.map((e) => ({
      libelle: this.libelleStatut[e.cle] || e.cle,
      valeur: e.val,
      hauteurPct: Math.round((e.val / max) * 100),
      couleur: couleurs[e.cle] || '#4c9aff',
    }));
  }
}
