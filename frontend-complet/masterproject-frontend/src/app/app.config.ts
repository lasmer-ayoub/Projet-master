// ============================================================
// Configuration globale de l'application Angular.
// On y branche : le routage, le client HTTP, et l'intercepteur
// qui ajoute automatiquement le token JWT a chaque requete.
// ============================================================
import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // withInterceptors enregistre notre intercepteur JWT
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
};
