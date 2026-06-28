package com.masterproject.config;

import com.masterproject.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig : la configuration centrale de la securite de l'application.
 *
 * On y definit :
 *  - quels endpoints sont PUBLICS (connexion, inscription),
 *  - que tous les autres necessitent un utilisateur authentifie,
 *  - que l'application est "stateless" (aucune session : tout passe par le JWT),
 *  - l'encodeur de mot de passe (BCrypt),
 *  - la configuration CORS (pour autoriser le front Angular a appeler l'API).
 *
 * @EnableMethodSecurity active les annotations comme @PreAuthorize, qui
 * permettent de proteger une methode precise (ex : reservee aux ADMIN).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Definit la chaine de filtres de securite : c'est ici qu'on dit
     * qui a le droit d'acceder a quoi.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // On desactive la protection CSRF : inutile ici car on n'utilise
            // pas de session/cookie, l'authentification se fait par token.
            .csrf(csrf -> csrf.disable())

            // On active la configuration CORS definie plus bas.
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Definition des regles d'acces aux URLs :
            .authorizeHttpRequests(auth -> auth
                    // Endpoints PUBLICS (accessibles sans token) :
                    .requestMatchers("/api/auth/**").permitAll()        // connexion / inscription
                    // Tous les autres endpoints exigent une authentification :
                    .anyRequest().authenticated()
            )

            // On passe en mode STATELESS : le serveur ne garde aucune session,
            // il s'appuie uniquement sur le token JWT a chaque requete.
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // On ajoute NOTRE filtre JWT avant le filtre d'authentification standard.
            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Encodeur de mot de passe : BCrypt.
     * C'est lui qui hache les mots de passe avant de les stocker, et qui
     * permet de verifier un mot de passe a la connexion. Les mots de passe
     * ne sont donc JAMAIS stockes en clair dans la base.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * L'AuthenticationManager est utilise par AuthService pour verifier
     * le couple (email + mot de passe) lors de la connexion.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configuration CORS : autorise le front Angular (qui tourne par defaut
     * sur http://localhost:4200) a appeler notre API. Sans cela, le navigateur
     * bloquerait les requetes venant d'une autre origine.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Origines autorisees (le front Angular). On peut en ajouter d'autres.
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // Methodes HTTP autorisees.
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // En-tetes autorisees.
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
