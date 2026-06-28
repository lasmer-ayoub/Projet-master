// ============================================================
// Modeles utilises uniquement par l'espace administrateur.
// Ils correspondent aux DTO renvoyes par les routes /api/admin/...
// ============================================================

// Vue d'un projet cote admin (AdminProjectDTO)
export interface AdminProject {
  id: number;
  nom: string;
  description: string;
  dateCreation: string;
  responsableNom: string;
  nombreMembres: number;
  membresNoms: string[];
  nombreTickets: number;
}

// Statistiques globales (AdminStatsDTO)
export interface AdminStats {
  totalUtilisateurs: number;
  utilisateursParRole: { [role: string]: number };
  totalProjets: number;
  totalTickets: number;
  ticketsParStatut: { [statut: string]: number };
}
