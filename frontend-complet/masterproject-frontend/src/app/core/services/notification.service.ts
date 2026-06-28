// ============================================================
// Service des notifications de l'utilisateur connecte.
// ============================================================
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Notification } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private http = inject(HttpClient);
  private api = API_URL;

  // Liste mes notifications (plus recentes d'abord)
  listerMes(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.api}/notifications`);
  }

  // Nombre de notifications non lues (pour le compteur de la cloche)
  compterNonLues(): Observable<{ nonLues: number }> {
    return this.http.get<{ nonLues: number }>(`${this.api}/notifications/count`);
  }

  // Marquer toutes mes notifications comme lues
  marquerToutesLues(): Observable<void> {
    return this.http.put<void>(`${this.api}/notifications/read-all`, {});
  }

  // Marquer une notification comme lue
  marquerLue(id: number): Observable<void> {
    return this.http.put<void>(`${this.api}/notifications/${id}/read`, {});
  }
}
