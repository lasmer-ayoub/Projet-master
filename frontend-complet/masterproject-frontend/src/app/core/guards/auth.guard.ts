// ============================================================
// Guard de route : protege les pages reservees aux utilisateurs
// connectes. Si aucun token n'est present, on redirige vers /login.
// ============================================================
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.estConnecte()) {
    return true; // l'utilisateur est connecte : acces autorise
  }

  // Pas connecte : on renvoie vers la page de connexion
  router.navigate(['/login']);
  return false;
};
