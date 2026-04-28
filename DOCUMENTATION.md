# Documentation Technique Complète - Gestion AVEC

## Table des Matières
1. [Présentation du Projet](#1-présentation-du-projet)
2. [Technologies Utilisées](#2-technologies-utilisées)
3. [Architecture du Projet](#3-architecture-du-projet)
4. [Structure Détaillée du Code](#4-structure-détaillée-du-code)
5. [Modèle de Données](#5-modèle-de-données)
6. [Explications du Code](#6-explications-du-code)
7. [Système d'Authentification](#7-système-dauthentification)
8. [Gestion des Rôles](#8-gestion-des-rôles)
9. [Fonctionnalités par Rôle](#9-fonctionnalités-par-rôle)
10. [Base de Données](#10-base-de-données)
11. [Guide d'Installation](#11-guide-dinstallation)
12. [Annexes](#12-annexes)

---

## 1. Présentation du Projet

### 1.1 Contexte
L'application **Gestion AVEC** (Association Villageoise d'Épargne et de Crédit) est une solution logicielle complète développée en JavaFX pour digitaliser la gestion des AVEC. Ces associations locales permettent aux villageois de mettre en commun leurs économies et d'accéder à des crédits.

### 1.2 Objectifs
- Digitaliser la gestion manuelle des AVEC
- Automatiser le suivi des épargnes et des prêts
- Gérer les cycles de formation (15 visites obligatoires)
- Faciliter la gestion des comités et des gardiens de clés
- Générer des rapports financiers

### 1.3 Utilisateurs Cibles
- Administrateurs (gestion globale)
- Agents de Terrain (superviseurs)
- Agents Villageois (formateurs)
- Membres de comité (Président, Secrétaire, Trésorier, Compteur)
- Membres simples

---

## 2. Technologies Utilisées

### 2.1 Environnement de Développement
| Technologie | Version | Rôle |
|-------------|---------|------|
| **Java** | 17+ (avec preview features) | Langage de programmation principal |
| **JavaFX** | 17.0.10 | Framework GUI (Interface graphique) |
| **MySQL** | 8.x | Base de données relationnelle |
| **Maven** | 3.x | Outil de build et gestion des dépendances |

### 2.2 Bibliothèques et Frameworks
```xml
<!-- JavaFX - Interface graphique -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17.0.10</version>
</dependency>

<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>17.0.10</version>
</dependency>

<!-- MySQL Connector -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.3.0</version>
</dependency>

<!-- BootstrapFX - Style harmonisé -->
<dependency>
    <groupId>org.kordamp.bootstrapfx</groupId>
    <artifactId>bootstrapfx-core</artifactId>
    <version>0.2.4</version>
</dependency>

<!-- JFreeChart - Graphiques et statistiques -->
<dependency>
    <groupId>org.jfree</groupId>
    <artifactId>jfreechart</artifactId>
    <version>1.5.3</version>
</dependency>

<!-- OpenPDF - Export PDF -->
<dependency>
    <groupId>com.github.librepdf</groupId>
    <artifactId>openpdf</artifactId>
    <version>1.3.26</version>
</dependency>
```

### 2.3 Configuration Maven (extrait du pom.xml)
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <javafx.version>17.0.10</javafx.version>
    <javafx.platform>win</javafx.platform>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-maven-plugin</artifactId>
            <version>0.0.8</version>
            <configuration>
                <mainClass>com.avec.MainApp</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 3. Architecture du Projet

### 3.1 Pattern Architectural : MVC + Services/DAO

L'application suit une architecture en couches :

```
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE PRÉSENTATION (VIEW)              │
│  LoginView, DashboardView, AdminDashboardView, etc.        │
│  - Interface utilisateur JavaFX                            │
│  - Gestion des événements (ActionEvent)                    │
│  - Validation de saisie                                    │
└─────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE MÉTIER (SERVICE)                │
│  MembreService, AvecService, PretService, etc.            │
│  - Logique métier (règles de gestion)                    │
│  - Validation des données                                 │
│  - Calculs (épargne, intérêts, capacité d'emprunt)     │
└─────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE ACCÈS DONNÉES (DAO)             │
│  MembreDAO, AvecDAO, PretDAO, etc.                        │
│  - Requêtes SQL (CRUD)                                   │
│  - Mapping ResultSet ↔ Objects                             │
│  - Gestion des connexions                                  │
└─────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    BASE DE DONNÉES                         │
│                        MySQL                               │
│  Tables: utilisateur, membre, avec, pret, remboursement  │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 Packages et Responsabilités

```
com.avec/
├── MainApp.java                    # Point d'entrée JavaFX
├── config/
│   ├── DBConnection.java          # Gestion connexion MySQL
│   └── Styles.java              # Constantes de style CSS
├── model/                        # Entités métier (POJO)
│   ├── Membre.java              # Membre d'une AVEC
│   ├── Avec.java                # Association Villageoise
│   ├── Pret.java                # Prêt accordé
│   ├── AchatPart.java           # Achat de parts sociales
│   ├── Remboursement.java       # Remboursement de prêt
│   ├── Caisse.java              # Caisse de l'AVEC
│   ├── Comptage.java            # Comptage des fonds
│   ├── Reunion.java             # Réunion de l'AVEC
│   ├── Presence.java             # Présence aux réunions
│   ├── Amende.java              # Amendes infligées
│   ├── ProcesVerbal.java        # Procès-verbal de réunion
│   ├── Cycle.java               # Cycle de développement
│   ├── Visite.java              # Visite de formation (15 modules)
│   ├── AgentVillageois.java     # Agent formateur
│   ├── AgentTerrain.java        # Agent superviseur
│   ├── Utilisateur.java        # Compte utilisateur
│   └── SessionUtilisateur.java # Session utilisateur connecté
├── enums/                       # Énumérations
│   ├── RoleComite.java         # AUCUN, PRESIDENT, SECRETAIRE, TRESORIER, COMPTEUR
│   ├── RoleDetenteurCle.java   # AUCUN, GARDIEN_CLE_1,2,3
│   ├── StatutMembre.java       # ACTIF, INACTIF
│   ├── StatutPret.java         # EN_ATTENTE, ACTIF, REMBOURSE, EN_RETARD, REJETE
│   ├── StatutAvec.java         # EN_FORMATION, ACTIVE, EN_PAUSE, TERMINE
│   ├── PhaseCycle.java         # PREPARATOIRE, INTENSIVE, DEVELOPPEMENT, MATURITE, TERMINE
│   ├── TypeAchatPart.java      # NORMAL, SOLIDARITE
│   ├── JourReunion.java        # LUNDI, MARDI, ..., DIMANCHE
│   └── ModuleFormation.java    # Les 15 modules de formation
├── dao/                         # Data Access Objects
│   ├── MembreDAO.java         # Requêtes SQL pour les membres
│   ├── AvecDAO.java           # Requêtes SQL pour les AVEC
│   ├── PretDAO.java           # Requêtes SQL pour les prêts
│   ├── AchatPartDAO.java      # Requêtes SQL pour les achats de parts
│   ├── RemboursementDAO.java  # Requêtes SQL pour les remboursements
│   ├── CaisseDAO.java         # Requêtes SQL pour la caisse
│   ├── ReunionDAO.java        # Requêtes SQL pour les réunions
│   ├── AgentVillageoisDao.java
│   ├── AgentTerrainDao.java
│   └── UtilisateurDao.java
├── service/                     # Logique métier
│   ├── MembreService.java     # Création, élection comité, gardiens
│   ├── AvecService.java       # Gestion cycles, phases, statistiques
│   ├── PretService.java       # Demandes, approbations, calculs
│   ├── CaisseService.java     # Gestion caisse, décaissements
│   ├── ComptageService.java   # Comptage des fonds
│   ├── ReunionService.java     # Gestion des réunions
│   └── UtilisateurService.java
├── view/                        # Interfaces graphiques
│   ├── LoginView.java         # Connexion (7 rôles)
│   ├── DashboardView.java     # Redirection vers le dashboard approprié
│   ├── AdminDashboardView.java        # Gestion globale
│   ├── PresidentDashboardView.java   # Gestion membre, comité, prêts
│   ├── SecretaireDashboardView.java  # Présences, PV, historique
│   ├── TresorierDashboardView.java  # Caisse, décaissements, rapports
│   ├── CompteurDashboardView.java   # Comptage fonds, vérification
│   ├── AgentVillageoisDashboardView.java
│   ├── AgentTerrainDashboardView.java
│   ├── MembreView.java        # CRUD membres
│   ├── AvecView.java          # CRUD AVEC
│   ├── PretView.java          # Gestion prêts
│   ├── CaisseView.java        # Gestion caisse
│   ├── ReunionView.java       # Gestion réunions
│   ├── AchatPartView.java     # Achats de parts
│   └── dialogs/
│       └── AvecDialog.java    # Dialogue création AVEC
└── utils/                       # Utilitaires
    ├── FormatUtils.java       # Formatage monnaie, dates
    ├── ValidationUtils.java   # Validations de saisie
    └── AlertUtils.java        # Boîtes de dialogue
```

---

## 4. Structure Détaillée du Code

### 4.1 Point d'Entrée : MainApp.java

```java
package com.avec;

import com.avec.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Système de Gestion AVEC");
        this.primaryStage.setResizable(true);
        
        // Afficher la vue de connexion
        showLoginView();
        
        primaryStage.show();
    }
    
    public void showLoginView() {
        LoginView loginView = new LoginView(this);
        Scene scene = new Scene(loginView.getRoot(), 400, 500);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
```

**Explication** : 
- `MainApp` étend `Application` (classe de base JavaFX)
- La méthode `start()` est le point d'entrée automatique
- `showLoginView()` crée la vue de connexion et l'affiche dans le `Stage` principal

---

## 5. Modèle de Données

### 5.1 Classe Membre (Extrait)

```java
package com.avec.model;

import com.avec.enums.RoleComite;
import com.avec.enums.RoleDetenteurCle;
import com.avec.enums.StatutMembre;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Membre {
    private Long id;
    private String nom;
    private String prenom;
    private String numeroCarte;  // Généré automatiquement: MEM-001-XXXX
    private StatutMembre estActif;
    private LocalDate dateAdhesion;
    private RoleComite roleComite;       // Président, Secrétaire, etc.
    private RoleDetenteurCle roleCle;    // Gardien de clé 1, 2 ou 3
    private BigDecimal totalEpargne;      // Calculé: nombreParts × prixPart
    private BigDecimal totalPretEnCours;  // Somme des prêts actifs
    private int nombreParts;              // Nombre total de parts achetées
    
    // Relations
    private Long avecId;                // AVEC de rattachement
    private Avec avec;
    private List<AchatPart> achatsParts; // Historique des achats
    private List<Pret> prets;            // Historique des prêts

    /**
     * Calcule la capacité d'emprunt (3 × épargne)
     * Règle métier: Un membre peut emprunter jusqu'à 3 fois son épargne
     */
    public BigDecimal calculerCapaciteEmprunt() {
        return getTotalEpargne().multiply(BigDecimal.valueOf(3));
    }

    /**
     * Vérifie si le membre peut emprunter un montant donné
     */
    public boolean peutEmprunter(BigDecimal montant) {
        return montant.compareTo(calculerCapaciteEmprunt()) <= 0;
    }
}
```

### 5.2 Classe Avec (Association)

```java
public class Avec {
    private Long id;
    private String nom;
    private String codeUnique;           // Généré: AVEC-2026-XXXX
    private PhaseCycle phaseCourante;     // PREPARATOIRE → INTENSIVE → ...
    private BigDecimal prixPart;          // Prix d'une part sociale
    private int nombreMembresMax;        // Entre 15 et 30
    private BigDecimal tauxFraisServiceMensuel; // Entre 5% et 10%
    
    // Cycle de formation (15 visites obligatoires)
    private List<Visite> visites;
    
    // Statistiques calculées
    private int nombreMembresActifs;
    private BigDecimal totalEpargne;
    private BigDecimal totalCredit;
    private int progressionFormation;      // En pourcentage
    
    /**
     * Vérifie si l'AVEC peut passer à la phase suivante
     * Règles: PREPARATOIRE(1 visite), INTENSIVE(10), DEVELOPPEMENT(13), MATURITE(15)
     */
    public boolean peutPasserPhaseSuivante() {
        if (visites == null) return false;
        return switch (phaseCourante) {
            case PREPARATOIRE -> visites.size() >= 1;
            case INTENSIVE -> visites.size() >= 10;
            case DEVELOPPEMENT -> visites.size() >= 13;
            case MATURITE -> visites.size() >= 15;
            default -> false;
        };
    }
}
```

### 5.3 Classe Pret (Prêt)

```java
public class Pret {
    private Long id;
    private String numeroPret;           // Généré: PRT-20260428-XXXX
    private BigDecimal montantInitial;     // Montant demandé
    private BigDecimal montantRestantDu;   // Principal + intérêts - remboursé
    private int dureeEnSemaines;          // Durée de remboursement
    private StatutPret statut;            // EN_ATTENTE, ACTIF, REMBOURSE...
    
    /**
     * Calcule le montant total dû (principal + intérêts)
     * Les intérêts = fraisServiceMensuel × nombre de mois
     */
    public BigDecimal getMontantTotalDu() {
        if (montantInitial == null || fraisServiceMensuel == null) 
            return BigDecimal.ZERO;
        
        int nombreMois = (int) Math.ceil(dureeEnSemaines / 4.0);
        BigDecimal totalInterets = fraisServiceMensuel.multiply(
            BigDecimal.valueOf(nombreMois));
        
        return montantInitial.add(totalInterets);
    }
}
```

---

## 6. Explications du Code

### 6.1 Couche DAO (Data Access Object)

La classe `MembreDAO` gère toutes les interactions avec la base de données pour l'entité `Membre`.

```java
public class MembreDAO {
    
    /**
     * Insère un nouveau membre dans la base
     * Utilise Statement.RETURN_GENERATED_KEYS pour récupérer l'ID auto-généré
     */
    public Membre insert(Membre membre) throws SQLException {
        String sql = "INSERT INTO membre (nom, prenom, numeroCarte, estActif, " +
                    "dateAdhesion, avec_id, roleComite, roleCle) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                                         Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getPrenom());
            stmt.setString(3, membre.getNumeroCarte());
            stmt.setBoolean(4, membre.getEstActif() == StatutMembre.ACTIF);
            stmt.setDate(5, Date.valueOf(membre.getDateAdhesion()));
            stmt.setLong(6, membre.getAvecId());
            stmt.setString(7, membre.getRoleComite().name());
            stmt.setString(8, membre.getRoleCle().name());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création du membre a échoué");
            }
            
            // Récupération de l'ID généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    membre.setId(generatedKeys.getLong(1));
                }
            }
        }
        return membre;
    }
    
    /**
     * Mapping ResultSet → Objet Membre
     * Convertit les données SQL en objets Java
     */
    private Membre mapResultSetToMembre(ResultSet rs) throws SQLException {
        Membre membre = new Membre();
        membre.setId(rs.getLong("id"));
        membre.setNom(rs.getString("nom"));
        membre.setPrenom(rs.getString("prenom"));
        
        // Conversion boolean (SQL) → Enum (Java)
        boolean estActif = rs.getBoolean("estActif");
        membre.setEstActif(estActif ? StatutMembre.ACTIF : StatutMembre.INACTIF);
        
        // Conversion String (SQL) → Enum (Java)
        String roleComiteStr = rs.getString("roleComite");
        if (roleComiteStr != null) {
            membre.setRoleComite(RoleComite.valueOf(roleComiteStr));
        }
        
        return membre;
    }
}
```

### 6.2 Couche Service (Logique Métier)

Le `MembreService` contient la logique métier pour les membres.

```java
public class MembreService {
    private final MembreDAO membreDAO;
    private final AvecDAO avecDAO;
    private final UtilisateurDao utilisateurDao;
    
    /**
     * Crée un nouveau membre avec son compte utilisateur
     * Règles métier:
     * 1. Vérifie que l'AVEC existe
     * 2. Vérifie que le nombre max de membres n'est pas atteint
     * 3. Génère un numéro de carte unique
     * 4. Crée l'utilisateur ET le membre (transaction manuelle)
     */
    public Membre creerMembre(String nom, String prenom, Long avecId,
                              String motDePasse, String telephone)
            throws SQLException, IllegalArgumentException {
        
        // Validations
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        
        // Vérifier que l'AVEC existe
        Avec avec = avecDAO.findById(avecId);
        if (avec == null) {
            throw new IllegalArgumentException("AVEC non trouvée");
        }
        
        // Vérifier capacité max (15-30 membres)
        int nombreMembresActifs = membreDAO.countActifsByAvecId(avecId);
        if (nombreMembresActifs >= avec.getNombreMembresMax()) {
            throw new IllegalArgumentException("Nombre maximum atteint");
        }
        
        // Générer numéro de carte unique
        String numeroCarte = genererNumeroCarte(avecId);
        
        // 1. Créer l'utilisateur (table utilisateur)
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(numeroCarte + "@membre.avec.com");
        utilisateur.setMotDePasse(motDePasse);
        utilisateur.setTelephone(telephone);
        utilisateurDao.ajouter(utilisateur);
        
        // 2. Créer le membre (table membre)
        Membre membre = new Membre();
        membre.setId(utilisateur.getId());  // Même ID (clé partagée)
        membre.setNom(nom);
        membre.setPrenom(prenom);
        membre.setNumeroCarte(numeroCarte);
        membre.setAvecId(avecId);
        membre.setRoleComite(RoleComite.AUCUN);
        membre.setRoleCle(RoleDetenteurCle.AUCUN);
        
        return membreDAO.insert(membre);
    }
    
    /**
     * Organise l'élection du comité (5 postes)
     * Règle: Président, Secrétaire, Trésorier, Compteur + 1 membre
     */
    public boolean organiserElection(long avecId, List<ResultatElection> resultats) 
            throws SQLException {
        if (resultats.size() != 5) {
            throw new IllegalArgumentException("Le comité doit avoir 5 membres");
        }
        
        // Réinitialiser tous les rôles
        membreDAO.resetAllRolesComite(avecId);
        
        // Assigner les nouveaux rôles
        for (ResultatElection resultat : resultats) {
            Membre membre = membreDAO.findById(resultat.getMembreId());
            if (!membre.isEligibleComite()) {
                throw new IllegalStateException("Membre non éligible");
            }
            membre.setRoleComite(resultat.getRole());
            membreDAO.updateRoleComite(membre.getId(), resultat.getRole());
        }
        return true;
    }
}
```

### 6.3 Couche Vue (Interface Graphique)

Exemple de création d'un tableau de bord avec JavaFX :

```java
public class AdminDashboardView {
    private TableView<Membre> membreTable;
    
    private VBox createMembresContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Barre d'outils
        HBox toolbar = new HBox(10);
        Button ajouterBtn = new Button("➕ Ajouter membre");
        ajouterBtn.setStyle(Styles.BOUTON_PRINCIPAL);
        ajouterBtn.setOnAction(e -> showAjoutMembre());
        
        toolbar.getChildren().addAll(ajouterBtn);
        
        // Tableau de données
        membreTable = new TableView<>();
        membreTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Définition des colonnes
        TableColumn<Membre, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Membre, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        
        TableColumn<Membre, String> colCarte = new TableColumn<>("N° Carte");
        colCarte.setCellValueFactory(new PropertyValueFactory<>("numeroCarte"));
        
        membreTable.getColumns().addAll(colId, colNom, colCarte);
        
        // Chargement des données
        chargerMembres();
        
        content.getChildren().addAll(toolbar, membreTable);
        return content;
    }
    
    private void chargerMembres() {
        try {
            List<Membre> membres = membreService.getAllMembres();
            membreTable.setItems(FXCollections.observableArrayList(membres));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres");
        }
    }
}
```

---

## 7. Système d'Authentification

### 7.1 Vue de Connexion (LoginView)

L'application gère 7 types d'utilisateurs avec des formulaires dynamiques :

```java
public class LoginView {
    private ComboBox<String> roleComboBox;
    private TextField emailField;
    private PasswordField passwordField;
    private TextField numeroCarteField;  // Spécifique aux membres
    
    private void changerFormulaire() {
        String role = roleComboBox.getValue();
        formContainer.getChildren().clear();
        
        switch (role) {
            case "Administrateur":
                formContainer.getChildren().add(createAdminForm());  // Email + MDP
                break;
            case "Président":
            case "Secrétaire":
            case "Trésorier":
            case "Compteur":
                formContainer.getChildren().add(createMembreForm()); // N°Carte + MDP
                break;
            case "Agent Villageois":
            case "Agent de Terrain":
                formContainer.getChildren().add(createAgentForm());   // Email + MDP
                break;
        }
    }
    
    private void loginMembreComite(Label messageLabel, String roleAttendu) {
        String numeroCarte = numeroCarteField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Recherche dans la base
        Membre membre = membreService.chercherParCarteEtMotDePasse(
            numeroCarte, password);
        
        if (membre == null) {
            messageLabel.setText("Numéro de carte ou mot de passe incorrect");
            return;
        }
        
        // Vérification du rôle (le membre doit avoir le rôle attendu)
        String roleMembre = membre.getRoleComite().getDescription();
        if (!roleMembre.equals(roleAttendu)) {
            messageLabel.setText("Vous n'avez pas le rôle " + roleAttendu);
            return;
        }
        
        // Connexion réussie → Stockage en session
        SessionUtilisateur.getInstance().connecterMembre(membre);
        
        // Redirection vers le dashboard approprié
        DashboardView dashboard = new DashboardView(mainApp, utilisateur);
        mainApp.getPrimaryStage().getScene().setRoot(dashboard.getRoot());
    }
}
```

### 7.2 Session Utilisateur

```java
public class SessionUtilisateur {
    private static SessionUtilisateur instance;
    private Utilisateur utilisateurConnecte;
    private Membre membreConnecte;
    private AgentVillageois agentVillageoisConnecte;
    private AgentTerrain agentTerrainConnecte;
    
    private SessionUtilisateur() {}
    
    public static SessionUtilisateur getInstance() {
        if (instance == null) {
            instance = new SessionUtilisateur();
        }
        return instance;
    }
    
    public void connecterMembre(Membre membre) {
        this.membreConnecte = membre;
        this.typeUtilisateur = "MEMBRE";
    }
    
    public String getNomUtilisateur() {
        if (membreConnecte != null) {
            return membreConnecte.getNomComplet();
        }
        // ... autres cas
        return "";
    }
}
```

---

## 8. Gestion des Rôles

### 8.1 Rôles et Permissions

| Rôle | Description | Permissions Principales |
|------|-------------|------------------------|
| **Administrateur** | Gestion globale | CRUD Utilisateurs, Agents, AVEC, Membres, Statistiques |
| **Président** | Chef de l'AVEC | Gestion membres, validation prêts, élection comité |
| **Secrétaire** | Administration | Présences, Procès-verbaux, historique parts |
| **Trésorier** | Finances | Caisse, décaissements, remboursements, rapports |
| **Compteur** | Contrôle | Comptage fonds, vérification carnets |
| **Agent Villageois** | Formateur | Création AVEC, formations, élection comité |
| **Agent Terrain** | Superviseur | Supervision AVEC, agents villageois, visites |

### 8.2 Énumération RoleComite

```java
public enum RoleComite {
    AUCUN("Membre simple"),
    PRESIDENT("Président"),
    SECRETAIRE("Secrétaire"),
    TRESORIER("Trésorier"),
    COMPTEUR("Compteur");
    
    private final String description;
    
    RoleComite(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
```

---

## 9. Fonctionnalités par Rôle

### 9.1 Administrateur
- **Tableau de bord** : Statistiques globales (nombre d'AVEC, membres, épargne totale)
- **Gestion Utilisateurs** : Ajout, modification, suppression
- **Gestion Agents** : Agents de terrain et villageois
- **Gestion AVEC** : Création, modification, suppression
- **Gestion Membres** : Vue globale de tous les membres

### 9.2 Président (Membre)
- **Gestion Membres** : Ajout, modification des membres de son AVEC
- **Comité** : Organisation des élections (5 postes)
- **Prêts** : Approbation ou rejet des demandes de prêts
- **Phases** : Validation des changements de phase (avec agent terrain)

### 9.3 Secrétaire (Membre)
- **Présences** : Enregistrement des présences aux réunions
- **Procès-Verbaux** : Rédaction et consultation des PV
- **Historique** : Suivi des achats de parts
- **Demandes Prêts** : Vue des demandes en attente

### 9.4 Trésorier (Membre)
- **Caisse** : Gestion de la caisse de l'AVEC
- **Décaissements** : Enregistrement des décaissements de prêts
- **Remboursements** : Suivi des remboursements
- **Rapports** : Génération de rapports financiers

### 9.5 Compteur (Membre)
- **Comptage** : Comptage physique des fonds
- **Vérification** : Vérification des carnets de cotisation
- **État Caisse** : Consultation de l'état de la caisse

### 9.6 Agent Villageois
- **Création AVEC** : Création de nouvelles AVEC
- **Formations** : Suivi des 15 modules de formation
- **Élection Comité** : Organisation des élections
- **Validation Phases** : Validation des changements de phase

### 9.7 Agent Terrain
- **Supervision** : Vue des AVEC supervisées
- **Agents Villageois** : Gestion des agents villageois sous sa responsabilité
- **Visites** : Planification et suivi des visites
- **Validation** : Validation finale des changements de phase

---

## 10. Base de Données

### 10.1 Schéma SQL (Principales Tables)

```sql
-- Table Utilisateur (comptes de connexion)
CREATE TABLE utilisateur (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    motDePasse VARCHAR(100) NOT NULL,
    telephone VARCHAR(20)
);

-- Table Agent Terrain
CREATE TABLE agent_terrain (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES utilisateur(id),
    -- autres champs spécifiques
);

-- Table Agent Villageois
CREATE TABLE agent_villageois (
    id BIGINT PRIMARY KEY,
    agent_terrain_id BIGINT,
    FOREIGN KEY (id) REFERENCES utilisateur(id),
    FOREIGN KEY (agent_terrain_id) REFERENCES agent_terrain(id)
);

-- Table AVEC
CREATE TABLE avec (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    code_unique VARCHAR(20) UNIQUE,
    statut VARCHAR(20) DEFAULT 'EN_FORMATION',
    phase_courante VARCHAR(20) DEFAULT 'PREPARATOIRE',
    prix_part DECIMAL(10,2),
    nombre_membres_max INT DEFAULT 25,
    taux_frais_service_mensuel DECIMAL(5,2) DEFAULT 10.00,
    agent_villageois_id BIGINT,
    agent_terrain_id BIGINT,
    FOREIGN KEY (agent_villageois_id) REFERENCES agent_villageois(id),
    FOREIGN KEY (agent_terrain_id) REFERENCES agent_terrain(id)
);

-- Table Membre
CREATE TABLE membre (
    id BIGINT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50),
    numero_carte VARCHAR(20) UNIQUE,
    estActif BOOLEAN DEFAULT TRUE,
    date_adhesion DATE,
    avec_id BIGINT,
    role_comite VARCHAR(20) DEFAULT 'AUCUN',
    role_cle VARCHAR(20) DEFAULT 'AUCUN',
    FOREIGN KEY (id) REFERENCES utilisateur(id),
    FOREIGN KEY (avec_id) REFERENCES avec(id)
);

-- Table Prêt
CREATE TABLE pret (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_pret VARCHAR(30) UNIQUE,
    montant_initial DECIMAL(15,2),
    montant_restant_du DECIMAL(15,2),
    duree_semaines INT,
    statut VARCHAR(20) DEFAULT 'EN_ATTENTE',
    emprunteur_id BIGINT,
    reunion_decaissement_id BIGINT,
    FOREIGN KEY (emprunteur_id) REFERENCES membre(id)
);

-- Table Achat Parts
CREATE TABLE achat_part (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    membre_id BIGINT,
    nombre_parts INT,
    montant_total DECIMAL(15,2),
    date_achat DATE,
    FOREIGN KEY (membre_id) REFERENCES membre(id)
);

-- Table Remboursement
CREATE TABLE remboursement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pret_id BIGINT,
    montant DECIMAL(15,2),
    date_remboursement DATE,
    reunion_id BIGINT,
    FOREIGN KEY (pret_id) REFERENCES pret(id)
);
```

### 10.2 Configuration de la Connexion (DBConnection.java)

```java
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_avec";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion à la base de données établie!");
            }
        } catch (Exception e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}
```

---

## 11. Guide d'Installation

### 11.1 Prérequis
- **JDK 17+** (avec support JavaFX)
- **MySQL 8.x** installé et démarré
- **Maven 3.x** installé
- **NetBeans** (ou tout autre IDE supportant Maven)

### 11.2 Étapes d'Installation

1. **Cloner le projet**
   ```bash
   git clone <url-du-repo>
   cd javaFx_project
   ```

2. **Créer la base de données MySQL**
   ```sql
   CREATE DATABASE gestion_avec;
   USE gestion_avec;
   -- Exécuter le script SQL fourni (tables.sql)
   ```

3. **Configurer la connexion**
   Ouvrir `src/main/java/com/avec/config/DBConnection.java`
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/gestion_avec";
   private static final String USER = "votre_user";
   private static final String PASSWORD = "votre_password";
   ```

4. **Compiler avec Maven**
   ```bash
   mvn clean compile
   ```

5. **Lancer l'application**
   ```bash
   mvn javafx:run
   ```

### 11.3 Premier Lancement
1. Créer un utilisateur Admin via MySQL :
   ```sql
   INSERT INTO utilisateur (nom, prenom, email, motDePasse) 
   VALUES ('Admin', 'System', 'admin@avec.com', 'admin123');
   ```
2. Lancer l'application et se connecter avec :
   - Email: `admin@avec.com`
   - Mot de passe: `admin123`

---

## 12. Annexes

### 12.1 Styles et Charte Graphique (Styles.java)

```java
public class Styles {
    // Couleurs de la charte
    public static final String VERT_PRINCIPAL = "#2E7D32";  // Vert pour la croissance
    public static final String BLEU_SECONDAIRE = "#1565C0"; // Bleu pour la confiance
    public static final String ACCENT_DORE = "#FFC107";     // Doré pour l'épargne
    public static final String GRIS_CLAIR = "#F5F5F5";     // Fond neutre
    
    // Style des boutons principaux
    public static final String BOUTON_PRINCIPAL = 
        "-fx-background-color: " + VERT_PRINCIPAL + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 8;" +
        "-fx-cursor: hand;";
    
    // Style des tableaux
    public static final String TABLEAU_ENTETE = 
        "-fx-background-color: " + VERT_PRINCIPAL + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-weight: bold;";
}
```

### 12.2 Utilitaires (FormatUtils.java)

```java
public class FormatUtils {
    
    /**
     * Formate un montant en monnaie (FCFA)
     */
    public static String formatCurrency(BigDecimal montant) {
        if (montant == null) return "0 FCA";
        return String.format("%,.0f FCA", montant);
    }
    
    /**
     * Formate une date (jj/mm/aaaa)
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
```

### 12.3 Règles Métier Importantes

1. **Capacité d'emprunt** : Maximum 3 × épargne totale
2. **Taux de frais** : Entre 5% et 10% par mois
3. **Nombre de membres** : Entre 15 et 30 par AVEC
4. **Comité** : Exactement 5 membres (Président, Secrétaire, Trésorier, Compteur + 1)
5. **Gardiens de clés** : Exactement 3, ne faisant pas partie du comité
6. **Formation** : 15 visites obligatoires avant passage en phase MATURITE
7. **Épargne** : Basée sur le nombre de parts × prix de la part

---

## Conclusion

Cette application JavaFX offre une solution complète pour la gestion des AVEC avec :
- Une architecture MVC+Services/DAO bien structurée
- Une gestion fine des rôles et permissions
- Des règles métier respectées (capacité d'emprunt, formation, comité)
- Une interface graphique moderne et intuitive
- Un système d'authentification multi-rôles
- Des statistiques et rapports financiers

**Développé avec JavaFX 17, MySQL 8 et Maven**
