package com.masterproject.controller;

import com.masterproject.dto.AuthResponse;
import com.masterproject.dto.LoginRequest;
import com.masterproject.dto.RegisterRequest;
import com.masterproject.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController : point d'entree de l'API pour l'authentification.
 *
 * @RestController : indique que cette classe gere des requetes HTTP et que
 *                   ses methodes renvoient directement du JSON.
 * @RequestMapping  : prefixe commun a toutes les URLs de ce controller.
 *
 * Ces deux endpoints sont PUBLICS (definis comme tels dans SecurityConfig).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Inscription d'un nouvel utilisateur.
     * @Valid declenche la validation du DTO (email valide, champs non vides...).
     * @RequestBody : les donnees JSON envoyees sont converties en RegisterRequest.
     */
    @PostMapping("/register")
    public AuthResponse inscription(@Valid @RequestBody RegisterRequest demande) {
        return authService.inscription(demande);
    }

    /** Connexion : renvoie le token JWT a utiliser pour les requetes suivantes. */
    @PostMapping("/login")
    public AuthResponse connexion(@Valid @RequestBody LoginRequest demande)
    {
        return authService.connexion(demande);
    }
}
