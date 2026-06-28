// ============================================================
// Intercepteur HTTP : il s'execute AVANT chaque requete sortante.
// Son role : ajouter automatiquement l'en-tete
//   Authorization: Bearer <token>
// si un token est present. On n'a donc jamais a le faire a la main
// (contrairement aux tests Postman manuels).
// ============================================================
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (requete, suivant) => {
  const auth = inject(AuthService);
  const token = auth.getToken();

  // Si on a un token, on clone la requete en ajoutant l'en-tete Authorization.
  if (token) {
    const requeteAvecToken = requete.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
    return suivant(requeteAvecToken);
  }

  // Sinon, on laisse passer la requete telle quelle (ex: login, register).
  return suivant(requete);
};
