package com.masterproject.repository;

import com.masterproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Repository pour l'entite User. */
public interface UserRepository extends JpaRepository<User, Long> {

    /** Recherche un utilisateur par son email (sert a la connexion). */
    Optional<User> findByEmail(String email);

    /** Vrai si un utilisateur avec cet email existe deja. */
    boolean existsByEmail(String email);

    /** Liste les utilisateurs selon qu'ils sont valides ou non (true/false). */
    List<User> findByValide(boolean valide);
}
