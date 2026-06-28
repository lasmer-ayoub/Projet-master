// Ticket (correspond a TicketDTO du backend)
export interface Ticket {
  id: number;
  titre: string;
  description: string;
  type: string;
  priorite: string;
  statut: string;
  dateCreation: string;
  dateLimite: string | null;
  projectId: number;
  responsableNom: string | null;
  responsableEmail: string | null;
}

// Entree d'historique (correspond a HistoryDTO)
export interface History {
  id: number;
  champModifie: string;
  ancienneValeur: string;
  nouvelleValeur: string;
  dateModification: string;
  auteurNom: string;
}
