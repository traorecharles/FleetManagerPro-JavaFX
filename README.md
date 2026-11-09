# 🚗 FleetManager Pro — Application desktop de gestion de parc automobile

Application **JavaFX** pour suivre le parc de véhicules d'une entreprise : véhicules, missions, affectations, entretiens, documents et statistiques, avec gestion des rôles.

![Java](https://img.shields.io/badge/Java-17+-orange) ![JavaFX](https://img.shields.io/badge/JavaFX-21-blue) ![MySQL](https://img.shields.io/badge/MySQL-8-4479A1) ![Maven](https://img.shields.io/badge/build-Maven-C71A36) ![JUnit](https://img.shields.io/badge/tests-JUnit%205-25A162)

## Fonctionnalités

| Module | Description |
|---|---|
| **Authentification** | Connexion par e-mail / mot de passe, 3 rôles : `admin`, `agent_logistique`, `consultant` |
| **Tableau de bord** | Indicateurs clés du parc en un coup d'œil |
| **Véhicules** | Fiche complète (carburant, transmission, kilométrage, statut, catégorie, département…) |
| **Missions** | Planification et suivi des missions (ordre de mission, chauffeur, statut) |
| **Affectations** | Attribution des véhicules aux utilisateurs / départements |
| **Entretien** | Planification et historique de la maintenance |
| **Documents** | Gestion des documents liés au parc |
| **Statistiques** | Visualisation des données du parc |
| **Administration** | Gestion des utilisateurs |

## Stack technique

- **Java 17+**, **JavaFX 21** (FXML + CSS)
- **MySQL 8** via JDBC (`mysql-connector-j`)
- **Maven** (`javafx-maven-plugin`)
- **JUnit 5** pour les tests de la couche service
- Architecture **MVC** : `model` / `controller` / `service` / `util`

## Structure

```
src/main
├── java/com/miage/fleet
│   ├── FleetApp.java          # point d'entrée JavaFX
│   ├── controller/            # un contrôleur par écran
│   ├── model/                 # Vehicle, Mission, Affectation, Entretien, User
│   ├── service/               # DatabaseService (JDBC), MockDataService (données de démo)
│   └── util/                  # connexion BD, notifications (toasts)
└── resources/com/miage/fleet
    ├── fxml/                  # vues
    ├── css/                   # thème
    └── database.properties    # configuration de la connexion MySQL
fleet_manager.sql              # schéma + jeu de données de démonstration
```

## Installation

1. **Prérequis** : JDK 17+, Maven, MySQL 8.
2. **Créer la base** :
   ```bash
   mysql -u root -p < fleet_manager.sql
   ```
3. **Configurer la connexion** dans `src/main/resources/com/miage/fleet/database.properties` (`db.url`, `db.user`, `db.password`).
4. **Lancer** :
   ```bash
   mvn javafx:run
   ```
5. **Tests** :
   ```bash
   mvn test
   ```

## Comptes de démonstration

Fournis par `fleet_manager.sql` (données fictives) :

| Rôle | E-mail | Mot de passe |
|---|---|---|
| Admin | `i.kone@societe.ci` | `admin123` |
| Agent logistique | `a.coulibaly@societe.ci` | `agent123` |
| Consultant | `b.traore@societe.ci` | `consul123` |

> ⚠️ Projet pédagogique : les mots de passe de démonstration sont stockés en clair. Une version de production devrait les hacher (bcrypt/Argon2).

## Auteur

**Charles Traoré** — projet réalisé dans le cadre de la formation MIAGE.
Version web du même sujet : [FleetManagerWeb](https://github.com/traorecharles/FleetManagerWeb).
