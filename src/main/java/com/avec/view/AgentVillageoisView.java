package com.avec.view;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.AgentTerrain;
import com.avec.model.AgentVillageois;
import com.avec.model.Utilisateur;
import com.avec.service.AgentTerrainService;
import com.avec.service.AgentVillageoisService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.List;

public class AgentVillageoisView {
    
    private MainApp mainApp;
    private Utilisateur utilisateur;
    private AgentVillageoisService agentVillageoisService;
    private AgentTerrainService agentTerrainService;
    private VBox root;
    private TableView<AgentVillageois> table;
    private ObservableList<AgentVillageois> agentList;
    
    // Formulaire
    private TextField nomField;
    private TextField prenomField;
    private TextField emailField;
    private TextField telephoneField;
    private PasswordField passwordField;
    private ComboBox<AgentTerrain> agentTerrainComboBox;
    private Button saveButton;
    private Button cancelButton;
    
    private AgentVillageois agentEnCours;
    
    // Icônes
    private static final String ICONE_AJOUTER = "➕";
    private static final String ICONE_MODIFIER = "✏️";
    private static final String ICONE_SUPPRIMER = "🗑️";
    private static final String ICONE_ACTUALISER = "🔄";
    private static final String ICONE_RECHERCHER = "🔍";
    
    public AgentVillageoisView(MainApp mainApp, Utilisateur utilisateur) {
        this.mainApp = mainApp;
        this.utilisateur = utilisateur;
        this.agentVillageoisService = new AgentVillageoisService();
        this.agentTerrainService = new AgentTerrainService();
        createView();
        loadAgents();
        loadAgentTerrains();
    }
    
    private void createView() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + Styles.BLANC + ";");
        
        // En-tête
        HBox header = createHeader();
        
        // Barre d'outils
        HBox toolbar = createToolbar();
        
        // Tableau des agents villageois
        table = createTable();
        
        // Formulaire
        VBox form = createForm();
        
        root.getChildren().addAll(header, toolbar, table, form);
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 10, 0));
        header.setStyle("-fx-border-color: " + Styles.GRIS_CLAIR + "; -fx-border-width: 0 0 2 0;");
        
        Label iconLabel = new Label("🌾");
        iconLabel.setStyle("-fx-font-size: 32px; -fx-padding: 0 10 0 0;");
        
        Label titleLabel = new Label("Gestion des Agents Villageois");
        titleLabel.setStyle(Styles.TITRE_PRINCIPAL);
        
        Label countLabel = new Label();
        countLabel.setStyle("-fx-background-color: " + Styles.BLEU_SECONDAIRE + ";" +
                "-fx-text-fill: white;" +
                "-fx-padding: 5 10;" +
                "-fx-background-radius: 15;");
        countLabel.setText(agentVillageoisService.getNombreAgentVillageois() + " agents");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(iconLabel, titleLabel, spacer, countLabel);
        
        return header;
    }
    
    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10, 0, 10, 0));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        // Barre de recherche
        HBox searchBox = new HBox(5);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un agent...");
        searchField.setPrefWidth(300);
        searchField.setStyle(Styles.CHAMP_TEXTE);
        
        Button searchButton = new Button(ICONE_RECHERCHER);
        searchButton.setStyle(Styles.BOUTON_PRINCIPAL);
        searchButton.setOnAction(e -> rechercherAgents(searchField.getText()));
        
        searchBox.getChildren().addAll(searchField, searchButton);
        
        // Boutons d'action
        Button addButton = new Button(ICONE_AJOUTER + " Ajouter");
        addButton.setStyle(Styles.BOUTON_PRINCIPAL);
        addButton.setOnMouseEntered(e -> addButton.setStyle(Styles.BOUTON_PRINCIPAL_HOVER));
        addButton.setOnMouseExited(e -> addButton.setStyle(Styles.BOUTON_PRINCIPAL));
        addButton.setOnAction(e -> nouvelAgent());
        
        Button editButton = new Button(ICONE_MODIFIER + " Modifier");
        editButton.setStyle(Styles.BOUTON_SECONDAIRE);
        editButton.setOnMouseEntered(e -> editButton.setStyle(Styles.BOUTON_SECONDAIRE_HOVER));
        editButton.setOnMouseExited(e -> editButton.setStyle(Styles.BOUTON_SECONDAIRE));
        editButton.setOnAction(e -> modifierAgent());
        
        Button deleteButton = new Button(ICONE_SUPPRIMER + " Supprimer");
        deleteButton.setStyle("-fx-background-color: " + Styles.ROUGE_ERREUR + "; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 8 15;");
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #B71C1C; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 8 15;"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: " + Styles.ROUGE_ERREUR + "; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 8 15;"));
        deleteButton.setOnAction(e -> supprimerAgent());
        
        Button refreshButton = new Button(ICONE_ACTUALISER + " Actualiser");
        refreshButton.setStyle(Styles.BOUTON_ACCENT);
        refreshButton.setOnMouseEntered(e -> refreshButton.setStyle("-fx-background-color: #FFA000; -fx-text-fill: " + Styles.NOIR + "; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 8 15;"));
        refreshButton.setOnMouseExited(e -> refreshButton.setStyle(Styles.BOUTON_ACCENT));
        refreshButton.setOnAction(e -> {
            loadAgents();
            searchField.clear();
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        toolbar.getChildren().addAll(searchBox, spacer, addButton, editButton, deleteButton, refreshButton);
        
        return toolbar;
    }
    
    private TableView<AgentVillageois> createTable() {
        TableView<AgentVillageois> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: " + Styles.BLANC + ";");
        
        // IMPORTANT: Ne pas ajouter de listener automatique ici
        // On utilisera le double-clic ou le bouton Modifier
        
        table.setRowFactory(tv -> {
            TableRow<AgentVillageois> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    AgentVillageois rowData = row.getItem();
                    agentEnCours = rowData;
                    afficherAgent(rowData);
                    passwordField.setDisable(true);
                    System.out.println("Double-clic: agentEnCours = " + (agentEnCours != null ? agentEnCours.getNom() : "null"));
                }
            });
            return row;
        });
        
        // Colonnes
        TableColumn<AgentVillageois, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(120);
        
        TableColumn<AgentVillageois, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(120);
        
        TableColumn<AgentVillageois, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);
        
        TableColumn<AgentVillageois, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colTelephone.setPrefWidth(120);
        
        TableColumn<AgentVillageois, String> colAgentTerrain = new TableColumn<>("Agent Terrain");
        colAgentTerrain.setCellValueFactory(cellData -> {
            AgentVillageois agent = cellData.getValue();
            if (agent != null && agent.getAgentTerrain() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    agent.getAgentTerrain().getPrenom() + " " + agent.getAgentTerrain().getNom()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colAgentTerrain.setPrefWidth(200);
        
        table.getColumns().addAll(colNom, colPrenom, colEmail, colTelephone, colAgentTerrain);
        
        return table;
    }
    
    private VBox createForm() {
        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";" +
                     "-fx-background-radius: 10;" +
                     "-fx-border-radius: 10;");
        
        Label formTitle = new Label("Formulaire Agent Villageois");
        formTitle.setStyle(Styles.TITRE_SECONDAIRE);
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 0, 10, 0));
        
        // Ligne 0 : Nom et Prénom
        Label nomLabel = new Label("Nom:");
        nomLabel.setStyle("-fx-font-weight: bold;");
        nomField = new TextField();
        nomField.setPromptText("Nom");
        nomField.setStyle(Styles.CHAMP_TEXTE);
        nomField.setPrefWidth(200);
        
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-weight: bold;");
        prenomField = new TextField();
        prenomField.setPromptText("Prénom");
        prenomField.setStyle(Styles.CHAMP_TEXTE);
        prenomField.setPrefWidth(200);
        
        grid.add(nomLabel, 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(prenomLabel, 2, 0);
        grid.add(prenomField, 3, 0);
        
        // Ligne 1 : Email et Téléphone
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-weight: bold;");
        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle(Styles.CHAMP_TEXTE);
        
        Label telephoneLabel = new Label("Téléphone:");
        telephoneLabel.setStyle("-fx-font-weight: bold;");
        telephoneField = new TextField();
        telephoneField.setPromptText("Téléphone");
        telephoneField.setStyle(Styles.CHAMP_TEXTE);
        
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(telephoneLabel, 2, 1);
        grid.add(telephoneField, 3, 1);
        
        // Ligne 2 : Mot de passe et Agent Terrain
        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setStyle(Styles.CHAMP_TEXTE);
        
        Label agentTerrainLabel = new Label("Agent Terrain:");
        agentTerrainLabel.setStyle("-fx-font-weight: bold;");
        agentTerrainComboBox = new ComboBox<>();
        agentTerrainComboBox.setPromptText("Sélectionner un agent terrain");
        agentTerrainComboBox.setStyle(Styles.CHAMP_TEXTE);
        agentTerrainComboBox.setPrefWidth(250);
        
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(agentTerrainLabel, 2, 2);
        grid.add(agentTerrainComboBox, 3, 2);
        
        // Boutons du formulaire
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        saveButton = new Button("💾 Enregistrer");
        saveButton.setStyle(Styles.BOUTON_PRINCIPAL);
        saveButton.setOnMouseEntered(e -> saveButton.setStyle(Styles.BOUTON_PRINCIPAL_HOVER));
        saveButton.setOnMouseExited(e -> saveButton.setStyle(Styles.BOUTON_PRINCIPAL));
        saveButton.setOnAction(e -> enregistrerAgent());
        
        cancelButton = new Button("❌ Annuler");
        cancelButton.setStyle(Styles.BOUTON_SECONDAIRE);
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(Styles.BOUTON_SECONDAIRE_HOVER));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(Styles.BOUTON_SECONDAIRE));
        cancelButton.setOnAction(e -> annulerFormulaire());
        
        buttonBox.getChildren().addAll(saveButton, cancelButton);
        
        form.getChildren().addAll(formTitle, grid, buttonBox);
        
        return form;
    }
    
    private void loadAgents() {
        List<AgentVillageois> agents = agentVillageoisService.listerAgentVillageois();
        agentList = FXCollections.observableArrayList(agents);
        table.setItems(agentList);
        
        // Mettre à jour le compteur
        HBox header = (HBox) root.getChildren().get(0);
        Label countLabel = (Label) header.getChildren().get(3);
        countLabel.setText(agentVillageoisService.getNombreAgentVillageois() + " agents");
    }
    
    private void loadAgentTerrains() {
        List<AgentTerrain> agents = agentTerrainService.listerAgentTerrain();
        agentTerrainComboBox.setItems(FXCollections.observableArrayList(agents));
    }
    
    private void rechercherAgents(String recherche) {
        if (recherche == null || recherche.trim().isEmpty()) {
            loadAgents();
            return;
        }
        
        String rechercheLower = recherche.toLowerCase().trim();
        List<AgentVillageois> tous = agentVillageoisService.listerAgentVillageois();
        List<AgentVillageois> filtres = tous.stream()
            .filter(a -> a.getNom().toLowerCase().contains(rechercheLower) ||
                         a.getPrenom().toLowerCase().contains(rechercheLower) ||
                         a.getEmail().toLowerCase().contains(rechercheLower) ||
                         (a.getTelephone() != null && a.getTelephone().contains(recherche)))
            .toList();
        
        table.setItems(FXCollections.observableArrayList(filtres));
    }
    
    private void nouvelAgent() {
        agentEnCours = new AgentVillageois();
        System.out.println("Nouvel agent créé: " + agentEnCours);
        
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        passwordField.clear();
        agentTerrainComboBox.setValue(null);
        passwordField.setDisable(false);
        
        table.getSelectionModel().clearSelection();
    }
    
    private void modifierAgent() {
        AgentVillageois selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            agentEnCours = selected;
            System.out.println("Agent sélectionné pour modification: " + agentEnCours.getNom());
            afficherAgent(selected);
            passwordField.setDisable(true);
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner un agent à modifier.");
        }
    }
    
    private void afficherAgent(AgentVillageois agent) {
        if (agent == null) {
            System.out.println("ERREUR: afficherAgent reçoit null");
            return;
        }
        
        agentEnCours = agent;
        System.out.println("Affichage agent: " + agent.getNom());
        
        nomField.setText(agent.getNom() != null ? agent.getNom() : "");
        prenomField.setText(agent.getPrenom() != null ? agent.getPrenom() : "");
        emailField.setText(agent.getEmail() != null ? agent.getEmail() : "");
        telephoneField.setText(agent.getTelephone() != null ? agent.getTelephone() : "");
        agentTerrainComboBox.setValue(agent.getAgentTerrain());
        passwordField.clear();
    }
    
    private void enregistrerAgent() {
        System.out.println("=== DÉBUT enregistrerAgent ===");
        System.out.println("agentEnCours = " + agentEnCours);
        
        if (agentEnCours == null) {
            System.out.println("ERREUR: agentEnCours est null!");
            showAlert("Erreur",
                    "Aucun agent villageois sélectionné. Veuillez cliquer sur 'Ajouter' ou sélectionner un agent dans la liste.");
            return;
        }
        
        System.out.println("ID agent = " + agentEnCours.getId());
        System.out.println("Nom agent = " + agentEnCours.getNom());
        
        // Validation des champs
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le nom est obligatoire");
            return;
        }
        if (prenomField.getText() == null || prenomField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le prénom est obligatoire");
            return;
        }
        if (emailField.getText() == null || emailField.getText().trim().isEmpty()) {
            showAlert("Erreur", "L'email est obligatoire");
            return;
        }
        
        AgentTerrain agentTerrainSelectionne = agentTerrainComboBox.getValue();
        if (agentTerrainSelectionne == null) {
            showAlert("Erreur", "Veuillez sélectionner un agent terrain");
            return;
        }
        
        if (agentEnCours.getId() == null && (passwordField.getText() == null || passwordField.getText().trim().isEmpty())) {
            showAlert("Erreur", "Le mot de passe est obligatoire pour un nouvel agent");
            return;
        }
        
        // Remplir l'agent villageois
        agentEnCours.setNom(nomField.getText().trim());
        agentEnCours.setPrenom(prenomField.getText().trim());
        agentEnCours.setEmail(emailField.getText().trim());
        agentEnCours.setTelephone(telephoneField.getText() != null ? telephoneField.getText().trim() : "");
        agentEnCours.setAgentTerrain(agentTerrainSelectionne);
        
        if (passwordField.getText() != null && !passwordField.getText().trim().isEmpty()) {
            agentEnCours.setMotDePasse(passwordField.getText().trim());
        }
        
        if (agentEnCours.getId() == null) {
            System.out.println("Tentative d'ajout d'un nouvel agent");
            if (agentVillageoisService.enregistrerAgentVillageois(agentEnCours)) {
                showInfo("Succès", "Agent villageois ajouté avec succès");
                loadAgents();
                annulerFormulaire();
            } else {
                showAlert("Erreur", "Échec de l'ajout de l'agent");
            }
        } else {
            System.out.println("Tentative de mise à jour de l'agent ID: " + agentEnCours.getId());
            if (agentVillageoisService.modifierAgentVillageois(agentEnCours)) {
                showInfo("Succès", "Agent villageois modifié avec succès");
                loadAgents();
                annulerFormulaire();
            } else {
                showAlert("Erreur", "Échec de la modification");
            }
        }
        System.out.println("=== FIN enregistrerAgent ===");
    }
    
    private void supprimerAgent() {
        AgentVillageois selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner un agent à supprimer.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'agent");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNomComplet() + " ?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (agentVillageoisService.supprimerAgentVillageois(selected.getId())) {
                    showInfo("Succès", "Agent supprimé avec succès");
                    loadAgents();
                    annulerFormulaire();
                } else {
                    showAlert("Erreur", "Échec de la suppression");
                }
            }
        });
    }
    
    private void annulerFormulaire() {
        agentEnCours = null;
        System.out.println("Formulaire annulé, agentEnCours = null");
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        passwordField.clear();
        agentTerrainComboBox.setValue(null);
        passwordField.setDisable(false);
        table.getSelectionModel().clearSelection();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public VBox getRoot() {
        return root;
    }
}