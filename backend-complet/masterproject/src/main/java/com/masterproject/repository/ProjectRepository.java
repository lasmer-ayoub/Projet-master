package com.masterproject.repository;

import com.masterproject.entity.Project;
import com.masterproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Repository pour l'entite Project. */
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /** Tous les projets crees par un utilisateur donne (= dont il est responsable). */
    List<Project> findByCreateur(User createur);
}
