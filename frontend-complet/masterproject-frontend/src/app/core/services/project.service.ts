// ============================================================
// Service des projets : creation, liste, detail, membres, stats.
// ============================================================
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Project, Member, ProjectStats } from '../models/project.model';

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private http = inject(HttpClient);
  private api = API_URL;

  // Liste des projets de l'utilisateur connecte
  listerMesProjets(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.api}/projects`);
  }

  // Detail d'un projet
  getProjet(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.api}/projects/${id}`);
  }

  // Creer un projet (l'utilisateur en devient le responsable)
  creerProjet(nom: string, description: string): Observable<Project> {
    return this.http.post<Project>(`${this.api}/projects`, { nom, description });
  }

  // Lister les membres d'un projet
  listerMembres(idProjet: number): Observable<Member[]> {
    return this.http.get<Member[]>(`${this.api}/projects/${idProjet}/members`);
  }

  // Ajouter (inviter) un membre par son email
  ajouterMembre(idProjet: number, email: string): Observable<Member> {
    return this.http.post<Member>(`${this.api}/projects/${idProjet}/members`, { email });
  }

  // Statistiques du projet (tableau de bord)
  getStatistiques(idProjet: number): Observable<ProjectStats> {
    return this.http.get<ProjectStats>(`${this.api}/projects/${idProjet}/stats`);
  }
}
