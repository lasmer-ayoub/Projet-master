// Utilisateur (correspond a UserDTO du backend)
export interface User {
  id: number;
  nom: string;
  email: string;
  role: string;     // role systeme : ADMIN ou USER
  valide: boolean;  // le compte a-t-il ete valide par l'administrateur ?
}

// Reponse renvoyee a la connexion / inscription (AuthResponse)
export interface AuthResponse {
  token: string | null; // null a l'inscription (compte pas encore actif)
  type: string;         // toujours "Bearer"
  utilisateur: User;
}
