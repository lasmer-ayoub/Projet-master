// Point d'entree de l'application Angular.
// On demarre le composant racine AppComponent avec sa configuration (app.config.ts).
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
