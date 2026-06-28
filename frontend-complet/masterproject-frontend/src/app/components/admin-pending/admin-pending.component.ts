// Onglet "Comptes en attente" de l'espace admin :
// affiche les inscriptions a valider, et permet de les valider.
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../core/services/admin.service';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-admin-pending',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-pending.component.html',
  styleUrl: './admin-pending.component.css',
})
export class AdminPendingComponent implements OnInit {
  private adminService = inject(AdminService);

  enAttente: User[] = [];
  chargement = true;

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement = true;
    this.adminService.listerComptesEnAttente().subscribe({
      next: (data) => { this.enAttente = data; this.chargement = false; },
      error: () => { this.chargement = false; },
    });
  }

  valider(u: User): void {
    this.adminService.validerUtilisateur(u.id).subscribe({
      next: () => this.charger(), // on recharge la liste (le compte disparait)
      error: (e) => alert(e?.error?.message || "Impossible de valider ce compte."),
    });
  }
}
