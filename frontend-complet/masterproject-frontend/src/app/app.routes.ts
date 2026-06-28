// ============================================================
// Les routes de l'application : quelle URL affiche quel composant.
// authGuard : il faut etre connecte. adminGuard : il faut etre ADMIN.
// ============================================================
import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ProjectListComponent } from './components/project-list/project-list.component';
import { ProjectDetailComponent } from './components/project-detail/project-detail.component';
import { BoardComponent } from './components/board/board.component';
import { TicketDetailComponent } from './components/ticket-detail/ticket-detail.component';
import { AdminComponent } from './components/admin/admin.component';
import { NotificationsComponent } from './components/notifications/notifications.component';
import { MonCompteComponent } from './components/mon-compte/mon-compte.component';

export const routes: Routes = [
  // Pages publiques
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // Pages protegees (il faut etre connecte)
  { path: 'projets', component: ProjectListComponent, canActivate: [authGuard] },
  { path: 'projets/:id', component: ProjectDetailComponent, canActivate: [authGuard] },
  { path: 'projets/:id/tableau', component: BoardComponent, canActivate: [authGuard] },
  { path: 'tickets/:id', component: TicketDetailComponent, canActivate: [authGuard] },
  { path: 'notifications', component: NotificationsComponent, canActivate: [authGuard] },
  { path: 'mon-compte', component: MonCompteComponent, canActivate: [authGuard] },

  // Espace administrateur (il faut etre ADMIN)
  { path: 'admin', component: AdminComponent, canActivate: [adminGuard] },

  // Redirections par defaut
  { path: '', redirectTo: 'projets', pathMatch: 'full' },
  { path: '**', redirectTo: 'projets' },
];
