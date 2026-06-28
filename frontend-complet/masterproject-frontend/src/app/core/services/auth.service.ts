// ============================================================
// Service d'authentification.
// Gere la connexion, l'inscription, le stockage du token JWT
// et de l'utilisateur connecte (dans le localStorage du navigateur).
// ============================================================
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../api.config';
import { AuthResponse, User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private api = API_URL;

  // Cles utilisees pour stocker les infos dans le navigateur
  private CLE_TOKEN = 'mp_token';
  private CLE_USER = 'mp_user';

  // --- Connexion ---
  login(email: string, motDePasse: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/auth/login`, { email, motDePasse })
      .pipe(tap((reponse) => this.enregistrerSession(reponse)));
  }

  // --- Inscription ---
  // Inscription : le compte est cree EN ATTENTE de validation par l'admin.
  // On ne connecte donc PAS automatiquement l'utilisateur (pas de token).
  register(nom: string, email: string, motDePasse: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/auth/register`, { nom, email, motDePasse });
  }

  // Enregistre le token et l'utilisateur apres une connexion reussie
  private enregistrerSession(reponse: AuthResponse): void {
    // A la connexion, un token est toujours present. Par securite, on ne
    // l'enregistre que s'il existe (a l'inscription il vaut null).
    if (reponse.token) {
      localStorage.setItem(this.CLE_TOKEN, reponse.token);
      localStorage.setItem(this.CLE_USER, JSON.stringify(reponse.utilisateur));
    }
  }

  // --- Deconnexion : on efface tout ---
  deconnexion(): void {
    localStorage.removeItem(this.CLE_TOKEN);
    localStorage.removeItem(this.CLE_USER);
  }

  // Recupere le token (utilise par l'intercepteur)
  getToken(): string | null {
    return localStorage.getItem(this.CLE_TOKEN);
  }

  // Vrai si un token est present
  estConnecte(): boolean {
    return !!this.getToken();
  }

  // Recupere l'utilisateur connecte (ou null)
  utilisateurCourant(): User | null {
    const data = localStorage.getItem(this.CLE_USER);
    return data ? JSON.parse(data) as User : null;
  }

  // Vrai si l'utilisateur connecte est administrateur
  estAdmin(): boolean {
    return this.utilisateurCourant()?.role === 'ADMIN';
  }

  // --- Profil de l'utilisateur connecte (depuis le serveur) ---
  getMonProfil(): Observable<User> {
    return this.http.get<User>(`${this.api}/users/me`);
  }

  // --- Liste de tous les utilisateurs (reserve a l'admin) ---
  listerUtilisateurs(): Observable<User[]> {
    return this.http.get<User[]>(`${this.api}/users`);
  }

  // --- Mon compte : modifier mon profil (nom + email) ---
  modifierMonProfil(nom: string, email: string): Observable<User> {
    return this.http.put<User>(`${this.api}/users/me`, { nom, email }).pipe(
      tap((user) => {
        // On met a jour l'utilisateur stocke localement (pour la sidebar, etc.)
        localStorage.setItem(this.CLE_USER, JSON.stringify(user));
      })
    );
  }

  // --- Mon compte : changer mon mot de passe ---
  changerMotDePasse(ancienMotDePasse: string, nouveauMotDePasse: string): Observable<void> {
    return this.http.put<void>(`${this.api}/users/me/password`,
      { ancienMotDePasse, nouveauMotDePasse });
  }
}
