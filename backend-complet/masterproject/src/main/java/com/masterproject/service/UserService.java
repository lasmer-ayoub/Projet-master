package com.masterproject.service;

import com.masterproject.dto.UserDTO;
import com.masterproject.dto.UpdateProfileRequest;
import com.masterproject.dto.ChangePasswordRequest;
import com.masterproject.entity.User;
import com.masterproject.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * UserService : logique metier liee aux utilisateurs.
 *
 * Contient notamment la methode getUtilisateurConnecte() qui retrouve
 * l'utilisateur actuellement authentifie : on s'en sert dans presque tous
 * les autres services (pour savoir QUI fait l'action).
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Recupere l'utilisateur actuellement connecte.
     *
     * Comment ? Le filtre JWT a deja place l'email de l'utilisateur dans le
     * "contexte de securite" de Spring. On lit cet email, puis on retrouve
     * l'utilisateur complet dans la base.
     */
    public User getUtilisateurConnecte() {
        // L'email est stocke comme "name" de l'authentification courante.
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur non authentifie"));
    }

    /**
     * Liste tous les utilisateurs (reserve a l'administrateur).
     * On convertit chaque entite User en UserDTO (sans le mot de passe).
     */
    public List<UserDTO> listerTous() {
        return userRepository.findAll().stream()
                .map(this::versDTO)
                .toList();
    }

    /**
     * Modifie le profil (nom + email) de l'utilisateur CONNECTE.
     * L'email doit rester unique.
     */
    public UserDTO modifierMonProfil(UpdateProfileRequest demande) {
        User moi = getUtilisateurConnecte();
        // Si l'email change, il ne doit pas etre deja pris par un autre compte.
        if (!moi.getEmail().equals(demande.email())
                && userRepository.existsByEmail(demande.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est deja utilise");
        }
        moi.setNom(demande.nom());
        moi.setEmail(demande.email());
        userRepository.save(moi);
        return versDTO(moi);
    }

    /**
     * Change le mot de passe de l'utilisateur CONNECTE.
     * On verifie d'abord que l'ancien mot de passe est correct.
     */
    public void changerMonMotDePasse(ChangePasswordRequest demande) {
        User moi = getUtilisateurConnecte();
        if (!passwordEncoder.matches(demande.ancienMotDePasse(), moi.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le mot de passe actuel est incorrect");
        }
        moi.setMotDePasse(passwordEncoder.encode(demande.nouveauMotDePasse()));
        userRepository.save(moi);
    }

    /** Convertit une entite User en UserDTO. */
    public UserDTO versDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getNom(),
                user.getEmail(),
                user.getRole().getLibelle(),
                user.isValide()
        );
    }
}
