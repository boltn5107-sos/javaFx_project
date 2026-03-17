package com.avec.view;

import com.avec.enums.PhaseCycle;
import com.avec.enums.StatutAvec;
import com.avec.enums.JourReunion;
import com.avec.model.Avec;
import com.avec.model.AgentVillageois;
import com.avec.service.AvecService;
import com.avec.service.AgentVillageoisService;
import com.avec.utils.AlertUtils;
import com.avec.utils.FormatUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Vue pour la gestion des AVEC
 */
public class AvecView {

    private final Stage primaryStage;
    private final AvecService avecService;
    private final AgentVillageoisService agentVillageoisService;
    private final Long agentVillageoisId;

    private TableView<Avec> tableAvecs;
    private ObservableList<Avec> avecsObservable;
    private ComboBox<StatutAvec> filtreStatut;
    private ComboBox<PhaseCycle> filtrePhase;
    private TextField rechercheField;
    private Label lblTotalAvecs;
    private Label lblEnFormation;
    private Label lblActives;

    public AvecView(Stage primaryStage, Long agentVillageoisId) {
        this.primaryStage = primaryStage;
        this.agentVillageoisId = agentVillageoisId;
        this.avecService = new AvecService();
        this.agentVillageoisService = new AgentVillageoisService();
        this.avecsObservable = FXCollections.observableArrayList();
    }

    /**
     * Crée la scène principale de gestion des AVEC
     */
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9;");

        // En-tête
        root.setTop(createHeader());

        // Centre
        VBox center = new VBox(15);
        center.setPadding(new Insets(20));

        // Barre de filtres
        center.getChildren().add(createFilterBar());

        // Tableau des AVEC
        center.getChildren().add(createTable());
        VBox.setVgrow(tableAvecs, Priority.ALWAYS);

        // Barre d'actions
        center.getChildren().add(createActionBar());

        root.setCenter(center);

        // Charger les données
        chargerDonnees();

        return new Scene(root, 1200, 700);
    }

    /**
     * Crée l'en-tête de la vue
     */
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setPadding(new Insets(20, 20, 0, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Gestion des AVEC");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Statistiques rapides
        lblTotalAvecs = new Label("Total: 0");
        lblTotalAvecs.setStyle("-fx-font-size: 14px; -fx-padding: 5 15; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");

        lblEnFormation = new Label("En formation: 0");
        lblEnFormation.setStyle("-fx-font-size: 14px; -fx-padding: 5 15; -fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5;");

        lblActives = new Label("Actives: 0");
        lblActives.setStyle("-fx-font-size: 14px; -fx-padding: 5 15; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5;");

        header.getChildren().addAll(title, spacer, lblTotalAvecs, lblEnFormation, lblActives);

        return header;
    }

    /**
     * Crée la barre de filtres
     */
    private HBox createFilterBar() {
        HBox filterBar = new HBox(10);
        filterBar.setPadding(new Insets(10, 0, 10, 0));

        // Filtre par statut
        filtreStatut = new ComboBox<>();
        filtreStatut.setItems(FXCollections.observableArrayList(StatutAvec.values()));
        filtreStatut.setPromptText("Filtrer par statut");
        filtreStatut.setPrefWidth(150);
        filtreStatut.setOnAction(e -> appliquerFiltres());

        // Filtre par phase
        filtrePhase = new ComboBox<>();
        filtrePhase.setItems(FXCollections.observableArrayList(PhaseCycle.values()));
        filtrePhase.setPromptText("Filtrer par phase");
        filtrePhase.setPrefWidth(150);
        filtrePhase.setOnAction(e -> appliquerFiltres());

        // Champ de recherche
        rechercheField = new TextField();
        rechercheField.setPromptText("Rechercher par nom...");
        rechercheField.setPrefWidth(300);
        rechercheField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 3 || newVal.isEmpty()) {
                rechercherAvecs(newVal);
            }
        });

        // Bouton réinitialiser
        Button btnReset = new Button("Réinitialiser");
        btnReset.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        btnReset.setOnAction(e -> resetFiltres());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBar.getChildren().addAll(filtreStatut, filtrePhase, rechercheField, btnReset, spacer);

        return filterBar;
    }

    /**
     * Crée le tableau des AVEC
     */
    private VBox createTable() {
        VBox container = new VBox(5);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label tableTitle = new Label("Liste des AVEC");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        tableAvecs = new TableView<>();
        tableAvecs.setItems(avecsObservable);
        tableAvecs.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableAvecs.setRowFactory(tv -> {
            TableRow<Avec> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    afficherDetailsAvec(row.getItem());
                }
            });
            return row;
        });

        // Colonnes
        TableColumn<Avec, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(200);

        TableColumn<Avec, String> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(new PropertyValueFactory<>("codeUnique"));
        colCode.setPrefWidth(120);

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
                    badge.getStyleClass().addAll("badge", getStatutBadgeClass(item));
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
                    badge.getStyleClass().addAll("badge", "badge-info");
                    setGraphic(badge);
                }
            }
        });

        TableColumn<Avec, Integer> colMembres = new TableColumn<>("Membres");
        colMembres.setCellValueFactory(new PropertyValueFactory<>("nombreMembresActifs"));
        colMembres.setPrefWidth(80);
        colMembres.setStyle("-fx-alignment: CENTER;");

        TableColumn<Avec, String> colEpargne = new TableColumn<>("Épargne");
        colEpargne.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        FormatUtils.formatCurrency(cellData.getValue().getTotalEpargne())
                ));
        colEpargne.setPrefWidth(120);
        colEpargne.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Avec, String> colLieu = new TableColumn<>("Lieu");
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieuReunion"));
        colLieu.setPrefWidth(150);

        TableColumn<Avec, String> colProchReunion = new TableColumn<>("Prochaine réunion");
        colProchReunion.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getProchaineReunion();
            String dateStr = date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            return new javafx.beans.property.SimpleStringProperty(dateStr);
        });
        colProchReunion.setPrefWidth(120);

        TableColumn<Avec, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(200);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDetails = new Button("Détails");
            private final Button btnPhase = new Button("Phase");
            private final Button btnEdit = new Button("Modifier");
            private final HBox pane = new HBox(5, btnDetails, btnPhase, btnEdit);

            {
                btnDetails.setOnAction(e -> {
                    Avec avec = getTableView().getItems().get(getIndex());
                    afficherDetailsAvec(avec);
                });

                btnPhase.setOnAction(e -> {
                    Avec avec = getTableView().getItems().get(getIndex());
                    changerPhase(avec);
                });

                btnEdit.setOnAction(e -> {
                    Avec avec = getTableView().getItems().get(getIndex());
                    editerAvec(avec);
                });

                btnDetails.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                btnPhase.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                btnEdit.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        tableAvecs.getColumns().addAll(
                colNom, colCode, colStatut, colPhase, colMembres,
                colEpargne, colLieu, colProchReunion, colActions
        );

        container.getChildren().addAll(tableTitle, tableAvecs);

        return container;
    }

    /**
     * Crée la barre d'actions
     */
    private HBox createActionBar() {
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_RIGHT);
        actionBar.setPadding(new Insets(10, 0, 0, 0));

        Button btnNouveau = new Button("Nouvelle AVEC");
        btnNouveau.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnNouveau.setOnAction(e -> creerNouvelleAvec());

        Button btnRefresh = new Button("Actualiser");
        btnRefresh.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnRefresh.setOnAction(e -> chargerDonnees());

        Button btnRapport = new Button("Générer rapport");
        btnRapport.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");
//        btnRapport.setOnAction(e -> genererRapport());

        actionBar.getChildren().addAll(btnNouveau, btnRefresh, btnRapport);

        return actionBar;
    }

    /**
     * Charge les données initiales
     */
    private void chargerDonnees() {
        try {
            List<Avec> avecs;
            if (agentVillageoisId != null) {
                avecs = avecService.getAvecsByAgentVillageois(agentVillageoisId);
            } else {
                avecs = avecService.getAllAvecs();
            }

            avecsObservable.setAll(avecs);
            mettreAJourStatistiques(avecs);

        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Impossible de charger les AVEC", e.getMessage());
        }
    }

    /**
     * Met à jour les statistiques
     */
    private void mettreAJourStatistiques(List<Avec> avecs) {
        long total = avecs.size();
        long enFormation = avecs.stream().filter(a -> a.getStatut() == StatutAvec.EN_FORMATION).count();
        long actives = avecs.stream().filter(a -> a.getStatut() == StatutAvec.ACTVIE).count();

        lblTotalAvecs.setText("Total: " + total);
        lblEnFormation.setText("En formation: " + enFormation);
        lblActives.setText("Actives: " + actives);
    }

    /**
     * Applique les filtres
     */
    private void appliquerFiltres() {
        try {
            List<Avec> toutesAvecs;
            if (agentVillageoisId != null) {
                toutesAvecs = avecService.getAvecsByAgentVillageois(agentVillageoisId);
            } else {
                toutesAvecs = avecService.getAllAvecs();
            }

            List<Avec> filtered = toutesAvecs.stream()
                    .filter(a -> filtreStatut.getValue() == null || a.getStatut() == filtreStatut.getValue())
                    .filter(a -> filtrePhase.getValue() == null || a.getPhaseCourante() == filtrePhase.getValue())
                    .toList();

            avecsObservable.setAll(filtered);

        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Erreur lors du filtrage", e.getMessage());
        }
    }

    /**
     * Réinitialise les filtres
     */
    private void resetFiltres() {
        filtreStatut.setValue(null);
        filtrePhase.setValue(null);
        rechercheField.clear();
        chargerDonnees();
    }

    /**
     * Recherche des AVEC
     */
    private void rechercherAvecs(String recherche) {
        try {
            if (recherche == null || recherche.trim().isEmpty()) {
                chargerDonnees();
            } else {
                List<Avec> resultats = avecService.rechercherAvecs(recherche);
                avecsObservable.setAll(resultats);
            }
        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Erreur lors de la recherche", e.getMessage());
        }
    }

    /**
     * Crée une nouvelle AVEC - avec dialogue intégré
     */
    private void creerNouvelleAvec() {
        // Création du dialogue
        Dialog<Avec> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Nouvelle AVEC");
        dialog.setHeaderText("Création d'une nouvelle AVEC");

        // Boutons
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom de l'AVEC");

        TextField prixPartField = new TextField();
        prixPartField.setPromptText("Prix de la part");

        TextField lieuField = new TextField();
        lieuField.setPromptText("Lieu de réunion");

        ComboBox<JourReunion> jourCombo = new ComboBox<>();
        jourCombo.setItems(FXCollections.observableArrayList(JourReunion.values()));
        jourCombo.setPromptText("Jour de réunion");

        TextField tauxField = new TextField();
        tauxField.setPromptText("Taux des frais (%)");
        tauxField.setText("10");

        TextField cotisationField = new TextField();
        cotisationField.setPromptText("Cotisation caisse solidarité");
        cotisationField.setText("100");

        CheckBox caisseActiveCheck = new CheckBox("Activer la caisse de solidarité");
        caisseActiveCheck.setSelected(true);

        // Ajout des champs
        grid.add(new Label("Nom*:"), 0, 0);
        grid.add(nomField, 1, 0);

        grid.add(new Label("Prix de la part*:"), 0, 1);
        grid.add(prixPartField, 1, 1);

        grid.add(new Label("Lieu de réunion*:"), 0, 2);
        grid.add(lieuField, 1, 2);

        grid.add(new Label("Jour de réunion*:"), 0, 3);
        grid.add(jourCombo, 1, 3);

        grid.add(new Label("Taux des frais (%):"), 0, 4);
        grid.add(tauxField, 1, 4);

        grid.add(new Label("Cotisation caisse:"), 0, 5);
        grid.add(cotisationField, 1, 5);

        grid.add(caisseActiveCheck, 0, 6, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Validation
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Listeners pour validation
        nomField.textProperty().addListener((obs, old, newVal) ->
                saveButton.setDisable(newVal.trim().isEmpty() ||
                        prixPartField.getText().trim().isEmpty() ||
                        lieuField.getText().trim().isEmpty() ||
                        jourCombo.getValue() == null));

        prixPartField.textProperty().addListener((obs, old, newVal) ->
                saveButton.setDisable(newVal.trim().isEmpty() ||
                        nomField.getText().trim().isEmpty() ||
                        lieuField.getText().trim().isEmpty() ||
                        jourCombo.getValue() == null));

        lieuField.textProperty().addListener((obs, old, newVal) ->
                saveButton.setDisable(newVal.trim().isEmpty() ||
                        nomField.getText().trim().isEmpty() ||
                        prixPartField.getText().trim().isEmpty() ||
                        jourCombo.getValue() == null));

        jourCombo.valueProperty().addListener((obs, old, newVal) ->
                saveButton.setDisable(newVal == null ||
                        nomField.getText().trim().isEmpty() ||
                        prixPartField.getText().trim().isEmpty() ||
                        lieuField.getText().trim().isEmpty()));

        // Résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Avec nouveau = new Avec();
                    nouveau.setNom(nomField.getText().trim());
                    nouveau.setPrixPart(new BigDecimal(prixPartField.getText().trim()));
                    nouveau.setLieuReunion(lieuField.getText().trim());
                    nouveau.setJourReunion(jourCombo.getValue());
                    nouveau.setTauxFraisServiceMensuel(new BigDecimal(tauxField.getText()));
                    nouveau.setCotisationCaisseSolidarite(new BigDecimal(cotisationField.getText()));
                    nouveau.setCaisseSolidariteActive(caisseActiveCheck.isSelected());
                    return nouveau;
                } catch (NumberFormatException e) {
                    AlertUtils.showError("Erreur", "Format de nombre invalide", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Avec> result = dialog.showAndWait();
        result.ifPresent(this::enregistrerNouvelleAvec);
    }

    /**
     * Enregistre une nouvelle AVEC
     */
    private void enregistrerNouvelleAvec(Avec nouvelleAvec) {
        try {
            Avec created = avecService.creerAvec(
                    nouvelleAvec.getNom(),
                    nouvelleAvec.getPrixPart(),
                    nouvelleAvec.getLieuReunion(),
                    nouvelleAvec.getJourReunion(),
                    agentVillageoisId != null ? agentVillageoisId : 1L // ID par défaut si null
            );

            avecsObservable.add(created);
            AlertUtils.showInfo("Succès", "AVEC créée avec succès",
                    "Code: " + created.getCodeUnique());

        } catch (SQLException | IllegalArgumentException e) {
            AlertUtils.showError("Erreur", "Impossible de créer l'AVEC", e.getMessage());
        }
    }

    /**
     * Édite une AVEC existante - avec dialogue intégré
     */
    private void editerAvec(Avec avec) {
        // Création du dialogue
        Dialog<Avec> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Modifier AVEC");
        dialog.setHeaderText("Modification de l'AVEC: " + avec.getNom());

        // Boutons
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomField = new TextField(avec.getNom());
        TextField prixPartField = new TextField(avec.getPrixPart().toString());
        TextField lieuField = new TextField(avec.getLieuReunion());
        ComboBox<JourReunion> jourCombo = new ComboBox<>();
        jourCombo.setItems(FXCollections.observableArrayList(JourReunion.values()));
        jourCombo.setValue(avec.getJourReunion());

        TextField tauxField = new TextField(avec.getTauxFraisServiceMensuel().toString());
        TextField cotisationField = new TextField(avec.getCotisationCaisseSolidarite().toString());
        CheckBox caisseActiveCheck = new CheckBox("Activer la caisse de solidarité");
        caisseActiveCheck.setSelected(avec.isCaisseSolidariteActive());

        // Ajout des champs
        grid.add(new Label("Nom*:"), 0, 0);
        grid.add(nomField, 1, 0);

        grid.add(new Label("Prix de la part*:"), 0, 1);
        grid.add(prixPartField, 1, 1);

        grid.add(new Label("Lieu de réunion*:"), 0, 2);
        grid.add(lieuField, 1, 2);

        grid.add(new Label("Jour de réunion*:"), 0, 3);
        grid.add(jourCombo, 1, 3);

        grid.add(new Label("Taux des frais (%):"), 0, 4);
        grid.add(tauxField, 1, 4);

        grid.add(new Label("Cotisation caisse:"), 0, 5);
        grid.add(cotisationField, 1, 5);

        grid.add(caisseActiveCheck, 0, 6, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    avec.setNom(nomField.getText().trim());
                    avec.setPrixPart(new BigDecimal(prixPartField.getText().trim()));
                    avec.setLieuReunion(lieuField.getText().trim());
                    avec.setJourReunion(jourCombo.getValue());
                    avec.setTauxFraisServiceMensuel(new BigDecimal(tauxField.getText()));
                    avec.setCotisationCaisseSolidarite(new BigDecimal(cotisationField.getText()));
                    avec.setCaisseSolidariteActive(caisseActiveCheck.isSelected());
                    return avec;
                } catch (NumberFormatException e) {
                    AlertUtils.showError("Erreur", "Format de nombre invalide", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Avec> result = dialog.showAndWait();
        result.ifPresent(avecModifiee -> {
            try {
                if (avecService.modifierAvec(avecModifiee)) {
                    int index = avecsObservable.indexOf(avec);
                    avecsObservable.set(index, avecModifiee);
                    AlertUtils.showInfo("Succès", "AVEC modifiée avec succès", null);
                }
            } catch (SQLException e) {
                AlertUtils.showError("Erreur", "Impossible de modifier l'AVEC", e.getMessage());
            }
        });
    }

    /**
     * Change la phase d'une AVEC - avec dialogue intégré
     */
    private void changerPhase(Avec avec) {
        // Création du dialogue
        Dialog<PhaseCycle> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Changer de phase");
        dialog.setHeaderText("Changer la phase de l'AVEC: " + avec.getNom());

        // Boutons
        ButtonType saveButtonType = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Formulaire
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label phaseActuelle = new Label("Phase actuelle: " + avec.getPhaseCourante().getLibelle());
        phaseActuelle.setStyle("-fx-font-weight: bold;");

        ComboBox<PhaseCycle> phaseCombo = new ComboBox<>();
        phaseCombo.setItems(FXCollections.observableArrayList(PhaseCycle.values()));
        phaseCombo.setPromptText("Sélectionner la nouvelle phase");

        // Ne proposer que les phases logiques
        PhaseCycle phaseCourante = avec.getPhaseCourante();
        if (phaseCourante == PhaseCycle.PREPERATIORE) {
            phaseCombo.setValue(PhaseCycle.INTENSIVE);
        } else if (phaseCourante == PhaseCycle.INTENSIVE) {
            phaseCombo.setValue(PhaseCycle.DEVELOPPEMENT);
        } else if (phaseCourante == PhaseCycle.DEVELOPPEMENT) {
            phaseCombo.setValue(PhaseCycle.MATURITE);
        } else if (phaseCourante == PhaseCycle.MATURITE) {
            phaseCombo.setValue(PhaseCycle.TERMINE);
        }

        content.getChildren().addAll(phaseActuelle, new Label("Nouvelle phase:"), phaseCombo);

        dialog.getDialogPane().setContent(content);

        // Résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return phaseCombo.getValue();
            }
            return null;
        });

        Optional<PhaseCycle> result = dialog.showAndWait();
        result.ifPresent(nouvellePhase -> {
            try {
                if (avecService.changerPhase(avec.getId(), nouvellePhase)) {
                    avec.setPhaseCourante(nouvellePhase);
                    tableAvecs.refresh();
                    AlertUtils.showInfo("Succès", "Phase changée avec succès",
                            "Nouvelle phase: " + nouvellePhase.getLibelle());
                }
            } catch (SQLException | IllegalStateException e) {
                AlertUtils.showError("Erreur", "Impossible de changer la phase", e.getMessage());
            }
        });
    }

    /**
     * Affiche les détails d'une AVEC
     */
    private void afficherDetailsAvec(Avec avec) {
        String details = String.format(
                "Code: %s\n" +
                        "Statut: %s\n" +
                        "Phase: %s\n" +
                        "Date création: %s\n" +
                        "Membres: %d / %d\n" +
                        "Prix de la part: %s\n" +
                        "Taux des frais: %.1f%%\n" +
                        "Épargne totale: %s\n" +
                        "Crédits en cours: %s\n" +
                        "Lieu de réunion: %s\n" +
                        "Jour de réunion: %s\n" +
                        "Prochaine réunion: %s\n" +
                        "Progression formation: %d%%",
                avec.getCodeUnique(),
                avec.getStatut().getLibelle(),
                avec.getPhaseCourante().getLibelle(),
                avec.getDateCreation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                avec.getNombreMembresActifs(),
                avec.getNombreMembresMax(),
                FormatUtils.formatCurrency(avec.getPrixPart()),
                avec.getTauxFraisServiceMensuel().doubleValue(),
                FormatUtils.formatCurrency(avec.getTotalEpargne()),
                FormatUtils.formatCurrency(avec.getTotalCredit()),
                avec.getLieuReunion() != null ? avec.getLieuReunion() : "Non défini",
                avec.getJourReunion() != null ? avec.getJourReunion().getLibelle() : "Non défini",
                avec.getProchaineReunion() != null ?
                        avec.getProchaineReunion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non planifiée",
                avec.getProgressionFormation()
        );

        AlertUtils.showInfo("Détails de l'AVEC", avec.getNom(), details);
    }

    /**
     * Génère un rapport
     */
//    private void genererRapport() {
//        try {
//            List<Avec> avecs = avecService.getAllAvecs();
//            StringBuilder rapport = new StringBuilder();
//            rapport.append("RAPPORT DES AVEC\n");
//            rapport.append("=").repeat(50).append("\n\n");
//            rapport.append("Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
//            rapport.append("Total AVEC: ").append(avecs.size()).append("\n\n");
//
//            rapport.append("RÉPARTITION PAR STATUT:\n");
//            long enFormation = avecs.stream().filter(a -> a.getStatut() == StatutAvec.EN_FORMATION).count();
//            long actives = avecs.stream().filter(a -> a.getStatut() == StatutAvec.ACTVIE).count();
//            long enPause = avecs.stream().filter(a -> a.getStatut() == StatutAvec.EN_PAUSE).count();
//            long terminees = avecs.stream().filter(a -> a.getStatut() == StatutAvec.TERMINE).count();
//
//            rapport.append("- En formation: ").append(enFormation).append("\n");
//            rapport.append("- Actives: ").append(actives).append("\n");
//            rapport.append("- En pause: ").append(enPause).append("\n");
//            rapport.append("- Terminées: ").append(terminees).append("\n\n");
//
//            AlertUtils.showInfo("Rapport", "Génération du rapport", rapport.toString());
//
//        } catch (SQLException e) {
//            AlertUtils.showError("Erreur", "Impossible de générer le rapport", e.getMessage());
//        }
//    }

    /**
     * Retourne la classe CSS pour le badge de statut
     */
    private String getStatutBadgeClass(StatutAvec statut) {
        return switch (statut) {
            case EN_FORMATION -> "badge-warning";
            case ACTVIE -> "badge-success";
            case EN_PAUSE -> "badge-info";
            case TERMINE, EN_DISSOLUTION -> "badge-secondary";
        };
    }
}