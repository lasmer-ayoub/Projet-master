package com.masterproject.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JwtService : la classe qui s'occupe de TOUT ce qui concerne le token JWT.
 *
 * Le principe (volontairement simple) :
 *  1. A la connexion, on GENERE un token signe qui contient l'email de
 *     l'utilisateur et une date d'expiration.
 *  2. A chaque requete suivante, le client renvoie ce token. On VERIFIE alors
 *     qu'il est bien signe par nous et qu'il n'est pas expire.
 *
 * Un token n'est pas chiffre mais SIGNE : personne ne peut le falsifier sans
 * connaitre notre cle secrete (definie dans application.properties).
 */
@Service
public class JwtService {

    /** La cle secrete, lue depuis application.properties (jwt.secret). */
    @Value("${jwt.secret}")
    private String secret;

    /** La duree de validite du token en millisecondes (jwt.expiration). */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Construit la cle de signature a partir du texte secret.
     * On utilise l'algorithme HMAC-SHA, d'ou la cle de type SecretKey.
     */
    private SecretKey getCleDeSignature() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genere un token JWT pour un utilisateur identifie par son email.
     *
     * Le token contient :
     *  - "subject"  : l'email de l'utilisateur (a qui appartient le token)
     *  - "issuedAt" : la date de creation
     *  - "expiration" : la date apres laquelle le token n'est plus valide
     *  - une signature avec notre cle secrete
     */
    public String genererToken(String email) {
        Date maintenant = new Date();
        Date dateExpiration = new Date(maintenant.getTime() + expiration);

        return Jwts.builder()
                .subject(email)                 // l'email est l'identifiant du token
                .issuedAt(maintenant)           // date d'emission
                .expiration(dateExpiration)     // date d'expiration
                .signWith(getCleDeSignature())  // signature avec la cle secrete
                .compact();                     // construit la chaine finale du token
    }

    /**
     * Extrait l'email (le "subject") contenu dans un token.
     * Au passage, cette operation verifie aussi la signature du token :
     * si le token a ete modifie ou mal signe, une exception est levee.
     */
    public String extraireEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getCleDeSignature())  // on verifie la signature
                .build()
                .parseSignedClaims(token)         // on lit le contenu du token
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Indique si un token est valide : il doit appartenir au bon utilisateur
     * ET ne pas etre expire.
     */
    public boolean tokenEstValide(String token, String emailAttendu) {
        try {
            String emailDuToken = extraireEmail(token);
            return emailDuToken.equals(emailAttendu) && !estExpire(token);
        } catch (Exception e) {
            // Si une exception survient (token falsifie, mal forme...), il est invalide.
            return false;
        }
    }

    /** Verifie si la date d'expiration du token est deja passee. */
    private boolean estExpire(String token) {
        Date dateExpiration = Jwts.parser()
                .verifyWith(getCleDeSignature())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return dateExpiration.before(new Date());
    }
}
