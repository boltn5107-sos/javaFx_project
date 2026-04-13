package com.avec.view;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.enums.TypeReunion;
import com.avec.model.*;
import com.avec.service.AchatPartService;
import com.avec.service.AvecService;
import com.avec.service.CycleService;
import com.avec.service.MembreService;
import com.avec.service.PretService;
import com.avec.service.ReunionService;
import com.avec.service.UtilisateurService;
import com.avec.utils.FormatUtils;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SecretaireDashboardView {

    private MainApp mainApp;
    private SessionUtilisateur session;
    private MembreService membreService;
    private AvecService avecService;
    private AchatPartService achatPartService;
    private PretService pretService;
    private CycleService cycleService;
    private ReunionService reunionService;
    private BorderPane root;

    private Membre secretaire;
    private Avec avec;

    private TableView<Membre> presenceTable;
    private TableView<Membre> membresTable;
    private TableView<Membre> dashboardMembersTable;

private static final String ICONE_TABLEAU_BORD = "📊";
    private static final String ICONE_PRESENCE = "✅";
    private static final String ICONE_PV = "📝";
    private static final String ICONE_AMENDES = "💰";
    private static final String ICONE_MEMBRES = "👥";
    private static final String ICONE_REUNIONS = "📅";
    private static final String ICONE_PARTS = "📈";
    private static final String ICONE_DECONNEXION = "🚪";

    public SecretaireDashboardView(MainApp mainApp) throws SQLException {
        this.mainApp = mainApp;
        this.session = SessionUtilisateur.getInstance();
        this.membreService = new MembreService();
        this.avecService = new AvecService();
        this.achatPartService = new AchatPartService();
        this.pretService = new PretService();
        this.cycleService = new CycleService();
        this.reunionService = new ReunionService();
        this.secretaire = session.getMembre();
        initData();
        createView();
    }

    private void initData() {
        try {
            if (secretaire != null && secretaire.getAvecId() != null) {
                this.avec = avecService.getAvecById(secretaire.getAvecId());
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les données: " + e.getMessage());
        }
    }

    private void createView() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";");
        root.setTop(createHeader());
        root.setLeft(createSidebar());
        showDashboard();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                "-fx-border-color: " + Styles.GRIS_CLAIR + ";" +
                "-fx-border-width: 0 0 2 0;");

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label logoLabel = new Label("📝");
        logoLabel.setStyle("-fx-font-size: 24px;");

        String titre = avec != null ? "SECRÉTAIRE - " + avec.getNom() : "SECRÉTAIRE";
        Label titleLabel = new Label(titre);
        titleLabel.setStyle(Styles.TITRE_PRINCIPAL);

        titleBox.getChildren().addAll(logoLabel, titleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox userBox = new HBox(15);
        userBox.setAlignment(Pos.CENTER_RIGHT);

        Label userIcon = new Label("👤");
        userIcon.setStyle("-fx-font-size: 20px;");

        Label userLabel = new Label(secretaire.getPrenom() + " " + secretaire.getNom());
        userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");

        Label roleLabel = new Label("(Secrétaire)");
        roleLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");

        Label dateLabel = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");

        Button logoutButton = new Button(ICONE_DECONNEXION + " Déconnexion");
        logoutButton.setStyle(Styles.BOUTON_SECONDAIRE);
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(Styles.BOUTON_SECONDAIRE_HOVER));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(Styles.BOUTON_SECONDAIRE));
        logoutButton.setOnAction(e -> logout());

        userBox.getChildren().addAll(userIcon, userLabel, roleLabel, dateLabel, logoutButton);

        header.getChildren().addAll(titleBox, spacer, userBox);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(280);
        sidebar.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                "-fx-border-color: " + Styles.GRIS_CLAIR + ";" +
                "-fx-border-width: 0 2 0 0;");

        VBox profileBox = new VBox(10);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(0, 0, 20, 0));
        profileBox.setStyle("-fx-border-color: " + Styles.GRIS_CLAIR + ";" +
                "-fx-border-width: 0 0 2 0;");

        Label avatarLabel = new Label("📝");
        avatarLabel.setStyle("-fx-font-size: 48px;");

        String nomAvec = avec != null ? avec.getNom() : "AVEC";
        Label avecLabel = new Label(nomAvec);
        avecLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        profileBox.getChildren().addAll(avatarLabel, avecLabel);

        VBox menuBox = new VBox(5);
        menuBox.setPadding(new Insets(20, 0, 0, 0));

        menuBox.getChildren().addAll(
                createMenuButton(ICONE_TABLEAU_BORD, "Tableau de bord", this::showDashboard),
                createMenuButton(ICONE_PARTS, "Historique Parts", this::showHistoriqueParts),
                createMenuButton("💳", "Demandes de prêts", this::showHistoriquePrets),
                createMenuButton(ICONE_PRESENCE, "Gestion des présences", this::showPresence),
                createMenuButton(ICONE_PV, "Procès-verbaux", this::showPV),
                createMenuButton(ICONE_AMENDES, "Liste des amendes", this::showAmendes),
                createMenuButton(ICONE_MEMBRES, "Liste des membres", this::showMembres),
                createMenuButton(ICONE_REUNIONS, "Calendrier", this::showCalendrier)
        );

        Button btnChangerMdp = new Button("🔒 Changer mot de passe");
        btnChangerMdp.setStyle(Styles.BOUTON_ACCENT);
        btnChangerMdp.setOnAction(e -> showChangerMotDePasse());

        sidebar.getChildren().addAll(profileBox, menuBox, btnChangerMdp);

        return sidebar;
    }

    private Button createMenuButton(String icon, String text, Runnable action) {
        Button button = new Button(icon + "  " + text);
        button.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: " + Styles.NOIR + "; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10 15; " +
                "-fx-alignment: CENTER_LEFT; " +
                "-fx-cursor: hand;");
        button.setMaxWidth(Double.MAX_VALUE);

        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + "; " +
                        "-fx-text-fill: " + Styles.VERT_PRINCIPAL + "; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 15; " +
                        "-fx-alignment: CENTER_LEFT; " +
                        "-fx-cursor: hand;")
        );

        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: transparent; " +
                        "-fx-text-fill: " + Styles.NOIR + "; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 15; " +
                        "-fx-alignment: CENTER_LEFT; " +
                        "-fx-cursor: hand;")
        );

        button.setOnAction(e -> action.run());

        return button;
    }

    private void showDashboard() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(20));
        dashboard.setAlignment(Pos.TOP_CENTER);

        try {
            int totalMembres = membreService.getMembresByAvecId(secretaire.getAvecId()).size();
            List<Membre> membres = membreService.getMembresByAvecId(secretaire.getAvecId());

            Label welcomeLabel = new Label("Tableau de bord du Secrétaire");
            welcomeLabel.setStyle(Styles.TITRE_PRINCIPAL);

            HBox statsBox = new HBox(20);
            statsBox.setAlignment(Pos.CENTER);
            statsBox.setPadding(new Insets(20, 0, 20, 0));

            VBox carte1 = createStatCard("👥", "Membres", String.valueOf(totalMembres), Styles.VERT_PRINCIPAL);
            VBox carte2 = createStatCard("📅", "Réunions", "4", Styles.BLEU_SECONDAIRE);
            VBox carte3 = createStatCard("💰", "Amendes", "---", Styles.ACCENT_DORE);

            statsBox.getChildren().addAll(carte1, carte2, carte3);

            VBox lastMeetingBox = new VBox(10);
            lastMeetingBox.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 20;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            Label lastMeetingTitle = new Label("Dernière réunion");
            lastMeetingTitle.setStyle(Styles.TITRE_SECONDAIRE);

            GridPane infoGrid = new GridPane();
            infoGrid.setHgap(15);
            infoGrid.setVgap(10);

            infoGrid.add(new Label("Date:"), 0, 0);
            infoGrid.add(new Label("Non encore tenue"), 1, 0);
            infoGrid.add(new Label("Type:"), 0, 1);
            infoGrid.add(new Label("---"), 1, 1);
            infoGrid.add(new Label("Présents:"), 0, 2);
            infoGrid.add(new Label("0/" + totalMembres), 1, 2);

           // lastMeetingBox.getChildren().addAll(lastMeetingTitle, infoGrid);

            VBox membresBox = new VBox(10);
            membresBox.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 20;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            membresBox.setMaxHeight(500);

            Label membresTitle = new Label("Membres - Achat de Parts");
            membresTitle.setStyle(Styles.TITRE_SECONDAIRE);

            TableView<Membre> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            table.setPrefHeight(400);
            table.setStyle("-fx-fixed-cell-size: 35;");

            TableColumn<Membre, String> colNom = new TableColumn<>("Nom");
            colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colNom.setPrefWidth(150);

            TableColumn<Membre, String> colPrenom = new TableColumn<>("Prénom");
            colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            colPrenom.setPrefWidth(150);

            TableColumn<Membre, String> colTelephone = new TableColumn<>("Téléphone");
            colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
            colTelephone.setPrefWidth(120);

            TableColumn<Membre, String> colParts = new TableColumn<>("Parts");
            colParts.setCellValueFactory(cellData -> {
                int parts = cellData.getValue().getNombreParts();
                String text = parts >= 0 ? String.valueOf(parts) : "0";
                return new SimpleStringProperty(text);
            });
            colParts.setCellFactory(col -> new TableCell<Membre, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        int parts = getTableView().getItems().get(getIndex()).getNombreParts();
                        setText(parts >= 0 ? String.valueOf(parts) : "0");
                        if (parts > 0) {
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        } else if (parts == 0) {
                            setStyle("-fx-text-fill: #7f8c8d;");
                        } else {
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        }
                    }
                }
            });
            colParts.setPrefWidth(80);
            colParts.setStyle("-fx-alignment: CENTER;");

            TableColumn<Membre, String> colStatut = new TableColumn<>("Statut");
            colStatut.setCellValueFactory(cellData -> {
                int parts = cellData.getValue().getNombreParts();
                String text;
                if (parts >= 0) text = "✓";
                else text = "⚠";
                return new SimpleStringProperty(text);
            });
            colStatut.setCellFactory(col -> new TableCell<Membre, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        int parts = getTableView().getItems().get(getIndex()).getNombreParts();
                        if (parts >= 0) {
                            setStyle("-fx-text-fill: #27ae60; -fx-font-size: 16px;");
                        } else {
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px;");
                        }
                    }
                }
            });
            colStatut.setPrefWidth(60);

            TableColumn<Membre, Void> colAction = new TableColumn<>("Actions");
            colAction.setPrefWidth(200);
            colAction.setCellFactory(col -> new TableCell<Membre, Void>() {
                private final HBox buttons = new HBox(5);
                private final Button btnAcheter = new Button("Acheter");
                private final Button btnVendre = new Button("Vendre");

                {
                    btnAcheter.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 5 10; -fx-font-size: 11px;");
                    btnVendre.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 10; -fx-font-size: 11px;");
                    
                    btnAcheter.setOnAction(e -> {
                        Membre membre = getTableView().getItems().get(getIndex());
                        afficherDialogAchatParts(membre);
                    });
                    
                    btnVendre.setOnAction(e -> {
                        Membre membre = getTableView().getItems().get(getIndex());
                        afficherDialogVenteParts(membre);
                    });
                    
                    buttons.getChildren().addAll(btnAcheter, btnVendre);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : buttons);
                }
            });

            table.getColumns().addAll(colNom, colPrenom, colTelephone, colParts, colAction);
            table.setItems(FXCollections.observableArrayList(membres));
            table.setPrefHeight(300);
            dashboardMembersTable = table;

            Label infoLabel = new Label("Cliquez sur 'Acheter' pour ajouter 1 à 5 parts pour un membre");
            infoLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");

            membresBox.getChildren().addAll(membresTitle, infoLabel, table);

            dashboard.getChildren().addAll(welcomeLabel, statsBox, lastMeetingBox, membresBox);

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur chargement données: " + e.getMessage());
        }

        root.setCenter(dashboard);
    }

    private void showPresence() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Gestion des présences");
        title.setStyle(Styles.TITRE_PRINCIPAL);

        HBox dateBox = new HBox(10);
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(Styles.CHAMP_TEXTE);

        Button chargerButton = new Button("Charger");
        chargerButton.setStyle(Styles.BOUTON_PRINCIPAL);

        dateBox.getChildren().addAll(new Label("Date de la réunion:"), datePicker, chargerButton);

        presenceTable = new TableView<>();
        presenceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Membre, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(150);

        TableColumn<Membre, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(150);

        TableColumn<Membre, Boolean> colPresent = new TableColumn<>("Présent");
        colPresent.setCellFactory(col -> new TableCell<Membre, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item != null && item);
                    setGraphic(checkBox);
                }
            }
        });
        colPresent.setPrefWidth(80);


        presenceTable.getColumns().addAll(colNom, colPrenom, colPresent);

        try {
            List<Membre> membres = membreService.getMembresByAvecId(secretaire.getAvecId());
            presenceTable.setItems(FXCollections.observableArrayList(membres));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
        }

        Button saveButton = new Button("💾 Enregistrer les présences");
        saveButton.setStyle(Styles.BOUTON_PRINCIPAL);
        saveButton.setPrefWidth(300);

        saveButton.setOnAction(e -> {
            showInfo("Succès", "Présences enregistrées avec succès!");
        });

        view.getChildren().addAll(title, dateBox, presenceTable, saveButton);
        root.setCenter(view);
    }

    private void showPV() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Procès-verbaux des réunions");
        title.setStyle(Styles.TITRE_PRINCIPAL);

        ListView<String> pvList = new ListView<>();
        pvList.setPrefHeight(300);
        pvList.getItems().addAll(
                "PV Réunion du 15/03/2024 - Réunion d'épargne",
                "PV Réunion du 08/03/2024 - Réunion de crédit",
                "PV Réunion du 01/03/2024 - Réunion d'épargne"
        );

        HBox buttonBox = new HBox(10);

        Button nouveauPV = new Button("📝 Nouveau PV");
        nouveauPV.setStyle(Styles.BOUTON_PRINCIPAL);

        Button modifierPV = new Button("✏️ Modifier");
        modifierPV.setStyle(Styles.BOUTON_SECONDAIRE);

        Button imprimeButton = new Button("🖨️ Imprimer");
        imprimeButton.setStyle(Styles.BOUTON_ACCENT);

        buttonBox.getChildren().addAll(nouveauPV, modifierPV, imprimeButton);

        view.getChildren().addAll(title, pvList, buttonBox);
        root.setCenter(view);
    }

    private void showAmendes() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Gestion des amendes");
        title.setStyle(Styles.TITRE_PRINCIPAL);

        TableView<Object> amendesTable = new TableView<>();

        TableColumn<Object, String> colDate = new TableColumn<>("Date");
        colDate.setPrefWidth(100);
        TableColumn<Object, String> colMembre = new TableColumn<>("Membre");
        colMembre.setPrefWidth(150);
        TableColumn<Object, String> colMotif = new TableColumn<>("Motif");
        colMotif.setPrefWidth(200);
        TableColumn<Object, String> colMontant = new TableColumn<>("Montant");
        colMontant.setPrefWidth(100);
        TableColumn<Object, String> colStatut = new TableColumn<>("Statut");
        colStatut.setPrefWidth(80);

        amendesTable.getColumns().addAll(colDate, colMembre, colMotif, colMontant, colStatut);

        view.getChildren().addAll(title, amendesTable);
        root.setCenter(view);
    }

    private void showMembres() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Liste des membres");
        title.setStyle(Styles.TITRE_PRINCIPAL);

        membresTable = new TableView<>();

        TableColumn<Membre, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        TableColumn<Membre, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        TableColumn<Membre, String> colCarte = new TableColumn<>("N° Carte");
        colCarte.setCellValueFactory(new PropertyValueFactory<>("numeroCarte"));
        TableColumn<Membre, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        membresTable.getColumns().addAll(colNom, colPrenom, colCarte, colTelephone);

        try {
            List<Membre> membres = membreService.getMembresByAvecId(secretaire.getAvecId());
            membresTable.setItems(FXCollections.observableArrayList(membres));
            membresTable.setPrefHeight(300);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
        }

        view.getChildren().addAll(title, membresTable);
        root.setCenter(view);
    }
    
    private void rafraichirListeMembres() {
        try {
            java.util.List<Membre> liste = membreService.getMembresByAvecId(secretaire.getAvecId());
            if (membresTable != null) {
                membresTable.setItems(FXCollections.observableArrayList(liste));
            }
            if (dashboardMembersTable != null) {
                dashboardMembersTable.setItems(FXCollections.observableArrayList(liste));
            }
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    private void showHistoriqueParts() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Historique détaillé des Parts");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        ComboBox<Membre> comboMembre = new ComboBox<>();
        comboMembre.setPromptText("Sélectionner un membre...");
        comboMembre.setPrefWidth(250);
        
        try {
            List<Membre> membres = membreService.getMembresByAvecId(secretaire.getAvecId());
            comboMembre.setItems(FXCollections.observableArrayList(membres));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres");
        }
        
        TableView<AchatPart> table = new TableView<>();
        table.setPrefHeight(400);
        
        TableColumn<AchatPart, Integer> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreParts"));
        colNombre.setPrefWidth(100);
        
        TableColumn<AchatPart, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(cellData -> {
            int nb = cellData.getValue().getNombreParts();
            String type = nb >= 0 ? "ACHAT" : "VENTE";
            return new SimpleStringProperty(type);
        });
        colType.setCellFactory(col -> new TableCell<AchatPart, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("VENTE".equals(item)) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    }
                }
            }
        });
        colType.setPrefWidth(100);
        
        TableColumn<AchatPart, String> colMontant = new TableColumn<>("Montant");
        colMontant.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatUtils.formatCurrency(cellData.getValue().getMontantTotal()))
        );
        colMontant.setPrefWidth(150);
        
        TableColumn<AchatPart, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getReunion() != null && cellData.getValue().getReunion().getDate() != null) {
                return new SimpleStringProperty(cellData.getValue().getReunion().getDateFormatted());
            }
            return new SimpleStringProperty("-");
        });
        colDate.setPrefWidth(120);
        
        table.getColumns().addAll(colType, colNombre, colMontant, colDate);
        
        HBox statsBox = new HBox(30);
        statsBox.setStyle("-fx-padding: 15; -fx-background-color: #ecf0f1; -fx-background-radius: 10;");
        
        Label lblTotalAchat = new Label("Parts achetées: 0");
        lblTotalAchat.setStyle("-fx-font-size: 14px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        Label lblTotalVente = new Label("Parts vendues: 0");
        lblTotalVente.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        
        Label lblTotalNet = new Label("Net: 0");
        lblTotalNet.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        statsBox.getChildren().addAll(lblTotalAchat, lblTotalVente, lblTotalNet);
        
        Button btnVoir = new Button("Voir");
        btnVoir.setStyle(Styles.BOUTON_PRINCIPAL);
        btnVoir.setOnAction(e -> {
            Membre membre = comboMembre.getValue();
            if (membre == null) {
                showAlert("Attention", "Veuillez sélectionner un membre");
                return;
            }
            try {
                List<AchatPart> historique = achatPartService.getAchatsByMembre(membre.getId());
                table.setItems(FXCollections.observableArrayList(historique));
                
                int totalAchat = historique.stream()
                    .filter(a -> a.getNombreParts() > 0)
                    .mapToInt(AchatPart::getNombreParts)
                    .sum();
                
                int totalVente = historique.stream()
                    .filter(a -> a.getNombreParts() < 0)
                    .mapToInt(AchatPart::getNombreParts)
                    .sum();
                
                int net = totalAchat + totalVente;
                
                lblTotalAchat.setText("Achats: " + totalAchat);
                lblTotalVente.setText("Ventes: " + Math.abs(totalVente));
                lblTotalNet.setText("Net: " + net);
                
                if (net >= 0) {
                    lblTotalNet.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
                } else {
                    lblTotalNet.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
                }
            } catch (SQLException ex) {
                showAlert("Erreur", "Impossible de charger l'historique: " + ex.getMessage());
            }
        });
        
        HBox filterBox = new HBox(10, comboMembre, btnVoir);
        
        view.getChildren().addAll(title, filterBox, statsBox, table);
        root.setCenter(view);
    }

    private void showCalendrier() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Calendrier des réunions");
        title.setStyle(Styles.TITRE_PRINCIPAL);

        try {
            Long avecId = secretaire.getAvecId();
            List<Reunion> reunions;
            if (avecId != null) {
                Cycle cycleActif = cycleService.getCycleEnCours(avecId);
                if (cycleActif != null) {
                    reunions = reunionService.listerReunionsParCycleId(cycleActif.getId());
                } else {
                    reunions = reunionService.listerReunions();
                }
            } else {
                reunions = reunionService.listerReunions();
            }

            if (reunions == null || reunions.isEmpty()) {
                Label noReunion = new Label("Aucune réunion trouvée");
                noReunion.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
                view.getChildren().addAll(title, noReunion);
            } else {
                TableView<Reunion> table = new TableView<>();
                table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                table.setPrefHeight(300);

                TableColumn<Reunion, String> colDate = new TableColumn<>("Date");
                colDate.setCellValueFactory(cellData -> {
                    if (cellData.getValue().getDate() != null) {
                        return new SimpleStringProperty(
                            cellData.getValue().getDate().format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            )
                        );
                    }
                    return new SimpleStringProperty("-");
                });
                colDate.setPrefWidth(120);

                TableColumn<Reunion, String> colType = new TableColumn<>("Type");
                colType.setCellValueFactory(new PropertyValueFactory<>("type"));
                colType.setPrefWidth(120);

                TableColumn<Reunion, String> colStatut = new TableColumn<>("Statut");
                colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
                colStatut.setCellFactory(col -> new TableCell<Reunion, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            if ("EN_COURS".equals(item)) {
                                setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            } else if ("PLANIFIEE".equals(item)) {
                                setStyle("-fx-text-fill: #3498db;");
                            } else if ("TERMINE".equals(item)) {
                                setStyle("-fx-text-fill: #95a5a6;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                });
                colStatut.setPrefWidth(100);

                table.getColumns().addAll(colDate, colType, colStatut);
                table.setItems(FXCollections.observableArrayList(reunions));

                view.getChildren().addAll(title, table);
            }
        } catch (Exception e) {
            Label errorLabel = new Label("Erreur: " + e.getMessage());
            view.getChildren().addAll(title, errorLabel);
        }

        root.setCenter(view);
    }

    private void showHistoriquePrets() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Historique des prêts");
        title.setStyle(Styles.TITRE_PRINCIPAL);

        HBox buttonBox = new HBox(10);
        
        Button btnNouvelleDemande = new Button("➕ Nouvelle demande");
        btnNouvelleDemande.setStyle(Styles.BOUTON_PRINCIPAL);
        btnNouvelleDemande.setOnAction(e -> afficherDialogNouvelleDemandePret());
        
        buttonBox.getChildren().add(btnNouvelleDemande);

        TableView<Pret> table = new TableView<>();
        table.setPrefHeight(450);
        
        TableColumn<Pret, String> colNumero = new TableColumn<>("N° Prêt");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroPret"));
        colNumero.setPrefWidth(110);
        
        TableColumn<Pret, String> colMembre = new TableColumn<>("Membre");
        colMembre.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEmprunteur() != null) {
                return new SimpleStringProperty(cellData.getValue().getEmprunteur().getNomComplet());
            }
            return new SimpleStringProperty("-");
        });
        colMembre.setPrefWidth(130);
        
        TableColumn<Pret, String> colMontant = new TableColumn<>("Montant");
        colMontant.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatUtils.formatCurrency(cellData.getValue().getMontantInitial()))
        );
        colMontant.setPrefWidth(100);
        
        TableColumn<Pret, String> colRembourse = new TableColumn<>("Remboursé");
        colRembourse.setCellValueFactory(cellData -> {
            java.math.BigDecimal initial = cellData.getValue().getMontantInitial();
            java.math.BigDecimal restant = cellData.getValue().getMontantRestantDu();
            if (initial != null && restant != null) {
                java.math.BigDecimal rembourse = initial.subtract(restant);
                return new SimpleStringProperty(FormatUtils.formatCurrency(rembourse));
            }
            return new SimpleStringProperty("0");
        });
        colRembourse.setPrefWidth(100);
        
        TableColumn<Pret, String> colRestant = new TableColumn<>("Restant");
        colRestant.setCellValueFactory(cellData -> 
            new SimpleStringProperty(FormatUtils.formatCurrency(cellData.getValue().getMontantRestantDu()))
        );
        colRestant.setPrefWidth(100);
        
        TableColumn<Pret, String> colDuree = new TableColumn<>("Durée");
        colDuree.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDureeEnSemaines() + " sem.")
        );
        colDuree.setPrefWidth(70);
        
        TableColumn<Pret, String> colDate = new TableColumn<>("Échéance");
        colDate.setCellValueFactory(cellData -> {
            java.time.LocalDate date = cellData.getValue().getDateEcheance();
            if (date != null) {
                return new SimpleStringProperty(date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            return new SimpleStringProperty("-");
        });
        colDate.setPrefWidth(100);
        
        TableColumn<Pret, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setCellFactory(col -> new TableCell<Pret, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("EN_ATTENTE".equals(item)) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    } else if ("ACTIF".equals(item)) {
                        setStyle("-fx-text-fill: #27ae60;");
                    } else if ("REMBOURSE".equals(item)) {
                        setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                    } else if ("IMPAYE".equals(item) || "EN_RETARD".equals(item)) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        colStatut.setPrefWidth(100);

        table.getColumns().addAll(colNumero, colMembre, colMontant, colRembourse, colRestant, colDuree, colDate, colStatut);

        try {
            List<Pret> prets = pretService.listerPretsParAvecId(secretaire.getAvecId());
            
            for (Pret pret : prets) {
                if (pret.getEmprunteurId() != null) {
                    try {
                        Membre membre = membreService.getMembreById(pret.getEmprunteurId());
                        pret.setEmprunteur(membre);
                    } catch (Exception e) {}
                }
            }
            
            table.setItems(FXCollections.observableArrayList(prets));
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger l'historique: " + e.getMessage());
        }

        view.getChildren().addAll(title, buttonBox, table);
        root.setCenter(view);
    }

    private void afficherDialogNouvelleDemandePret() {
        Dialog<Pret> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle demande de prêt");
        dialog.setHeaderText("Enregistrer une demande de prêt");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        Label lblMembre = new Label("Membre:");
        ComboBox<Membre> comboMembre = new ComboBox<>();
        comboMembre.setPromptText("Sélectionner un membre...");
        comboMembre.setPrefWidth(250);
        
        try {
            List<Membre> membres = membreService.getMembresByAvecId(secretaire.getAvecId());
            comboMembre.setItems(FXCollections.observableArrayList(membres));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres");
        }

        Label lblMontant = new Label("Montant:");
        TextField montantField = new TextField();
        montantField.setPromptText("Montant demandé");
        montantField.setPrefWidth(200);

        Label lblDuree = new Label("Durée (semaines):");
        ComboBox<Integer> dureeCombo = new ComboBox<>();
        dureeCombo.getItems().addAll(4, 8, 12, 16, 20, 24, 32, 40, 48);
        dureeCombo.setValue(12);
        dureeCombo.setPrefWidth(100);

        Label lblReunion = new Label("Réunion:");
        ComboBox<Reunion> reunionCombo = new ComboBox<>();
        reunionCombo.setPromptText("Sélectionner une réunion...");
        reunionCombo.setPrefWidth(250);
        
        try {
            List<Reunion> reunions = reunionService.getReunionsApprouveesParAvecId(secretaire.getAvecId());
            reunionCombo.setItems(FXCollections.observableArrayList(reunions));
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les réunions");
        }

        grid.add(lblMembre, 0, 0);
        grid.add(comboMembre, 1, 0);
        grid.add(lblMontant, 0, 1);
        grid.add(montantField, 1, 1);
        grid.add(lblDuree, 0, 2);
        grid.add(dureeCombo, 1, 2);
        grid.add(lblReunion, 0, 3);
        grid.add(reunionCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Membre membre = comboMembre.getValue();
                if (membre == null) {
                    showAlert("Erreur", "Veuillez sélectionner un membre");
                    return null;
                }
                
                String montantText = montantField.getText().trim();
                if (montantText.isEmpty()) {
                    showAlert("Erreur", "Veuillez saisir un montant");
                    return null;
                }
                
                try {
                    BigDecimal montant = new BigDecimal(montantText);
                    if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                        showAlert("Erreur", "Le montant doit être supérieur à 0");
                        return null;
                    }
                    BigDecimal epargne = BigDecimal.ZERO;
                    if (avec != null && avec.getPrixPart() != null) {
                        epargne = BigDecimal.valueOf(membre.getNombreParts()).multiply(avec.getPrixPart());
                    }
                    BigDecimal plafond = epargne.multiply(BigDecimal.valueOf(3));
                    if (montant.compareTo(plafond) > 0) {
                        showAlert("Erreur", "Le montant ne peut pas dépasser 3x l'épargne du membre.\nÉpargne: " + epargne + " | Plafond: " + plafond);
                        return null;
                    }
                    
                    Reunion reunion = reunionCombo.getValue();
                    if (reunion == null) {
                        showAlert("Erreur", "Veuillez sélectionner une réunion");
                        return null;
                    }
                    
                    Pret pret = new Pret();
                    pret.setEmprunteur(membre);
                    pret.setEmprunteurId(membre.getId());
                    pret.setMontantInitial(montant);
                    pret.setDureeEnSemaines(dureeCombo.getValue());
                    pret.setReunionDecaissementId(reunion.getId());
                    if (avec != null) {
                        pret.setFraisServiceMensuel(avec.getTauxFraisServiceMensuel());
                    }
                    return pret;
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Montant invalide");
                    return null;
                }
            }
            return null;
        });

        Optional<Pret> result = dialog.showAndWait();
        result.ifPresent(pret -> {
            boolean success = pretService.enregistrerPret(pret);
            if (success) {
                showInfo("Succès", "Demande de prêt enregistrée avec succès!\nEn attente d'approbation du président.");
                showHistoriquePrets();
            } else {
                showAlert("Erreur", "Impossible d'enregistrer la demande de prêt");
            }
        });
    }

    private void afficherDialogAchatParts(Membre membre) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Acheter des Parts");
        dialog.setHeaderText("Achat de parts pour " + membre.getNomComplet());

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        Label lblMembre = new Label("Membre:");
        Label lblNomMembre = new Label(membre.getNomComplet());
        lblNomMembre.setStyle("-fx-font-weight: bold;");

        Label lblPartsActuelles = new Label("Parts actuelles:");
        Label lblNbParts = new Label(String.valueOf(membre.getNombreParts()));

        Label lblReunion = new Label("Réunion:");
        ComboBox<Reunion> comboReunion = new ComboBox<>();
        comboReunion.setPromptText("Sélectionner une réunion...");
        comboReunion.setPrefWidth(250);
        
        try {
            List<Reunion> toutesReunions;
            if (secretaire.getAvecId() != null) {
                Cycle cycleActif = cycleService.getCycleEnCours(secretaire.getAvecId());
                if (cycleActif != null) {
                    toutesReunions = reunionService.listerReunionsParCycleId(cycleActif.getId());
                } else {
                    toutesReunions = reunionService.listerReunions();
                }
            } else {
                toutesReunions = reunionService.listerReunions();
            }
            
            List<Reunion> reunionsEpargne = toutesReunions.stream()
                .filter(r -> r.getType() == TypeReunion.EPARGNE)
                .toList();
            
            if (reunionsEpargne != null && !reunionsEpargne.isEmpty()) {
                comboReunion.setItems(FXCollections.observableArrayList(reunionsEpargne));
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les réunions");
        }

        Label lblNombre = new Label("Nombre de parts:");
        Spinner<Integer> spinnerParts = new Spinner<>(1, 5, 1);
        spinnerParts.setEditable(true);
        spinnerParts.setPrefWidth(100);

        grid.add(lblMembre, 0, 0);
        grid.add(lblNomMembre, 1, 0);
        grid.add(lblPartsActuelles, 0, 1);
        grid.add(lblNbParts, 1, 1);
        grid.add(lblReunion, 0, 2);
        grid.add(comboReunion, 1, 2);
        grid.add(lblNombre, 0, 3);
        grid.add(spinnerParts, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Reunion reunion = comboReunion.getValue();
                if (reunion == null) {
                    showAlert("Erreur", "Veuillez sélectionner une réunion");
                    return null;
                }
                int nombreParts = spinnerParts.getValue();
                if (nombreParts <= 0) {
                    showAlert("Erreur", "Le nombre de parts doit être positif");
                    return null;
                }
                
                try {
                    AchatPart achat = new AchatPart();
                    achat.setMembre(membre);
                    achat.setMembreId(membre.getId());
                    achat.setNombreParts(nombreParts);
                    achat.setDate(LocalDate.now());
                    achat.setTypeAchat("ACHAT");
                    achat.setReunion(reunion);
                    achat.setReunionId(reunion.getId());
                    
                    if (avec != null) {
                        achat.setMontant(BigDecimal.valueOf(nombreParts).multiply(avec.getPrixPart()));
                    }
                    
                    boolean success = achatPartService.enregistrerAchatPart(achat);
                    if (success) {
                        showInfo("Succès", "Achat de " + nombreParts + " part(s) enregistré!");
                        showHistoriqueParts();
                    } else {
                        showAlert("Erreur", "Impossible d'enregistrer l'achat");
                    }
                } catch (Exception ex) {
                    showAlert("Erreur", "Erreur: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void afficherDialogVenteParts(Membre membre) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Vendre des Parts");
        dialog.setHeaderText("Vendre des parts pour " + membre.getNomComplet());

        ButtonType saveButtonType = new ButtonType("Vendre", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        Label lblMembre = new Label("Membre:");
        Label lblNomMembre = new Label(membre.getNomComplet());
        lblNomMembre.setStyle("-fx-font-weight: bold;");

        Label lblPartsActuelles = new Label("Parts actuelles:");
        Label lblNbParts = new Label(String.valueOf(membre.getNombreParts()));

        Label lblReunion = new Label("Réunion:");
        ComboBox<Reunion> comboReunion = new ComboBox<>();
        comboReunion.setPromptText("Sélectionner une réunion...");
        comboReunion.setPrefWidth(250);
        
        try {
            List<Reunion> toutesReunions;
            if (secretaire.getAvecId() != null) {
                Cycle cycleActif = cycleService.getCycleEnCours(secretaire.getAvecId());
                if (cycleActif != null) {
                    toutesReunions = reunionService.listerReunionsParCycleId(cycleActif.getId());
                } else {
                    toutesReunions = reunionService.listerReunions();
                }
            } else {
                toutesReunions = reunionService.listerReunions();
            }
            
            List<Reunion> reunionsEpargne = toutesReunions.stream()
                .filter(r -> r.getType() == TypeReunion.EPARGNE)
                .toList();
            
            if (reunionsEpargne != null && !reunionsEpargne.isEmpty()) {
                comboReunion.setItems(FXCollections.observableArrayList(reunionsEpargne));
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les réunions");
        }

        Label lblNombre = new Label("Nombre de parts:");
        Spinner<Integer> spinnerParts = new Spinner<>(1, membre.getNombreParts(), 1);
        spinnerParts.setEditable(true);
        spinnerParts.setPrefWidth(100);

        grid.add(lblMembre, 0, 0);
        grid.add(lblNomMembre, 1, 0);
        grid.add(lblPartsActuelles, 0, 1);
        grid.add(lblNbParts, 1, 1);
        grid.add(lblReunion, 0, 2);
        grid.add(comboReunion, 1, 2);
        grid.add(lblNombre, 0, 3);
        grid.add(spinnerParts, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Reunion reunion = comboReunion.getValue();
                if (reunion == null) {
                    showAlert("Erreur", "Veuillez sélectionner une réunion");
                    return null;
                }
                int nombreParts = spinnerParts.getValue();
                if (nombreParts <= 0) {
                    showAlert("Erreur", "Le nombre de parts doit être positif");
                    return null;
                }
                if (nombreParts > membre.getNombreParts()) {
                    showAlert("Erreur", "Le nombre de parts à vendre ne peut pas dépasser les parts actuelles");
                    return null;
                }
                
                try {
                    AchatPart vente = new AchatPart();
                    vente.setMembre(membre);
                    vente.setMembreId(membre.getId());
                    vente.setNombreParts(-nombreParts);
                    vente.setDate(LocalDate.now());
                    vente.setTypeAchat("VENTE");
                    vente.setReunion(reunion);
                    vente.setReunionId(reunion.getId());
                    
                    if (avec != null) {
                        vente.setMontant(BigDecimal.valueOf(-nombreParts).multiply(avec.getPrixPart()));
                    }
                    
                    boolean success = achatPartService.enregistrerAchatPart(vente);
                    if (success) {
                        showInfo("Succès", "Vente de " + nombreParts + " part(s) enregistrée!");
                        showHistoriqueParts();
                    } else {
                        showAlert("Erreur", "Impossible d'enregistrer la vente");
                    }
                } catch (Exception ex) {
                    showAlert("Erreur", "Erreur: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showChangerMotDePasse() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Changer mon mot de passe");
        dialog.setHeaderText("Modification du mot de passe");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        Label infoLabel = new Label("Veuillez saisir vos informations :");
        infoLabel.setStyle("-fx-font-weight: bold;");

        PasswordField ancienMdpField = new PasswordField();
        ancienMdpField.setPromptText("Ancien mot de passe");
        ancienMdpField.setStyle(Styles.CHAMP_TEXTE);

        PasswordField nouveauMdpField = new PasswordField();
        nouveauMdpField.setPromptText("Nouveau mot de passe (min. 4 caractères)");
        nouveauMdpField.setStyle(Styles.CHAMP_TEXTE);

        PasswordField confirmationMdpField = new PasswordField();
        confirmationMdpField.setPromptText("Confirmer le nouveau mot de passe");
        confirmationMdpField.setStyle(Styles.CHAMP_TEXTE);

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        messageLabel.setWrapText(true);

        content.getChildren().addAll(
                infoLabel,
                new Separator(),
                new Label("Ancien mot de passe :"), ancienMdpField,
                new Label("Nouveau mot de passe :"), nouveauMdpField,
                new Label("Confirmation :"), confirmationMdpField,
                messageLabel
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText("Modifier");

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String ancienMdp = ancienMdpField.getText();
                String nouveauMdp = nouveauMdpField.getText();
                String confirmation = confirmationMdpField.getText();

                if (ancienMdp == null || ancienMdp.isEmpty()) {
                    messageLabel.setText("Veuillez saisir votre ancien mot de passe");
                    return null;
                }

                if (nouveauMdp == null || nouveauMdp.isEmpty()) {
                    messageLabel.setText("Veuillez saisir un nouveau mot de passe");
                    return null;
                }

                if (nouveauMdp.length() < 4) {
                    messageLabel.setText("Le nouveau mot de passe doit contenir au moins 4 caractères");
                    return null;
                }

                if (!nouveauMdp.equals(confirmation)) {
                    messageLabel.setText("Les nouveaux mots de passe ne correspondent pas");
                    return null;
                }

                Long userId = session.getId();
                UtilisateurService userService = new UtilisateurService();
                if (userService.changerMotDePasse(userId, ancienMdp, nouveauMdp, confirmation)) {
                    showInfo("Succès", "Votre mot de passe a été modifié avec succès !");
                    return ButtonType.OK;
                } else {
                    messageLabel.setText("Ancien mot de passe incorrect");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
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

    private void logout() {
        session.deconnecter();
        LoginView loginView = new LoginView(mainApp);
        mainApp.getPrimaryStage().getScene().setRoot(loginView.getRoot());
        mainApp.getPrimaryStage().setMaximized(false);
        mainApp.getPrimaryStage().centerOnScreen();
    }
    
    private VBox createStatCard(String icon, String label, String value, String color) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 20;" +
                "-fx-alignment: center;" +
                "-fx-min-width: 150;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        card.getChildren().addAll(iconLabel, valueLabel, textLabel);
        return card;
    }

    public BorderPane getRoot() {
        return root;
    }
}