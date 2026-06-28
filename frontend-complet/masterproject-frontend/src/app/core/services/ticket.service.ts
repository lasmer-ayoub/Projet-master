// ============================================================
// Service des tickets : CRUD, changement de statut, historique.
// ============================================================
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Ticket, History } from '../models/ticket.model';

// Donnees envoyees pour creer/modifier un ticket
export interface DonneesTicket {
  titre: string;
  description: string;
  type: string;
  priorite: string;
  statut?: string;            // utilise seulement a la modification
  dateLimite?: string | null; // date limite (format AAAA-MM-JJ), optionnelle
  responsableEmail?: string;  // email du membre a qui on affecte le ticket
}

@Injectable({ providedIn: 'root' })
export class TicketService {
  private http = inject(HttpClient);
  private api = API_URL;

  // Lister les tickets d'un projet, avec filtres optionnels
  listerTickets(idProjet: number, filtres?: { statut?: string; priorite?: string; responsable?: string }): Observable<Ticket[]> {
    let params = new HttpParams();
    if (filtres?.statut) params = params.set('statut', filtres.statut);
    if (filtres?.priorite) params = params.set('priorite', filtres.priorite);
    if (filtres?.responsable) params = params.set('responsable', filtres.responsable);
    return this.http.get<Ticket[]>(`${this.api}/projects/${idProjet}/tickets`, { params });
  }

  // Detail d'un ticket
  getTicket(id: number): Observable<Ticket> {
    return this.http.get<Ticket>(`${this.api}/tickets/${id}`);
  }

  // Creer un ticket dans un projet
  creerTicket(idProjet: number, donnees: DonneesTicket): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.api}/projects/${idProjet}/tickets`, donnees);
  }

  // Modifier un ticket
  modifierTicket(id: number, donnees: DonneesTicket): Observable<Ticket> {
    return this.http.put<Ticket>(`${this.api}/tickets/${id}`, donnees);
  }

  // Changer uniquement le statut (deplacement dans le Kanban)
  changerStatut(id: number, statut: string): Observable<Ticket> {
    return this.http.patch<Ticket>(`${this.api}/tickets/${id}/statut?valeur=${statut}`, {});
  }

  // Supprimer un ticket
  supprimerTicket(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/tickets/${id}`);
  }

  // Historique des modifications d'un ticket
  getHistorique(id: number): Observable<History[]> {
    return this.http.get<History[]>(`${this.api}/tickets/${id}/historique`);
  }
}
