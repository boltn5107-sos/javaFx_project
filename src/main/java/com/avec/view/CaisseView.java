package com.avec.view;

import com.avec.model.Caisse;
import com.avec.model.Avec;
import com.avec.service.CaisseService;
import com.avec.service.AvecService;
import com.avec.utils.AlertUtils;
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
import java.util.List;
import java.util.Optional;

/**
 * Vue pour la gestion des caisses
 */
public class CaisseView {

    private final Stage primaryStage;
    private final CaisseService caisseService;
    private final AvecService avecService;
    private final Long avecId;

    private TableView<Caisse> tableCaisses;
    private ObservableList<Caisse> caissesObservable;
    private ComboBox<Avec> comboAvec;
    private Label lblCodeSecurite;

    public CaisseView(Stage primaryStage, Long avecId) {
        this.primaryStage = primaryStage;
        this.avecId = avecId;
        this.caisseService = new CaisseService();
        this.avecService = new AvecService();
        this.caissesObservable = FXCollections.observableArrayList();
    }

    /**
     * Crée la scène principale de gestion des caisses
     */
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9;");

        // En-tête
        root.setTop(createHeader());

        // Centre
        VBox center = new VBox(20);
        center.setPadding(new Insets(20));

        // Sélecteur d'AVEC (si nécessaire)
        if (avecId == null) {
            center.getChildren().add(createAvecSelector());
        }

        // Détails de la caisse
        center.getChildren().add(createCaisseDetail());

        // Tableau des caisses (pour l'admin)
        if (avecId == null) {
            center.getChildren().add(createTable());
            VBox.setVgrow(tableCaisses, Priority.ALWAYS);
        }

        root.setCenter(center);

        // Charger les données
        chargerDonnees();

        return new Scene(root, 1000, 700);
    }

    /**
     * Crée l'en-tête de la vue
     */
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setPadding(new Insets(20, 20, 0, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Gestion des Caisses");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer);

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
        comboAvec.setOnAction(e -> chargerCaissePourAvec());

        container.getChildren().addAll(label, comboAvec);

        return container;
    }

    /**
     * Crée la section des détails de la caisse
     */
    private VBox createCaisseDetail() {
        VBox container = new VBox(15);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label title = new Label("Détails de la caisse");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        // Informations de la caisse
        grid.add(new Label("ID Caisse:"), 0, 0);
        Label lblId = new Label("-");
        lblId.setStyle("-fx-font-weight: bold;");
        grid.add(lblId, 1, 0);

        grid.add(new Label("AVEC:"), 0, 1);
        Label lblAvec = new Label("-");
        lblAvec.setStyle("-fx-font-weight: bold;");
        grid.add(lblAvec, 1, 1);

        grid.add(new Label("Code de sécurité:"), 0, 2);
        lblCodeSecurite = new Label("-");
        lblCodeSecurite.setStyle("-fx-font-family: monospace; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        grid.add(lblCodeSecurite, 1, 2);

        // Boutons d'action
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        Button btnGenererCode = new Button("Générer nouveau code");
        btnGenererCode.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 8 15;");
        btnGenererCode.setOnAction(e -> genererNouveauCode());

        Button btnVerifierCode = new Button("Vérifier code");
        btnVerifierCode.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15;");
        btnVerifierCode.setOnAction(e -> verifierCode());

        actionBox.getChildren().addAll(btnGenererCode, btnVerifierCode);

        // Stocker les labels pour mise à jour
        grid.getProperties().put("lblId", lblId);
        grid.getProperties().put("lblAvec", lblAvec);

        container.getChildren().addAll(title, grid, actionBox);

        return container;
    }

    /**
     * Crée le tableau des caisses (pour admin)
     */
    private VBox createTable() {
        VBox container = new VBox(5);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        container.setPadding(new Insets(15));

        Label tableTitle = new Label("Liste de toutes les caisses");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        tableCaisses = new TableView<>();
        tableCaisses.setItems(caissesObservable);
        tableCaisses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Colonnes
        TableColumn<Caisse, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(80);

        TableColumn<Caisse, Long> colAvecId = new TableColumn<>("ID AVEC");
        colAvecId.setCellValueFactory(new PropertyValueFactory<>("avecId"));
        colAvecId.setPrefWidth(100);

        TableColumn<Caisse, String> colCode = new TableColumn<>("Code de sécurité");
        colCode.setCellValueFactory(new PropertyValueFactory<>("codeSecurite"));
        colCode.setPrefWidth(200);

        TableColumn<Caisse, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(150);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnVoir = new Button("Voir");
            private final HBox pane = new HBox(5, btnVoir);

            {
                btnVoir.setOnAction(e -> {
                    Caisse caisse = getTableView().getItems().get(getIndex());
                    voirDetailsCaisse(caisse);
                });
                btnVoir.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
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

        tableCaisses.getColumns().addAll(colId, colAvecId, colCode, colActions);

        container.getChildren().addAll(tableTitle, tableCaisses);

        return container;
    }

    /**
     * Charge les données initiales
     */
    private void chargerDonnees() {
        try {
            // Charger la liste des AVEC pour le sélecteur
            if (comboAvec != null) {
                List<Avec> avecs = avecService.getAllAvecs();
                comboAvec.setItems(FXCollections.observableArrayList(avecs));
            }

            // Si un ID d'AVEC est fourni, charger directement sa caisse
            if (avecId != null) {
                chargerCaissePourAvecId(avecId);
            }

            // Charger toutes les caisses pour l'admin
            if (tableCaisses != null) {
                List<Caisse> caisses = caisseService.getAllCaisses();
                caissesObservable.setAll(caisses);
            }

        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Impossible de charger les données", e.getMessage());
        }
    }

    /**
     * Charge la caisse pour l'AVEC sélectionnée
     */
    private void chargerCaissePourAvec() {
        Avec selectedAvec = comboAvec.getValue();
        if (selectedAvec != null) {
            chargerCaissePourAvecId(selectedAvec.getId());
        }
    }

    /**
     * Charge la caisse pour un ID d'AVEC spécifique
     */
    private void chargerCaissePourAvecId(long avecId) {
        try {
            Caisse caisse = caisseService.getCaisseByAvecId(avecId);
            afficherDetailsCaisse(caisse);

        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Impossible de charger la caisse", e.getMessage());
        }
    }

    /**
     * Affiche les détails d'une caisse
     */
    private void afficherDetailsCaisse(Caisse caisse) {
        if (caisse == null) return;

        // Mettre à jour les labels
        VBox container = (VBox) primaryStage.getScene().lookup(".vbox");
        if (container != null) {
            GridPane grid = (GridPane) container.lookup(".grid-pane");
            if (grid != null) {
                Label lblId = (Label) grid.getProperties().get("lblId");
                Label lblAvec = (Label) grid.getProperties().get("lblAvec");

                if (lblId != null) lblId.setText(String.valueOf(caisse.getId()));
                if (lblAvec != null) {
                    try {
                        Avec avec = avecService.getAvecById(caisse.getAvecId());
                        lblAvec.setText(avec != null ? avec.getNom() : "Inconnue");
                    } catch (SQLException e) {
                        lblAvec.setText("AVEC #" + caisse.getAvecId());
                    }
                }
            }
        }

        lblCodeSecurite.setText(caisse.getCodeSecurite());
    }

    /**
     * Génère un nouveau code de sécurité
     */
    private void genererNouveauCode() {
        if (avecId == null && (comboAvec == null || comboAvec.getValue() == null)) {
            AlertUtils.showWarning("Attention", "Veuillez sélectionner une AVEC", null);
            return;
        }

        long idAvec = avecId != null ? avecId : comboAvec.getValue().getId();

        try {
            Caisse caisse = caisseService.getCaisseByAvecId(idAvec);
            if (caisse != null) {
                String nouveauCode = caisseService.genererNouveauCodeSecurite(caisse.getId());
                lblCodeSecurite.setText(nouveauCode);
                AlertUtils.showInfo("Succès", "Nouveau code généré", "Code: " + nouveauCode);

                // Rafraîchir le tableau si nécessaire
                if (tableCaisses != null) {
                    List<Caisse> caisses = caisseService.getAllCaisses();
                    caissesObservable.setAll(caisses);
                }
            }
        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Impossible de générer un nouveau code", e.getMessage());
        }
    }

    /**
     * Vérifie un code de sécurité
     */
    private void verifierCode() {
        if (avecId == null && (comboAvec == null || comboAvec.getValue() == null)) {
            AlertUtils.showWarning("Attention", "Veuillez sélectionner une AVEC", null);
            return;
        }

        // Dialogue pour saisir le code
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Vérification du code");
        dialog.setHeaderText("Entrez le code de sécurité à vérifier");
        dialog.setContentText("Code:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(code -> {
            try {
                long idAvec = avecId != null ? avecId : comboAvec.getValue().getId();
                Caisse caisse = caisseService.getCaisseByAvecId(idAvec);

                if (caisse != null) {
                    boolean valide = caisseService.verifierCodeSecurite(caisse.getId(), code);
                    if (valide) {
                        AlertUtils.showInfo("Résultat", "Code valide", "Le code est correct.");
                    } else {
                        AlertUtils.showError("Résultat", "Code invalide", "Le code saisi ne correspond pas.");
                    }
                }
            } catch (SQLException e) {
                AlertUtils.showError("Erreur", "Erreur lors de la vérification", e.getMessage());
            }
        });
    }

    /**
     * Affiche les détails d'une caisse (depuis le tableau)
     */
    private void voirDetailsCaisse(Caisse caisse) {
        try {
            Avec avec = avecService.getAvecById(caisse.getAvecId());
            String message = String.format(
                    "ID Caisse: %d\n" +
                            "AVEC: %s\n" +
                            "Code de sécurité: %s",
                    caisse.getId(),
                    avec != null ? avec.getNom() : "AVEC #" + caisse.getAvecId(),
                    caisse.getCodeSecurite()
            );

            AlertUtils.showInfo("Détails de la caisse", "Caisse #" + caisse.getId(), message);
        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Impossible de charger les détails", e.getMessage());
        }
    }
}