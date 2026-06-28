package com.masterproject.config;

import com.masterproject.entity.Role;
import com.masterproject.entity.User;
import com.masterproject.repository.RoleRepository;
import com.masterproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer : code execute AUTOMATIQUEMENT au demarrage de l'application.
 *
 * Il sert a preparer les donnees de base pour que l'application soit
 * utilisable immediatement (tres pratique le jour de la soutenance) :
 *  1. il cree les deux roles systeme ADMIN et USER s'ils n'existent pas,
 *  2. il cree un compte administrateur par defaut s'il n'existe pas.
 *
 * En implementant CommandLineRunner, la methode run() est appelee une fois
 * que l'application a fini de demarrer.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Informations du compte admin lues depuis application.properties.
    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.nom}")
    private String adminNom;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. On s'assure que les deux roles existent. Si un role n'existe pas,
        //    on le cree. (orElseGet n'execute la creation que si besoin.)
        Role roleAdmin = roleRepository.findByLibelle("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));
        roleRepository.findByLibelle("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));

        // 2. On cree le compte administrateur s'il n'existe pas deja.
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(
                    adminNom,
                    adminEmail,
                    passwordEncoder.encode(adminPassword), // mot de passe hache
                    roleAdmin
            );
            // L'administrateur est valide d'office (sinon il ne pourrait pas
            // se connecter pour valider les autres comptes).
            admin.setValide(true);
            userRepository.save(admin);
            System.out.println(">> Compte administrateur cree : " + adminEmail
                    + " (mot de passe : " + adminPassword + ")");
        }
    }
}
