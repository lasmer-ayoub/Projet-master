package com.masterproject.service;

import com.masterproject.dto.*;
import com.masterproject.entity.*;
import com.masterproject.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminService : logique metier reservee a l'administrateur.
 *
 * Regroupe :
 *  - la gestion des utilisateurs (creer / modifier / supprimer),
 *  - la vue de TOUS les projets (avec responsable, membres, nombre de tickets),
 *  - la suppression d'un projet,
 *  - les statistiques globales pour les graphes.
 *
 * Toutes ses methodes ne sont appelees que par AdminController, lui-meme
 * protege par @PreAuthorize("hasRole('ADMIN')").
 */
@Service
public class AdminService {

    // Toutes les dependances sont fournies par Spring via le constructeur.
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final HistoryRepository historyRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public AdminService(UserRepository userRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder,
                        ProjectRepository projectRepository,
                        ProjectMemberRepository projectMemberRepository,
                        TicketRepository ticketRepository,
                        CommentRepository commentRepository,
                        HistoryRepository historyRepository,
                        NotificationRepository notificationRepository,
                        UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.historyRepository = historyRepository;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    // ============================================================
    //   GESTION DES UTILISATEURS
    // ============================================================

    /** Liste tous les utilisateurs. */
    public List<UserDTO> listerUtilisateurs() {
        return userRepository.findAll().stream()
                .map(userService::versDTO)
                .toList();
    }

    /** Liste les comptes EN ATTENTE de validation (valide = false). */
    public List<UserDTO> listerComptesEnAttente() {
        return userRepository.findByValide(false).stream()
                .map(userService::versDTO)
                .toList();
    }

    /** Valide un compte : l'utilisateur pourra desormais se connecter. */
    public UserDTO validerUtilisateur(Long id) {
        User utilisateur = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        utilisateur.setValide(true);
        userRepository.save(utilisateur);
        return userService.versDTO(utilisateur);
    }

    /** Cree un nouvel utilisateur (avec le role choisi par l'admin). */
    public UserDTO creerUtilisateur(CreateUserRequest demande) {
        // 1. L'email doit etre unique.
        if (userRepository.existsByEmail(demande.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est deja utilise");
        }
        // 2. On recupere le role demande (ADMIN ou USER).
        Role role = trouverRole(demande.role());

        // 3. On cree l'utilisateur en hachant le mot de passe.
        User utilisateur = new User(
                demande.nom(),
                demande.email(),
                passwordEncoder.encode(demande.motDePasse()),
                role
        );
        // Un compte cree par l'admin est valide directement (pas besoin d'attendre).
        utilisateur.setValide(true);
        userRepository.save(utilisateur);
        return userService.versDTO(utilisateur);
    }

    /** Modifie le nom, l'email et le role d'un utilisateur existant. */
    public UserDTO modifierUtilisateur(Long id, UpdateUserRequest demande) {
        User utilisateur = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        // Si l'email change, il doit rester unique.
        if (!utilisateur.getEmail().equals(demande.email())
                && userRepository.existsByEmail(demande.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est deja utilise");
        }

        utilisateur.setNom(demande.nom());
        utilisateur.setEmail(demande.email());
        utilisateur.setRole(trouverRole(demande.role()));
        userRepository.save(utilisateur);
        return userService.versDTO(utilisateur);
    }

    /**
     * Supprime un utilisateur.
     *
     * Regles de securite :
     *  - on ne peut pas supprimer son PROPRE compte,
     *  - on ne peut pas supprimer un utilisateur qui est RESPONSABLE (createur)
     *    d'un ou plusieurs projets : il faut d'abord supprimer ces projets.
     *
     * Sinon, on detache proprement l'utilisateur de ses donnees liees
     * (sinon la base refuserait la suppression a cause des cles etrangeres).
     */
    @Transactional
    public void supprimerUtilisateur(Long id) {
        User utilisateur = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        // Interdit de se supprimer soi-meme.
        User connecte = userService.getUtilisateurConnecte();
        if (connecte.getId().equals(utilisateur.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Vous ne pouvez pas supprimer votre propre compte.");
        }

        // Interdit si l'utilisateur est responsable de projets.
        List<Project> projetsCrees = projectRepository.findByCreateur(utilisateur);
        if (!projetsCrees.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cet utilisateur est responsable de " + projetsCrees.size()
                            + " projet(s). Supprimez d'abord ces projets.");
        }

        // On detache l'utilisateur de ses donnees liees :
        // 1. ses commentaires
        commentRepository.findByAuteur(utilisateur).forEach(commentRepository::delete);
        // 2. ses entrees d'historique
        historyRepository.findByUser(utilisateur).forEach(historyRepository::delete);
        // 3. les tickets dont il est responsable : on enleve juste le responsable
        ticketRepository.findByResponsable(utilisateur).forEach(ticket -> {
            ticket.setResponsable(null);
            ticketRepository.save(ticket);
        });
        // 4. ses appartenances a des projets (en tant que membre)
        projectMemberRepository.findByUser(utilisateur).forEach(projectMemberRepository::delete);

        // 4 bis. ses notifications (sinon la base refuse la suppression)
        notificationRepository.findByDestinataireOrderByDateCreationDesc(utilisateur)
                .forEach(notificationRepository::delete);

        // 5. enfin, l'utilisateur lui-meme
        userRepository.delete(utilisateur);
    }

    // ============================================================
    //   GESTION DES PROJETS (vue admin)
    // ============================================================

    /** Liste TOUS les projets de l'application, avec responsable et membres. */
    public List<AdminProjectDTO> listerTousLesProjets() {
        return projectRepository.findAll().stream().map(projet -> {
            List<ProjectMember> membres = projectMemberRepository.findByProject(projet);
            List<String> nomsMembres = membres.stream()
                    .map(m -> m.getUser().getNom())
                    .toList();
            int nbTickets = ticketRepository.findByProject(projet).size();

            return new AdminProjectDTO(
                    projet.getId(),
                    projet.getNom(),
                    projet.getDescription(),
                    projet.getDateCreation(),
                    projet.getCreateur().getNom(),   // le createur est le responsable
                    membres.size(),
                    nomsMembres,
                    nbTickets
            );
        }).toList();
    }

    /**
     * Supprime un projet et tout ce qui lui est rattache :
     * tickets (et leurs commentaires + historique), puis membres, puis le projet.
     */
    @Transactional
    public void supprimerProjet(Long id) {
        Project projet = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet introuvable"));

        // 1. Pour chaque ticket : supprimer commentaires + historique, puis le ticket.
        ticketRepository.findByProject(projet).forEach(ticket -> {
            commentRepository.findByTicketOrderByDateCreationAsc(ticket).forEach(commentRepository::delete);
            historyRepository.findByTicketOrderByDateModificationDesc(ticket).forEach(historyRepository::delete);
            ticketRepository.delete(ticket);
        });

        // 2. Supprimer les membres du projet.
        projectMemberRepository.findByProject(projet).forEach(projectMemberRepository::delete);

        // 3. Supprimer le projet.
        projectRepository.delete(projet);
    }

    // ============================================================
    //   STATISTIQUES GLOBALES (pour les graphes)
    // ============================================================

    public AdminStatsDTO getStatistiques() {
        List<User> utilisateurs = userRepository.findAll();

        // Repartition des utilisateurs par role (ADMIN / USER)
        Map<String, Long> parRole = new LinkedHashMap<>();
        for (User u : utilisateurs) {
            String r = u.getRole().getLibelle();
            parRole.put(r, parRole.getOrDefault(r, 0L) + 1);
        }

        // Repartition des tickets par statut
        List<Ticket> tickets = ticketRepository.findAll();
        Map<String, Long> parStatut = new LinkedHashMap<>();
        for (Ticket t : tickets) {
            String s = t.getStatut().name();
            parStatut.put(s, parStatut.getOrDefault(s, 0L) + 1);
        }

        return new AdminStatsDTO(
                utilisateurs.size(),
                parRole,
                projectRepository.count(),
                tickets.size(),
                parStatut
        );
    }

    // ============================================================
    //   Methode utilitaire
    // ============================================================

    /** Retrouve un role par son libelle, ou renvoie une erreur claire. */
    private Role trouverRole(String libelle) {
        return roleRepository.findByLibelle(libelle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Role invalide : utilisez ADMIN ou USER"));
    }
}