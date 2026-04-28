package com.avec.view;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.enums.PhaseCycle;
import com.avec.enums.RoleComite;
import com.avec.enums.StatutCycle;
import com.avec.enums.StatutMembre;
import com.avec.model.AgentVillageois;
import com.avec.model.Avec;
import com.avec.model.Cycle;
import com.avec.model.Membre;
import com.avec.model.SessionUtilisateur;
import com.avec.service.AvecService;
import com.avec.service.CycleService;
import com.avec.service.MembreService;
import com.avec.service.UtilisateurService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private MembreService membreService;
    private CycleService cycleService;
    private BorderPane root;
    
    private AgentVillageois agentVillageois;
    private TableView<Avec> avecTable;
    private TableView<Membre> membreTable;
    private Avec avecSelectionne;
    
    private static final String STYLE_BOUTON_NORMAL = 
            "-fx-background-color: transparent; " +
            "-fx-text-fill: " + Styles.GRIS_CLAIR + "; " +
            "-fx-alignment: CENTER_LEFT; " +
            "-fx-padding: 10 15; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand;";
        
        private static final String STYLE_BOUTON_ACTIF = 
            "-fx-background-color: " + Styles.GRIS_CLAIR + "; " +
            "-fx-text-fill: " + Styles.VERT_PRINCIPAL + "; " +
            "-fx-alignment: CENTER_LEFT; " +
            "-fx-padding: 10 15; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;";
        
        private static final String STYLE_BOUTON_SURVOL = 
            "-fx-background-color: " + Styles.GRIS_CLAIR + "; " +
            "-fx-text-fill: " + Styles.VERT_PRINCIPAL + "; " +
            "-fx-alignment: CENTER_LEFT; " +
            "-fx-padding: 10 15; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand;";

    
    private static final String ICONE_TABLEAU_BORD = "📊";
    private static final String ICONE_FORMATION = "📚";
    private static final String ICONE_AVEC = "🤝";
    private static final String ICONE_MODULES = "📖";
    private static final String ICONE_CALENDRIER = "📅";
    private static final String ICONE_VALIDATION = "✅";
    private static final String ICONE_HONORAIRES = "💰";
    private static final String ICONE_CYCLE = "🔄";
    private static final String ICONE_DECONNEXION = "🚪";
    
    public AgentVillageoisDashboardView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.session = SessionUtilisateur.getInstance();
        this.avecService = new AvecService();
        this.membreService = new MembreService();
        this.cycleService = new CycleService();
        this.agentVillageois = session.getAgentVillageois();
        
        if (this.agentVillageois == null) {
            System.err.println("ERREUR: Aucun agent villageois connecté!");
            root = new BorderPane();
            Label errorLabel = new Label("Erreur: Vous n'êtes pas connecté en tant qu'agent villageois");
            errorLabel.setStyle("-fx-text-fill: red;");
            root.setCenter(errorLabel);
            return;
        }
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
        sidebar.setStyle("-fx-background-color: " + Styles.VERT_PRINCIPAL + ";" +
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
        
        // ✅ Création des ToggleButton
        ToggleButton btnDashboard = new ToggleButton(ICONE_TABLEAU_BORD + "  Tableau de bord");
        btnDashboard.setMaxWidth(Double.MAX_VALUE);
        btnDashboard.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnMesAvec = new ToggleButton(ICONE_AVEC + "  Mes AVEC");
        btnMesAvec.setMaxWidth(Double.MAX_VALUE);
        btnMesAvec.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnModules = new ToggleButton(ICONE_MODULES + "  Modules de formation");
        btnModules.setMaxWidth(Double.MAX_VALUE);
        btnModules.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnPlanning = new ToggleButton(ICONE_CALENDRIER + "  Planning visites");
        btnPlanning.setMaxWidth(Double.MAX_VALUE);
        btnPlanning.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnValidation = new ToggleButton(ICONE_VALIDATION + "  Validation de phase");
        btnValidation.setMaxWidth(Double.MAX_VALUE);
        btnValidation.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnHonoraires = new ToggleButton(ICONE_HONORAIRES + "  Mes honoraires");
        btnHonoraires.setMaxWidth(Double.MAX_VALUE);
        btnHonoraires.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnCycle = new ToggleButton(ICONE_CYCLE + "  Gestion des cycles");
        btnCycle.setMaxWidth(Double.MAX_VALUE);
        btnCycle.setStyle(STYLE_BOUTON_NORMAL);
        
        // ✅ Ajout des effets de survol
        ToggleButton[] allButtons = {btnDashboard, btnMesAvec, btnModules, btnPlanning, btnValidation, btnHonoraires,btnCycle};
        
        for (ToggleButton btn : allButtons) {
            btn.setOnMouseEntered(e -> {
                if (!btn.isSelected()) {
                    btn.setStyle(STYLE_BOUTON_SURVOL);
                }
            });
            btn.setOnMouseExited(e -> {
                if (!btn.isSelected()) {
                    btn.setStyle(STYLE_BOUTON_NORMAL);
                }
            });
        }
        
        // ✅ Actions des boutons
        btnDashboard.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnDashboard.setStyle(STYLE_BOUTON_ACTIF);
            showDashboard();
        });
        
        btnMesAvec.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnMesAvec.setStyle(STYLE_BOUTON_ACTIF);
            showMesAvec();
        });
        
        btnModules.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnModules.setStyle(STYLE_BOUTON_ACTIF);
            showModules();
        });
        
        btnPlanning.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnPlanning.setStyle(STYLE_BOUTON_ACTIF);
            showPlanning();
        });
        
        btnValidation.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnValidation.setStyle(STYLE_BOUTON_ACTIF);
            createValidationContent();
        });
        
        btnHonoraires.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnHonoraires.setStyle(STYLE_BOUTON_ACTIF);
            showHonoraires();
        });
        
        btnCycle.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnCycle.setStyle(STYLE_BOUTON_ACTIF);
            showGestionCyclesGlobale();
        });

        
        // ✅ Groupe de toggle (un seul sélectionné à la fois)
        ToggleGroup group = new ToggleGroup();
        for (ToggleButton btn : allButtons) {
            btn.setToggleGroup(group);
        }
        
        // ✅ Sélectionner le premier bouton par défaut
        btnDashboard.setSelected(true);
        btnDashboard.setStyle(STYLE_BOUTON_ACTIF);
        
        menuBox.getChildren().addAll(btnDashboard, btnMesAvec, btnModules, btnPlanning, btnValidation, btnHonoraires,btnCycle);
        
        // Bouton changer mot de passe
        Button btnChangerMdp = new Button("🔒  Changer mot de passe");
        btnChangerMdp.setStyle(Styles.BOUTON_ACCENT);
        btnChangerMdp.setMaxWidth(Double.MAX_VALUE);
        btnChangerMdp.setPadding(new Insets(10, 15, 10, 15));
        btnChangerMdp.setOnAction(e -> showChangerMotDePasse());
        
        // Espaceur pour pousser le bouton en bas
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        sidebar.getChildren().addAll(profileBox, menuBox, spacer, btnChangerMdp);
        
        return sidebar;
    }
    
    /**
     * Réinitialise le style de tous les boutons
     */
    private void resetAllButtonsStyle(ToggleButton[] buttons, String style) {
        for (ToggleButton btn : buttons) {
            if (!btn.isSelected()) {
                btn.setStyle(style);
            }
        }
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
    
   
    
    /**
     * ✅ Création d'une AVEC par l'agent villageois
     */
    private void showCreerAvec() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Créer une AVEC");
        dialog.setHeaderText("Nouvelle Association Villageoise d'Épargne et de Crédit");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(450);
        
        // Nom de l'AVEC
        Label nomLabel = new Label("Nom de l'AVEC *");
        nomLabel.setStyle("-fx-font-weight: bold;");
        TextField nomField = new TextField();
        nomField.setPromptText("Ex: AVEC Ndiarème");
        nomField.setStyle(Styles.CHAMP_TEXTE);
        
        // Prix de la part
        Label prixLabel = new Label("Prix de la part (FCFA) *");
        prixLabel.setStyle("-fx-font-weight: bold;");
        TextField prixPartField = new TextField();
        prixPartField.setPromptText("Ex: 500");
        prixPartField.setStyle(Styles.CHAMP_TEXTE);
        
        
        // Nombre max de membres
        Label nbMembresLabel = new Label("Nombre max de membres");
        Spinner<Integer> nbMembresSpinner = new Spinner<>(10, 30, 15);
        nbMembresSpinner.setStyle(Styles.CHAMP_TEXTE);
        
        // Taux frais service
        Label tauxLabel = new Label("Taux frais service mensuel (%)");
        TextField tauxField = new TextField("5");
        tauxField.setStyle(Styles.CHAMP_TEXTE);
        
        content.getChildren().addAll(
            nomLabel, nomField,
            prixLabel, prixPartField,
            nbMembresLabel, nbMembresSpinner,
            tauxLabel, tauxField
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                	String nom = nomField.getText().trim();
                    BigDecimal prixPart = new BigDecimal(prixPartField.getText().trim());
                    
                    if (nom.isEmpty() || prixPart.compareTo(BigDecimal.ZERO) <= 0) {
                        showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
                        return null;
                    }
                    
                    // ✅ Créer l'AVEC avec l'agent villageois connecté
                    Avec avec = avecService.creerAvec(nom, prixPart, agentVillageois.getId());
                    
                    // Mettre à jour les paramètres supplémentaires
                    avec.setNombreMembresMax(nbMembresSpinner.getValue());
                    avec.setTauxFraisServiceMensuel(new BigDecimal(tauxField.getText().trim()));
                    avecService.modifierAvec(avec);
                    
                    showInfo("Succès", "AVEC créée avec succès!\n" +
                            "Nom: " + avec.getNom() + "\n" +
                            "Code: " + avec.getCodeUnique());
                    
                    // Rafraîchir la liste
                    try {
                        List<Avec> avecs = avecService.getAvecsByAgentVillageois(agentVillageois.getId());
                        if (avecs != null) {
                            avecTable.setItems(FXCollections.observableArrayList(avecs));
                        }
                    } catch (SQLException e) {
                        showAlert("Erreur", "Erreur: " + e.getMessage());
                    }
                    
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    
    
    
    /**
     * ✅ Gestion du comité de gestion
     */
    private void gererComite(Avec avec) {
        try {
            List<Membre> membres = membreService.getMembresByAvecId(avec.getId());
            List<Membre> membresActifs = membres.stream()
                    .filter(m -> m.getEstActif() == StatutMembre.ACTIF)
                    .collect(Collectors.toList());
            
            if (membresActifs.isEmpty()) {
                showAlert("Information", "Aucun membre actif dans cette AVEC. Ajoutez d'abord des membres.");
                return;
            }
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Comité de gestion");
            dialog.setHeaderText("Élection du comité pour " + avec.getNom());
            
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.setPrefWidth(400);
            
            Label infoLabel = new Label("Sélectionnez les membres du comité selon la procédure d'élection (vote secret avec cailloux):");
            infoLabel.setStyle("-fx-font-weight: bold;");
            infoLabel.setWrapText(true);
            
            ComboBox<Membre> presidentCombo = new ComboBox<>();
            presidentCombo.setPromptText("Président");
            presidentCombo.setItems(FXCollections.observableArrayList(membresActifs));
            presidentCombo.setStyle(Styles.CHAMP_TEXTE);
            
            ComboBox<Membre> secretaireCombo = new ComboBox<>();
            secretaireCombo.setPromptText("Secrétaire");
            secretaireCombo.setItems(FXCollections.observableArrayList(membresActifs));
            secretaireCombo.setStyle(Styles.CHAMP_TEXTE);
            
            ComboBox<Membre> tresorierCombo = new ComboBox<>();
            tresorierCombo.setPromptText("Trésorier");
            tresorierCombo.setItems(FXCollections.observableArrayList(membresActifs));
            tresorierCombo.setStyle(Styles.CHAMP_TEXTE);
            
            ComboBox<Membre> compteur1Combo = new ComboBox<>();
            compteur1Combo.setPromptText("Compteur 1");
            compteur1Combo.setItems(FXCollections.observableArrayList(membresActifs));
            compteur1Combo.setStyle(Styles.CHAMP_TEXTE);
            
            ComboBox<Membre> compteur2Combo = new ComboBox<>();
            compteur2Combo.setPromptText("Compteur 2");
            compteur2Combo.setItems(FXCollections.observableArrayList(membresActifs));
            compteur2Combo.setStyle(Styles.CHAMP_TEXTE);
            
            content.getChildren().addAll(
                infoLabel,
                new Separator(),
                new Label("Président:"), presidentCombo,
                new Label("Secrétaire:"), secretaireCombo,
                new Label("Trésorier:"), tresorierCombo,
                new Label("Compteur 1:"), compteur1Combo,
                new Label("Compteur 2:"), compteur2Combo
            );
            
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    try {
                        List<MembreService.ResultatElection> resultats = new ArrayList<>();
                        resultats.add(new MembreService.ResultatElection(presidentCombo.getValue().getId(), RoleComite.PRESIDENT));
                        resultats.add(new MembreService.ResultatElection(secretaireCombo.getValue().getId(), RoleComite.SECRETAIRE));
                        resultats.add(new MembreService.ResultatElection(tresorierCombo.getValue().getId(), RoleComite.TRESORIER));
                        resultats.add(new MembreService.ResultatElection(compteur1Combo.getValue().getId(), RoleComite.COMPTEUR));
                        resultats.add(new MembreService.ResultatElection(compteur2Combo.getValue().getId(), RoleComite.COMPTEUR));
                        
                        if (membreService.organiserElection(avec.getId(), resultats)) {
                            showInfo("Succès", "Comité de gestion élu avec succès!");
                        } else {
                            showAlert("Erreur", "Échec de l'élection");
                        }
                    } catch (Exception e) {
                        showAlert("Erreur", "Erreur: " + e.getMessage());
                    }
                }
                return null;
            });
            
            dialog.showAndWait();
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
        }
    }
    
    /**
     * ✅ Gestion des membres (ouverture de la vue MembreView)
     */
//    private void gererMembres(Avec avec) {
//        // Ouvrir la vue de gestion des membres
//        MembreView membreView = new MembreView(mainApp, session.getUtilisateur());
//        root.setCenter(membreView.getRoot());
//        
//        // Filtrer pour afficher uniquement les membres de cette AVEC
//        // Cette partie nécessite une modification de MembreView pour accepter un filtre
//    }
    
    private void demarrerFormation(Avec avec) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Formation AVEC");
        alert.setHeaderText("Démarrage de la formation - " + avec.getNom());
        
        TextArea content = new TextArea();
        content.setText("Module 1: Groupes, leadership et élections\n\n" +
                        "Objectifs:\n" +
                        "- Auto-sélection individuelle\n" +
                        "- Rôle de l'Assemblée Générale\n" +
                        "- Rôles des dirigeants\n" +
                        "- Préparation aux élections\n" +
                        "- Procédures d'élection (vote secret avec cailloux)\n\n" +
                        "Procédure:\n" +
                        "1. Disposer les chaises en cercle (voir diagramme du guide)\n" +
                        "2. Expliquer les qualités d'un bon membre\n" +
                        "3. Expliquer les rôles (Président, Secrétaire, Trésorier, Compteurs)\n" +
                        "4. Organiser les élections avec la méthode des cailloux\n" +
                        "5. Faire signer le contrat entre l'AVEC et l'Agent Villageois");
        content.setWrapText(true);
        content.setEditable(false);
        content.setPrefHeight(300);
        content.setPrefWidth(400);
        
        alert.getDialogPane().setContent(content);
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
    
    private void createValidationContent() {
		VBox content = new VBox(15);
		content.setPadding(new Insets(20));

		Label title = new Label("Validation des phases de formation");
		title.setStyle(Styles.TITRE_SECONDAIRE);

		Label description = new Label(
				"Selon le guide AVEC, les phases de formation sont :\n" + "• Phase préparatoire (Réunions A et B)\n"
						+ "• Phase intensive (12 semaines)\n" + "• Phase de développement (12 semaines)\n"
						+ "• Phase de maturité (12 semaines)\n" + "• Répartition du capital et élections");
		description.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");
		description.setWrapText(true);

		TableView<Avec> validationTable = new TableView<>();
		validationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<Avec, String> colNom = new TableColumn<>("AVEC");
		colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
		colNom.setPrefWidth(150);

		TableColumn<Avec, String> colPhaseActuelle = new TableColumn<>("Phase actuelle");
		colPhaseActuelle.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
				cellData.getValue().getPhaseCourante().getLibelle()));
		colPhaseActuelle.setPrefWidth(120);

		TableColumn<Avec, String> colProchainePhase = new TableColumn<>("Prochaine phase");
		colProchainePhase.setPrefWidth(120);

		TableColumn<Avec, String> colAction = new TableColumn<>("Action");
		colAction.setCellFactory(col -> new TableCell<Avec, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					Button btn = new Button("✅ Valider");
					btn.setStyle(Styles.BOUTON_PRINCIPAL);
					btn.setOnAction(e -> {
						Avec avec = getTableView().getItems().get(getIndex());
						validerPhase(avec);
					});
					setGraphic(btn);
				}
			}
		});
		colAction.setPrefWidth(100);

		validationTable.getColumns().addAll(colNom, colPhaseActuelle, colProchainePhase, colAction);

		try {
			List<Avec> avecs = avecService.getAvecsByAgentVillageois(agentVillageois.getId());
			validationTable.setItems(FXCollections.observableArrayList(avecs));
		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de charger les AVEC: " + e.getMessage());
		}

		content.getChildren().addAll(title, description, validationTable);
		
        root.setCenter(content);

	}
    
    /**
     * ✅ Retourne la phase suivante selon le guide AVEC
     */
    private PhaseCycle getPhaseSuivante(PhaseCycle phaseActuelle) {
        if (phaseActuelle == null) return PhaseCycle.PREPARATOIRE;
        
        switch (phaseActuelle) {
            case PREPARATOIRE:
                return PhaseCycle.INTENSIVE;
            case INTENSIVE:
                return PhaseCycle.DEVELOPPEMENT;
            case DEVELOPPEMENT:
                return PhaseCycle.MATURITE;
            case MATURITE:
                return PhaseCycle.TERMINE;
            default:
                return null;
        }
    }
    
    /**
     * ✅ Valider le passage à la phase suivante (Agent Villageois)
     */
    private void validerPhase(Avec avec) {
        PhaseCycle phaseActuelle = avec.getPhaseCourante();
        PhaseCycle phaseSuivante = getPhaseSuivante(phaseActuelle);
        
        if (phaseSuivante == null) {
            showAlert("Information", "L'AVEC a déjà terminé son cycle de formation.");
            return;
        }
        
        String message = String.format(
            "Validation du passage de phase pour %s\n\n" +
            "Phase actuelle: %s\n" +
            "Phase suivante: %s\n\n" +
            "Confirmez-vous que cette AVEC a complété la phase %s ?",
            avec.getNom(),
            phaseActuelle.getLibelle(),
            phaseSuivante.getLibelle(),
            phaseActuelle.getLibelle()
        );
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Validation de phase");
        confirm.setHeaderText("Passage à la phase suivante");
        confirm.setContentText(message);
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    avecService.changerPhase(avec.getId(), phaseSuivante);
                    showInfo("Succès", String.format(
                        "Phase validée avec succès!\n" +
                        "%s passe en phase %s",
                        avec.getNom(),
                        phaseSuivante.getLibelle()
                    ));
                    // Rafraîchir la liste des AVEC
                    rafraichirListeAvec();
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la validation: " + e.getMessage());
                }
            }
        });
    }
    
/**
     * ✅ Rafraîchir la liste des AVEC
     */
//    private void rafraichirListeAvec() {
//        try {
//            List<Avec> avecs = avecService.getAvecsByAgentVillageois(agentVillageois.getId());
//            if (avecs != null) {
//                avecTable.setItems(FXCollections.observableArrayList(avecs));
//            }
//        } catch (SQLException e) {
//            showAlert("Erreur", "Impossible de rafraîchir la liste: " + e.getMessage());
//        }
//    }
    
    /**
     * ✅ Démarrer un nouveau cycle pour une AVEC
     */
    /**
     * ✅ Démarrer un nouveau cycle pour une AVEC
     */
    private void demarrerCycle(Avec avec) {
        try {
            // Vérifier si un cycle existe déjà
            List<Cycle> cyclesExistants = cycleService.getCyclesByAvecId(avec.getId());
            Cycle cycleEnCours = cycleService.getCycleEnCours(avec.getId());
            
            if (cycleEnCours != null && cycleEnCours.getStatut() == StatutCycle.EN_COURS) {
                showAlert("Information", "Un cycle est déjà en cours pour " + avec.getNom() + 
                    "\nN° cycle: " + cycleEnCours.getNumeroCycle() +
                    "\nDate début: " + cycleEnCours.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                return;
            }
            
            int nouveauNumero = cyclesExistants.isEmpty() ? 1 : cyclesExistants.size() + 1;
            String phaseMessage = cyclesExistants.isEmpty() ? 
                "Ce sera le 1er cycle (période de formation de 36 semaines)" :
                "Nouveau cycle après répartition";
            
            String message = String.format(
                "Démarrer le cycle %d pour %s ?\n\n%s",
                nouveauNumero,
                avec.getNom(),
                phaseMessage
            );
            
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Démarrer un cycle");
            confirm.setHeaderText("Nouveau cycle");
            confirm.setContentText(message);
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        Cycle nouveauCycle = cycleService.creerNouveauCycle(avec);
                        
                        // Mettre à jour la phase de l'AVEC
                        avecService.changerPhase(avec.getId(), PhaseCycle.PREPARATOIRE);
                        
                        showInfo("Succès", String.format(
                            "Cycle %d démarré avec succès pour %s!\n" +
                            "Date début: %s\n" +
                            "Phase: %s",
                            nouveauCycle.getNumeroCycle(),
                            avec.getNom(),
                            nouveauCycle.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            PhaseCycle.PREPARATOIRE.getLibelle()
                        ));
                        
                        rafraichirListeAvec();
                        
                    } catch (Exception e) {
                        showAlert("Erreur", "Erreur lors du démarrage du cycle: " + e.getMessage());
                    }
                }
            });
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de vérifier les cycles: " + e.getMessage());
        }
    }
    
    /**
     * ✅ Modifier la colonne d'action dans showMesPour inclure la validation de phase
     */
    private void rafraichirListeAvec() {
        try {
            List<Avec> avecs = avecService.getAvecsByAgentVillageois(agentVillageois.getId());
            if (avecs != null) {
                avecTable.setItems(FXCollections.observableArrayList(avecs));
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de rafraîchir la liste: " + e.getMessage());
        }
    }
    
    /**
     * ✅ Modifier la colonne d'action dans showMesAvec() pour inclure la validation de phase
     */
    private void showMesAvec() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("AVEC sous ma responsabilité");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        HBox toolbar = new HBox(10);
        
        Button creerAvecBtn = new Button("➕ Créer une AVEC");
        creerAvecBtn.setStyle(Styles.BOUTON_PRINCIPAL);
        creerAvecBtn.setOnAction(e -> showCreerAvec());
        
        Button actualiserBtn = new Button("🔄 Actualiser");
        actualiserBtn.setStyle(Styles.BOUTON_SECONDAIRE);
        actualiserBtn.setOnAction(e -> rafraichirListeAvec());
        
        toolbar.getChildren().addAll(creerAvecBtn, actualiserBtn);
        
        avecTable = new TableView<>();
        avecTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Avec, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Avec, String> colNom = new TableColumn<>("Nom AVEC");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(150);
        
        TableColumn<Avec, String> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(new PropertyValueFactory<>("codeUnique"));
        colCode.setPrefWidth(100);
        
        TableColumn<Avec, String> colPhase = new TableColumn<>("Phase");
        colPhase.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhaseCourante().getLibelle()));
        colPhase.setPrefWidth(120);
        
        TableColumn<Avec, Integer> colMembres = new TableColumn<>("Membres");
        colMembres.setCellValueFactory(new PropertyValueFactory<>("nombreMembresMax"));
        colMembres.setPrefWidth(80);
        
     // Dans showMesAvec(), modifiez la colonne colAction
        TableColumn<Avec, String> colAction = new TableColumn<>("Action");
        colAction.setCellFactory(col -> new TableCell<Avec, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttonBox = new HBox(5);
                    
                    Button formationBtn = new Button("📖 Formation");
                    formationBtn.setStyle(Styles.BOUTON_PRINCIPAL);
                    formationBtn.setOnAction(e -> {
                        Avec avec = getTableView().getItems().get(getIndex());
                        demarrerFormation(avec);
                    });
                    
                    Button comiteBtn = new Button("👥 Comité");
                    comiteBtn.setStyle(Styles.BOUTON_SECONDAIRE);
                    comiteBtn.setOnAction(e -> {
                        Avec avec = getTableView().getItems().get(getIndex());
                        gererComite(avec);
                    });
                    
                    Button membresBtn = new Button("👤 Membres");
                    membresBtn.setStyle(Styles.BOUTON_ACCENT);
                    membresBtn.setOnAction(e -> {
                        Avec avec = getTableView().getItems().get(getIndex());
                        gererMembres(avec);
                    });
                    
                    // ✅ Bouton pour voir les détails du cycle
                    Button cycleBtn = new Button("🔄 Cycle");
                    cycleBtn.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
                    cycleBtn.setOnAction(e -> {
                        Avec avec = getTableView().getItems().get(getIndex());
                        showDetailsCycle(avec);
                    });
                    
                    // ✅ Bouton de validation de phase
                    Button validerPhaseBtn = new Button("✅ Valider phase");
                    PhaseCycle phase = getTableView().getItems().get(getIndex()).getPhaseCourante();
                    
                    if (phase == PhaseCycle.TERMINE) {
                        validerPhaseBtn.setDisable(true);
                        validerPhaseBtn.setText("✅ Cycle terminé");
                        validerPhaseBtn.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white;");
                    } else {
                        validerPhaseBtn.setStyle("-fx-background-color: #2E7D32;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-padding: 5 10;" +
                                    "-fx-background-radius: 5;" +
                                    "-fx-cursor: hand;");
                        validerPhaseBtn.setOnAction(e -> {
                            Avec avec = getTableView().getItems().get(getIndex());
                            validerPhase(avec);
                        });
                    }
                    
                    // ✅ Bouton démarrer cycle (existant)
                    Button demarrerCycleBtn = new Button("🚀 Démarrer cycle");
                    demarrerCycleBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
                    demarrerCycleBtn.setOnAction(e -> {
                        Avec avec = getTableView().getItems().get(getIndex());
                        demarrerCycle(avec);
                    });
                    
                    buttonBox.getChildren().addAll(formationBtn, comiteBtn, membresBtn, cycleBtn, validerPhaseBtn, demarrerCycleBtn);
                    setGraphic(buttonBox);
                }
            }
        });
        colAction.setPrefWidth(500); // Agrandir pour accueillir tous les boutons
        
        avecTable.getColumns().addAll(colId, colNom, colCode, colPhase, colMembres, colAction);
        
        try {
            List<Avec> avecs = avecService.getAvecsByAgentVillageois(agentVillageois.getId());
            if (avecs != null) {
                avecTable.setItems(FXCollections.observableArrayList(avecs));
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les AVEC: " + e.getMessage());
        }
        
        view.getChildren().addAll(title, toolbar, avecTable);
        VBox.setVgrow(avecTable, Priority.ALWAYS);
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
    
    /**
     * ✅ Gestion des membres d'une AVEC (Agent Villageois peut ajouter des membres)
     */
    private void gererMembres(Avec avec) {
        this.avecSelectionne = avec;
        
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Gestion des membres - " + avec.getNom());
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        // Barre d'outils
        HBox toolbar = new HBox(10);
        
        Button ajouterBtn = new Button("➕ Ajouter un membre");
        ajouterBtn.setStyle(Styles.BOUTON_PRINCIPAL);
        ajouterBtn.setOnAction(e -> showAjoutMembre(avec));
        
        Button electionBtn = new Button("🗳️ Organiser l'élection");
        electionBtn.setStyle(Styles.BOUTON_SECONDAIRE);
        electionBtn.setOnAction(e -> showElectionComite(avec));
        
        Button actualiserBtn = new Button("🔄 Actualiser");
        actualiserBtn.setStyle(Styles.BOUTON_ACCENT);
        actualiserBtn.setOnAction(e -> chargerMembres(avec));
        
        toolbar.getChildren().addAll(ajouterBtn, electionBtn, actualiserBtn);
        
        // Tableau des membres
        membreTable = new TableView<>();
        membreTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Membre, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Membre, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(100);
        
        TableColumn<Membre, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(100);
        
        TableColumn<Membre, String> colCarte = new TableColumn<>("N° Carte");
        colCarte.setCellValueFactory(new PropertyValueFactory<>("numeroCarte"));
        colCarte.setPrefWidth(120);
        
        TableColumn<Membre, String> colRole = new TableColumn<>("Rôle");
        colRole.setCellValueFactory(cellData -> {
            Membre m = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                m.getRoleComite() != null ? m.getRoleComite().getDescription() : "Membre"
            );
        });
        colRole.setPrefWidth(120);
        
        TableColumn<Membre, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData -> {
            Membre m = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                m.getEstActif() != null ? m.getEstActif().getLibelle() : "Actif"
            );
        });
        colStatut.setPrefWidth(80);
        
        membreTable.getColumns().addAll(colId, colNom, colPrenom, colCarte, colRole, colStatut);
        
        chargerMembres(avec);
        
        view.getChildren().addAll(title, toolbar, membreTable);
        VBox.setVgrow(membreTable, Priority.ALWAYS);
        
        root.setCenter(view);
    }
    
    /**
     * ✅ Ajout d'un membre par l'Agent Villageois
     */
    private void showAjoutMembre(Avec avec) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un membre");
        dialog.setHeaderText("Ajouter un nouveau membre à " + avec.getNom());
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);
        
        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        nomField.setStyle(Styles.CHAMP_TEXTE);
        
        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");
        prenomField.setStyle(Styles.CHAMP_TEXTE);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe (pour la connexion)");
        passwordField.setStyle(Styles.CHAMP_TEXTE);
        
        TextField telephoneField = new TextField();
        telephoneField.setPromptText("Téléphone");
        telephoneField.setStyle(Styles.CHAMP_TEXTE);
        
        content.getChildren().addAll(
            new Label("Nom:"), nomField,
            new Label("Prénom:"), prenomField,
            new Label("Mot de passe:"), passwordField,
            new Label("Téléphone:"), telephoneField
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    String nom = nomField.getText().trim();
                    String prenom = prenomField.getText().trim();
                    String password = passwordField.getText().trim();
                    String telephone = telephoneField.getText().trim();
                    
                    if (nom.isEmpty() || prenom.isEmpty() || password.isEmpty() || telephone.isEmpty()) {
                        showAlert("Erreur", "Tous les champs sont obligatoires");
                        return null;
                    }
                    
                    Membre membre = membreService.creerMembre(
                        nom, prenom, avec.getId(), password,telephone
                    );
                    
                    showInfo("Succès", "Membre ajouté avec succès!\n" +
                            "Numéro de carte: " + membre.getNumeroCarte() + "\n" +
                            "Mot de passe: " + password);
                    
                    chargerMembres(avec);
                    
                } catch (SQLException | IllegalArgumentException e) {
                    showAlert("Erreur", "Erreur: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    
    
    /**
     * ✅ Élection du comité (après avoir ajouté au moins 5 membres)
     */
    private void showElectionComite(Avec avec) {
        try {
            List<Membre> membres = membreService.getMembresByAvecId(avec.getId());
            List<Membre> actifs = membres.stream()
                    .filter(m -> m.getEstActif() == StatutMembre.ACTIF)
                    .collect(Collectors.toList());
            
            if (actifs.size() < 5) {
                showAlert("Attention", "Il faut au moins 5 membres actifs pour élire le comité.\n" +
                        "Membres actifs: " + actifs.size() + "\n" +
                        "Ajoutez d'abord des membres.");
                return;
            }
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Élection du comité");
            dialog.setHeaderText("Élection du comité de gestion pour " + avec.getNom());
            
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.setPrefWidth(400);
            
            Label infoLabel = new Label("Sélectionnez les résultats de l'élection (vote secret avec cailloux):");
            infoLabel.setStyle("-fx-font-weight: bold;");
            infoLabel.setWrapText(true);
            
            ComboBox<Membre> presidentCombo = new ComboBox<>();
            presidentCombo.setPromptText("Président");
            presidentCombo.setItems(FXCollections.observableArrayList(actifs));
            presidentCombo.setStyle(Styles.CHAMP_TEXTE);
            
            ComboBox<Membre> secretaireCombo = new ComboBox<>();
            secretaireCombo.setPromptText("Secrétaire");
            secretaireCombo.setItems(FXCollections.observableArrayList(actifs));
            secretaireCombo.setStyle(Styles.CHAMP_TEXTE);
            
            ComboBox<Membre> tresorierCombo = new ComboBox<>();
            tresorierCombo.setPromptText("Trésorier");
            tresorierCombo.setItems(FXCollections.observableArrayList(actifs));
            tresorierCombo.setStyle(Styles.CHAMP_TEXTE);
            
            ComboBox<Membre> compteur1Combo = new ComboBox<>();
            compteur1Combo.setPromptText("Compteur 1");
            compteur1Combo.setItems(FXCollections.observableArrayList(actifs));
            compteur1Combo.setStyle(Styles.CHAMP_TEXTE);
            
            ComboBox<Membre> compteur2Combo = new ComboBox<>();
            compteur2Combo.setPromptText("Compteur 2");
            compteur2Combo.setItems(FXCollections.observableArrayList(actifs));
            compteur2Combo.setStyle(Styles.CHAMP_TEXTE);
            
            content.getChildren().addAll(
                infoLabel,
                new Separator(),
                new Label("Président:"), presidentCombo,
                new Label("Secrétaire:"), secretaireCombo,
                new Label("Trésorier:"), tresorierCombo,
                new Label("Compteur 1:"), compteur1Combo,
                new Label("Compteur 2:"), compteur2Combo
            );
            
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    try {
                        List<MembreService.ResultatElection> resultats = new ArrayList<>();
                        resultats.add(new MembreService.ResultatElection(presidentCombo.getValue().getId(), RoleComite.PRESIDENT));
                        resultats.add(new MembreService.ResultatElection(secretaireCombo.getValue().getId(), RoleComite.SECRETAIRE));
                        resultats.add(new MembreService.ResultatElection(tresorierCombo.getValue().getId(), RoleComite.TRESORIER));
                        resultats.add(new MembreService.ResultatElection(compteur1Combo.getValue().getId(), RoleComite.COMPTEUR));
                        resultats.add(new MembreService.ResultatElection(compteur2Combo.getValue().getId(), RoleComite.COMPTEUR));
                        
                        if (membreService.organiserElection(avec.getId(), resultats)) {
                            String presidentNom = presidentCombo.getValue().getNomComplet();
                            showInfo("Succès", "Comité de gestion élu avec succès!\n" +
                                    "Président: " + presidentNom + "\n\n" +
                                    "Le président peut maintenant se connecter avec sa carte pour gérer les membres.");
                            chargerMembres(avec);
                        } else {
                            showAlert("Erreur", "Échec de l'élection");
                        }
                    } catch (Exception e) {
                        showAlert("Erreur", "Erreur: " + e.getMessage());
                    }
                }
                return null;
            });
            
            dialog.showAndWait();
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
        }
    }
    
    private void chargerMembres(Avec avec) {
        try {
            List<Membre> membres = membreService.getMembresByAvecId(avec.getId());
            membreTable.setItems(FXCollections.observableArrayList(membres));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
        }
    }
    
    /**
     * Affiche les détails du cycle en cours pour une AVEC
     */
    /**
     * Affiche les détails du cycle en cours pour une AVEC
     */
    private void showDetailsCycle(Avec avec) {
        try {
            Cycle cycleEnCours = cycleService.getCycleEnCours(avec.getId());
            
            if (cycleEnCours == null) {
                showAlert("Information", "Aucun cycle en cours pour " + avec.getNom());
                return;
            }
            
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.setPrefWidth(450);
            
            Label title = new Label("Cycle en cours - " + avec.getNom());
            title.setStyle(Styles.TITRE_PRINCIPAL);
            
            GridPane grid = new GridPane();
            grid.setHgap(15);
            grid.setVgap(10);
            
            grid.add(new Label("📅 N° cycle:"), 0, 0);
            grid.add(new Label(String.valueOf(cycleEnCours.getNumeroCycle())), 1, 0);
            grid.add(new Label("📅 Date début:"), 0, 1);
            grid.add(new Label(cycleEnCours.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))), 1, 1);
            grid.add(new Label("📅 Date fin prévue:"), 0, 2);
            grid.add(new Label(cycleEnCours.getDateFinPrevue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))), 1, 2);
            grid.add(new Label("📊 Statut:"), 0, 3);
            grid.add(new Label(cycleEnCours.getStatut().getLibelle()), 1, 3);
            grid.add(new Label("📚 Réunions effectuées:"), 0, 4);
            grid.add(new Label(cycleEnCours.getNombreReunionsEffectuees() + " / 36"), 1, 4);
            
            // Barre de progression
            int progression = (cycleEnCours.getNombreReunionsEffectuees() * 100) / 36;
            ProgressBar progressBar = new ProgressBar(progression / 100.0);
            progressBar.setPrefWidth(200);
            
            content.getChildren().addAll(title, grid, new Label("Progression:"), progressBar);
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Détails du cycle");
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
            dialog.showAndWait();
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger le cycle: " + e.getMessage());
        }
    }
    /**
     * Vue globale de tous les cycles de toutes les AVEC
     */
    private void showGestionCyclesGlobale() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Gestion des cycles de formation");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        try {
            List<Avec> avecs = avecService.getAvecsByAgentVillageois(agentVillageois.getId());
            
            if (avecs == null || avecs.isEmpty()) {
                Label emptyLabel = new Label("Aucune AVEC trouvée.");
                emptyLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");
                view.getChildren().addAll(title, emptyLabel);
                root.setCenter(view);
                return;
            }
            
            Accordion accordion = new Accordion();
            
            for (Avec avec : avecs) {
                VBox avecContent = new VBox(10);
                avecContent.setPadding(new Insets(15));
                
                // En-tête
                Label avecTitle = new Label(avec.getNom());
                avecTitle.setStyle(Styles.TITRE_SECONDAIRE);
                
                // Récupérer les cycles
                List<Cycle> cycles = cycleService.getCyclesByAvecId(avec.getId());
                Cycle cycleEnCours = cycleService.getCycleEnCours(avec.getId());
                
                if (cycles.isEmpty()) {
                    // Aucun cycle
                    Button demarrerBtn = new Button("🚀 Démarrer le 1er cycle");
                    demarrerBtn.setStyle(Styles.BOUTON_PRINCIPAL);
                    demarrerBtn.setOnAction(e -> demarrerCycle(avec));
                    avecContent.getChildren().addAll(avecTitle, demarrerBtn);
                    
                } else if (cycleEnCours != null) {
                    // Cycle en cours
                    GridPane infoGrid = new GridPane();
                    infoGrid.setHgap(15);
                    infoGrid.setVgap(10);
                    infoGrid.setPadding(new Insets(10));
                    infoGrid.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";" +
                                     "-fx-background-radius: 10;");
                    
                    infoGrid.add(new Label("N° cycle:"), 0, 0);
                    infoGrid.add(new Label(String.valueOf(cycleEnCours.getNumeroCycle())), 1, 0);
                    infoGrid.add(new Label("Date début:"), 0, 1);
                    infoGrid.add(new Label(cycleEnCours.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))), 1, 1);
                    infoGrid.add(new Label("Date fin prévue:"), 0, 2);
                    infoGrid.add(new Label(cycleEnCours.getDateFinPrevue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))), 1, 2);
                    infoGrid.add(new Label("Statut:"), 0, 3);
                    infoGrid.add(new Label(cycleEnCours.getStatut().getLibelle()), 1, 3);
                    infoGrid.add(new Label("Progression:"), 0, 4);
                    
                    int progression = (cycleEnCours.getNombreReunionsEffectuees() * 100) / 36;
                    ProgressBar progressBar = new ProgressBar(progression / 100.0);
                    progressBar.setPrefWidth(200);
                    
                    // Bouton pour terminer le cycle (Module 7)
                    Button repartitionBtn = new Button("✅ Organiser la répartition");
                    repartitionBtn.setStyle(Styles.BOUTON_SUCCES);
                    repartitionBtn.setOnAction(e -> organiserRepartition(avec, cycleEnCours));
                    
                    if (!cycleEnCours.estConforme()) {
                        repartitionBtn.setDisable(true);
                        repartitionBtn.setTooltip(new Tooltip("Il faut au moins 36 réunions avant la répartition"));
                    }
                    
                    avecContent.getChildren().addAll(avecTitle, infoGrid, progressBar, repartitionBtn);
                    
                } else {
                    // Cycle terminé - afficher le dernier cycle
                    Cycle dernierCycle = cycles.get(0);
                    
                    GridPane resultGrid = new GridPane();
                    resultGrid.setHgap(15);
                    resultGrid.setVgap(10);
                    resultGrid.setPadding(new Insets(10));
                    resultGrid.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";" +
                                       "-fx-background-radius: 10;");
                    
                    resultGrid.add(new Label("Dernier cycle:"), 0, 0);
                    resultGrid.add(new Label("N° " + dernierCycle.getNumeroCycle()), 1, 0);
                    resultGrid.add(new Label("Date fin:"), 0, 1);
                    resultGrid.add(new Label(dernierCycle.getDateFinReelle().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))), 1, 1);
                    resultGrid.add(new Label("Valeur part:"), 0, 2);
                    resultGrid.add(new Label(formatMontant(dernierCycle.getValeurPart())), 1, 2);
                    
                    Button nouveauCycleBtn = new Button("🔄 Démarrer nouveau cycle");
                    nouveauCycleBtn.setStyle(Styles.BOUTON_PRINCIPAL);
                    nouveauCycleBtn.setOnAction(e -> demarrerCycle(avec));
                    
                    avecContent.getChildren().addAll(avecTitle, resultGrid, nouveauCycleBtn);
                }
                
                TitledPane titledPane = new TitledPane(avec.getNom(), avecContent);
                titledPane.setExpanded(cycleEnCours != null);
                accordion.getPanes().add(titledPane);
            }
            
            view.getChildren().addAll(title, accordion);
            VBox.setVgrow(accordion, Priority.ALWAYS);
            
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les données: " + e.getMessage());
        }
        
        root.setCenter(view);
    }
    
    /**
     * Organiser la répartition du capital (Module 7)
     */
    private void organiserRepartition(Avec avec, Cycle cycle) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Module 7 - Répartition du capital");
        dialog.setHeaderText("Clôture du cycle et répartition du capital");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);
        
        Label infoLabel = new Label("Saisissez les données finales du cycle:");
        infoLabel.setStyle("-fx-font-weight: bold;");
        
        Label creditLabel = new Label("Fonds de crédit final (FCFA):");
        TextField creditField = new TextField();
        creditField.setStyle(Styles.CHAMP_TEXTE);
        
        Label partsLabel = new Label("Total des parts achetées:");
        TextField partsField = new TextField();
        partsField.setStyle(Styles.CHAMP_TEXTE);
        
        // Calcul automatique de la valeur de la part
        Label valeurLabel = new Label("Valeur d'une part:");
        Label valeurValue = new Label("0 FCFA");
        valeurValue.setStyle("-fx-text-fill: " + Styles.VERT_PRINCIPAL + "; -fx-font-weight: bold;");
        
        creditField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                BigDecimal fonds = new BigDecimal(newVal);
                int parts = partsField.getText().isEmpty() ? 0 : Integer.parseInt(partsField.getText());
                if (parts > 0) {
                    BigDecimal valeur = fonds.divide(BigDecimal.valueOf(parts), 2, BigDecimal.ROUND_HALF_UP);
                    valeurValue.setText(formatMontant(valeur));
                }
            } catch (NumberFormatException e) {
                valeurValue.setText("0 FCFA");
            }
        });
        
        partsField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                BigDecimal fonds = creditField.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(creditField.getText());
                int parts = newVal.isEmpty() ? 0 : Integer.parseInt(newVal);
                if (parts > 0) {
                    BigDecimal valeur = fonds.divide(BigDecimal.valueOf(parts), 2, BigDecimal.ROUND_HALF_UP);
                    valeurValue.setText(formatMontant(valeur));
                }
            } catch (NumberFormatException e) {
                valeurValue.setText("0 FCFA");
            }
        });
        
        content.getChildren().addAll(infoLabel, creditLabel, creditField, partsLabel, partsField, valeurLabel, valeurValue);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    BigDecimal fondsCredit = new BigDecimal(creditField.getText().trim());
                    int totalParts = Integer.parseInt(partsField.getText().trim());
                    
                    if (fondsCredit.compareTo(BigDecimal.ZERO) <= 0) {
                        showAlert("Erreur", "Le fonds de crédit doit être supérieur à 0");
                        return null;
                    }
                    
                    if (totalParts <= 0) {
                        showAlert("Erreur", "Le nombre de parts doit être supérieur à 0");
                        return null;
                    }
                    
                    Cycle cycleTermine = cycleService.terminerCycle(cycle.getId(), fondsCredit, totalParts);
                    
                    if (cycleTermine != null) {
                        showInfo("Succès", 
                            "Cycle terminé avec succès!\n\n" +
                            "Résultats de la répartition:\n" +
                            "• Fonds de crédit total: " + formatMontant(fondsCredit) + "\n" +
                            "• Total parts: " + totalParts + "\n" +
                            "• Valeur d'une part: " + formatMontant(cycleTermine.getValeurPart()) + "\n\n" +
                            "Chaque membre recevra: (nombre de parts) × " + formatMontant(cycleTermine.getValeurPart()));
                        
                        rafraichirListeAvec();
                        showGestionCyclesGlobale();
                    } else {
                        showAlert("Erreur", "Échec de la clôture du cycle");
                    }
                    
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Format de nombre invalide");
                } catch (Exception e) {
                    showAlert("Erreur", e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    /**
     * Formate un montant en FCFA
     */
    private String formatMontant(BigDecimal montant) {
        if (montant == null) return "0 FCFA";
        return String.format("%,.0f FCFA", montant).replace(',', ' ');
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
                
                // Validation
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
                
                // Appeler le service
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