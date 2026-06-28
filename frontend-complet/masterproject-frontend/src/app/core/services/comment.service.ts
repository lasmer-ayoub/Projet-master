// ============================================================
// Service des commentaires d'un ticket.
// ============================================================
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Comment } from '../models/comment.model';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private http = inject(HttpClient);
  private api = API_URL;

  // Lister les commentaires d'un ticket
  listerCommentaires(idTicket: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.api}/tickets/${idTicket}/comments`);
  }

  // Ajouter un commentaire
  ajouterCommentaire(idTicket: number, contenu: string): Observable<Comment> {
    return this.http.post<Comment>(`${this.api}/tickets/${idTicket}/comments`, { contenu });
  }
}
