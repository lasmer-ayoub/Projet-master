# MasterProject — Application collaborative de gestion de projets

Application web collaborative de gestion de projets, fondée sur une To-Do List et un système de tickets organisés en tableau Kanban (à la manière de Trello / Jira). Réalisée dans le cadre d'un projet de fin d'études à ITeam University.

## Fonctionnalités

- Authentification par JWT avec validation des comptes par un administrateur
- Gestion des projets avec membres et rôles
- Rôles à deux niveaux : système (ADMIN / USER) et projet (RESPONSABLE / MEMBRE)
- Gestion des tickets (CRUD, type, priorité, statut, date limite, attribution)
- Tableau Kanban interactif avec glisser-déposer
- Commentaires et historique des modifications sur les tickets
- Notifications (invitation à un projet, attribution d'un ticket)
- Tableau de bord avec statistiques d'avancement
- Espace d'administration (gestion des utilisateurs, supervision des projets, statistiques globales)

## Technologies utilisées

**Backend :** Java 17, Spring Boot 3.5, Spring Security, JWT, Hibernate / JPA, MySQL
**Frontend :** Angular 18, TypeScript, HTML, CSS
**Base de données :** MySQL (via XAMPP)

## Architecture

Le backend suit une architecture en couches :

Contrôleur  ->  Service  ->  Repository  ->  Base de données

Le frontend (Angular) communique avec le backend via une API REST (JSON), sécurisée par un jeton JWT envoyé à chaque requête.

## Prérequis

- Java 17 ou supérieur
- Maven
- Node.js et npm
- Angular CLI (`npm install -g @angular/cli`)
- MySQL en cours d'exécution (XAMPP)

## Installation et lancement

### Backend

```bash
cd masterproject
# Vérifier que MySQL est lancé (XAMPP) sur le port 3306
# La base "masterproject" est créée automatiquement au premier démarrage
mvn spring-boot:run
```

Le backend démarre sur `http://localhost:8080`.

Un compte administrateur est créé automatiquement au démarrage :
- Email : `admin@masterproject.com`
- Mot de passe : `admin123`

### Frontend

```bash
cd masterproject-frontend
npm install
ng serve
```

Le frontend démarre sur `http://localhost:4200`.

## Configuration

Les paramètres de la base de données se trouvent dans
`masterproject/src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/masterproject?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

## Structure du projet
