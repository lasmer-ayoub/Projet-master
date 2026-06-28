package com.masterproject.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter : un filtre execute AVANT chaque requete protegee.
 *
 * Son role :
 *  1. lire l'en-tete "Authorization" de la requete,
 *  2. en extraire le token JWT (apres le mot "Bearer "),
 *  3. verifier que le token est valide,
 *  4. si oui, dire a Spring Security "cet utilisateur est authentifie".
 *
 * OncePerRequestFilter garantit que ce filtre ne s'execute qu'une seule fois
 * par requete.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    // 2. S'il n'y a pas d'en-tete, ou s'il ne commence pas par "Bearer ",
    //    on laisse passer la requete sans authentifier (elle sera bloquee
    //    plus loin si l'endpoint est protege).
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. On recupere l'en-tete "Authorization".
        String enteteAuth = request.getHeader("Authorization");

        if (enteteAuth == null || !enteteAuth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. On extrait le token (on enleve les 7 caracteres de "Bearer ").
        String token = enteteAuth.substring(7);

        try {
            // 4. On lit l'email contenu dans le token.
            String email = jwtService.extraireEmail(token);

            // 5. Si on a un email ET que l'utilisateur n'est pas deja authentifie :
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // On charge l'utilisateur depuis la base.
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // On verifie que le token est bien valide pour cet utilisateur.
                if (jwtService.tokenEstValide(token, userDetails.getUsername())) {

                    // On cree l'objet d'authentification de Spring Security.
                    UsernamePasswordAuthenticationToken authentification =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authentification.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    // On enregistre l'utilisateur comme authentifie pour cette requete.
                    SecurityContextHolder.getContext().setAuthentication(authentification);
                }
            }
        } catch (Exception e) {
            // Token invalide / expire : on n'authentifie pas, on laisse Spring gerer.
        }

        // On passe la main au filtre suivant dans la chaine.
        filterChain.doFilter(request, response);
    }
}
