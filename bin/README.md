# Gestion AVEC - Application JavaFX

## Description

Application de gestion des Associations Villageoises d'Épargne et de Crédit (AVEC) développée en JavaFX avec une architecture MVC.

## Fonctionnalités

### Rôles et Dashboards

| Rôle | Fonctionnalités |
|------|-----------------|
| **Admin** | Gestion utilisateurs, agents, AVEC, membres, statistiques globales |
| **Président** | Gestion membres, comité, demandes prêts, amendes, validation phases |
| **Secrétaire** | Présences, procès-verbaux, historique parts, demandes prêts |
| **Trésorier** | Caisse, décaissements, remboursements, rapports financiers |
| **Compteur** | Comptage des fonds, vérification carnets, état caisse |
| **Agent Villageois** | Création AVEC, formations, election comité, validation phases |
| **Agent Terrain** | Supervision AVEC, agents villageois, visites |

## Technologies

- **Java** 17+
- **JavaFX** 17 (GUI)
- **MySQL** 8.x (Base de données)
- **Maven** (Build)
- **Architecture** MVC + Services/DAO

## Structure du Projet

```
src/main/java/com/avec/
├── config/          # Configuration (DB, Styles)
├── dao/             # Accès données
├── enums/           # Énumérations
├── model/           # Modèles métier
├── service/         # Logique métier
├── utils/           # Utilitaires
└── view/            # Interfaces utilisateur
```

## Installation

1. Créer la base de données MySQL
2. Importer les tables SQL
3. Configurer `DBConnection.java` avec vos identifiants
4. Compiler avec `mvn compile`
5. Exécuter avec `mvn javafx:run`

## Base de données

Tables principales :
- `utilisateur`, `agent_villageois`, `agent_terrain`
- `avec`, `membre`, `cycle`, `reunion`
- `achat_part`, `pret`, `remboursement`
- `amende`, `presence`, `comptage`, `proces_verbal`
- `caisse`, `visite`

## Capture d'écran

L'application propose des interfaces graphiques modernes avec :
- Tableau de bord statistiques
- Formulaires de saisie
- Tableaux de gestion
- Dialogues de confirmation

## License

Projet éducatif - AVEC