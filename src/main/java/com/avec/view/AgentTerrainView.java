package com.avec.view;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.AgentTerrain;
import com.avec.model.Utilisateur;
import com.avec.service.AgentTerrainService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.List;

public class AgentTerrainView {
    
    private MainApp mainApp;
    private Utilisateur utilisateur;
    private AgentTerrainService agentTerrainService;
    private VBox root;
    private TableView<AgentTerrain> table;
    private ObservableList<AgentTerrain> agentList;
    
    // Formulaire
    private TextField nomField;
    private TextField prenomField;
    private TextField emailField;
    private TextField telephoneField;
    private PasswordField passwordField;
    private Button saveButton;
    private Button cancelButton;
    
    private AgentTerrain agentEnCours;
    
    // Icônes
    private static final String ICONE_AJOUTER = "➕";
    private static final String ICONE_MODIFIER = "✏️";
    private static final String ICONE_SUPPRIMER = "🗑️";
    private static final String ICONE_ACTUALISER = "🔄";
    private static final String ICONE_RECHERCHER = "🔍";
    
    public AgentTerrainView(MainApp mainApp, Utilisateur utilisateur) {
        this.mainApp = mainApp;
        this.utilisateur = utilisateur;
        this.agentTerrainService = new AgentTerrainService();
        createView();
        loadAgents();
    }
    
    private void createView() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + Styles.BLANC + ";");
        
        // En-tête
        HBox header = createHeader();
        
        // Barre d'outils
        HBox toolbar = createToolbar();
        
        // Tableau des agents terrain
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
        
        Label iconLabel = new Label("🏞️");
        iconLabel.setStyle("-fx-font-size: 32px; -fx-padding: 0 10 0 0;");
        
        Label titleLabel = new Label("Gestion des Agents Terrain");
        titleLabel.setStyle(Styles.TITRE_PRINCIPAL);
        
        Label countLabel = new Label();
        countLabel.setStyle("-fx-background-color: " + Styles.VERT_PRINCIPAL + ";" +
                          "-fx-text-fill: white;" +
                          "-fx-padding: 5 10;" +
                          "-fx-background-radius: 15;");
        countLabel.setText(agentTerrainService.getNombreAgentTerrain() + " agents");
        
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
    
    private TableView<AgentTerrain> createTable() {
        TableView<AgentTerrain> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: " + Styles.BLANC + ";");
        table.setRowFactory(tv -> {
            TableRow<AgentTerrain> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    AgentTerrain rowData = row.getItem();
                    afficherAgent(rowData);
                }
            });
            return row;
        });
        
        // Colonnes
        TableColumn<AgentTerrain, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(150);
        
        TableColumn<AgentTerrain, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(150);
        
        TableColumn<AgentTerrain, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(250);
        
        TableColumn<AgentTerrain, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colTelephone.setPrefWidth(150);
        
        table.getColumns().addAll(colNom, colPrenom, colEmail, colTelephone);
        
        return table;
    }
    
    private VBox createForm() {
        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";" +
                     "-fx-background-radius: 10;" +
                     "-fx-border-radius: 10;");
        
        Label formTitle = new Label("Formulaire Agent Terrain");
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
        emailField.setPrefWidth(250);
        
        Label telephoneLabel = new Label("Téléphone:");
        telephoneLabel.setStyle("-fx-font-weight: bold;");
        telephoneField = new TextField();
        telephoneField.setPromptText("Téléphone");
        telephoneField.setStyle(Styles.CHAMP_TEXTE);
        telephoneField.setPrefWidth(150);
        
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(telephoneLabel, 2, 1);
        grid.add(telephoneField, 3, 1);
        
        // Ligne 2 : Mot de passe
        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setStyle(Styles.CHAMP_TEXTE);
        passwordField.setPrefWidth(200);
        
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        
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
        List<AgentTerrain> agents = agentTerrainService.listerAgentTerrain();
        agentList = FXCollections.observableArrayList(agents);
        table.setItems(agentList);
        
        // Mettre à jour le compteur
        HBox header = (HBox) root.getChildren().get(0);
        Label countLabel = (Label) header.getChildren().get(3);
        countLabel.setText(agentTerrainService.getNombreAgentTerrain() + " agents");
    }
    
    private void rechercherAgents(String recherche) {
        if (recherche == null || recherche.trim().isEmpty()) {
            loadAgents();
            return;
        }
        
        String rechercheLower = recherche.toLowerCase().trim();
        List<AgentTerrain> tous = agentTerrainService.listerAgentTerrain();
        List<AgentTerrain> filtres = tous.stream()
            .filter(a -> a.getNom().toLowerCase().contains(rechercheLower) ||
                         a.getPrenom().toLowerCase().contains(rechercheLower) ||
                         a.getEmail().toLowerCase().contains(rechercheLower) ||
                         (a.getTelephone() != null && a.getTelephone().contains(recherche)))
            .toList();
        
        table.setItems(FXCollections.observableArrayList(filtres));
    }
    
    private void nouvelAgent() {
        agentEnCours = new AgentTerrain();
        afficherAgent(agentEnCours);
        passwordField.setDisable(false);
    }
    
    private void modifierAgent() {
        AgentTerrain selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            afficherAgent(selected);
            passwordField.setDisable(true); // Ne pas modifier le mot de passe
        } else {
            showAlert("Sélection required", "Veuillez sélectionner un agent à modifier.");
        }
    }
    
    private void afficherAgent(AgentTerrain agent) {
        agentEnCours = agent;
        
        nomField.setText(agent.getNom() != null ? agent.getNom() : "");
        prenomField.setText(agent.getPrenom());
        emailField.setText(agent.getEmail());
        telephoneField.setText(agent.getTelephone());
        passwordField.clear();
    }
    
    private void enregistrerAgent() {
    	
    	if (agentEnCours == null) {
    	    showAlert("Erreur", "Aucun agent sélectionné...");
    	    return;
    	}
        // Validation
        if (nomField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le nom est obligatoire");
            return;
        }
        if (prenomField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le prénom est obligatoire");
            return;
        }
        if (emailField.getText().trim().isEmpty()) {
            showAlert("Erreur", "L'email est obligatoire");
            return;
        }
        if (agentEnCours.getId() == null && passwordField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le mot de passe est obligatoire pour un nouvel agent");
            return;
        }
        
        // Remplir l'agent
        agentEnCours.setNom(nomField.getText().trim());
        agentEnCours.setPrenom(prenomField.getText().trim());
        agentEnCours.setEmail(emailField.getText().trim());
        agentEnCours.setTelephone(telephoneField.getText().trim());
        
        if (!passwordField.getText().trim().isEmpty()) {
            agentEnCours.setMotDePasse(passwordField.getText().trim());
        }
        
        if (agentEnCours.getId() == null) {
            // Nouvel agent
            if (agentTerrainService.enregistrerAgentTerrain(agentEnCours)) {
                showInfo("Succès", "Agent terrain ajouté avec succès");
                loadAgents();
                annulerFormulaire();
            } else {
                showAlert("Erreur", "Échec de l'ajout de l'agent");
            }
        } else {
            // Mise à jour
            if (agentTerrainService.modifierAgentTerrain(agentEnCours)) {
                showInfo("Succès", "Agent terrain modifié avec succès");
                loadAgents();
                annulerFormulaire();
            } else {
                showAlert("Erreur", "Échec de la modification");
            }
        }
    }
    
    private void supprimerAgent() {
        AgentTerrain selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection required", "Veuillez sélectionner un agent à supprimer.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'agent");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNomComplet() + " ?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (agentTerrainService.supprimerAgentTerrain(selected.getId())) {
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
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        passwordField.clear();
        passwordField.setDisable(false);
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