// Page Notifications : liste les notifications de l'utilisateur connecte,
// et permet de toutes les marquer comme lues.
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../core/services/notification.service';
import { Notification } from '../../core/models/notification.model';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css',
})
export class NotificationsComponent implements OnInit {
  private notifService = inject(NotificationService);

  notifications: Notification[] = [];
  chargement = true;

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement = true;
    this.notifService.listerMes().subscribe({
      next: (data) => { this.notifications = data; this.chargement = false; },
      error: () => { this.chargement = false; },
    });
  }

  get nombreNonLues(): number {
    return this.notifications.filter((n) => !n.lue).length;
  }

  toutMarquerLues(): void {
    this.notifService.marquerToutesLues().subscribe({
      next: () => this.charger(),
    });
  }
}
