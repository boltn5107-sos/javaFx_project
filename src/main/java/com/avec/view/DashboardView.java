package com.avec.view;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.Utilisateur;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class DashboardView {
    
    private MainApp mainApp;
    private Utilisateur utilisateur;
    private BorderPane root;
    
    // Constantes pour les icônes (émojis)
    private static final String ICONE_TABLEAU_BORD = "📊";
    private static final String ICONE_MEMBRES = "👥";
    private static final String ICONE_PRETS = "💰";
    private static final String ICONE_REUNIONS = "📅";
    private static final String ICONE_CYCLES = "🔄";
    private static final String ICONE_ACHATS = "🛒";
    private static final String ICONE_REGLES = "📋";
    private static final String ICONE_VISITES = "📍";
    private static final String ICONE_DECONNEXION = "🚪";
    
    public DashboardView(MainApp mainApp, Utilisateur utilisateur) {
        this.mainApp = mainApp;
        this.utilisateur = utilisateur;
        createView();
    }
    
    private void createView() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";");
        
        // En-tête
        HBox header = createHeader();
        root.setTop(header);
        
        // Menu latéral
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);
        
        // Zone centrale
        VBox centerContent = createCenterContent();
        root.setCenter(centerContent);
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                       "-fx-border-color: " + Styles.GRIS_CLAIR + ";" +
                       "-fx-border-width: 0 0 2 0;");
        
        // Logo/Titre
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label logoLabel = new Label("🤝");
        logoLabel.setStyle("-fx-font-size: 24px;");
        
        Label titleLabel = new Label("GESTION AVEC");
        titleLabel.setStyle(Styles.TITRE_PRINCIPAL);
        
        titleBox.getChildren().addAll(logoLabel, titleLabel);
        
        // Espaceur
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Info utilisateur
        HBox userBox = new HBox(15);
        userBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label userIcon = new Label("👤");
        userIcon.setStyle("-fx-font-size: 20px;");
        
        Label userLabel = new Label(utilisateur.getPrenom() + " " + utilisateur.getNom());
        userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
        
        Button logoutButton = new Button(ICONE_DECONNEXION + " Déconnexion");
        logoutButton.setStyle(Styles.BOUTON_SECONDAIRE);
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(Styles.BOUTON_SECONDAIRE_HOVER));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(Styles.BOUTON_SECONDAIRE));
        logoutButton.setOnAction(e -> logout());
        
        userBox.getChildren().addAll(userIcon, userLabel, logoutButton);
        
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
        
        // Profil rapide
        VBox profileBox = new VBox(10);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(0, 0, 20, 0));
        profileBox.setStyle("-fx-border-color: " + Styles.GRIS_CLAIR + ";" +
                           "-fx-border-width: 0 0 2 0;");
        
        Label avatarLabel = new Label("👤");
        avatarLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
        
        Label nameLabel = new Label(utilisateur.getNomComplet());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label roleLabel = new Label("Administrateur");
        roleLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + "; -fx-font-size: 12px;");
        
        profileBox.getChildren().addAll(avatarLabel, nameLabel, roleLabel);
        
        // Menu
        VBox menuBox = new VBox(5);
        menuBox.setPadding(new Insets(20, 0, 0, 0));
//        menuBox.getChildren().addAll(
//                createMenuButton(ICONE_TABLEAU_BORD, "Tableau de bord", this::showDashboard),
//                createMenuButton(ICONE_MEMBRES, "Gestion des Membres", this::showMembreView),
//                createMenuButton(ICONE_PRETS, "Gestion des Prêts", this::showPretView),
//                createMenuButton(ICONE_REUNIONS, "Gestion des Réunions", this::showReunionView),
//                createMenuButton(ICONE_CYCLES, "Gestion des Cycles", this::showCycleView),
//                createMenuButton(ICONE_ACHATS, "Achats de Parts", this::showAchatPartView),
//                createMenuButton(ICONE_REGLES, "Règles AVEC", this::showRegleView),
//                createMenuButton(ICONE_VISITES, "Visites de terrain", this::showVisiteView)
//            );
            
        
        
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
    
    private VBox createCenterContent() {
        VBox center = new VBox(20);
        center.setPadding(new Insets(20));
        
        // Cartes de statistiques
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        
        // Carte 1 : Membres
        VBox membreCard = createStatCard("👥", "Membres Actifs", "24", Styles.VERT_PRINCIPAL);
        
        // Carte 2 : Prêts
        VBox pretCard = createStatCard("💰", "Prêts en cours", "1.2M FCFA", Styles.BLEU_SECONDAIRE);
        
        // Carte 3 : Épargne
        VBox epargneCard = createStatCard("🏦", "Épargne totale", "3.5M FCFA", Styles.ACCENT_DORE);
        
        // Carte 4 : Réunions
        VBox reunionCard = createStatCard("📅", "Réunions (mois)", "8", Styles.VERT_PRINCIPAL);
        
        statsBox.getChildren().addAll(membreCard, pretCard, epargneCard, reunionCard);
        
        // Titre de bienvenue
        Label welcomeLabel = new Label("Bienvenue sur votre espace de gestion AVEC");
        welcomeLabel.setStyle(Styles.TITRE_PRINCIPAL);
        
        // Message de bienvenue
        Label descriptionLabel = new Label(
            "Gérez efficacement votre Association Villageoise d'Épargne et de Crédit.\n" +
            "Sélectionnez une option dans le menu latéral pour commencer."
        );
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Styles.GRIS_FONCE + ";");
        descriptionLabel.setWrapText(true);
        
        center.getChildren().addAll(welcomeLabel, descriptionLabel, statsBox);
        
        return center;
    }
    
    private VBox createStatCard(String icon, String label, String value, String color) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                     "-fx-background-radius: 10;" +
                     "-fx-padding: 20;" +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");
        
        card.getChildren().addAll(iconLabel, valueLabel, labelLabel);
        
        return card;
    }
    
    private void logout() {
        LoginView loginView = new LoginView(mainApp);
        mainApp.getPrimaryStage().getScene().setRoot(loginView.getRoot());
        mainApp.getPrimaryStage().setMaximized(false);
        mainApp.getPrimaryStage().centerOnScreen();
    }
    
    private void showDashboard() {
        root.setCenter(createCenterContent());
    }
    
//    private void showMembreView() {
//        MembreView membreView = new MembreView(mainApp, utilisateur);
//        root.setCenter(membreView.getRoot());
//    }
//    
//    private void showPretView() {
//        PretView pretView = new PretView(mainApp, utilisateur);
//        root.setCenter(pretView.getRoot());
//    }
//    
//    private void showReunionView() {
//        ReunionView reunionView = new ReunionView(mainApp, utilisateur);
//        root.setCenter(reunionView.getRoot());
//    }
//    
//    private void showCycleView() {
//        CycleView cycleView = new CycleView(mainApp, utilisateur);
//        root.setCenter(cycleView.getRoot());
//    }
//    
//    private void showAchatPartView() {
//        AchatPartView achatPartView = new AchatPartView(mainApp, utilisateur);
//        root.setCenter(achatPartView.getRoot());
//    }
//    
//    private void showRegleView() {
//        RegleView regleView = new RegleView(mainApp, utilisateur);
//        root.setCenter(regleView.getRoot());
//    }
//    
//    private void showVisiteView() {
//        VisiteView visiteView = new VisiteView(mainApp, utilisateur);
//        root.setCenter(visiteView.getRoot());
//    }
    
    public BorderPane getRoot() {
        return root;
    }
}