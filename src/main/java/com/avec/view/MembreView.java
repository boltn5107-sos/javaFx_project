package com.avec.view;

import com.avec.enums.RoleComite;
import com.avec.enums.RoleDetenteurCle;
import com.avec.enums.StatutMembre;
import com.avec.model.Avec;
import com.avec.model.Membre;
import com.avec.service.AvecService;
import com.avec.service.MembreService;
import com.avec.utils.AlertUtils;
import com.avec.utils.FormatUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Vue pour la gestion des membres
 */
public class MembreView {

    private final Stage primaryStage;
    private final MembreService membreService;
    private final AvecService avecService;
    private final Long avecId;

    private TableView<Membre> tableMembres;
    private ObservableList<Membre> membresObservable;
    private ComboBox<Avec> comboAvec;
    private ComboBox<StatutMembre> filtreStatut;
    private ComboBox<RoleComite> filtreRole;
    private TextField rechercheField;
    private Label lblTotalMembres;
    private Label lblMembresActifs;
    private Label lblTotalEpargne;

    public MembreView(Stage primaryStage, Long avecId) {
        this.primaryStage = primaryStage;
        this.avecId = avecId;
        this.membreService = new MembreService();
        this.avecService = new AvecService();
        this.membresObservable = FXCollections.observableArrayList();
    }

    /**
     * Crée la scène principale de gestion des membres
     */
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9;");

        // En-tête
        root.setTop(createHeader());

        // Centre
        VBox center = new VBox(15);
        center.setPadding(new Insets(20));

        // Sélecteur d'AVEC (si nécessaire)
        if (avecId == null) {
            center.getChildren().add(createAvecSelector());
        }

        // Barre de filtres
        center.getChildren().add(createFilterBar());

        // Tableau des membres
        center.getChildren().add(createMembresTable());
        VBox.setVgrow(tableMembres, Priority.ALWAYS);

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

        Label title = new Label("Gestion des Membres");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Statistiques
        lblTotalMembres = new Label("Total: 0");
        lblTotalMembres.setStyle("-fx-font-size: 14px; -fx-padding: 5 15; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");

        lblMembresActifs = new Label("Actifs: 0");
        lblMembresActifs.setStyle("-fx-font-size: 14px; -fx-padding: 5 15; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5;");

        lblTotalEpargne = new Label("Épargne: 0 FCFA");
        lblTotalEpargne.setStyle("-fx-font-size: 14px; -fx-padding: 5 15; -fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5;");

        header.getChildren().addAll(title, spacer, lblTotalMembres, lblMembresActifs, lblTotalEpargne);

        return header;
    }

    /**
     * Crée le sélecteur d'AVEC
     */
    private VBox createAvecSelector() {
        VBox container = new VBox(10);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label label = new Label("Sélectionner une AVEC");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        comboAvec = new ComboBox<>();
        comboAvec.setPromptText("Choisir une AVEC...");
        comboAvec.setPrefWidth(400);
        comboAvec.setOnAction(e -> chargerMembresParAvec());

        container.getChildren().addAll(label, comboAvec);

        return container;
    }

    /**
     * Crée la barre de filtres
     */
    private HBox createFilterBar() {
        HBox filterBar = new HBox(10);
        filterBar.setPadding(new Insets(10, 0, 10, 0));

        // Filtre par statut
        filtreStatut = new ComboBox<>();
        filtreStatut.setItems(FXCollections.observableArrayList(StatutMembre.values()));
        filtreStatut.setPromptText("Filtrer par statut");
        filtreStatut.setPrefWidth(150);
        filtreStatut.setOnAction(e -> appliquerFiltres());

        // Filtre par rôle
        filtreRole = new ComboBox<>();
        filtreRole.setItems(FXCollections.observableArrayList(RoleComite.values()));
        filtreRole.setPromptText("Filtrer par rôle");
        filtreRole.setPrefWidth(150);
        filtreRole.setOnAction(e -> appliquerFiltres());

        // Champ de recherche
        rechercheField = new TextField();
        rechercheField.setPromptText("Rechercher un membre...");
        rechercheField.setPrefWidth(300);
        rechercheField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 3 || newVal.isEmpty()) {
                rechercherMembres(newVal);
            }
        });

        // Bouton réinitialiser
        Button btnReset = new Button("Réinitialiser");
        btnReset.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        btnReset.setOnAction(e -> resetFiltres());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBar.getChildren().addAll(filtreStatut, filtreRole, rechercheField, btnReset, spacer);

        return filterBar;
    }

    /**
     * Crée le tableau des membres
     */
    private VBox createMembresTable() {
        VBox container = new VBox(5);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label tableTitle = new Label("Liste des membres");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        tableMembres = new TableView<>();
        tableMembres.setItems(membresObservable);
        tableMembres.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableMembres.setRowFactory(tv -> {
            TableRow<Membre> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    afficherDetailsMembre(row.getItem());
                }
            });
            return row;
        });

        // Colonnes
        TableColumn<Membre, String> colNumero = new TableColumn<>("N° Carte");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroCarte"));
        colNumero.setPrefWidth(120);

        TableColumn<Membre, String> colNom = new TableColumn<>("Nom complet");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        colNom.setPrefWidth(200);

        TableColumn<Membre, StatutMembre> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setPrefWidth(100);
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(StatutMembre item, boolean empty) {
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

        TableColumn<Membre, String> colRole = new TableColumn<>("Rôle Comité");
        colRole.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoleComite().getDescription())
        );
        colRole.setPrefWidth(120);

        TableColumn<Membre, String> colGardien = new TableColumn<>("Gardien Clé");
        colGardien.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoleCle().getLibelle())
        );
        colGardien.setPrefWidth(120);

        TableColumn<Membre, String> colEpargne = new TableColumn<>("Épargne");
        colEpargne.setCellValueFactory(cellData ->
                new SimpleStringProperty(FormatUtils.formatCurrency(cellData.getValue().getTotalEpargne()))
        );
        colEpargne.setPrefWidth(120);
        colEpargne.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Membre, String> colPrets = new TableColumn<>("Prêts en cours");
        colPrets.setCellValueFactory(cellData ->
                new SimpleStringProperty(FormatUtils.formatCurrency(cellData.getValue().getTotalPretEnCours()))
        );
        colPrets.setPrefWidth(120);
        colPrets.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Membre, String> colAdhesion = new TableColumn<>("Adhésion");
        colAdhesion.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateAdhesion();
            String dateStr = date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            return new SimpleStringProperty(dateStr);
        });
        colAdhesion.setPrefWidth(100);

        TableColumn<Membre, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(200);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDetails = new Button("Détails");
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDesactiver = new Button("Désactiver");
            private final HBox pane = new HBox(5, btnDetails, btnEdit, btnDesactiver);

            {
                btnDetails.setOnAction(e -> {
                    Membre membre = getTableView().getItems().get(getIndex());
                    afficherDetailsMembre(membre);
                });

                btnEdit.setOnAction(e -> {
                    Membre membre = getTableView().getItems().get(getIndex());
                    editerMembre(membre);
                });

                btnDesactiver.setOnAction(e -> {
                    Membre membre = getTableView().getItems().get(getIndex());
                    desactiverMembre(membre);
                });

                btnDetails.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                btnDesactiver.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
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

        tableMembres.getColumns().addAll(
                colNumero, colNom, colStatut, colRole, colGardien,
                colEpargne, colPrets, colAdhesion, colActions
        );

        container.getChildren().addAll(tableTitle, tableMembres);

        return container;
    }

    /**
     * Crée la barre d'actions
     */
    private HBox createActionBar() {
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_RIGHT);
        actionBar.setPadding(new Insets(10, 0, 0, 0));

        Button btnNouveau = new Button("Nouveau membre");
        btnNouveau.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnNouveau.setOnAction(e -> creerNouveauMembre());

        Button btnElection = new Button("Organiser élection");
        btnElection.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnElection.setOnAction(e -> organiserElection());

        Button btnGardiens = new Button("Désigner gardiens");
        btnGardiens.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnGardiens.setOnAction(e -> designerGardiens());

        Button btnRefresh = new Button("Actualiser");
        btnRefresh.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnRefresh.setOnAction(e -> chargerDonnees());

        actionBar.getChildren().addAll(btnNouveau, btnElection, btnGardiens, btnRefresh);

        return actionBar;
    }

    /**
     * Charge les données initiales
     */
    private void chargerDonnees() {
        try {
            if (comboAvec != null) {
                List<Avec> avecs = avecService.getAllAvecs();
                comboAvec.setItems(FXCollections.observableArrayList(avecs));
            }

            if (avecId != null) {
                chargerMembresParAvecId(avecId);
            } else if (comboAvec != null && comboAvec.getItems().size() > 0) {
                comboAvec.getSelectionModel().selectFirst();
            }

        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Impossible de charger les données", e.getMessage());
        }
    }

    /**
     * Charge les membres pour l'AVEC sélectionnée
     */
    private void chargerMembresParAvec() {
        if (comboAvec.getValue() != null) {
            chargerMembresParAvecId(comboAvec.getValue().getId());
        }
    }

    /**
     * Charge les membres pour un ID d'AVEC spécifique
     */
    private void chargerMembresParAvecId(long avecId) {
        try {
            List<Membre> membres = membreService.getMembresByAvecId(avecId);
            membresObservable.setAll(membres);
            mettreAJourStatistiques(membres);

        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Impossible de charger les membres", e.getMessage());
        }
    }

    /**
     * Met à jour les statistiques
     */
    private void mettreAJourStatistiques(List<Membre> membres) {
        long total = membres.size();
        long actifs = membres.stream().filter(m -> m.getStatut() == StatutMembre.ACTIF).count();
        java.math.BigDecimal totalEpargne = membres.stream()
                .map(Membre::getTotalEpargne)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        lblTotalMembres.setText("Total: " + total);
        lblMembresActifs.setText("Actifs: " + actifs);
        lblTotalEpargne.setText("Épargne: " + FormatUtils.formatCurrency(totalEpargne));
    }

    /**
     * Applique les filtres
     */
    private void appliquerFiltres() {
        List<Membre> tous = new ArrayList<>(membresObservable);

        List<Membre> filtres = tous.stream()
                .filter(m -> filtreStatut.getValue() == null || m.getStatut() == filtreStatut.getValue())
                .filter(m -> filtreRole.getValue() == null ||
                        filtreRole.getValue() == RoleComite.AUCUN ||
                        m.getRoleComite() == filtreRole.getValue())
                .toList();

        tableMembres.setItems(FXCollections.observableArrayList(filtres));
    }

    /**
     * Réinitialise les filtres
     */
    private void resetFiltres() {
        filtreStatut.setValue(null);
        filtreRole.setValue(null);
        rechercheField.clear();
        tableMembres.setItems(membresObservable);
    }

    /**
     * Recherche des membres
     */
    private void rechercherMembres(String recherche) {
        if (avecId == null && (comboAvec == null || comboAvec.getValue() == null)) {
            return;
        }

        long idAvec = avecId != null ? avecId : comboAvec.getValue().getId();

        try {
            if (recherche == null || recherche.trim().isEmpty()) {
                chargerMembresParAvecId(idAvec);
            } else {
                List<Membre> resultats = membreService.rechercherMembres(idAvec, recherche);
                tableMembres.setItems(FXCollections.observableArrayList(resultats));
            }
        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Erreur lors de la recherche", e.getMessage());
        }
    }

    /**
     * Crée un nouveau membre
     */
    private void creerNouveauMembre() {
        if (avecId == null && (comboAvec == null || comboAvec.getValue() == null)) {
            AlertUtils.showWarning("Attention", "Veuillez sélectionner une AVEC", null);
            return;
        }

        long idAvec = avecId != null ? avecId : comboAvec.getValue().getId();

        Dialog<Membre> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Nouveau membre");
        dialog.setHeaderText("Ajouter un nouveau membre");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");

        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");

        TextField professionField = new TextField();
        professionField.setPromptText("Profession");

        TextField villageField = new TextField();
        villageField.setPromptText("Village");

        TextField telephoneField = new TextField();
        telephoneField.setPromptText("Téléphone");

        grid.add(new Label("Nom*:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom*:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Profession:"), 0, 2);
        grid.add(professionField, 1, 2);
        grid.add(new Label("Village:"), 0, 3);
        grid.add(villageField, 1, 3);
        grid.add(new Label("Téléphone:"), 0, 4);
        grid.add(telephoneField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        nomField.textProperty().addListener((obs, old, newVal) ->
                saveButton.setDisable(newVal.trim().isEmpty() || prenomField.getText().trim().isEmpty()));

        prenomField.textProperty().addListener((obs, old, newVal) ->
                saveButton.setDisable(newVal.trim().isEmpty() || nomField.getText().trim().isEmpty()));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Membre membre = new Membre();
                membre.setNom(nomField.getText().trim());
                membre.setPrenom(prenomField.getText().trim());
                membre.setProfession(professionField.getText());
                membre.setVillage(villageField.getText());
                membre.setTelephone(telephoneField.getText());
                return membre;
            }
            return null;
        });

        Optional<Membre> result = dialog.showAndWait();
        result.ifPresent(membre -> {
            try {
                Membre created = membreService.creerMembre(
                        membre.getNom(),
                        membre.getPrenom(),
                        idAvec,
                        membre.getProfession(),
                        membre.getVillage(),
                        membre.getTelephone()
                );

                membresObservable.add(created);
                mettreAJourStatistiques(membresObservable);
                AlertUtils.showInfo("Succès", "Membre créé", "Numéro de carte: " + created.getNumeroCarte());

            } catch (SQLException | IllegalArgumentException e) {
                AlertUtils.showError("Erreur", "Impossible de créer le membre", e.getMessage());
            }
        });
    }

    /**
     * Édite un membre
     */
    private void editerMembre(Membre membre) {
        Dialog<Membre> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Modifier membre");
        dialog.setHeaderText("Modification de " + membre.getNomComplet());

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomField = new TextField(membre.getNom());
        TextField prenomField = new TextField(membre.getPrenom());
        TextField professionField = new TextField(membre.getProfession());
        TextField villageField = new TextField(membre.getVillage());
        TextField telephoneField = new TextField(membre.getTelephone());

        ComboBox<StatutMembre> statutCombo = new ComboBox<>();
        statutCombo.setItems(FXCollections.observableArrayList(StatutMembre.values()));
        statutCombo.setValue(membre.getStatut());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Profession:"), 0, 2);
        grid.add(professionField, 1, 2);
        grid.add(new Label("Village:"), 0, 3);
        grid.add(villageField, 1, 3);
        grid.add(new Label("Téléphone:"), 0, 4);
        grid.add(telephoneField, 1, 4);
        grid.add(new Label("Statut:"), 0, 5);
        grid.add(statutCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                membre.setNom(nomField.getText().trim());
                membre.setPrenom(prenomField.getText().trim());
                membre.setProfession(professionField.getText());
                membre.setVillage(villageField.getText());
                membre.setTelephone(telephoneField.getText());
                membre.setStatut(statutCombo.getValue());
                return membre;
            }
            return null;
        });

        Optional<Membre> result = dialog.showAndWait();
        result.ifPresent(membreModifie -> {
            try {
                if (membreService.modifierMembre(membreModifie)) {
                    tableMembres.refresh();
                    mettreAJourStatistiques(membresObservable);
                    AlertUtils.showInfo("Succès", "Membre modifié", null);
                }
            } catch (SQLException e) {
                AlertUtils.showError("Erreur", "Impossible de modifier le membre", e.getMessage());
            }
        });
    }

    /**
     * Désactive un membre
     */
    private void desactiverMembre(Membre membre) {
        boolean confirm = AlertUtils.showConfirmation(
                "Confirmation",
                "Désactiver le membre",
                "Voulez-vous vraiment désactiver " + membre.getNomComplet() + " ?"
        );

        if (confirm) {
            try {
                if (membreService.desactiverMembre(membre.getId())) {
                    membre.setStatut(StatutMembre.INACTIF);
                    tableMembres.refresh();
                    mettreAJourStatistiques(membresObservable);
                    AlertUtils.showInfo("Succès", "Membre désactivé", null);
                }
            } catch (SQLException | IllegalStateException e) {
                AlertUtils.showError("Erreur", "Impossible de désactiver", e.getMessage());
            }
        }
    }

    /**
     * Organise une élection
     */
    private void organiserElection() {
        if (avecId == null && (comboAvec == null || comboAvec.getValue() == null)) {
            AlertUtils.showWarning("Attention", "Veuillez sélectionner une AVEC", null);
            return;
        }

        AlertUtils.showInfo("Élection", "Fonctionnalité à implémenter",
                "L'interface d'élection avec urnes virtuelles sera disponible prochainement.");
    }

    /**
     * Désigne les gardiens de clés
     */
    private void designerGardiens() {
        if (avecId == null && (comboAvec == null || comboAvec.getValue() == null)) {
            AlertUtils.showWarning("Attention", "Veuillez sélectionner une AVEC", null);
            return;
        }

        AlertUtils.showInfo("Gardiens", "Fonctionnalité à implémenter",
                "La désignation des gardiens de clés sera disponible prochainement.");
    }

    /**
     * Affiche les détails d'un membre
     */
    private void afficherDetailsMembre(Membre membre) {
        String details = String.format(
                "Numéro de carte: %s\n" +
                        "Nom complet: %s\n" +
                        "Statut: %s\n" +
                        "Date d'adhésion: %s\n" +
                        "Rôle au comité: %s\n" +
                        "Gardien de clé: %s\n" +
                        "Nombre de parts: %d\n" +
                        "Épargne totale: %s\n" +
                        "Prêts en cours: %s\n" +
                        "Capacité d'emprunt: %s\n" +
                        "Profession: %s\n" +
                        "Village: %s\n" +
                        "Téléphone: %s",
                membre.getNumeroCarte(),
                membre.getNomComplet(),
                membre.getStatut().getLibelle(),
                FormatUtils.formatDate(membre.getDateAdhesion()),
                membre.getRoleComite().getDescription(),
                membre.getRoleCle().getLibelle(),
                membre.getNombreParts(),
                FormatUtils.formatCurrency(membre.getTotalEpargne()),
                FormatUtils.formatCurrency(membre.getTotalPretEnCours()),
                FormatUtils.formatCurrency(membre.calculerCapaciteEmprunt()),
                membre.getProfession() != null ? membre.getProfession() : "Non renseignée",
                membre.getVillage() != null ? membre.getVillage() : "Non renseigné",
                membre.getTelephone() != null ? membre.getTelephone() : "Non renseigné"
        );

        AlertUtils.showInfo("Détails du membre", membre.getNomComplet(), details);
    }

    /**
     * Retourne la classe CSS pour le badge de statut
     */
    private String getStatutBadgeClass(StatutMembre statut) {
        return switch (statut) {
            case ACTIF -> "badge-success";
            case INACTIF -> "badge-secondary";
            case SUSPENDU -> "badge-warning";
            case RADIE -> "badge-danger";
        };
    }
}