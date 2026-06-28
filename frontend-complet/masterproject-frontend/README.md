# Frontend Angular — Application de gestion de projets

Interface web (Angular 18) pour l'application collaborative de gestion de projets
(To Do List et tickets, style Kanban). Elle communique avec le backend Spring Boot.

## Prérequis

- Node.js 18 ou plus (vérifier avec `node -v`)
- Le backend Spring Boot doit tourner sur **http://localhost:8080**
  (et MySQL démarré dans XAMPP)

## Installation et lancement

Ouvrir le dossier dans VS Code, puis dans un terminal :

```bash
npm install        # installe les dépendances (à faire une seule fois)
npm start          # lance l'application
```

L'application s'ouvre sur **http://localhost:4200**.

> La première commande `npm install` télécharge Angular et peut prendre une minute.

## Connexion

Utiliser le compte administrateur créé automatiquement par le backend :

- Email : `admin@masterproject.com`
- Mot de passe : `admin123`

Ou créer un compte via la page « Créer un compte ».

## Organisation du code (src/app)

- `core/models/` : les interfaces TypeScript (miroir des DTO du backend).
- `core/services/` : la communication avec l'API (auth, projets, tickets, commentaires).
- `core/interceptors/auth.interceptor.ts` : ajoute automatiquement le token JWT à chaque requête.
- `core/guards/auth.guard.ts` : protège les pages réservées aux utilisateurs connectés.
- `components/` : les composants (chacun avec ses fichiers `.ts`, `.html`, `.css`) :
  - `login`, `register` : authentification
  - `navbar` : barre de navigation
  - `project-list`, `project-detail` : projets et tableau de bord
  - `members` : membres d'un projet (lister, inviter)
  - `board` : le tableau Kanban (glisser-déposer pour changer le statut)
  - `ticket-card` : une carte ticket
  - `ticket-dialog` : fenêtre de création / modification d'un ticket
  - `ticket-detail` : détail d'un ticket (commentaires + historique)

## Lien avec le backend

L'adresse de l'API est définie dans `src/app/core/api.config.ts`
(`http://localhost:8080/api`). Si le backend change d'adresse, modifier uniquement ce fichier.
