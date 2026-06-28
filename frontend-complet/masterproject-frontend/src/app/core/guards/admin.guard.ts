// ============================================================
// Guard reserve a l'administrateur.
// Si l'utilisateur n'est pas connecte OU n'est pas ADMIN,
// il est renvoye vers la page des projets.
// ============================================================
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.estConnecte() && auth.estAdmin()) {
    return true;
  }
  router.navigate(['/projets']);
  return false;
};
