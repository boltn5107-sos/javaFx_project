package com.avec.view;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.enums.PhaseCycle;
import com.avec.enums.StatutAvec;
import com.avec.model.Avec;
import com.avec.model.AgentVillageois;
import com.avec.model.Utilisateur;
import com.avec.service.AvecService;
import com.avec.service.AgentVillageoisService;
import com.avec.utils.AlertUtils;
import com.avec.utils.FormatUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vue pour la gestion des AVEC
 */
public class AvecView {

    private MainApp mainApp;
    private Utilisateur utilisateur;
    private Stage primaryStage;
    private AvecService avecService;
    private AgentVillageoisService agentVillageoisService;
    private BorderPane root;
    private TableView<Avec> table;
    private ObservableList<Avec> avecList;

    // Formulaire
    private TextField nomField;
    private TextField prixPartField;
    private TextField tauxFraisField;
    private TextField nombreMembresField;
    private ComboBox<StatutAvec> statutCombo;
    private ComboBox<PhaseCycle> phaseCombo;
    private ComboBox<AgentVillageois> agentVillageoisCombo;

    private Button saveButton;
    private Button cancelButton;
    private VBox formContainer;
    private Dialog<ButtonType> avecDialog;

    private Avec avecEnCours;

    // Icônes
    private static final String ICONE_AJOUTER = "➕";
    private static final String ICONE_MODIFIER = "✏️";
    private static final String ICONE_SUPPRIMER = "🗑️";
    private static final String ICONE_ACTUALISER = "🔄";
    private static final String ICONE_RECHERCHER = "🔍";
    private static final String ICONE_CHANGER_PHASE = "🔄";

    public AvecView(MainApp mainApp, Utilisateur utilisateur) {
        this.mainApp = mainApp;
        this.utilisateur = utilisateur;
        this.primaryStage = mainApp.getPrimaryStage();
        this.avecService = new AvecService();
        this.agentVillageoisService = new AgentVillageoisService();
        createView();
        loadAvecs();
        loadAgentsVillageois();
    }

    /**
     * Crée la structure de la vue
     */
    private void createView() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + Styles.BLANC + ";");

        // Contenu principal
        VBox mainContent = createMainContent();
        root.setCenter(mainContent);
    }

    /**
     * Crée le contenu principal
     */
    private VBox createMainContent() {
        VBox mainContent = new VBox(10);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: " + Styles.BLANC + ";");

        // En-tête
        HBox header = createHeader();

        // Barre d'outils
        HBox toolbar = createToolbar();

        // Tableau des AVEC
        table = createTable();

        // Formulaire (sera affiché dans une Dialog)
        VBox form = createForm();

        // Stocker le formulaire pour la Dialog
        this.formContainer = form;

        mainContent.getChildren().addAll(header, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        return mainContent;
    }

    /**
     * Crée l'en-tête
     */
    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 10, 0));
        header.setStyle("-fx-border-color: " + Styles.GRIS_CLAIR + "; -fx-border-width: 0 0 2 0;");

        Label iconLabel = new Label("🏘️");
        iconLabel.setStyle("-fx-font-size: 32px; -fx-padding: 0 10 0 0;");

        Label titleLabel = new Label("Gestion des AVEC");
        titleLabel.setStyle(Styles.TITRE_PRINCIPAL);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statsBox = new HBox(10);

        Label totalLabel = new Label("Total: 0");
        totalLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15;");

        Label formationLabel = new Label("En formation: 0");
        formationLabel.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15;");

        Label activeLabel = new Label("Actives: 0");
        activeLabel.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15;");

        statsBox.getChildren().addAll(totalLabel, formationLabel, activeLabel);
        header.getChildren().addAll(iconLabel, titleLabel, spacer, statsBox);

        return header;
    }

    /**
     * Crée la barre d'outils
     */
    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10, 0, 10, 0));
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Barre de recherche
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher une AVEC...");
        searchField.setPrefWidth(300);
        searchField.setStyle(Styles.CHAMP_TEXTE);

        Button searchButton = new Button(ICONE_RECHERCHER);
        searchButton.setStyle(Styles.BOUTON_PRINCIPAL);
        searchButton.setOnAction(e -> rechercherAvecs(searchField.getText()));

        // Filtres
        ComboBox<StatutAvec> statutFilter = new ComboBox<>();
        statutFilter.setItems(FXCollections.observableArrayList(StatutAvec.values()));
        statutFilter.setPromptText("Filtrer par statut");
        statutFilter.setStyle(Styles.CHAMP_TEXTE);
        statutFilter.setOnAction(e -> filtrerParStatut(statutFilter.getValue()));

        ComboBox<PhaseCycle> phaseFilter = new ComboBox<>();
        phaseFilter.setItems(FXCollections.observableArrayList(PhaseCycle.values()));
        phaseFilter.setPromptText("Filtrer par phase");
        phaseFilter.setStyle(Styles.CHAMP_TEXTE);
        phaseFilter.setOnAction(e -> filtrerParPhase(phaseFilter.getValue()));

        // Boutons d'action
        Button addButton = new Button(ICONE_AJOUTER + " Ajouter");
        addButton.setStyle(Styles.BOUTON_PRINCIPAL);
        addButton.setOnAction(e -> afficherDialogAjouter());

        Button editButton = new Button(ICONE_MODIFIER + " Modifier");
        editButton.setStyle(Styles.BOUTON_SECONDAIRE);
        editButton.setOnAction(e -> afficherDialogModifier());

        Button phaseButton = new Button(ICONE_CHANGER_PHASE + " Changer phase");
        phaseButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: " + Styles.NOIR + "; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 8 15;");
        phaseButton.setOnAction(e -> changerPhase());

        Button deleteButton = new Button(ICONE_SUPPRIMER + " Supprimer");
        deleteButton.setStyle("-fx-background-color: " + Styles.ROUGE_ERREUR + "; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 8 15;");
        deleteButton.setOnAction(e -> supprimerAvec());

        Button refreshButton = new Button(ICONE_ACTUALISER + " Actualiser");
        refreshButton.setStyle(Styles.BOUTON_ACCENT);
        refreshButton.setOnAction(e -> {
            System.out.println(">>> REFRESH: Bouton actualiser clique");
            loadAvecs();
            searchField.clear();
            statutFilter.setValue(null);
            phaseFilter.setValue(null);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(
                searchField, searchButton, spacer,
                statutFilter, phaseFilter,
                addButton, editButton, phaseButton, deleteButton, refreshButton
        );

        return toolbar;
    }

    /**
     * Crée le tableau des AVEC
     */
    private TableView<Avec> createTable() {
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: " + Styles.BLANC + ";");

        // Double-clic pour modifier
        table.setRowFactory(tv -> {
            TableRow<Avec> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Avec rowData = row.getItem();
                    afficherAvec(rowData);
                    avecEnCours = rowData;
                }
            });
            return row;
        });

        // Colonnes
        TableColumn<Avec, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);

        TableColumn<Avec, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(200);

        TableColumn<Avec, String> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(new PropertyValueFactory<>("codeUnique"));
        colCode.setPrefWidth(150);

        TableColumn<Avec, StatutAvec> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setPrefWidth(120);
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(StatutAvec item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item.getLibelle());
                    String color = switch (item) {
                        case EN_FORMATION -> "#f39c12";
                        case ACTIVE -> "#27ae60";
                        case EN_PAUSE -> "#e74c3c";
                        case TERMINE, EN_DISSOLUTION -> "#95a5a6";
                    };
                    badge.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 3;");
                    setGraphic(badge);
                }
            }
        });

        TableColumn<Avec, PhaseCycle> colPhase = new TableColumn<>("Phase");
        colPhase.setCellValueFactory(new PropertyValueFactory<>("phaseCourante"));
        colPhase.setPrefWidth(120);
        colPhase.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(PhaseCycle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item.getLibelle());
                    badge.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 3;");
                    setGraphic(badge);
                }
            }
        });

        TableColumn<Avec, Integer> colMembres = new TableColumn<>("Membres max");
        colMembres.setCellValueFactory(new PropertyValueFactory<>("nombreMembresMax"));
        colMembres.setPrefWidth(100);
        colMembres.setStyle("-fx-alignment: CENTER;");

        TableColumn<Avec, BigDecimal> colPrixPart = new TableColumn<>("Prix part");
        colPrixPart.setCellValueFactory(new PropertyValueFactory<>("prixPart"));
        colPrixPart.setPrefWidth(120);
        colPrixPart.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(FormatUtils.formatCurrency(item));
                }
            }
        });

        TableColumn<Avec, BigDecimal> colTaux = new TableColumn<>("Taux frais");
        colTaux.setCellValueFactory(new PropertyValueFactory<>("tauxFraisServiceMensuel"));
        colTaux.setPrefWidth(100);
        colTaux.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item + "%");
                }
            }
        });

        TableColumn<Avec, LocalDate> colDateCreation = new TableColumn<>("Date création");
        colDateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        colDateCreation.setPrefWidth(120);
        colDateCreation.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });

        table.getColumns().addAll(colId, colNom, colCode, colStatut, colPhase,
                colMembres, colPrixPart, colTaux, colDateCreation);

        return table;
    }

    /**
     * Crée le formulaire
     */
    private VBox createForm() {
        VBox form = new VBox(15);
        form.setPadding(new Insets(10));
        form.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";" +
                "-fx-background-radius: 10;");

        Label formTitle = new Label("Formulaire AVEC");
        formTitle.setStyle(Styles.TITRE_SECONDAIRE);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 0, 10, 0));

        int row = 0;

        // Nom
        grid.add(new Label("Nom:"), 0, row);
        nomField = new TextField();
        nomField.setPromptText("Nom de l'AVEC");
        nomField.setStyle(Styles.CHAMP_TEXTE);
        nomField.setPrefWidth(300);
        grid.add(nomField, 1, row);
        row++;

        // Prix de la part
        grid.add(new Label("Prix de la part (FCFA):"), 0, row);
        prixPartField = new TextField();
        prixPartField.setPromptText("Prix de la part");
        prixPartField.setStyle(Styles.CHAMP_TEXTE);
        grid.add(prixPartField, 1, row);
        row++;

        // Taux des frais
        grid.add(new Label("Taux des frais (%):"), 0, row);
        tauxFraisField = new TextField();
        tauxFraisField.setPromptText("Taux (5-10%)");
        tauxFraisField.setText("10");
        tauxFraisField.setStyle(Styles.CHAMP_TEXTE);
        grid.add(tauxFraisField, 1, row);
        row++;

        // Nombre max de membres
        grid.add(new Label("Nombre max de membres:"), 0, row);
        nombreMembresField = new TextField();
        nombreMembresField.setPromptText("Entre 15 et 30");
        nombreMembresField.setText("25");
        nombreMembresField.setStyle(Styles.CHAMP_TEXTE);
        grid.add(nombreMembresField, 1, row);
        row++;

        // Statut
        grid.add(new Label("Statut:"), 0, row);
        statutCombo = new ComboBox<>();
        statutCombo.setItems(FXCollections.observableArrayList(StatutAvec.values()));
        statutCombo.setStyle(Styles.CHAMP_TEXTE);
        grid.add(statutCombo, 1, row);
        row++;

        // Phase
        grid.add(new Label("Phase:"), 0, row);
        phaseCombo = new ComboBox<>();
        phaseCombo.setItems(FXCollections.observableArrayList(PhaseCycle.values()));
        phaseCombo.setStyle(Styles.CHAMP_TEXTE);
        grid.add(phaseCombo, 1, row);

        // Agent Villageois
        grid.add(new Label("Agent Villageois:"), 0, row);
        agentVillageoisCombo = new ComboBox<>();
        agentVillageoisCombo.setStyle(Styles.CHAMP_TEXTE);
        agentVillageoisCombo.setPromptText("Sélectionner un agent");
        grid.add(agentVillageoisCombo, 1, row);
        row++;

        form.getChildren().addAll(formTitle, grid);

        return form;
    }

    /**
     * Affiche la dialog pour ajouter une nouvelle AVEC
     */
    private void afficherDialogAjouter() {
        avecEnCours = new Avec();
        viderFormulaire();

        avecDialog = new Dialog<>();
        avecDialog.setTitle("Nouvelle AVEC");
        avecDialog.setHeaderText("Créer une nouvelle AVEC");
        avecDialog.initOwner(primaryStage);
        avecDialog.getDialogPane().setContent(formContainer);

        // Boutons de la dialog
        ButtonType saveType = new ButtonType("💾 Enregistrer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("❌ Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        avecDialog.getDialogPane().getButtonTypes().addAll(saveType, cancelType);

        // Action du bouton Enregistrer
        Button saveButton = (Button) avecDialog.getDialogPane().lookupButton(saveType);
        saveButton.setOnAction(e -> {
            System.out.println(">>> DIALOG: Bouton Enregistrer clique!");
            enregistrerAvec();
            avecDialog.close();
        });

        // Action du bouton Annuler
        Button cancelButton = (Button) avecDialog.getDialogPane().lookupButton(cancelType);
        cancelButton.setOnAction(e -> {
            annulerFormulaire();
            avecDialog.close();
        });

        avecDialog.showAndWait();
        loadAvecs();
    }

    /**
     * Affiche la dialog pour modifier une AVEC
     */
    private void afficherDialogModifier() {
        Avec selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Sélection requise", "Veuillez sélectionner une AVEC à modifier.", null);
            return;
        }

        avecEnCours = selected;
        afficherAvec(selected);

        avecDialog = new Dialog<>();
        avecDialog.setTitle("Modifier AVEC");
        avecDialog.setHeaderText("Modifier: " + selected.getNom());
        avecDialog.initOwner(primaryStage);
        avecDialog.getDialogPane().setContent(formContainer);

        // Boutons de la dialog
        ButtonType saveType = new ButtonType("💾 Enregistrer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("❌ Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        avecDialog.getDialogPane().getButtonTypes().addAll(saveType, cancelType);

        // Action du bouton Enregistrer
        Button saveButton = (Button) avecDialog.getDialogPane().lookupButton(saveType);
        saveButton.setOnAction(e -> {
            System.out.println(">>> DIALOG: Bouton Enregistrer clique!");
            enregistrerAvec();
            avecDialog.close();
        });

        // Action du bouton Annuler
        Button cancelButton = (Button) avecDialog.getDialogPane().lookupButton(cancelType);
        cancelButton.setOnAction(e -> {
            annulerFormulaire();
            avecDialog.close();
        });

        avecDialog.showAndWait();
        loadAvecs();
    }

    /**
     * Charge la liste des AVEC
     */
    private void loadAvecs() {
        System.out.println(">>> LOAD: Debut loadAvecs()");
        try {
            List<Avec> avecs = avecService.getAllAvecs();
            System.out.println(">>> LOAD: " + avecs.size() + " AVEC trouvees");
            avecList = FXCollections.observableArrayList(avecs);
            table.setItems(avecList);
            System.out.println(">>> LOAD: Table mise a jour");
            mettreAJourStatistiques(avecs);
        } catch (SQLException e) {
            System.err.println(">>> LOAD ERROR: " + e.getMessage());
            AlertUtils.showError("Erreur", "Impossible de charger les AVEC", e.getMessage());
        }
    }

    /**
     * Charge la liste des agents villageois
     */
    private void loadAgentsVillageois() {
        try {
            List<AgentVillageois> agents = agentVillageoisService.listerAgentVillageois();
            agentVillageoisCombo.setItems(FXCollections.observableArrayList(agents));
        } catch (Exception e) {
            System.err.println("Erreur chargement agents villageois: " + e.getMessage());
        }
    }

    /**
     * Met à jour les statistiques
     */
    private void mettreAJourStatistiques(List<Avec> avecs) {
        VBox mainContent = (VBox) root.getCenter();
        HBox header = (HBox) mainContent.getChildren().get(0);
        HBox statsBox = (HBox) header.getChildren().get(3);
        Label totalLabel = (Label) statsBox.getChildren().get(0);
        Label formationLabel = (Label) statsBox.getChildren().get(1);
        Label activeLabel = (Label) statsBox.getChildren().get(2);

        long total = avecs.size();
        long enFormation = avecs.stream().filter(a -> a.getStatut() == StatutAvec.EN_FORMATION).count();
        long actives = avecs.stream().filter(a -> a.getStatut() == StatutAvec.ACTIVE).count();

        totalLabel.setText("Total: " + total);
        formationLabel.setText("En formation: " + enFormation);
        activeLabel.setText("Actives: " + actives);
    }

    /**
     * Recherche des AVEC
     */
    private void rechercherAvecs(String recherche) {
        if (recherche == null || recherche.trim().isEmpty()) {
            loadAvecs();
            return;
        }

        try {
            List<Avec> resultats = avecService.rechercherAvecs(recherche);
            table.setItems(FXCollections.observableArrayList(resultats));
        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Erreur lors de la recherche", e.getMessage());
        }
    }

    /**
     * Filtre par statut
     */
    private void filtrerParStatut(StatutAvec statut) {
        if (statut == null) {
            loadAvecs();
            return;
        }

        try {
            List<Avec> avecs = avecService.getAvecsByStatut(statut);
            table.setItems(FXCollections.observableArrayList(avecs));
        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Erreur lors du filtrage", e.getMessage());
        }
    }

    /**
     * Filtre par phase
     */
    private void filtrerParPhase(PhaseCycle phase) {
        if (phase == null) {
            loadAvecs();
            return;
        }

        try {
            List<Avec> avecs = avecService.getAvecsByPhase(phase);
            table.setItems(FXCollections.observableArrayList(avecs));
        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Erreur lors du filtrage", e.getMessage());
        }
    }

    /**
     * Prépare le formulaire pour un nouvel AVEC
     */
    private void nouvelAvec() {
        avecEnCours = new Avec();
        viderFormulaire();
        table.getSelectionModel().clearSelection();
    }

    /**
     * Affiche un AVEC dans le formulaire
     */
    private void afficherAvec(Avec avec) {
        nomField.setText(avec.getNom() != null ? avec.getNom() : "");
        prixPartField.setText(avec.getPrixPart() != null ? avec.getPrixPart().toString() : "");
        tauxFraisField.setText(avec.getTauxFraisServiceMensuel() != null ? avec.getTauxFraisServiceMensuel().toString() : "10");
        nombreMembresField.setText(String.valueOf(avec.getNombreMembresMax()));
        statutCombo.setValue(avec.getStatut());
        phaseCombo.setValue(avec.getPhaseCourante());

        // Sélectionner l'agent villageois
        if (avec.getAgentVillageoisId() != null) {
            for (AgentVillageois agent : agentVillageoisCombo.getItems()) {
                if (agent.getId().equals(avec.getAgentVillageoisId())) {
                    agentVillageoisCombo.setValue(agent);
                    break;
                }
            }
        }
    }

    /**
     * Vide le formulaire
     */
    private void viderFormulaire() {
        nomField.clear();
        prixPartField.clear();
        tauxFraisField.setText("10");
        nombreMembresField.setText("25");
        statutCombo.setValue(null);
        phaseCombo.setValue(null);
        agentVillageoisCombo.setValue(null);
    }

    /**
     * Enregistre l'AVEC
     */
    private void enregistrerAvec() {
        System.out.println(">>> DEBUG: Debut enregistrerAvec()");

        if (avecEnCours == null) {
            System.out.println(">>> DEBUG: avecEnCours est null!");
            AlertUtils.showError("Erreur", "Aucune AVEC sélectionnée", "Veuillez cliquer sur 'Ajouter' ou sélectionner une AVEC.");
            return;
        }

        System.out.println(">>> DEBUG: Validation des champs...");

        // Validation des champs
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            System.out.println(">>> DEBUG: Nom vide!");
            AlertUtils.showError("Erreur", "Nom obligatoire", "Le nom de l'AVEC est obligatoire.");
            return;
        }
        if (prixPartField.getText() == null || prixPartField.getText().trim().isEmpty()) {
            System.out.println(">>> DEBUG: Prix vide!");
            AlertUtils.showError("Erreur", "Prix de la part obligatoire", "Le prix de la part est obligatoire.");
            return;
        }

        try {
            BigDecimal prixPart = new BigDecimal(prixPartField.getText().trim());
            BigDecimal tauxFrais = new BigDecimal(tauxFraisField.getText().trim());
            int nombreMembresMax = Integer.parseInt(nombreMembresField.getText().trim());

            System.out.println(">>> DEBUG: prixPart=" + prixPart + ", tauxFrais=" + tauxFrais);

            // Validation des valeurs
            if (prixPart.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println(">>> DEBUG: Prix invalide!");
                AlertUtils.showError("Erreur", "Prix invalide", "Le prix de la part doit être positif.");
                return;
            }
            if (tauxFrais.compareTo(BigDecimal.valueOf(5)) < 0 || tauxFrais.compareTo(BigDecimal.valueOf(10)) > 0) {
                System.out.println(">>> DEBUG: Taux invalide!");
                AlertUtils.showError("Erreur", "Taux invalide", "Le taux des frais doit être entre 5% et 10%.");
                return;
            }
            if (nombreMembresMax < 15 || nombreMembresMax > 30) {
                System.out.println(">>> DEBUG: Nombre membres invalide!");
                AlertUtils.showError("Erreur", "Nombre de membres invalide", "Le nombre de membres doit être entre 15 et 30.");
                return;
            }

            // Remplir l'AVEC
            avecEnCours.setNom(nomField.getText().trim());
            avecEnCours.setPrixPart(prixPart);
            avecEnCours.setTauxFraisServiceMensuel(tauxFrais);
            avecEnCours.setNombreMembresMax(nombreMembresMax);

            if (avecEnCours.getId() == null) {
                // NOUVELLE AVEC
                System.out.println(">>> DEBUG: Creation nouvelle AVEC...");
                String codeUnique = "AVEC-" + System.currentTimeMillis();
                avecEnCours.setCodeUnique(codeUnique);
                avecEnCours.setDateCreation(LocalDate.now());
                avecEnCours.setPhaseCourante(PhaseCycle.PREPARATOIRE);
                avecEnCours.setStatut(StatutAvec.EN_FORMATION);

                AgentVillageois selectedAgent = agentVillageoisCombo.getValue();
                System.out.println(">>> DEBUG: Agent selectionne = " + selectedAgent);

                if (selectedAgent == null) {
                    System.out.println(">>> DEBUG: Agent villageois NULL!");
                    AlertUtils.showError("Erreur", "Agent villageois requis", "Veuillez sélectionner un agent villageois dans le menu.");
                    return;
                }
                avecEnCours.setAgentVillageoisId(selectedAgent.getId());

                System.out.println(">>> DEBUG: Appel avecService.creerAvec()...");
                Avec created = avecService.creerAvec(avecEnCours);
                System.out.println(">>> DEBUG: Resultat = " + created);

                if (created != null) {
                    System.out.println(">>> DEBUG: SUCCES!");
                    AlertUtils.showInfo("Succès", "AVEC créée", "Code: " + created.getCodeUnique());
                    loadAvecs();
                    annulerFormulaire();
                } else {
                    System.out.println(">>> DEBUG: ECHEC - created est null!");
                    AlertUtils.showError("Erreur", "Échec de la création", null);
                }
            } else {
                // MODIFICATION
                System.out.println(">>> DEBUG: Mode MODIFICATION");
                System.out.println(">>> DEBUG: avecEnCours.id = " + avecEnCours.getId());

                if (statutCombo.getValue() != null) {
                    avecEnCours.setStatut(statutCombo.getValue());
                }
                if (phaseCombo.getValue() != null) {
                    avecEnCours.setPhaseCourante(phaseCombo.getValue());
                }

                System.out.println(">>> DEBUG: Appel modifierAvec()...");
                boolean success = avecService.modifierAvec(avecEnCours);
                System.out.println(">>> DEBUG: Resultat = " + success);

                if (success) {
                    System.out.println(">>> DEBUG: SUCCES modification!");
                    AlertUtils.showInfo("Succès", "AVEC modifiée", null);
                    loadAvecs();
                    annulerFormulaire();
                } else {
                    System.out.println(">>> DEBUG: ECHEC modification!");
                    AlertUtils.showError("Erreur", "Échec de la modification", null);
                }
            }

        } catch (NumberFormatException e) {
            AlertUtils.showError("Erreur", "Format de nombre invalide", "Vérifiez les champs numériques.");
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("=== ERREUR D'ENREGISTREMENT ===");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Erreur d'enregistrement", e.getClass().getSimpleName(), e.getMessage());
        } catch (Exception e) {
            System.err.println("=== ERREUR INATTENDUE ===");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Erreur", "Erreur inattendue", e.getMessage());
        }
    }

    /**
     * Change la phase d'une AVEC
     */
    private void changerPhase() {
        Avec selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Sélection requise", "Veuillez sélectionner une AVEC.", null);
            return;
        }

        Dialog<PhaseCycle> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Changer de phase");
        dialog.setHeaderText("Changer la phase de: " + selected.getNom());

        ButtonType saveButtonType = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label phaseActuelle = new Label("Phase actuelle: " + selected.getPhaseCourante().getLibelle());
        phaseActuelle.setStyle("-fx-font-weight: bold;");

        ComboBox<PhaseCycle> phaseCombo = new ComboBox<>();
        phaseCombo.setItems(FXCollections.observableArrayList(PhaseCycle.values()));
        phaseCombo.setPromptText("Sélectionner la nouvelle phase");

        PhaseCycle courante = selected.getPhaseCourante();
        if (courante == PhaseCycle.PREPARATOIRE) {
            phaseCombo.setValue(PhaseCycle.INTENSIVE);
        } else if (courante == PhaseCycle.INTENSIVE) {
            phaseCombo.setValue(PhaseCycle.DEVELOPPEMENT);
        } else if (courante == PhaseCycle.DEVELOPPEMENT) {
            phaseCombo.setValue(PhaseCycle.MATURITE);
        } else if (courante == PhaseCycle.MATURITE) {
            phaseCombo.setValue(PhaseCycle.TERMINE);
        }

        content.getChildren().addAll(phaseActuelle, new Label("Nouvelle phase:"), phaseCombo);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return phaseCombo.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(nouvellePhase -> {
            if (nouvellePhase != null) {
                try {
                    if (avecService.changerPhase(selected.getId(), nouvellePhase)) {
                        selected.setPhaseCourante(nouvellePhase);
                        table.refresh();
                        AlertUtils.showInfo("Succès", "Phase changée", "Nouvelle phase: " + nouvellePhase.getLibelle());
                    }
                } catch (SQLException | IllegalStateException e) {
                    AlertUtils.showError("Erreur", "Impossible de changer la phase", e.getMessage());
                }
            }
        });
    }

    /**
     * Supprime une AVEC
     */
    private void supprimerAvec() {
        Avec selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Sélection requise", "Veuillez sélectionner une AVEC à supprimer.", null);
            return;
        }

        boolean confirm = AlertUtils.showConfirmation(
                "Confirmation",
                "Supprimer l'AVEC",
                "Êtes-vous sûr de vouloir supprimer " + selected.getNom() + " ?"
        );

        if (confirm) {
            try {
                if (avecService.supprimerAvec(selected.getId())) {
                    AlertUtils.showInfo("Succès", "AVEC supprimée", null);
                    loadAvecs();
                    annulerFormulaire();
                }
            } catch (SQLException | IllegalStateException e) {
                AlertUtils.showError("Erreur", "Impossible de supprimer", e.getMessage());
            }
        }
    }

    /**
     * Annule le formulaire
     */
    private void annulerFormulaire() {
        avecEnCours = null;
        viderFormulaire();
        table.getSelectionModel().clearSelection();
    }

    public BorderPane getRoot() {
        return root;
    }
}