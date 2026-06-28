package com.masterproject.service;

import com.masterproject.dto.AuthResponse;
import com.masterproject.dto.LoginRequest;
import com.masterproject.dto.RegisterRequest;
import com.masterproject.entity.Role;
import com.masterproject.entity.User;
import com.masterproject.repository.RoleRepository;
import com.masterproject.repository.UserRepository;
import com.masterproject.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * AuthService : gere l'inscription et la connexion des utilisateurs.
 * C'est ici que les tokens JWT sont generes.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    /**
     * Inscription d'un nouvel utilisateur.
     * Le nouvel utilisateur recoit automatiquement le role systeme USER,
     * MAIS son compte est cree NON VALIDE : il devra etre approuve par
     * l'administrateur avant de pouvoir se connecter.
     *
     * On ne genere donc PAS de token ici (pas de connexion automatique).
     */
    public AuthResponse inscription(RegisterRequest demande) {
        // 1. On verifie que l'email n'est pas deja utilise.
        if (userRepository.existsByEmail(demande.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cet email est deja utilise");
        }

        // 2. On recupere le role USER (cree au demarrage par DataInitializer).
        Role roleUser = roleRepository.findByLibelle("USER")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Le role USER est introuvable"));

        // 3. On cree l'utilisateur en HACHANT son mot de passe avec BCrypt.
        User utilisateur = new User(
                demande.nom(),
                demande.email(),
                passwordEncoder.encode(demande.motDePasse()),
                roleUser
        );
        // Le compte est NON valide : il attend l'approbation de l'admin.
        utilisateur.setValide(false);
        userRepository.save(utilisateur);

        // 4. Pas de token : le compte n'est pas encore actif.
        //    On renvoie juste les infos de l'utilisateur (token = null).
        return new AuthResponse(null, "Bearer", userService.versDTO(utilisateur));
    }

    /**
     * Connexion d'un utilisateur existant.
     * On verifie le couple (email + mot de passe) via Spring Security.
     */
    public AuthResponse connexion(LoginRequest demande) {
        try {
            // L'AuthenticationManager verifie l'email et le mot de passe.
            // Si le mot de passe est faux, il leve une BadCredentialsException.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            demande.email(),
                            demande.motDePasse()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Email ou mot de passe incorrect");
        }

        // Identifiants valides : on retrouve l'utilisateur et on genere son token.
        User utilisateur = userRepository.findByEmail(demande.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));

        // VERIFICATION IMPORTANTE : le compte doit avoir ete valide par l'admin.
        // Sinon, meme avec le bon mot de passe, la connexion est refusee.
        if (!utilisateur.isValide()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Votre compte n'a pas encore ete valide par l'administrateur.");
        }

        String token = jwtService.genererToken(utilisateur.getEmail());

        return new AuthResponse(token, "Bearer", userService.versDTO(utilisateur));
    }
}
