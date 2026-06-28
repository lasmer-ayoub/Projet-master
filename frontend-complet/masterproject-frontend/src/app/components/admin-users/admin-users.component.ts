// Onglet "Utilisateurs" de l'espace admin :
// liste, creation, modification et suppression des utilisateurs.
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, CreateUser, UpdateUser } from '../../core/services/admin.service';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.css',
})
export class AdminUsersComponent implements OnInit {
  private adminService = inject(AdminService);

  utilisateurs: User[] = [];
  chargement = true;
  erreur = '';

  // Fenetre de creation / modification
  dialogueOuvert = false;
  modeModification = false;
  idEnCours: number | null = null;

  // Champs du formulaire
  nom = '';
  email = '';
  motDePasse = '';
  role = 'USER';

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement = true;
    this.adminService.listerUtilisateurs().subscribe({
      next: (data) => { this.utilisateurs = data; this.chargement = false; },
      error: () => { this.chargement = false; },
    });
  }

  // --- Ouverture de la fenetre ---
  ouvrirCreation(): void {
    this.modeModification = false;
    this.idEnCours = null;
    this.nom = ''; this.email = ''; this.motDePasse = ''; this.role = 'USER';
    this.erreur = '';
    this.dialogueOuvert = true;
  }

  ouvrirModification(u: User): void {
    this.modeModification = true;
    this.idEnCours = u.id;
    this.nom = u.nom; this.email = u.email; this.role = u.role;
    this.motDePasse = '';
    this.erreur = '';
    this.dialogueOuvert = true;
  }

  fermer(): void { this.dialogueOuvert = false; }

  // --- Enregistrement (creation ou modification) ---
  enregistrer(): void {
    this.erreur = '';
    if (this.modeModification && this.idEnCours !== null) {
      const donnees: UpdateUser = { nom: this.nom, email: this.email, role: this.role };
      this.adminService.modifierUtilisateur(this.idEnCours, donnees).subscribe({
        next: () => { this.fermer(); this.charger(); },
        error: (e) => this.erreur = e?.error?.message || "Erreur lors de la modification.",
      });
    } else {
      const donnees: CreateUser = { nom: this.nom, email: this.email, motDePasse: this.motDePasse, role: this.role };
      this.adminService.creerUtilisateur(donnees).subscribe({
        next: () => { this.fermer(); this.charger(); },
        error: (e) => this.erreur = e?.error?.message || "Erreur lors de la création (email déjà utilisé ?).",
      });
    }
  }

  // --- Suppression ---
  supprimer(u: User): void {
    if (!confirm(`Supprimer l'utilisateur "${u.nom}" ?`)) return;
    this.adminService.supprimerUtilisateur(u.id).subscribe({
      next: () => this.charger(),
      error: (e) => alert(e?.error?.message || "Impossible de supprimer cet utilisateur."),
    });
  }
}
