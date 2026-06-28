package com.masterproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application : c'est le POINT DE DEMARRAGE.
 *
 * @SpringBootApplication regroupe trois annotations :
 *  - @Configuration       : cette classe peut definir de la configuration,
 *  - @EnableAutoConfiguration : Spring Boot configure tout seul beaucoup de
 *    choses (serveur web, connexion a la base...) a partir des dependances,
 *  - @ComponentScan       : Spring scanne le package com.masterproject et tous
 *    ses sous-packages pour trouver les @RestController, @Service, etc.
 *
 * Pour lancer l'application : clic droit sur ce fichier dans IntelliJ -> Run,
 * ou bien executer la methode main() ci-dessous.
 */
@SpringBootApplication
public class MasterprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasterprojectApplication.class, args);
    }
}
