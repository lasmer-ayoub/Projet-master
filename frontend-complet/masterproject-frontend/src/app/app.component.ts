// Composant racine de l'application.
// Mise en page : la sidebar a gauche + la zone de contenu a droite.
// La sidebar se cache toute seule si l'utilisateur n'est pas connecte
// (dans ce cas, les pages login/register occupent tout l'ecran).
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from './components/sidebar/sidebar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {}
