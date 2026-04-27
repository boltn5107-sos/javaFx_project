# Documentation Technique - Gestion AVEC

## Table des Matières
1. Présentation du Projet
2. Technologies Utilisées
3. Architecture
4. Modèle de Données
5. Exemples de Code

---

## 1. Présentation du Projet

Application de gestion des **Associations Villageoises d'Épargne et de Crédit (AVEC)** développée en JavaFX. L'application permet de gérer l'ensemble des activités d'une AVEC :
- Gestion des membres et du comité
- Épargne et achat de parts
- Demandes et remboursements de prêts
- Comptage des fonds
- Suivi des formations

---

## 2. Technologies Utilisées

| Technologie | Version | Description |
|-------------|---------|-------------|
| **Java** | 17+ | Langage de programmation |
| **JavaFX** | 17.0.10 | Framework GUI |
| **MySQL** | 8.x | Base de données relationnelle |
| **Maven** | 3.x | Outil de build |

### Dépendances Maven (pom.xml)

```xml
<dependencies>
    <!-- JavaFX Controls -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.10</version>
    </dependency>
    
    <!-- MySQL Connector -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.3.0</version>
    </dependency>
</dependencies>
```

---

## 3. Architecture

### Modèle MVC + Services/DAO

```
┌─────────────────────────────────────────────────────────────┐
│                        VIEW (JavaFX)                        │
│  LoginView, DashboardView, AdminDashboardView, etc.        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      SERVICES                               │
│  MembreService, PretService, CaisseService, etc.           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         DAO                                 │
│  MembreDAO, PretDAO, CaisseDAO, etc.                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    BASE DE DONNÉES                          │
│                        MySQL                                │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. Modèle de Données

### Tables Principales

```sql
-- AVEC (Association)
CREATE TABLE avec (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    code_unique VARCHAR(20) UNIQUE,
    phase_courante VARCHAR(20),
    prix_part DECIMAL(10,2),
    nombre_membres_max INT
);

-- Membre
CREATE TABLE membre (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50),
    numero_carte VARCHAR(20),
    avec_id BIGINT,
    est_actif BOOLEAN DEFAULT TRUE,
    role_comite VARCHAR(20),
    role_cle VARCHAR(20)
);

-- Pret
CREATE TABLE pret (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emprunteur_id BIGINT,
    montant_initial DECIMAL(15,2),
    montant_restant DECIMAL(15,2),
    duree_semaines INT,
    statut VARCHAR(20)
);
```

---

## 5. Exemples de Code

### 5.1 Modèle (Membre.java)

```java
package com.avec.model;

import com.avec.enums.RoleComite;
import com.avec.enums.RoleDetenteurCle;
import com.avec.enums.StatutMembre;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Membre {
    private Long id;
    private String nom;
    private String prenom;
    private String numeroCarte;
    private StatutMembre estActif;
    private LocalDate dateAdhesion;
    private RoleComite roleComite;
    private RoleDetenteurCle roleCle;
    private BigDecimal totalEpargne;
    private int nombreParts;
    private String motDePasse;
    private Long avecId;
    
    public Membre() {
        this.estActif = StatutMembre.ACTIF;
        this.roleComite = RoleComite.AUCUN;
        this.roleCle = RoleDetenteurCle.AUCUN;
    }
    
    // Getters et Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomComplet() {
        return (prenom != null ? prenom + " " : "") + 
               (nom != null ? nom.toUpperCase() : "");
    }
}
```

### 5.2 DAO (MembreDAO.java)

```java
package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.model.Membre;
import com.avec.enums.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembreDAO {
    private Connection connection;
    
    public MembreDAO() {
        this.connection = DBConnection.getConnection();
    }
    
    public List<Membre> findByAvecId(Long avecId) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membre WHERE avec_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, avecId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(mapResultSetToMembre(rs));
                }
            }
        }
        return membres;
    }
    
    private Membre mapResultSetToMembre(ResultSet rs) throws SQLException {
        Membre membre = new Membre();
        membre.setId(rs.getLong("id"));
        membre.setNom(rs.getString("nom"));
        membre.setPrenom(rs.getString("prenom"));
        membre.setNumeroCarte(rs.getString("numero_carte"));
        boolean estActif = rs.getBoolean("estActif");
        membre.setEstActif(estActif ? StatutMembre.ACTIF : StatutMembre.INACTIF);
        return membre;
    }
}
```

### 5.3 Service (MembreService.java)

```java
package com.avec.service;

import com.avec.dao.MembreDAO;
import com.avec.model.Membre;
import java.sql.SQLException;
import java.util.List;

public class MembreService {
    private MembreDAO membreDAO;
    
    public MembreService() {
        this.membreDAO = new MembreDAO();
    }
    
    public List<Membre> getMembresByAvecId(Long avecId) throws SQLException {
        return membreDAO.findByAvecId(avecId);
    }
    
    public Membre getMembreById(Long id) throws SQLException {
        return membreDAO.findById(id);
    }
    
    public boolean modifierMembre(Membre membre) throws SQLException {
        return membreDAO.update(membre);
    }
    
    public int getNombreTotalMembres() throws SQLException {
        return membreDAO.countAll();
    }
}
```

### 5.4 Vue (Dashboard Admin)

```java
package com.avec.view;

import com.avec.MainApp;
import com.avec.model.*;
import com.avec.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdminDashboardView {
    private MainApp mainApp;
    private BorderPane root;
    private UtilisateurService utilisateurService;
    private AvecService avecService;
    private MembreService membreService;
    
    public AdminDashboardView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.utilisateurService = new UtilisateurService();
        this.avecService = new AvecService();
        this.membreService = new MembreService();
        createView();
    }
    
    private void createView() {
        root = new BorderPane();
        root.setTop(createHeader());
        root.setLeft(createSidebar());
        showDashboard();
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: white; -fx-padding: 15;");
        
        Label title = new Label("ADMINISTRATION");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button logoutBtn = new Button("Déconnexion");
        header.getChildren().addAll(title, spacer, logoutBtn);
        return header;
    }
}
```

### 5.5 Gestion des Prêts (Secrétaire)

```java
private void afficherDialogPaiementPret(Pret pret, TableView<Pret> table) {
    Dialog<Remboursement> dialog = new Dialog<>();
    dialog.setTitle("Enregistrer un remboursement");
    dialog.setHeaderText("Paiement du prêt #" + pret.getNumeroPret());

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(15);
    grid.setPadding(new Insets(20));

    Label lblEmprunteur = new Label("Emprunteur:");
    Label lblNomEmprunteur = new Label(pret.getEmprunteur().getNomComplet());
    
    Label lblRestant = new Label("Restant dû:");
    Label lblRestantValue = new Label(FormatUtils.formatCurrency(pret.getMontantRestantDu()));
    
    TextField montantField = new TextField();
    montantField.setText(pret.getMontantRestantDu().toString());
    
    ComboBox<Reunion> reunionCombo = new ComboBox<>();
    // Chargement des réunions...
    
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == saveButtonType) {
            BigDecimal montant = new BigDecimal(montantField.getText());
            // Enregistrement du remboursement
        }
        return null;
    });
}
```

### 5.6 Comptage des Fonds (Compteur)

```java
private void showComptage() {
    VBox view = new VBox(15);
    
    GridPane form = new GridPane();
    form.setHgap(15);
    form.setVgap(15);
    
    DatePicker datePicker = new DatePicker(LocalDate.now());
    
    TextField fondCreditField = new TextField();
    fondCreditField.setPromptText("Montant en FCA");
    
    TextField amendesField = new TextField();
    amendesField.setPromptText("Montant en FCA");
    
    Label totalValueLabel = new Label("0 FCA");
    
    // Calcul automatique du total
    fondCreditField.textProperty().addListener((obs, oldVal, newVal) -> {
        try {
            double fond = fondCreditField.getText().isEmpty() ? 0 : 
                Double.parseDouble(fondCreditField.getText());
            double amd = amendesField.getText().isEmpty() ? 0 : 
                Double.parseDouble(amendesField.getText());
            totalValueLabel.setText(String.format("%,.0f FCA", fond + amd));
        } catch (NumberFormatException e) {}
    });
    
    Button validerButton = new Button("Enregistrer le comptage");
    validerButton.setOnAction(e -> {
        Comptage comptage = new Comptage();
        comptage.setFondCredit(new BigDecimal(fondCreditField.getText()));
        comptage.setAmendes(new BigDecimal(amendesField.getText()));
        comptageService.save(comptage);
    });
}
```

---

## 6. Énumérations

### RoleComite.java

```java
package com.avec.enums;

public enum RoleComite {
    AUCUN("Membre"),
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

### StatutPret.java

```java
package com.avec.enums;

public enum StatutPret {
    EN_ATTENTE("En attente"),
    ACTIF("Actif"),
    REMBOURSE("Remboursé"),
    EN_RETARD("En retard"),
    REJETE("Rejeté");
    
    private final String libelle;
    
    StatutPret(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
```

---

## 7. Configuration Base de Données

### DBConnection.java

```java
package com.avec.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_avec";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

---

## 8. Structure des Packages

```
com.avec/
├── config/
│   ├── DBConnection.java
│   └── Styles.java
├── dao/
│   ├── MembreDAO.java
│   ├── PretDAO.java
│   ├── CaisseDAO.java
│   └── ...
├── enums/
│   ├── RoleComite.java
│   ├── StatutPret.java
│   └── PhaseCycle.java
├── model/
│   ├── Membre.java
│   ├── Pret.java
│   ├── Avec.java
│   └── ...
├── service/
│   ├── MembreService.java
│   ├── PretService.java
│   └── ...
├── utils/
│   ├── FormatUtils.java
│   └── ValidationUtils.java
└── view/
    ├── LoginView.java
    ├── AdminDashboardView.java
    ├── PresidentDashboardView.java
    └── ...
```

---

## 9. Conclusion

Ce projet JavaFXimplémente une solution complète pour la gestion des AVEC avec une architecture moderne MVC. L'application offre des interfaces graphiques intuitives pour chaque rôle d'utilisateur et gère l'ensemble des opérations financières et administratives.

**Développé avec JavaFX 17 et MySQL 8**