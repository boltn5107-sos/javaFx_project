package com.avec.view;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.AgentVillageois;
import com.avec.model.Avec;
import com.avec.model.SessionUtilisateur;
import com.avec.service.AvecService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class AgentVillageoisDashboardView {
    
    private MainApp mainApp;
    private SessionUtilisateur session;
    private AvecService avecService;
    private BorderPane root;
    
    private AgentVillageois agentVillageois;
    private TableView<Avec> avecTable;
    
    private static final String ICONE_TABLEAU_BORD = "📊";
    private static final String ICONE_FORMATION = "📚";
    private static final String ICONE_AVEC = "🤝";
    private static final String ICONE_MODULES = "📖";
    private static final String ICONE_CALENDRIER = "📅";
    private static final String ICONE_HONORAIRES = "💰";
    private static final String ICONE_DECONNEXION = "🚪";
    
    public AgentVillageoisDashboardView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.session = SessionUtilisateur.getInstance();
        this.avecService = new AvecService();
        this.agentVillageois = session.getAgentVillageois();
        createView();
        loadData();
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
        
        Label logoLabel = new Label("🌾");
        logoLabel.setStyle("-fx-font-size: 24px;");
        
        Label titleLabel = new Label("AGENT VILLAGEOIS - Formateur");
        titleLabel.setStyle(Styles.TITRE_PRINCIPAL);
        
        titleBox.getChildren().addAll(logoLabel, titleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox userBox = new HBox(15);
        userBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label userIcon = new Label("👤");
        userIcon.setStyle("-fx-font-size: 20px;");
        
        Label userLabel = new Label(agentVillageois.getPrenom() + " " + agentVillageois.getNom());
        userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
        
        Label roleLabel = new Label("(Agent Villageois)");
        roleLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");
        
        Button logoutButton = new Button(ICONE_DECONNEXION + " Déconnexion");
        logoutButton.setStyle(Styles.BOUTON_SECONDAIRE);
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(Styles.BOUTON_SECONDAIRE_HOVER));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(Styles.BOUTON_SECONDAIRE));
        logoutButton.setOnAction(e -> logout());
        
        userBox.getChildren().addAll(userIcon, userLabel, roleLabel, logoutButton);
        
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
        
        Label avatarLabel = new Label("🌾");
        avatarLabel.setStyle("-fx-font-size: 48px;");
        
        Label nameLabel = new Label(agentVillageois.getNomComplet());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        profileBox.getChildren().addAll(avatarLabel, nameLabel);
        
        VBox menuBox = new VBox(5);
        menuBox.setPadding(new Insets(20, 0, 0, 0));
        
        menuBox.getChildren().addAll(
            createMenuButton(ICONE_TABLEAU_BORD, "Tableau de bord", this::showDashboard),
            createMenuButton(ICONE_AVEC, "Mes AVEC", this::showMesAvec),
            createMenuButton(ICONE_MODULES, "Modules de formation", this::showModules),
            createMenuButton(ICONE_CALENDRIER, "Planning visites", this::showPlanning),
            createMenuButton(ICONE_HONORAIRES, "Mes honoraires", this::showHonoraires)
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
            List<Avec> avecs = avecService.getAvecsByAgentVillageois(agentVillageois.getId());
            int nbAvec = avecs != null ? avecs.size() : 0;
            
            Label welcomeLabel = new Label("Bienvenue, " + agentVillageois.getPrenom());
            welcomeLabel.setStyle(Styles.TITRE_PRINCIPAL);
            
            HBox statsBox = new HBox(20);
            statsBox.setAlignment(Pos.CENTER);
            statsBox.setPadding(new Insets(20, 0, 20, 0));
            
            VBox carte1 = createStatCard("🤝", "AVEC formées", String.valueOf(nbAvec), Styles.VERT_PRINCIPAL);
            VBox carte2 = createStatCard("📚", "Modules", "7", Styles.BLEU_SECONDAIRE);
            VBox carte3 = createStatCard("📍", "Visites prévues", "15", Styles.ACCENT_DORE);
            VBox carte4 = createStatCard("💰", "Honoraires", "0 FCFA", Styles.VERT_PRINCIPAL);
            
            statsBox.getChildren().addAll(carte1, carte2, carte3, carte4);
            
            dashboard.getChildren().addAll(welcomeLabel, statsBox);
            
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur chargement données: " + e.getMessage());
        }
        
        root.setCenter(dashboard);
    }
    
    private void showMesAvec() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("AVEC sous ma responsabilité");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        avecTable = new TableView<>();
        avecTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Avec, String> colNom = new TableColumn<>("Nom AVEC");
        colNom.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        
        TableColumn<Avec, String> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCodeUnique()));
        
        TableColumn<Avec, String> colPhase = new TableColumn<>("Phase");
        colPhase.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhaseCourante().getLibelle()));
        
        TableColumn<Avec, String> colMembres = new TableColumn<>("Membres");
        colMembres.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreMembresMax() + " max"));
        
        TableColumn<Avec, String> colProchaine = new TableColumn<>("Prochaine visite");
        colProchaine.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getProchaineReunion();
            return new javafx.beans.property.SimpleStringProperty(
                date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "À planifier"
            );
        });
        
        TableColumn<Avec, String> colAction = new TableColumn<>("Action");
        colAction.setCellFactory(col -> new TableCell<Avec, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button btn = new Button("📖 Démarrer formation");
                    btn.setStyle(Styles.BOUTON_PRINCIPAL);
                    btn.setOnAction(e -> {
                        Avec avec = getTableView().getItems().get(getIndex());
                        demarrerFormation(avec);
                    });
                    setGraphic(btn);
                }
            }
        });
        
        avecTable.getColumns().addAll(colNom, colCode, colPhase, colMembres, colProchaine, colAction);
        
        try {
            List<Avec> avecs = avecService.getAvecsByAgentVillageois(agentVillageois.getId());
            if (avecs != null) {
                avecTable.setItems(javafx.collections.FXCollections.observableArrayList(avecs));
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les AVEC: " + e.getMessage());
        }
        
        view.getChildren().addAll(title, avecTable);
        root.setCenter(view);
    }
    
    private void demarrerFormation(Avec avec) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Formation AVEC");
        alert.setHeaderText("Démarrage de la formation - " + avec.getNom());
        alert.setContentText("Module 1: Groupes, leadership et élections\n\n" +
                            "Objectifs:\n" +
                            "- Auto-sélection individuelle\n" +
                            "- Rôle de l'Assemblée Générale\n" +
                            "- Rôles des dirigeants\n" +
                            "- Préparation aux élections\n" +
                            "- Procédures d'élection (vote secret avec cailloux)");
        alert.showAndWait();
    }
    
    private void showModules() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Modules de formation AVEC");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        Accordion accordion = new Accordion();
        
        // Module 1
        TitledPane module1 = createModulePane(
            "Module 1: Groupes, leadership et élections",
            "Objectif: Former les membres sur l'auto-sélection et les élections\n\n" +
            "Contenu:\n" +
            "• Auto-sélection individuelle (qualités d'un bon membre)\n" +
            "• Rôle de l'Assemblée Générale\n" +
            "• Rôles des dirigeants (Président, Secrétaire, Trésorier, Compteurs)\n" +
            "• Préparation aux élections\n" +
            "• Procédures d'élection (vote secret avec cailloux)\n\n" +
            "Durée: 1 réunion\n" +
            "Matériel: 3 récipients, cartes de couleur, cailloux"
        );
        
        // Module 2
        TitledPane module2 = createModulePane(
            "Module 2: Règlements - Caisse Solidarité, parts, crédit",
            "Objectif: Établir les règles financières de l'AVEC\n\n" +
            "Contenu:\n" +
            "• Caisse de Solidarité (cotisations et subventions)\n" +
            "• Prix de la part (1-5 parts/semaine)\n" +
            "• Règles de crédit (max 3x épargne, durée max 12 semaines)\n" +
            "• Taux des frais de service (5-10%)\n" +
            "• Sécurité des fonds (coffre à 3 cadenas)\n\n" +
            "Durée: 1 réunion\n" +
            "Matériel: Règlement intérieur vierge"
        );
        
        // Module 3
        TitledPane module3 = createModulePane(
            "Module 3: Élaboration du Règlement Intérieur",
            "Objectif: Finaliser le règlement de l'AVEC\n\n" +
            "Contenu:\n" +
            "• Gouvernance de l'Association\n" +
            "• Composition du comité\n" +
            "• Amendes et infractions\n" +
            "• Signature du règlement par tous les membres\n\n" +
            "Durée: 1-2 réunions\n" +
            "Matériel: Règlement intérieur signé"
        );
        
        // Module 4
        TitledPane module4 = createModulePane(
            "Module 4: Première réunion d'épargne (Semaine 1)",
            "Objectif: Organiser la première collecte d'épargne\n\n" +
            "Contenu:\n" +
            "• Désignation des gardiens des clés\n" +
            "• Cotisations à la Caisse de Solidarité\n" +
            "• Achat de parts (1-5)\n" +
            "• Enregistrement dans les carnets\n" +
            "• Calcul et mémorisation des soldes\n\n" +
            "Durée: 1 réunion\n" +
            "Matériel: Coffre, carnets, tampon"
        );
        
        // Module 5
        TitledPane module5 = createModulePane(
            "Module 5: Première réunion de crédit (Semaine 4)",
            "Objectif: Décaisser les premiers prêts\n\n" +
            "Contenu:\n" +
            "• Procédure de demande de prêt\n" +
            "• Calcul du montant disponible\n" +
            "• Décaissement des prêts\n" +
            "• Enregistrement dans les carnets\n\n" +
            "Durée: 1 réunion\n" +
            "Matériel: Carnets de prêts"
        );
        
        // Module 6
        TitledPane module6 = createModulePane(
            "Module 6: Premier remboursement (Semaine 8)",
            "Objectif: Gérer les remboursements et nouveaux prêts\n\n" +
            "Contenu:\n" +
            "• Remboursement des prêts\n" +
            "• Calcul des intérêts\n" +
            "• Nouveaux décaissements\n" +
            "• Transition vers phase développement\n\n" +
            "Durée: 1 réunion\n" +
            "Matériel: Carnets de prêts, calculatrice"
        );
        
        // Module 7
        TitledPane module7 = createModulePane(
            "Module 7: Répartition du capital, élections et indépendance",
            "Objectif: Clôturer le cycle et préparer le suivant\n\n" +
            "Contenu:\n" +
            "• Répartition du capital (calcul valeur part)\n" +
            "• Élections du nouveau comité\n" +
            "• Préparation du cycle suivant\n" +
            "• Indépendance de l'AVEC\n\n" +
            "Durée: 1 réunion\n" +
            "Matériel: Calculatrice, carnets"
        );
        
        accordion.getPanes().addAll(module1, module2, module3, module4, module5, module6, module7);
        
        view.getChildren().addAll(title, accordion);
        root.setCenter(view);
    }
    
    private TitledPane createModulePane(String title, String content) {
        TitledPane pane = new TitledPane();
        pane.setText(title);
        pane.setStyle("-fx-font-weight: bold;");
        
        TextArea contentArea = new TextArea(content);
        contentArea.setWrapText(true);
        contentArea.setEditable(false);
        contentArea.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";");
        
        pane.setContent(contentArea);
        return pane;
    }
    
    private void showPlanning() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Planning des visites de formation");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        // Calendrier
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(Styles.CHAMP_TEXTE);
        
        TableView<Object> planningTable = new TableView<>();
        planningTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Object, String> colDate = new TableColumn<>("Date");
        colDate.setPrefWidth(100);
        TableColumn<Object, String> colHeure = new TableColumn<>("Heure");
        colHeure.setPrefWidth(80);
        TableColumn<Object, String> colAvec = new TableColumn<>("AVEC");
        colAvec.setPrefWidth(150);
        TableColumn<Object, String> colModule = new TableColumn<>("Module");
        colModule.setPrefWidth(150);
        TableColumn<Object, String> colStatut = new TableColumn<>("Statut");
        colStatut.setPrefWidth(100);
        
        planningTable.getColumns().addAll(colDate, colHeure, colAvec, colModule, colStatut);
        
        view.getChildren().addAll(title, datePicker, planningTable);
        root.setCenter(view);
    }
    
    private void showHonoraires() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Mes honoraires");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                     "-fx-background-radius: 10;");
        
        grid.add(new Label("Nombre de visites effectuées:"), 0, 0);
        grid.add(new Label("0"), 1, 0);
        
        grid.add(new Label("Honoraires par visite:"), 0, 1);
        grid.add(new Label("À définir"), 1, 1);
        
        grid.add(new Label("Total des honoraires:"), 0, 2);
        Label totalLabel = new Label("0 FCFA");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
        grid.add(totalLabel, 1, 2);
        
        grid.add(new Label("Mode de paiement:"), 0, 3);
        grid.add(new Label("En espèces"), 1, 3);
        
        view.getChildren().addAll(title, grid);
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
    
    private void loadData() {
        // Charger les données
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