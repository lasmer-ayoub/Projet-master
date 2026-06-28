package com.masterproject.repository;

import com.masterproject.entity.Project;
import com.masterproject.entity.ProjectMember;
import com.masterproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** Repository pour l'entite ProjectMember (liaison utilisateur / projet). */
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    /** Tous les membres d'un projet donne. */
    List<ProjectMember> findByProject(Project project);

    /** Tous les projets auxquels un utilisateur participe. */
    List<ProjectMember> findByUser(User user);

    /**
     * La ligne de liaison entre un utilisateur et un projet, si elle existe.
     * Sert a verifier si une personne est bien membre d'un projet, et avec
     * quel role (RESPONSABLE ou MEMBRE).
     */
    Optional<ProjectMember> findByUserAndProject(User user, Project project);
}
