package com.avec.view;

import com.avec.model.AchatPart;
import com.avec.model.Avec;
import com.avec.model.Membre;
import com.avec.model.Reunion;
import com.avec.service.AchatPartService;
import com.avec.service.AvecService;
import com.avec.service.MembreService;
import com.avec.service.ReunionService;
import com.avec.utils.AlertUtils;
import com.avec.utils.FormatUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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

public class AchatPartView {

    private final Stage primaryStage;
    private final AchatPartService achatPartService;
    private final MembreService membreService;
    private final AvecService avecService;
    private final ReunionService reunionService;
    private final Long avecId;
    private final Long reunionId;

    private TableView<AchatPart> tableAchats;
    private ObservableList<AchatPart> achatsObservable;
    private ComboBox<Membre> comboMembre;
    private ComboBox<Reunion> comboReunion;
    private Label lblTotalParts;
    private Label lblTotalMontant;
    private Label lblPrixPart;

    public AchatPartView(Stage primaryStage, Long avecId, Long reunionId) {
        this.primaryStage = primaryStage;
        this.avecId = avecId;
        this.reunionId = reunionId;
        this.achatPartService = new AchatPartService();
        this.membreService = new MembreService();
        this.avecService = new AvecService();
        this.reunionService = new ReunionService();
        this.achatsObservable = FXCollections.observableArrayList();
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9;");

        root.setTop(createHeader());

        VBox center = new VBox(15);
        center.setPadding(new Insets(20));

        center.getChildren().add(createMembreSelector());
        center.getChildren().add(createAchatsTable());
        VBox.setVgrow(tableAchats, Priority.ALWAYS);
        center.getChildren().add(createActionBar());

        root.setCenter(center);

        chargerDonnees();

        return new Scene(root, 900, 600);
    }

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setPadding(new Insets(20, 20, 0, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Achat de Parts");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblPrixPart = new Label("Prix part: 0 XAF");
        lblPrixPart.setStyle("-fx-font-size: 14px; -fx-padding: 5 15; -fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 5;");

        lblTotalParts = new Label("Parts: 0");
        lblTotalParts.setStyle("-fx-font-size: 14px; -fx-padding: 5 15; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");

        lblTotalMontant = new Label("Total: 0 XAF");
        lblTotalMontant.setStyle("-fx-font-size: 14px; -fx-padding: 5 15; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5;");

        header.getChildren().addAll(title, spacer, lblPrixPart, lblTotalParts, lblTotalMontant);

        return header;
    }

    private VBox createMembreSelector() {
        VBox container = new VBox(10);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label label = new Label("Sélectionner un membre");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox selectorBox = new HBox(10);

        comboMembre = new ComboBox<>();
        comboMembre.setPromptText("Choisir un membre...");
        comboMembre.setPrefWidth(300);
        comboMembre.setOnAction(e -> chargerAchatsMembre());

        comboReunion = new ComboBox<>();
        comboReunion.setPromptText("Choisir une réunion...");
        comboReunion.setPrefWidth(200);

        Button btnRefresh = new Button("Actualiser");
        btnRefresh.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        btnRefresh.setOnAction(e -> chargerDonnees());

        selectorBox.getChildren().addAll(comboMembre, comboReunion, btnRefresh);

        container.getChildren().addAll(label, selectorBox);

        return container;
    }

    private VBox createAchatsTable() {
        VBox container = new VBox(5);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label tableTitle = new Label("Historique des achats de parts");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        tableAchats = new TableView<>();
        tableAchats.setItems(achatsObservable);
        tableAchats.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableAchats.setPrefHeight(250);

        TableColumn<AchatPart, Integer> colNombre = new TableColumn<>("Nombre parts");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreParts"));
        colNombre.setPrefWidth(120);
        colNombre.setStyle("-fx-alignment: CENTER;");

        TableColumn<AchatPart, String> colMontant = new TableColumn<>("Montant");
        colMontant.setCellValueFactory(cellData ->
                new SimpleStringProperty(FormatUtils.formatCurrency(cellData.getValue().getMontantTotal()))
        );
        colMontant.setPrefWidth(150);
        colMontant.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<AchatPart, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cellData -> {
            Long id = cellData.getValue().getReunionId();
            if (id != null && id > 0) {
                Reunion r = reunionService.chercherReunionParId(id);
                if (r != null && r.getDate() != null) {
                    return new SimpleStringProperty(r.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
            return new SimpleStringProperty("");
        });
        colDate.setPrefWidth(120);

        TableColumn<AchatPart, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(150);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox pane = new HBox(5, btnSupprimer);

            {
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnSupprimer.setOnAction(e -> {
                    AchatPart achat = getTableView().getItems().get(getIndex());
                    supprimerAchat(achat);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableAchats.getColumns().addAll(colNombre, colMontant, colDate, colActions);

        container.getChildren().addAll(tableTitle, tableAchats);

        return container;
    }

    private HBox createActionBar() {
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_RIGHT);
        actionBar.setPadding(new Insets(10, 0, 0, 0));

        Button btnNouveau = new Button("Nouvel achat");
        btnNouveau.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnNouveau.setOnAction(e -> creerNouvelAchat());

        Button btnFermer = new Button("Fermer");
        btnFermer.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnFermer.setOnAction(e -> primaryStage.close());

        actionBar.getChildren().addAll(btnNouveau, btnFermer);

        return actionBar;
    }

    private void chargerDonnees() {
        try {
            List<Membre> membres = avecId != null ? 
                    membreService.getMembresByAvecId(avecId) : 
                    membreService.getAllMembres();
            comboMembre.setItems(FXCollections.observableArrayList(membres));

            try {
                List<Reunion> toutesReunions = reunionService.listerReunions();
                comboReunion.setItems(FXCollections.observableArrayList(toutesReunions));
            } catch (Exception e) {
                comboReunion.setItems(FXCollections.observableArrayList());
            }

            if (avecId != null) {
                Avec avec = avecService.getAvecById(avecId);
                if (avec != null && avec.getPrixPart() != null) {
                    lblPrixPart.setText("Prix part: " + FormatUtils.formatCurrency(avec.getPrixPart()));
                }
            }

        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Impossible de charger les données", e.getMessage());
        }
    }

    private void chargerAchatsMembre() {
        Membre membre = comboMembre.getValue();
        if (membre == null || membre.getId() == null) {
            achatsObservable.clear();
            mettreAJourStatistiques(0, BigDecimal.ZERO);
            return;
        }

        try {
            List<AchatPart> achats = achatPartService.getAchatsByMembre(membre.getId());
            achatsObservable.setAll(achats);

            int totalParts = achats.stream().mapToInt(AchatPart::getNombreParts).sum();
            BigDecimal totalMontant = achats.stream()
                    .map(AchatPart::getMontantTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            mettreAJourStatistiques(totalParts, totalMontant);

        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Impossible de charger les achats", e.getMessage());
        }
    }

    private void mettreAJourStatistiques(int totalParts, BigDecimal totalMontant) {
        lblTotalParts.setText("Parts: " + totalParts);
        lblTotalMontant.setText("Total: " + FormatUtils.formatCurrency(totalMontant));
    }

    private void creerNouvelAchat() {
        Membre membre = comboMembre.getValue();
        if (membre == null) {
            AlertUtils.showWarning("Attention", "Veuillez sélectionner un membre", null);
            return;
        }

        Reunion reunion = comboReunion.getValue();

        Dialog<AchatPart> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Nouvel achat de parts");
        dialog.setHeaderText("Enregistrer un achat de parts pour " + membre.getNomComplet());

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label lblMembre = new Label("Membre:");
        Label lblNomMembre = new Label(membre.getNomComplet());

        Label lblPrix = new Label("Prix d'une part:");
        Label lblPrixValue = new Label();
        try {
            if (membre.getAvecId() != null) {
                BigDecimal prix = achatPartService.getPrixPart(membre.getAvecId());
                lblPrixValue.setText(FormatUtils.formatCurrency(prix));
            }
        } catch (SQLException e) {
            lblPrixValue.setText("N/A");
        }

        Spinner<Integer> spinnerParts = new Spinner<>(1, 100, 1);
        spinnerParts.setEditable(true);
        spinnerParts.setPrefWidth(150);

        Label lblTotal = new Label("Montant total:");
        Label lblTotalValue = new Label();
        spinnerParts.valueProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (membre.getAvecId() != null && newVal != null) {
                    BigDecimal prix = achatPartService.getPrixPart(membre.getAvecId());
                    BigDecimal total = prix.multiply(BigDecimal.valueOf(newVal));
                    lblTotalValue.setText(FormatUtils.formatCurrency(total));
                }
            } catch (SQLException e) {
                lblTotalValue.setText("0 XAF");
            }
        });
        lblTotalValue.setText("0 XAF");

        //Reunion reunion = comboReunion.getValue();
        Label lblReunion = new Label("Réunion (optionnel):");
        ComboBox<Reunion> comboReunionDialog = new ComboBox<>();
        comboReunionDialog.getItems().add(null);
        if (comboReunion.getItems() != null) {
            comboReunionDialog.getItems().addAll(comboReunion.getItems());
        }
        if (reunion != null) {
            comboReunionDialog.setValue(reunion);
        }

        grid.add(lblMembre, 0, 0);
        grid.add(lblNomMembre, 1, 0);
        grid.add(lblPrix, 0, 1);
        grid.add(lblPrixValue, 1, 1);
        grid.add(new Label("Nombre de parts*:"), 0, 2);
        grid.add(spinnerParts, 1, 2);
        grid.add(lblTotal, 0, 3);
        grid.add(lblTotalValue, 1, 3);
        grid.add(lblReunion, 0, 4);
        grid.add(comboReunionDialog, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                AchatPart achat = new AchatPart();
                achat.setNombreParts(spinnerParts.getValue());
                achat.setMembreId(membre.getId());
                if (reunion != null) {
                    achat.setReunionId(reunion.getId());
                }
                return achat;
            }
            return null;
        });

        Optional<AchatPart> result = dialog.showAndWait();
        result.ifPresent(achat -> {
            try {
                long idReunion = achat.getReunionId() != null ? achat.getReunionId() : 0;
                achatPartService.acheterParts(achat.getNombreParts(), membre.getId(), idReunion);
                
                chargerAchatsMembre();
                AlertUtils.showInfo("Succès", "Achat enregistré", 
                        "Nombre de parts: " + achat.getNombreParts());
                
            } catch (SQLException | IllegalArgumentException e) {
                AlertUtils.showError("Erreur", "Impossible d'enregistrer l'achat", e.getMessage());
            }
        });
    }

    private void supprimerAchat(AchatPart achat) {
        boolean confirm = AlertUtils.showConfirmation(
                "Confirmation",
                "Supprimer l'achat",
                "Voulez-vous vraiment supprimer cet achat de " + achat.getNombreParts() + " parts?"
        );

        if (confirm) {
            try {
                if (achatPartService.supprimerAchat(achat.getId())) {
                    chargerAchatsMembre();
                    AlertUtils.showInfo("Succès", "Achat supprimé", null);
                }
            } catch (SQLException e) {
                AlertUtils.showError("Erreur", "Impossible de supprimer l'achat", e.getMessage());
            }
        }
    }
}