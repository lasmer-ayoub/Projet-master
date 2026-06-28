// ============================================================
// Service de l'espace administrateur.
// Appelle les routes /api/admin/... (reservees a l'admin).
// ============================================================
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { User } from '../models/user.model';
import { AdminProject, AdminStats } from '../models/admin.model';

// Donnees pour creer un utilisateur
export interface CreateUser {
  nom: string;
  email: string;
  motDePasse: string;
  role: string;
}
// Donnees pour modifier un utilisateur (sans mot de passe)
export interface UpdateUser {
  nom: string;
  email: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);
  private api = API_URL;

  // --- Utilisateurs ---
  listerUtilisateurs(): Observable<User[]> {
    return this.http.get<User[]>(`${this.api}/admin/users`);
  }
  creerUtilisateur(donnees: CreateUser): Observable<User> {
    return this.http.post<User>(`${this.api}/admin/users`, donnees);
  }
  modifierUtilisateur(id: number, donnees: UpdateUser): Observable<User> {
    return this.http.put<User>(`${this.api}/admin/users/${id}`, donnees);
  }
  supprimerUtilisateur(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/admin/users/${id}`);
  }

  // Liste les comptes EN ATTENTE de validation
  listerComptesEnAttente(): Observable<User[]> {
    return this.http.get<User[]>(`${this.api}/admin/users/pending`);
  }
  // Valide un compte (l'utilisateur pourra alors se connecter)
  validerUtilisateur(id: number): Observable<User> {
    return this.http.put<User>(`${this.api}/admin/users/${id}/validate`, {});
  }

  // --- Projets ---
  listerProjets(): Observable<AdminProject[]> {
    return this.http.get<AdminProject[]>(`${this.api}/admin/projects`);
  }
  supprimerProjet(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/admin/projects/${id}`);
  }

  // --- Statistiques ---
  getStatistiques(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.api}/admin/stats`);
  }
}
