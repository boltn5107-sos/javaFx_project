package com.avec.view;

import java.sql.SQLException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.Avec;
import com.avec.model.Comptage;
import com.avec.model.Membre;
import com.avec.model.SessionUtilisateur;
import com.avec.service.AvecService;
import com.avec.service.ComptageService;
import com.avec.service.MembreService;
import com.avec.service.UtilisateurService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CompteurDashboardView {
    
    private MainApp mainApp;
    private SessionUtilisateur session;
    private MembreService membreService;
    private AvecService avecService;
    private ComptageService comptageService;
    private UtilisateurService utilisateurService;
    private BorderPane root;
    
    private Membre compteur;
    private Avec avec;
    
    private TableView<Membre> membresTable; private static final String STYLE_BOUTON_NORMAL = 
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
    private static final String ICONE_COMPTAGE = "🧮";
    private static final String ICONE_VERIFICATION = "✅";
    private static final String ICONE_RAPPORTS = "📋";
    private static final String ICONE_CAISSE = "💰";
    private static final String ICONE_DECONNEXION = "🚪";
    
    public CompteurDashboardView(MainApp mainApp) throws SQLException {
        this.mainApp = mainApp;
        this.session = SessionUtilisateur.getInstance();
        this.membreService = new MembreService();
        this.avecService = new AvecService();
        this.comptageService = new ComptageService();
        this.compteur = session.getMembre();
        initData();
        createView();
    }
    
    private void initData() {
        try {
            refreshirData();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les données: " + e.getMessage());
        }
    }
    
    private void refreshirData() throws SQLException {
        if (compteur != null && compteur.getAvecId() != null) {
            this.avec = avecService.getAvecById(compteur.getAvecId());
        }
    }
    
    private void createView() throws SQLException {
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
        
        Label logoLabel = new Label("🧮");
        logoLabel.setStyle("-fx-font-size: 24px;");
        
        String titre = avec != null ? "COMPTEUR - " + avec.getNom() : "COMPTEUR";
        Label titleLabel = new Label(titre);
        titleLabel.setStyle(Styles.TITRE_PRINCIPAL);
        
        titleBox.getChildren().addAll(logoLabel, titleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox userBox = new HBox(15);
        userBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label userIcon = new Label("👤");
        userIcon.setStyle("-fx-font-size: 20px;");
        
        Label userLabel = new Label(compteur.getPrenom() + " " + compteur.getNom());
        userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
        
        Label roleLabel = new Label("(Compteur)");
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
        
        Label avatarLabel = new Label("🧮");
        avatarLabel.setStyle("-fx-font-size: 48px;");
        
        String nomAvec = avec != null ? avec.getNom() : "AVEC";
        Label avecLabel = new Label(nomAvec);
        avecLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        profileBox.getChildren().addAll(avatarLabel, avecLabel);
        
        VBox menuBox = new VBox(5);
        menuBox.setPadding(new Insets(20, 0, 0, 0));
        
        // ✅ Création des ToggleButton
        ToggleButton btnDashboard = new ToggleButton(ICONE_TABLEAU_BORD + "  Tableau de bord");
        btnDashboard.setMaxWidth(Double.MAX_VALUE);
        btnDashboard.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnComptage = new ToggleButton(ICONE_COMPTAGE + "  Comptage des fonds");
        btnComptage.setMaxWidth(Double.MAX_VALUE);
        btnComptage.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnVerification = new ToggleButton(ICONE_VERIFICATION + "  Vérification des carnets");
        btnVerification.setMaxWidth(Double.MAX_VALUE);
        btnVerification.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnEtatCaisse = new ToggleButton(ICONE_CAISSE + "  État de la caisse");
        btnEtatCaisse.setMaxWidth(Double.MAX_VALUE);
        btnEtatCaisse.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnRapports = new ToggleButton(ICONE_RAPPORTS + "  Rapports de comptage");
        btnRapports.setMaxWidth(Double.MAX_VALUE);
        btnRapports.setStyle(STYLE_BOUTON_NORMAL);
        
        // ✅ Ajout des effets de survol
        ToggleButton[] allButtons = {btnDashboard, btnComptage, btnVerification, btnEtatCaisse, btnRapports};
        
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
            try {
                showDashboard();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        
        btnComptage.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnComptage.setStyle(STYLE_BOUTON_ACTIF);
            showComptage();
        });
        
        btnVerification.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnVerification.setStyle(STYLE_BOUTON_ACTIF);
            showVerification();
        });
        
        btnEtatCaisse.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnEtatCaisse.setStyle(STYLE_BOUTON_ACTIF);
            showEtatCaisse();
        });
        
        btnRapports.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnRapports.setStyle(STYLE_BOUTON_ACTIF);
            showRapports();
        });
        
        // ✅ Groupe de toggle (un seul sélectionné à la fois)
        ToggleGroup group = new ToggleGroup();
        for (ToggleButton btn : allButtons) {
            btn.setToggleGroup(group);
        }
        
        // ✅ Sélectionner le premier bouton par défaut
        btnDashboard.setSelected(true);
        btnDashboard.setStyle(STYLE_BOUTON_ACTIF);
        
        menuBox.getChildren().addAll(btnDashboard, btnComptage, btnVerification, btnEtatCaisse, btnRapports);
        
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
    
    
private void showDashboard() throws SQLException {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(20));
        dashboard.setAlignment(Pos.TOP_CENTER);
        
        refreshirData();
        
        try {
            int totalMembres = membreService.getMembresByAvecId(compteur.getAvecId()).size();
            
            Comptage dernierComptage = comptageService.getLastComptage(compteur.getAvecId());
            BigDecimal fondsCredit = dernierComptage != null ? dernierComptage.getFondCredit() : BigDecimal.ZERO;
            BigDecimal caisseSolidarite = avec != null ? avec.getCotisationCaisseSolidarite() : BigDecimal.ZERO;
            int totalParts = 0;
            for (Membre m : membreService.getMembresByAvecId(compteur.getAvecId())) {
                totalParts += m.getNombreParts();
            }
            
            String fondosFormat = String.format("%,.0f", fondsCredit).replace(',', ' ') + " FCA";
            String solidariteFormat = String.format("%,.0f", caisseSolidarite.multiply(BigDecimal.valueOf(totalMembres))).replace(',', ' ') + " FCA";
            
            Label welcomeLabel = new Label("Tableau de bord du Compteur");
            welcomeLabel.setStyle(Styles.TITRE_PRINCIPAL);
            
            HBox statsBox = new HBox(20);
            statsBox.setAlignment(Pos.CENTER);
            statsBox.setPadding(new Insets(20, 0, 20, 0));
            
            VBox carte1 = createStatCard("👥", "Membres", String.valueOf(totalMembres), Styles.VERT_PRINCIPAL);
            VBox carte2 = createStatCard("💰", "Fonds crédit", fondosFormat, Styles.BLEU_SECONDAIRE);
            VBox carte3 = createStatCard("🤝", "Caisse solidarité", solidariteFormat, Styles.ACCENT_DORE);
            VBox carte4 = createStatCard("📊", "Parts totales", String.valueOf(totalParts), Styles.VERT_PRINCIPAL);
            
            statsBox.getChildren().addAll(carte1, carte2, carte3, carte4);
            
            VBox infoBox = new VBox(10);
            infoBox.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 20;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            
            Label infoTitle = new Label("Procédure de comptage");
            infoTitle.setStyle(Styles.TITRE_SECONDAIRE);
            
            Label etape1 = new Label("1. Compter l'argent dans la cuvette des Compteurs");
            Label etape2 = new Label("2. Compter l'argent dans la cuvette des amendes");
            Label etape3 = new Label("3. Additionner les deux montants");
            Label etape4 = new Label("4. Vérifier que le total correspond au fonds de crédit attendu");
            Label etape5 = new Label("5. Annoncer le résultat à l'Assemblée");
            
            infoBox.getChildren().addAll(infoTitle, etape1, etape2, etape3, etape4, etape5);
            
            dashboard.getChildren().addAll(welcomeLabel, statsBox, infoBox);
            
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur chargement données: " + e.getMessage());
        }
        
       root.setCenter(dashboard);
    }
    
    private void showComptage() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Comptage des fonds");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        // Formulaire de comptage
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                     "-fx-background-radius: 10;");
        
        Label dateLabel = new Label("Date du comptage:");
        dateLabel.setStyle("-fx-font-weight: bold;");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(Styles.CHAMP_TEXTE);
        
        Label fondCreditLabel = new Label("Fonds de crédit (cuvette Compteurs):");
        fondCreditLabel.setStyle("-fx-font-weight: bold;");
        TextField fondCreditField = new TextField();
        fondCreditField.setPromptText("Montant en FCFA");
        fondCreditField.setStyle(Styles.CHAMP_TEXTE);
        
        Label amendesLabel = new Label("Amendes (cuvette amendes):");
        amendesLabel.setStyle("-fx-font-weight: bold;");
        TextField amendesField = new TextField();
        amendesField.setPromptText("Montant en FCFA");
        amendesField.setStyle(Styles.CHAMP_TEXTE);
        
        Label totalLabel = new Label("Total:");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
        Label totalValueLabel = new Label("0 FCFA");
        totalValueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
        
        // Calcul automatique du total
        fondCreditField.textProperty().addListener((obs, oldVal, newVal) -> calculerTotal(fondCreditField, amendesField, totalValueLabel));
        amendesField.textProperty().addListener((obs, oldVal, newVal) -> calculerTotal(fondCreditField, amendesField, totalValueLabel));
        
        form.add(dateLabel, 0, 0);
        form.add(datePicker, 1, 0);
        form.add(fondCreditLabel, 0, 1);
        form.add(fondCreditField, 1, 1);
        form.add(amendesLabel, 0, 2);
        form.add(amendesField, 1, 2);
        form.add(totalLabel, 0, 3);
        form.add(totalValueLabel, 1, 3);
        
        // Boutons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button validerButton = new Button("✅ Valider le comptage");
        validerButton.setStyle(Styles.BOUTON_PRINCIPAL);
        validerButton.setOnAction(e -> {
            try {
                LocalDate dateComptage = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
                BigDecimal fondCredit = fondCreditField.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(fondCreditField.getText().replace(" ", "").replace(",", "."));
                BigDecimal amendes = amendesField.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(amendesField.getText().replace(" ", "").replace(",", "."));
                
                Comptage comptage = new Comptage();
                comptage.setAvecId(compteur.getAvecId());
                comptage.setDateComptage(dateComptage);
                comptage.setFondCredit(fondCredit);
                comptage.setAmendes(amendes);
                comptage.setCompteurId(compteur.getId());
                comptage.setEstConfirme(false);
                
                comptageService.save(comptage);
                
                showInfo("Succès", "Comptage enregistré avec succès!");
                fondCreditField.clear();
                amendesField.clear();
                totalValueLabel.setText("0 FCFA");
                
            } catch (Exception ex) {
                showAlert("Erreur", "Erreur lors de l'enregistrement: " + ex.getMessage());
            }
        });
        
        Button annulerButton = new Button("❌ Annuler");
        annulerButton.setStyle(Styles.BOUTON_SECONDAIRE);
        annulerButton.setOnAction(e -> {
            fondCreditField.clear();
            amendesField.clear();
            datePicker.setValue(LocalDate.now());
            totalValueLabel.setText("0");
        });
        
        buttonBox.getChildren().addAll(validerButton, annulerButton);
        
        view.getChildren().addAll(title, form, buttonBox);
        root.setCenter(view);
    }
    
    private void calculerTotal(TextField fondCredit, TextField amendes, Label totalLabel) {
        try {
            double fond = fondCredit.getText().isEmpty() ? 0 : Double.parseDouble(fondCredit.getText());
            double amd = amendes.getText().isEmpty() ? 0 : Double.parseDouble(amendes.getText());
            double total = fond + amd;
            totalLabel.setText(String.format("%,.0f FCFA", total));
        } catch (NumberFormatException e) {
            totalLabel.setText("0 FCFA");
        }
    }
    
    private void showVerification() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Vérification des carnets de compte");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        // Tableau des membres avec leurs parts
        TableView<Membre> verificationTable = new TableView<>();
        verificationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Membre, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(120);
        
        TableColumn<Membre, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(120);
        
        TableColumn<Membre, Integer> colParts = new TableColumn<>("Parts achetées");
        colParts.setCellValueFactory(new PropertyValueFactory<>("nombreParts"));
        colParts.setPrefWidth(100);
        
        TableColumn<Membre, String> colTotal = new TableColumn<>("Total épargne");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalEpargne"));
        colTotal.setPrefWidth(120);
        
        TableColumn<Membre, Boolean> colVerifie = new TableColumn<>("Vérifié");
        colVerifie.setCellFactory(col -> new TableCell<Membre, Boolean>() {
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
        colVerifie.setPrefWidth(80);
        
        verificationTable.getColumns().addAll(colNom, colPrenom, colParts, colTotal, colVerifie);
        
        try {
            List<Membre> membres = membreService.getMembresByAvecId(compteur.getAvecId());
            verificationTable.setItems(FXCollections.observableArrayList(membres));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
        }
        
        Button verifierTous = new Button("✅ Vérifier tous les carnets");
        verifierTous.setStyle(Styles.BOUTON_PRINCIPAL);
        verifierTous.setPrefWidth(300);
        
        verifierTous.setOnAction(e -> {
            for (Membre m : verificationTable.getItems()) {
                // TODO: Marquer comme vérifié dans la base
            }
            showInfo("Succès", "Tous les carnets ont été vérifiés!");
        });
        
        view.getChildren().addAll(title, verificationTable, verifierTous);
        root.setCenter(view);
    }
    
private void showEtatCaisse() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("État de la caisse");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                     "-fx-background-radius: 10;");
        
        Label creditLabel = new Label("Fonds de crédit:");
        creditLabel.setStyle("-fx-font-weight: bold;");
        Label creditValue = new Label("0 FCA");
        creditValue.setStyle("-fx-text-fill: " + Styles.VERT_PRINCIPAL + "; -fx-font-size: 16px;");
        
        Label solidariteLabel = new Label("Caisse solidarité:");
        solidariteLabel.setStyle("-fx-font-weight: bold;");
        Label solidariteValue = new Label("0 FCA");
        solidariteValue.setStyle("-fx-text-fill: " + Styles.BLEU_SECONDAIRE + "; -fx-font-size: 16px;");
        
        Label totalLabel = new Label("Total général:");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label totalValue = new Label("0 FCA");
        totalValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Styles.ACCENT_DORE + ";");
        
        grid.add(creditLabel, 0, 0);
        grid.add(creditValue, 1, 0);
        grid.add(solidariteLabel, 0, 1);
        grid.add(solidariteValue, 1, 1);
        grid.add(totalLabel, 0, 2);
        grid.add(totalValue, 1, 2);
        
        Button actualiserButton = new Button("🔄 Actualiser");
        actualiserButton.setStyle(Styles.BOUTON_PRINCIPAL);
        actualiserButton.setOnAction(e -> {
            try {
                Comptage dernierComptage = comptageService.getLastComptage(compteur.getAvecId());
                BigDecimal fondsCredit = dernierComptage != null ? dernierComptage.getTotal() : BigDecimal.ZERO;
                
                int totalMembres = membreService.getMembresByAvecId(compteur.getAvecId()).size();
                BigDecimal solidarite = avec != null && avec.getCotisationCaisseSolidarite() != null 
                    ? avec.getCotisationCaisseSolidarite().multiply(BigDecimal.valueOf(totalMembres)) 
                    : BigDecimal.ZERO;
                BigDecimal total = fondsCredit.add(solidarite);
                
                creditValue.setText(String.format("%,.0f", fondsCredit).replace(',', ' ') + " FCA");
                solidariteValue.setText(String.format("%,.0f", solidarite).replace(',', ' ') + " FCA");
                totalValue.setText(String.format("%,.0f", total).replace(',', ' ') + " FCA");
                
            } catch (Exception ex) {
                showAlert("Erreur", "Erreur lors de l'actualisation: " + ex.getMessage());
            }
        });
        
        view.getChildren().addAll(title, grid, actualiserButton);
        root.setCenter(view);
    }
    
private void showRapports() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Rapports de comptage");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        TableView<Comptage> rapportsTable = new TableView<>();
        rapportsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Comptage, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateComptageFormatted()));
        colDate.setPrefWidth(120);
        
        TableColumn<Comptage, String> colFondCredit = new TableColumn<>("Fonds crédit");
        colFondCredit.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFondCredit() != null 
                ? String.format("%,.0f", cellData.getValue().getFondCredit()).replace(',', ' ') + " FCA" 
                : "0 FCA"));
        colFondCredit.setPrefWidth(120);
        
        TableColumn<Comptage, String> colAmendes = new TableColumn<>("Amendes");
        colAmendes.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAmendes() != null 
                ? String.format("%,.0f", cellData.getValue().getAmendes()).replace(',', ' ') + " FCA" 
                : "0 FCA"));
        colAmendes.setPrefWidth(100);
        
        TableColumn<Comptage, String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTotalFormatted()));
        colTotal.setPrefWidth(120);
        
        TableColumn<Comptage, String> colConfirme = new TableColumn<>("Statut");
        colConfirme.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().isEstConfirme() ? "Confirmé" : "En attente"));
        colConfirme.setPrefWidth(80);
        
        TableColumn<Comptage, String> colCompteur = new TableColumn<>("Compteur");
        colCompteur.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCompteur() != null 
                ? cellData.getValue().getCompteur().getPrenom() + " " + cellData.getValue().getCompteur().getNom()
                : ""));
        colCompteur.setPrefWidth(120);
        
        rapportsTable.getColumns().addAll(colDate, colFondCredit, colAmendes, colTotal, colConfirme, colCompteur);
        
        try {
            List<Comptage> comptages = comptageService.getByAvecId(compteur.getAvecId());
            rapportsTable.setItems(FXCollections.observableArrayList(comptages));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les rapports: " + e.getMessage());
        }
        
        Button exporterPDF = new Button("📎 Exporter en PDF");
        exporterPDF.setStyle(Styles.BOUTON_SECONDAIRE);
        exporterPDF.setOnAction(e -> showInfo("Info", "Fonctionnalité en cours de développement"));
        
        view.getChildren().addAll(title, rapportsTable, exporterPDF);
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