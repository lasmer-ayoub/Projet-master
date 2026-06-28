# masterproject — Backend

Application web collaborative de gestion de projets basée sur une **To Do List** et des **tickets** (style Trello/Jira simplifié).

Backend **Spring Boot** (API REST) — Projet de Master.

---

## 1. Technologies utilisées

| Couche | Technologie |
|--------|-------------|
| Langage | Java 17 |
| Framework | Spring Boot 3.5.3 |
| Sécurité | Spring Security + JWT |
| Persistance | Spring Data JPA / Hibernate |
| Base de données | MySQL (via XAMPP) |

| Build | Maven |

---

## 2. Prérequis

1. **JDK 17 ou plus récent** (17, 21, 24… tout fonctionne).
2. **IntelliJ IDEA** (Community suffit).
3. **XAMPP** (pour MySQL).

### Vérifier ta version de Java

Ouvre un terminal (ou le terminal intégré d'IntelliJ : menu *View → Tool Windows → Terminal*) et tape :

```bash
java -version
```

- Si la première ligne affiche `version "17"`, `"21"`, `"24"`… → c'est bon ✅
- Si ça affiche une version inférieure à 17, ou « commande introuvable » → installe un JDK récent (par ex. via IntelliJ : *File → Project Structure → SDK → Add SDK → Download JDK*, choisis la 17 ou 21).

Dans IntelliJ, vérifie aussi que le SDK du projet est défini :
*File → Project Structure → Project → SDK* → choisis une version 17+.

---

## 3. Préparer la base de données (XAMPP)

1. Lance **XAMPP** et démarre le module **MySQL** (bouton *Start*).
2. C'est tout ! Tu n'as **pas besoin de créer la base à la main** : l'application crée automatiquement la base `masterproject` au démarrage (grâce à `createDatabaseIfNotExist=true`), et Hibernate crée les tables tout seul.

> Configuration par défaut utilisée (celle de XAMPP) : utilisateur `root`, **sans mot de passe**, port `3306`.
> Si ton MySQL a un mot de passe, modifie `spring.datasource.password` dans
> `src/main/resources/application.properties`.

---

## 4. Lancer le projet

1. Ouvre le dossier `masterproject` dans IntelliJ (*File → Open*).
2. Attends que Maven télécharge les dépendances (barre de progression en bas).
3. Ouvre la classe `MasterprojectApplication.java`
   (`src/main/java/com/masterproject/`).
4. Clique sur la flèche verte ▶ à côté de `main()` → **Run**.

L'application démarre sur **http://localhost:8080**.

Au premier lancement, un compte administrateur est créé automatiquement :

- **Email** : `admin@masterproject.com`
- **Mot de passe** : `admin123`

---

## 5. Tester l'API

Le test se fait avec **Postman** (simple et rapide).

1. Ouvre Postman → *Import* → sélectionne le fichier
   **`masterproject.postman_collection.json`** (à la racine du projet).
2. La collection « masterproject API » apparaît avec toutes les requêtes prêtes.
3. Lance d'abord la requête **Login** : le token est **automatiquement enregistré**
   dans la collection (grâce à un petit script), donc toutes les autres requêtes
   l'utilisent sans que tu aies à le copier-coller.
4. Enchaîne ensuite : *Créer un projet → Créer un ticket → Changer le statut…*

---

## 6. Scénario de démonstration (pour la soutenance)

Un parcours simple à montrer au jury :

1. **Login** en tant qu'admin.
2. **Register** d'un second utilisateur (ex. `membre@test.com`).
3. **Créer un projet** → l'admin en devient automatiquement le **responsable**.
4. **Ajouter** `membre@test.com` au projet.
5. **Créer un ticket**, l'affecter à un membre.
6. **Changer le statut** du ticket (À faire → En cours → Terminé) : déplacement Kanban.
7. **Ajouter un commentaire**.
8. **Consulter l'historique** du ticket (toutes les modifications tracées).
9. **Consulter les statistiques** du projet (avancement, répartition par statut).

---

## 7. Principaux endpoints

| Méthode | URL | Description | Accès |
|---------|-----|-------------|-------|
| POST | `/api/auth/register` | Inscription | Public |
| POST | `/api/auth/login` | Connexion (renvoie le token) | Public |
| GET | `/api/users` | Liste des utilisateurs | ADMIN |
| GET | `/api/users/me` | Mon profil | Connecté |
| POST | `/api/projects` | Créer un projet | Connecté |
| GET | `/api/projects` | Mes projets | Connecté |
| GET | `/api/projects/{id}` | Détail d'un projet | Membre |
| POST | `/api/projects/{id}/members` | Ajouter un membre | Responsable |
| GET | `/api/projects/{id}/members` | Membres du projet | Membre |
| GET | `/api/projects/{id}/stats` | Statistiques | Membre |
| POST | `/api/projects/{projectId}/tickets` | Créer un ticket | Responsable |
| GET | `/api/projects/{projectId}/tickets` | Lister / filtrer les tickets | Membre |
| GET | `/api/tickets/{id}` | Détail d'un ticket | Membre |
| PUT | `/api/tickets/{id}` | Modifier un ticket | Membre |
| PATCH | `/api/tickets/{id}/statut?valeur=EN_COURS` | Changer le statut | Membre |
| DELETE | `/api/tickets/{id}` | Supprimer un ticket | Responsable |
| GET | `/api/tickets/{id}/historique` | Historique du ticket | Membre |
| POST | `/api/tickets/{ticketId}/comments` | Ajouter un commentaire | Membre |
| GET | `/api/tickets/{ticketId}/comments` | Lister les commentaires | Membre |

---

## 8. Organisation du code (architecture en couches)

```
com.masterproject
├── entity        → les tables de la base (User, Project, Ticket, ...)
│   └── enums     → valeurs fixes (Statut, Priorite, TypeTicket, RoleProjet)
├── repository    → accès à la base (Spring Data JPA)
├── dto           → objets échangés avec le front (protègent les entités)
├── service       → logique métier
├── controller    → points d'entrée de l'API REST
├── security      → JWT (génération, validation, filtre)
├── config        → sécurité, données initiales
└── exception     → gestion centralisée des erreurs
```

Le flux d'une requête : **Controller → Service → Repository → Base de données**,
puis retour sous forme de **DTO**.
