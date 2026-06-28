package com.masterproject.security;

import com.masterproject.entity.User;
import com.masterproject.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CustomUserDetailsService : fait le lien entre NOTRE entite User et le
 * systeme de securite de Spring.
 *
 * Spring Security ne connait pas notre classe User. Il manipule un objet
 * standard appele "UserDetails". Cette classe se charge de :
 *  1. retrouver l'utilisateur dans la base a partir de son email,
 *  2. le convertir en UserDetails que Spring Security sait utiliser.
 *
 * On transforme aussi le role systeme ("ADMIN" / "USER") en "autorite"
 * Spring Security, prefixee par "ROLE_" (convention de Spring).
 * Ainsi "ADMIN" devient l'autorite "ROLE_ADMIN", ce qui permet d'ecrire
 * plus tard hasRole('ADMIN') dans la configuration.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Injection du repository par le constructeur (bonne pratique).
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Methode appelee par Spring Security pour charger un utilisateur.
     * Ici, le "username" est en realite l'email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // On cherche l'utilisateur ; s'il n'existe pas, on leve une exception.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Aucun utilisateur avec l'email : " + email));

        // On construit l'autorite a partir du role systeme (ex : "ROLE_ADMIN").
        List<SimpleGrantedAuthority> autorites =
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getLibelle()));

        // On renvoie l'objet UserDetails standard de Spring Security.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),       // identifiant
                user.getMotDePasse(),  // mot de passe (deja hache)
                autorites              // ses droits
        );
    }
}
