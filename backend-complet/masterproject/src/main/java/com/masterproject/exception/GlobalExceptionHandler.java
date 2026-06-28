package com.masterproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler : centralise la gestion des erreurs de l'application.
 *
 * @RestControllerAdvice : cette classe "intercepte" les exceptions levees par
 * n'importe quel controller et renvoie une reponse JSON propre, plutot qu'une
 * grosse trace d'erreur technique.
 *
 * (Les erreurs de type ResponseStatusException, levees dans nos services avec
 * un code precis comme 403 ou 404, sont deja gerees automatiquement par Spring
 * avec le bon code HTTP : on n'a donc pas besoin de les traiter ici.)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gere les erreurs de VALIDATION (@Valid).
     * Quand un champ envoye est invalide (email mal forme, champ vide...),
     * on renvoie une erreur 400 avec, pour chaque champ fautif, le message
     * d'erreur correspondant.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> gererErreursValidation(
            MethodArgumentNotValidException exception) {

        Map<String, String> erreurs = new HashMap<>();
        // On parcourt chaque champ en erreur pour recuperer son message.
        for (FieldError erreur : exception.getBindingResult().getFieldErrors()) {
            erreurs.put(erreur.getField(), erreur.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreurs);
    }
}
