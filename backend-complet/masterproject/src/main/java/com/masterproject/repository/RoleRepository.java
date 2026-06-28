package com.masterproject.repository;

import com.masterproject.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository pour l'entite Role.
 *
 * En heritant de JpaRepository, on recupere GRATUITEMENT toutes les
 * methodes de base : save(), findById(), findAll(), delete()...
 * On n'a donc PAS besoin d'ecrire le code SQL nous-memes.
 *
 * Il suffit de declarer une methode en respectant la convention de nommage
 * de Spring Data, et Spring genere la requete automatiquement.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /** Cherche un role par son libelle (ex : "ADMIN"). */
    Optional<Role> findByLibelle(String libelle);
}
