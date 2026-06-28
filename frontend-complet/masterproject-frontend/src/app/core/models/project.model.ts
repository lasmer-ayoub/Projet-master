// Projet (correspond a ProjectDTO du backend)
export interface Project {
  id: number;
  nom: string;
  description: string;
  dateCreation: string;
  createurNom: string;
  monRoleProjet: string; // RESPONSABLE ou MEMBRE (role de l'utilisateur connecte dans CE projet)
}

// Membre d'un projet (correspond a MemberDTO)
export interface Member {
  userId: number;
  nom: string;
  email: string;
  roleProjet: string; // RESPONSABLE ou MEMBRE
  dateAjout: string;
}

// Statistiques d'un projet (correspond a ProjectStatsDTO)
export interface ProjectStats {
  totalTickets: number;
  ticketsParStatut: { [statut: string]: number };
  pourcentageTermine: number;
}
