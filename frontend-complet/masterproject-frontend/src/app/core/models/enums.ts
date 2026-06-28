// ============================================================
// Les valeurs possibles (enums) cote frontend.
// Elles doivent correspondre EXACTEMENT aux enums du backend.
// ============================================================

// Type d'un ticket
export const TYPES_TICKET = ['TACHE', 'BUG', 'AMELIORATION', 'DEMANDE', 'URGENCE'] as const;

// Priorite d'un ticket
export const PRIORITES = ['FAIBLE', 'MOYENNE', 'HAUTE', 'CRITIQUE'] as const;

// Statuts d'un ticket = colonnes du tableau Kanban
export const STATUTS = ['A_FAIRE', 'EN_COURS', 'EN_ATTENTE', 'A_VALIDER', 'BLOQUE', 'ANNULE', 'TERMINE'] as const;

// Libelles lisibles pour l'affichage (le backend stocke A_FAIRE, on affiche "A faire")
export const LIBELLE_STATUT: Record<string, string> = {
  A_FAIRE: 'À faire',
  EN_COURS: 'En cours',
  EN_ATTENTE: 'En attente',
  A_VALIDER: 'À valider',
  BLOQUE: 'Bloqué',
  ANNULE: 'Annulé',
  TERMINE: 'Terminé',
};

export const LIBELLE_PRIORITE: Record<string, string> = {
  FAIBLE: 'Faible',
  MOYENNE: 'Moyenne',
  HAUTE: 'Haute',
  CRITIQUE: 'Critique',
};

export const LIBELLE_TYPE: Record<string, string> = {
  TACHE: 'Tâche',
  BUG: 'Bug',
  AMELIORATION: 'Amélioration',
  DEMANDE: 'Demande',
  URGENCE: 'Urgence',
};
