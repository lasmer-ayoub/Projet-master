package com.masterproject.controller;

import com.masterproject.dto.*;
import com.masterproject.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminController : toutes les operations reservees a l'administrateur.
 *
 * @PreAuthorize("hasRole('ADMIN')") place au niveau de la CLASSE s'applique
 * a TOUTES les methodes : si un utilisateur non-admin appelle l'une de ces
 * routes, Spring Security renvoie automatiquement une erreur 403 (interdit).
 *
 * Toutes les routes commencent par /api/admin.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ---------- UTILISATEURS ----------

    /** Liste tous les utilisateurs. */
    @GetMapping("/users")
    public List<UserDTO> listerUtilisateurs() {
        return adminService.listerUtilisateurs();
    }

    /** Liste les comptes en attente de validation. */
    @GetMapping("/users/pending")
    public List<UserDTO> listerComptesEnAttente() {
        return adminService.listerComptesEnAttente();
    }

    /** Valide un compte (l'utilisateur pourra se connecter). */
    @PutMapping("/users/{id}/validate")
    public UserDTO validerUtilisateur(@PathVariable Long id) {
        return adminService.validerUtilisateur(id);
    }

    /** Cree un utilisateur. */
    @PostMapping("/users")
    public UserDTO creerUtilisateur(@Valid @RequestBody CreateUserRequest demande) {
        return adminService.creerUtilisateur(demande);
    }

    /** Modifie un utilisateur (nom, email, role). */
    @PutMapping("/users/{id}")
    public UserDTO modifierUtilisateur(@PathVariable Long id,
                                       @Valid @RequestBody UpdateUserRequest demande) {
        return adminService.modifierUtilisateur(id, demande);
    }

    /** Supprime un utilisateur. */
    @DeleteMapping("/users/{id}")
    public void supprimerUtilisateur(@PathVariable Long id) {
        adminService.supprimerUtilisateur(id);
    }

    // ---------- PROJETS ----------

    /** Liste TOUS les projets (avec responsable, membres, nombre de tickets). */
    @GetMapping("/projects")
    public List<AdminProjectDTO> listerProjets() {
        return adminService.listerTousLesProjets();
    }

    /** Supprime un projet et tout son contenu. */
    @DeleteMapping("/projects/{id}")
    public void supprimerProjet(@PathVariable Long id) {
        adminService.supprimerProjet(id);
    }

    // ---------- STATISTIQUES ----------

    /** Statistiques globales pour les graphes. */
    @GetMapping("/stats")
    public AdminStatsDTO statistiques() {
        return adminService.getStatistiques();
    }
}
