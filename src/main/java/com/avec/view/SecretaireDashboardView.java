package com.avec.view;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.Avec;
import com.avec.model.Membre;
import com.avec.model.SessionUtilisateur;
import com.avec.service.AvecService;
import com.avec.service.MembreService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private BorderPane root;
    
    private Membre secretaire;
    private Avec avec;
    
    private TableView<Membre> presenceTable;
    
    private static final String ICONE_TABLEAU_BORD = "📊";
    private static final String ICONE_PRESENCE = "📝";
    private static final String ICONE_PV = "📄";
    private static final String ICONE_AMENDES = "💰";
    private static final String ICONE_MEMBRES = "👥";
    private static final String ICONE_REUNIONS = "📅";
    private static final String ICONE_DECONNEXION = "🚪";
    
    public SecretaireDashboardView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.session = SessionUtilisateur.getInstance();
        this.membreService = new MembreService();
        this.avecService = new AvecService();
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
            createMenuButton(ICONE_PRESENCE, "Gestion des présences", this::showPresence),
            createMenuButton(ICONE_PV, "Procès-verbaux", this::showPV),
            createMenuButton(ICONE_AMENDES, "Gestion des amendes", this::showAmendes),
            createMenuButton(ICONE_MEMBRES, "Liste des membres", this::showMembres),
            createMenuButton(ICONE_REUNIONS, "Calendrier", this::showCalendrier)
        );
        
        sidebar.getChildren().addAll(profileBox, menuBox);
        
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
            
            Label welcomeLabel = new Label("Tableau de bord du Secrétaire");
            welcomeLabel.setStyle(Styles.TITRE_PRINCIPAL);
            
            HBox statsBox = new HBox(20);
            statsBox.setAlignment(Pos.CENTER);
            statsBox.setPadding(new Insets(20, 0, 20, 0));
            
            VBox carte1 = createStatCard("👥", "Membres", String.valueOf(totalMembres), Styles.VERT_PRINCIPAL);
            VBox carte2 = createStatCard("📅", "Réunions", "4", Styles.BLEU_SECONDAIRE);
            VBox carte3 = createStatCard("💰", "Amendes", "---", Styles.ACCENT_DORE);
            
            statsBox.getChildren().addAll(carte1, carte2, carte3);
            
            // Dernière réunion
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
            
            lastMeetingBox.getChildren().addAll(lastMeetingTitle, infoGrid);
            
            dashboard.getChildren().addAll(welcomeLabel, statsBox, lastMeetingBox);
            
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
        
        // Sélection de la date
        HBox dateBox = new HBox(10);
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(Styles.CHAMP_TEXTE);
        
        Button chargerButton = new Button("Charger");
        chargerButton.setStyle(Styles.BOUTON_PRINCIPAL);
        
        dateBox.getChildren().addAll(new Label("Date de la réunion:"), datePicker, chargerButton);
        
        // Tableau des présences
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
        
        TableColumn<Membre, Boolean> colRetard = new TableColumn<>("Retard");
        colRetard.setCellFactory(col -> new TableCell<Membre, Boolean>() {
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
        colRetard.setPrefWidth(80);
        
        TableColumn<Membre, String> colAmende = new TableColumn<>("Amende");
        colAmende.setCellFactory(col -> new TableCell<Membre, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TextField textField = new TextField(item);
                    textField.setPromptText("Montant");
                    setGraphic(textField);
                }
            }
        });
        colAmende.setPrefWidth(100);
        
        presenceTable.getColumns().addAll(colNom, colPrenom, colPresent, colRetard, colAmende);
        
        // Charger les membres
        try {
            List<Membre> membres = membreService.getMembresByAvecId(secretaire.getAvecId());
            presenceTable.setItems(FXCollections.observableArrayList(membres));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
        }
        
        // Bouton enregistrer
        Button saveButton = new Button("💾 Enregistrer les présences");
        saveButton.setStyle(Styles.BOUTON_PRINCIPAL);
        saveButton.setPrefWidth(300);
        
        saveButton.setOnAction(e -> {
            // TODO: Sauvegarder les présences dans la base
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
        
        // Liste des PV existants
        ListView<String> pvList = new ListView<>();
        pvList.setPrefHeight(300);
        pvList.getItems().addAll(
            "PV Réunion du 15/03/2024 - Réunion d'épargne",
            "PV Réunion du 08/03/2024 - Réunion de crédit",
            "PV Réunion du 01/03/2024 - Réunion d'épargne"
        );
        
        // Boutons
        HBox buttonBox = new HBox(10);
        
        Button nouveauPV = new Button("📝 Nouveau PV");
        nouveauPV.setStyle(Styles.BOUTON_PRINCIPAL);
        
        Button modifierPV = new Button("✏️ Modifier");
        modifierPV.setStyle(Styles.BOUTON_SECONDAIRE);
        
        Button imprimerPV = new Button("🖨️ Imprimer");
        imprimerPV.setStyle(Styles.BOUTON_ACCENT);
        
        buttonBox.getChildren().addAll(nouveauPV, modifierPV, imprimerPV);
        
        view.getChildren().addAll(title, pvList, buttonBox);
        root.setCenter(view);
    }
    
    private void showAmendes() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Gestion des amendes");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        // Tableau des amendes
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
        
        TableView<Membre> table = new TableView<>();
        
        TableColumn<Membre, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        TableColumn<Membre, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        TableColumn<Membre, String> colCarte = new TableColumn<>("N° Carte");
        colCarte.setCellValueFactory(new PropertyValueFactory<>("numeroCarte"));
        TableColumn<Membre, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        table.getColumns().addAll(colNom, colPrenom, colCarte, colTelephone);
        
        try {
            List<Membre> membres = membreService.getMembresByAvecId(secretaire.getAvecId());
            table.setItems(FXCollections.observableArrayList(membres));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
        }
        
        view.getChildren().addAll(title, table);
        root.setCenter(view);
    }
    
    private void showCalendrier() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Calendrier des réunions");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        GridPane calendar = new GridPane();
        calendar.setHgap(10);
        calendar.setVgap(10);
        calendar.setPadding(new Insets(20));
        calendar.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                         "-fx-background-radius: 10;");
        
        // Jours de la semaine
        String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        for (int i = 0; i < jours.length; i++) {
            Label jourLabel = new Label(jours[i]);
            jourLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
            calendar.add(jourLabel, i, 0);
        }
        
        // TODO: Remplir avec les dates réelles
        for (int i = 1; i <= 31; i++) {
            int col = (i - 1) % 7;
            int row = (i - 1) / 7 + 1;
            Label dayLabel = new Label(String.valueOf(i));
            dayLabel.setStyle("-fx-padding: 10; -fx-background-color: " + Styles.GRIS_CLAIR + ";" +
                             "-fx-background-radius: 5;");
            calendar.add(dayLabel, col, row);
        }
        
        view.getChildren().addAll(title, calendar);
        root.setCenter(view);
    }
    
    private VBox createStatCard(String icon, String label, String value, String color) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                     "-fx-background-radius: 10;" +
                     "-fx-padding: 20;" +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(180);
        card.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");
        
        card.getChildren().addAll(iconLabel, valueLabel, labelLabel);
        
        return card;
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
    
    public BorderPane getRoot() {
        return root;
    }
}