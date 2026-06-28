package com.masterproject.controller;

import com.masterproject.dto.*;
import com.masterproject.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProjectController : endpoints lies aux projets, a leurs membres et a
 * leurs statistiques.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /** Cree un projet (le createur en devient le responsable). */
    @PostMapping
    public ProjectDTO creer(@Valid @RequestBody CreateProjectRequest demande) {
        return projectService.creerProjet(demande);
    }

    /** Liste les projets auxquels l'utilisateur connecte participe. */
    @GetMapping
    public List<ProjectDTO> mesProjets() {
        return projectService.listerMesProjets();
    }

    /**
     * Detail d'un projet.
     * @PathVariable : recupere la valeur {id} presente dans l'URL.
     */
    @GetMapping("/{id}")
    public ProjectDTO detail(@PathVariable Long id) {
        return projectService.getProjet(id);
    }

    /** Ajoute un membre au projet (reserve au responsable). */
    @PostMapping("/{id}/members")
    public MemberDTO ajouterMembre(@PathVariable Long id,
                                   @Valid @RequestBody AddMemberRequest demande) {
        return projectService.ajouterMembre(id, demande);
    }

    /** Liste les membres du projet. */
    @GetMapping("/{id}/members")
    public List<MemberDTO> membres(@PathVariable Long id) {
        return projectService.listerMembres(id);
    }

    /** Statistiques du projet (tableau de bord : avancement, repartition...). */
    @GetMapping("/{id}/stats")
    public ProjectStatsDTO statistiques(@PathVariable Long id) {
        return projectService.getStatistiques(id);
    }
}
