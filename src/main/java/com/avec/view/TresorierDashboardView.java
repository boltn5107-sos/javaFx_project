package com.avec.view;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.Avec;
import com.avec.model.Membre;
import com.avec.model.Pret;
import com.avec.model.Remboursement;
import com.avec.model.Reunion;
import com.avec.model.SessionUtilisateur;
import com.avec.service.AvecService;
import com.avec.service.MembreService;
import com.avec.service.PretService;
import com.avec.service.RemboursementService;
import com.avec.service.ReunionService;
import com.avec.service.UtilisateurService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TresorierDashboardView {
    
    private MainApp mainApp;
    private SessionUtilisateur session;
    private MembreService membreService;
    private AvecService avecService;
    private PretService pretService;
    private RemboursementService remboursementService;
    private ReunionService reunionService;
    private UtilisateurService utilisateurService;
    private BorderPane root;
    
    private Membre tresorier;
    private Avec avec;
    private Reunion reunionEnCours;
    
    private TableView<Pret> pretsTable;
    private TableView<Remboursement> remboursementsTable;
    private TableView<Object> transactionsTable;
    
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
    private static final String ICONE_CAISSE = "💰";
    private static final String ICONE_PRETS = "💳";
    private static final String ICONE_REMBOURSEMENTS = "🔄";
    private static final String ICONE_RAPPORTS = "📋";
    private static final String ICONE_HISTORIQUE = "📜";
    private static final String ICONE_DECONNEXION = "🚪";
    
    public TresorierDashboardView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.session = SessionUtilisateur.getInstance();
        this.membreService = new MembreService();
        this.avecService = new AvecService();
        this.pretService = new PretService();
        this.remboursementService = new RemboursementService();
        this.reunionService = new ReunionService();
        this.tresorier = session.getMembre();
        initData();
        createView();
    }
    
    private void initData() {
        try {
            if (tresorier != null && tresorier.getAvecId() != null) {
                this.avec = avecService.getAvecById(tresorier.getAvecId());
                this.reunionEnCours = reunionService.getReunionEnCoursParAvec(tresorier.getAvecId());
            }
        } catch (Exception e) {
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
        
        Label logoLabel = new Label("💰");
        logoLabel.setStyle("-fx-font-size: 24px;");
        
        String titre = avec != null ? "TRÉSORIER - " + avec.getNom() : "TRÉSORIER";
        Label titleLabel = new Label(titre);
        titleLabel.setStyle(Styles.TITRE_PRINCIPAL);
        
        titleBox.getChildren().addAll(logoLabel, titleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox userBox = new HBox(15);
        userBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label userIcon = new Label("👤");
        userIcon.setStyle("-fx-font-size: 20px;");
        
        Label userLabel = new Label(tresorier.getPrenom() + " " + tresorier.getNom());
        userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
        
        Label roleLabel = new Label("(Trésorier)");
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
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + Styles.VERT_PRINCIPAL + ";" +
                        "-fx-border-color: " + Styles.GRIS_CLAIR + ";" +
                        "-fx-border-width: 0 2 0 0;");
        
        VBox profileBox = new VBox(10);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(0, 0, 20, 0));
        profileBox.setStyle("-fx-border-color: " + Styles.GRIS_CLAIR + ";" +
                           "-fx-border-width: 0 0 2 0;");
        
        Label avatarLabel = new Label("💰");
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
        
        ToggleButton btnCaisse = new ToggleButton(ICONE_CAISSE + "  Gestion de la caisse");
        btnCaisse.setMaxWidth(Double.MAX_VALUE);
        btnCaisse.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnDecaissements = new ToggleButton(ICONE_PRETS + "  Décaissements");
        btnDecaissements.setMaxWidth(Double.MAX_VALUE);
        btnDecaissements.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnRemboursements = new ToggleButton(ICONE_REMBOURSEMENTS + "  Remboursements");
        btnRemboursements.setMaxWidth(Double.MAX_VALUE);
        btnRemboursements.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnHistorique = new ToggleButton(ICONE_HISTORIQUE + "  Historique");
        btnHistorique.setMaxWidth(Double.MAX_VALUE);
        btnHistorique.setStyle(STYLE_BOUTON_NORMAL);
        
        ToggleButton btnRapports = new ToggleButton(ICONE_RAPPORTS + "  Rapports financiers");
        btnRapports.setMaxWidth(Double.MAX_VALUE);
        btnRapports.setStyle(STYLE_BOUTON_NORMAL);
        
        // ✅ Ajout des effets de survol
        ToggleButton[] allButtons = {btnDashboard, btnCaisse, btnDecaissements, btnRemboursements, btnHistorique, btnRapports};
        
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
        
        btnCaisse.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnCaisse.setStyle(STYLE_BOUTON_ACTIF);
            showCaisse();
        });
        
        btnDecaissements.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnDecaissements.setStyle(STYLE_BOUTON_ACTIF);
            showDecaissements();
        });
        
        btnRemboursements.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnRemboursements.setStyle(STYLE_BOUTON_ACTIF);
            showRemboursements();
        });
        
        btnHistorique.setOnAction(e -> {
            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
            btnHistorique.setStyle(STYLE_BOUTON_ACTIF);
            showHistorique();
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
        
        menuBox.getChildren().addAll(btnDashboard, btnCaisse, btnDecaissements, btnRemboursements, btnHistorique, btnRapports);
        
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
    private void showGestionReunion() {
        if (tresorier != null && tresorier.getAvecId() != null) {
            ReunionView rv = new ReunionView(tresorier.getAvecId());
            rv.afficher();
        } else {
            showAlert("Erreur", "Aucune AVEC associée au trésorier");
        }
    }
    
    private void showDashboard() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(20));
        dashboard.setAlignment(Pos.TOP_CENTER);
        
        // Utilisation des services sans try-catch SQLException
        // car les services gèrent les exceptions en interne
        BigDecimal fondCredit = avec != null ? avec.getTotalCredit() : BigDecimal.ZERO;
        BigDecimal fondSolidarite = avec != null ? avec.getCotisationCaisseSolidarite() : BigDecimal.ZERO;
        int pretsActifs = pretService.compterPretsActifsParAvecId(tresorier.getAvecId());
        int totalRemboursements = remboursementService.getNombreRemboursements();
        
        Label welcomeLabel = new Label("Tableau de bord du Trésorier");
        welcomeLabel.setStyle(Styles.TITRE_PRINCIPAL);
        
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(20, 0, 20, 0));
        
        VBox carte1 = createStatCard("💰", "Fonds de crédit", 
            formatMontant(fondCredit), Styles.VERT_PRINCIPAL);
        VBox carte2 = createStatCard("🤝", "Caisse solidarité", 
            formatMontant(fondSolidarite), Styles.BLEU_SECONDAIRE);
        VBox carte3 = createStatCard("💳", "Prêts en cours", 
            String.valueOf(pretsActifs), Styles.ACCENT_DORE);
        VBox carte4 = createStatCard("🔄", "Remboursements", 
            String.valueOf(totalRemboursements), Styles.VERT_PRINCIPAL);
        
        statsBox.getChildren().addAll(carte1, carte2, carte3, carte4);
        
        // Informations de l'AVEC
        VBox infoBox = new VBox(10);
        infoBox.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label infoTitle = new Label("Informations financières");
        infoTitle.setStyle(Styles.TITRE_SECONDAIRE);
        
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(10);
        
        if (avec != null) {
            infoGrid.add(new Label("Prix d'une part:"), 0, 0);
            infoGrid.add(new Label(formatMontant(avec.getPrixPart())), 1, 0);
            infoGrid.add(new Label("Taux frais service:"), 0, 1);
            infoGrid.add(new Label(avec.getTauxFraisServiceMensuel() + "%"), 1, 1);
            infoGrid.add(new Label("Durée max prêt:"), 0, 2);
            //infoGrid.add(new Label(avec.getDureeMaxPretSemaines() + " semaines"), 1, 2);
            infoGrid.add(new Label("Plafond emprunt:"), 0, 3);
            infoGrid.add(new Label("3x l'épargne"), 1, 3);
            infoGrid.add(new Label("Cotisation solidarité:"), 0, 4);
            infoGrid.add(new Label(formatMontant(avec.getCotisationCaisseSolidarite())), 1, 4);
        } else {
            infoGrid.add(new Label("Données non disponibles"), 0, 0);
        }
        
        infoBox.getChildren().addAll(infoTitle, infoGrid);
        
        dashboard.getChildren().addAll(welcomeLabel, statsBox, infoBox);
        
        root.setCenter(dashboard);
    }
    
    private void showCaisse() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Gestion de la caisse");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        // État actuel
        GridPane etatGrid = new GridPane();
        etatGrid.setHgap(20);
        etatGrid.setVgap(15);
        etatGrid.setPadding(new Insets(20));
        etatGrid.setStyle("-fx-background-color: " + Styles.BLANC + ";" +
                         "-fx-background-radius: 10;");
        
        Label creditLabel = new Label("Fonds de crédit:");
        creditLabel.setStyle("-fx-font-weight: bold;");
        TextField creditField = new TextField();
        creditField.setEditable(false);
        creditField.setStyle(Styles.CHAMP_TEXTE);
        
        Label solidariteLabel = new Label("Caisse solidarité:");
        solidariteLabel.setStyle("-fx-font-weight: bold;");
        TextField solidariteField = new TextField();
        solidariteField.setEditable(false);
        solidariteField.setStyle(Styles.CHAMP_TEXTE);
        
        if (avec != null) {
            creditField.setText(formatMontant(avec.getTotalCredit()));
            solidariteField.setText(formatMontant(avec.getCotisationCaisseSolidarite()));
        }
        
        etatGrid.add(creditLabel, 0, 0);
        etatGrid.add(creditField, 1, 0);
        etatGrid.add(solidariteLabel, 0, 1);
        etatGrid.add(solidariteField, 1, 1);
        
        // Boutons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button auditButton = new Button("🔍 Vérifier la caisse");
        auditButton.setStyle(Styles.BOUTON_SECONDAIRE);
        auditButton.setOnAction(e -> verifierCaisse());
        
        Button imprimerButton = new Button("🖨️ Imprimer état");
        imprimerButton.setStyle(Styles.BOUTON_ACCENT);
        
        buttonBox.getChildren().addAll(auditButton, imprimerButton);
        
        view.getChildren().addAll(title, etatGrid, buttonBox);
        root.setCenter(view);
    }
    
    private void verifierCaisse() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Vérification de caisse");
        alert.setHeaderText("État de la caisse");
        
        StringBuilder content = new StringBuilder();
        content.append("=== VÉRIFICATION DE CAISSE ===\n\n");
        content.append("Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        content.append("AVEC: ").append(avec != null ? avec.getNom() : "N/A").append("\n\n");
        
        BigDecimal fondCredit = avec != null ? avec.getTotalCredit() : BigDecimal.ZERO;
        BigDecimal fondSolidarite = avec != null ? avec.getCotisationCaisseSolidarite() : BigDecimal.ZERO;
        
        content.append("Fonds de crédit: ").append(formatMontant(fondCredit)).append("\n");
        content.append("Caisse solidarité: ").append(formatMontant(fondSolidarite)).append("\n");
        content.append("Total: ").append(formatMontant(fondCredit.add(fondSolidarite))).append("\n\n");
        
        content.append("Vérifié par: ").append(tresorier.getNomComplet()).append("\n");
        content.append("Signature: _______________");
        
        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setPrefHeight(300);
        textArea.setPrefWidth(400);
        
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
    
    private void showDecaissements() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Décaissements de prêts");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        // Formulaire de décaissement
        TitledPane decaissementPane = new TitledPane();
        decaissementPane.setText("Nouveau décaissement");
        decaissementPane.setExpanded(true);
        
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(10);
        form.setPadding(new Insets(15));
        
        Label membreLabel = new Label("Emprunteur:");
        membreLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<Membre> membreCombo = new ComboBox<>();
        membreCombo.setPromptText("Sélectionner un membre");
        membreCombo.setStyle(Styles.CHAMP_TEXTE);
        try {
        List<Membre> membres = membreService.getMembresByAvecId(tresorier.getAvecId());
        membreCombo.setItems(FXCollections.observableArrayList(membres));
        }catch(SQLException e) {
        	e.printStackTrace();
        }
        Label montantLabel = new Label("Montant du prêt:");
        montantLabel.setStyle("-fx-font-weight: bold;");
        TextField montantField = new TextField();
        montantField.setPromptText("Montant en FCFA");
        montantField.setStyle(Styles.CHAMP_TEXTE);
        
        Label dureeLabel = new Label("Durée (semaines):");
        dureeLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<Integer> dureeCombo = new ComboBox<>();
        dureeCombo.getItems().addAll(4, 8, 12, 16, 20, 24);
        dureeCombo.setValue(12);
        dureeCombo.setStyle(Styles.CHAMP_TEXTE);
        
        Label fraisLabel = new Label("Frais de service mensuels:");
        fraisLabel.setStyle("-fx-font-weight: bold;");
        Label fraisValue = new Label("0 FCFA");
        fraisValue.setStyle("-fx-text-fill: " + Styles.VERT_PRINCIPAL + "; -fx-font-weight: bold;");
        
        // Calcul automatique des frais
        montantField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                BigDecimal montant = new BigDecimal(newVal);
                if (avec != null && avec.getTauxFraisServiceMensuel() != null) {
                    BigDecimal taux = avec.getTauxFraisServiceMensuel();
                    BigDecimal frais = montant.multiply(taux).divide(new BigDecimal(100));
                    fraisValue.setText(formatMontant(frais) + " / mois");
                }
            } catch (NumberFormatException e) {
                fraisValue.setText("0 FCFA / mois");
            }
        });
        
        form.add(membreLabel, 0, 0);
        form.add(membreCombo, 1, 0);
        form.add(montantLabel, 0, 1);
        form.add(montantField, 1, 1);
        form.add(dureeLabel, 0, 2);
        form.add(dureeCombo, 1, 2);
        form.add(fraisLabel, 0, 3);
        form.add(fraisValue, 1, 3);
        
        decaissementPane.setContent(form);
        
        // Liste des prêts en cours
        TitledPane pretsEnCoursPane = new TitledPane();
        pretsEnCoursPane.setText("Prêts en cours");
        pretsEnCoursPane.setExpanded(true);
        
        pretsTable = new TableView<>();
        pretsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        pretsTable.setPrefHeight(200);
        
        TableColumn<Pret, String> colMembre = new TableColumn<>("Emprunteur");
        colMembre.setCellValueFactory(cellData -> {
            Pret p = cellData.getValue();
            if (p.getEmprunteur() != null) {
                return new javafx.beans.property.SimpleStringProperty(p.getEmprunteur().getNomComplet());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        TableColumn<Pret, String> colMontant = new TableColumn<>("Montant");
        colMontant.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(formatMontant(cellData.getValue().getMontantInitial())));
        
        TableColumn<Pret, String> colRestant = new TableColumn<>("Restant dû");
        colRestant.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(formatMontant(cellData.getValue().getMontantRestantDu())));
        
        TableColumn<Pret, String> colEcheance = new TableColumn<>("Échéance");
        colEcheance.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateEcheance();
            return new javafx.beans.property.SimpleStringProperty(
                date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-"
            );
        });
        
        TableColumn<Pret, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut().toString()));
        
        pretsTable.getColumns().addAll(colMembre, colMontant, colRestant, colEcheance, colStatut);
        
        pretsEnCoursPane.setContent(pretsTable);
        
        // Boutons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button decaisserButton = new Button("💸 Décaisser");
        decaisserButton.setStyle(Styles.BOUTON_PRINCIPAL);
        decaisserButton.setOnAction(e -> decaisserPret(membreCombo, montantField, dureeCombo));
        
        Button annulerButton = new Button("❌ Annuler");
        annulerButton.setStyle(Styles.BOUTON_SECONDAIRE);
        
        buttonBox.getChildren().addAll(decaisserButton, annulerButton);
        
        view.getChildren().addAll(title, decaissementPane, pretsEnCoursPane, buttonBox);
        root.setCenter(view);
        
        // Charger les prêts
        chargerPrets();
    }
    
    private void chargerPrets() {
        List<Pret> prets = pretService.listerPretsParAvecId(tresorier.getAvecId());
        pretsTable.setItems(FXCollections.observableArrayList(prets));
    }
    
    private void decaisserPret(ComboBox<Membre> membreCombo, TextField montantField, 
                                ComboBox<Integer> dureeCombo) {
        try {
            Membre emprunteur = membreCombo.getValue();
            if (emprunteur == null) {
                showAlert("Erreur", "Veuillez sélectionner un emprunteur");
                return;
            }
            
            String montantText = montantField.getText().trim();
            if (montantText.isEmpty()) {
                showAlert("Erreur", "Veuillez saisir un montant");
                return;
            }
            
            BigDecimal montant = new BigDecimal(montantText);
            if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Erreur", "Le montant doit être supérieur à 0");
                return;
            }
            
            int duree = dureeCombo.getValue();
            
            // Vérifier le plafond (3x l'épargne)
            BigDecimal plafond = emprunteur.getTotalEpargne().multiply(new BigDecimal(3));
            if (montant.compareTo(plafond) > 0) {
                showAlert("Erreur", "Le montant demandé dépasse le plafond (3x épargne = " + 
                         formatMontant(plafond) + ")");
                return;
            }
            
            // Vérifier le fonds de crédit disponible
            if (avec != null && montant.compareTo(avec.getTotalCredit()) > 0) {
                showAlert("Erreur", "Fonds de crédit insuffisant. Disponible: " + 
                         formatMontant(avec.getTotalCredit()));
                return;
            }
            
            // Vérifier la réunion en cours
            if (reunionEnCours == null) {
                showAlert("Erreur", "Aucune réunion en cours. Veuillez d'abord créer une réunion.");
                return;
            }
            
            // Vérifier que la réunion est de type CREDIT
            if (reunionEnCours.getType() != com.avec.enums.TypeReunion.CREDIT) {
                showAlert("Erreur", "Les prêts ne peuvent être accordés que lors d'une réunion de type CRÉDIT.\n" +
                         "La réunion actuelle est de type: " + reunionEnCours.getType());
                return;
            }
            
            // Créer le prêt
            Pret pret = new Pret();
            pret.setEmprunteur(emprunteur);
            pret.setEmprunteurId(emprunteur.getId());
            pret.setMontantInitial(montant);
            pret.setDureeEnSemaines(duree);
            pret.setFraisServiceMensuel(avec.getTauxFraisServiceMensuel());
            pret.setReunionDecaissement(reunionEnCours);
            pret.setReunionDecaissementId(reunionEnCours.getId());
            
            // Calculer le montant total dû
            BigDecimal montantTotal = pret.getMontantTotalDu();
            pret.setMontantRestantDu(montantTotal);
            
            // Enregistrer le prêt
            boolean success = pretService.enregistrerPret(pret);
            
            if (success) {
                showInfo("Succès", "Prêt décaissé avec succès!\n" +
                        "Montant: " + formatMontant(montant) + "\n" +
                        "Emprunteur: " + emprunteur.getNomComplet() + "\n" +
                        "Durée: " + duree + " semaines\n" +
                        "Total à rembourser: " + formatMontant(montantTotal));
                
                // Réinitialiser le formulaire
                montantField.clear();
                dureeCombo.setValue(12);
                membreCombo.setValue(null);
                
                // Rafraîchir la liste
                chargerPrets();
                
                // Mettre à jour le dashboard
                showDashboard();
            } else {
                showAlert("Erreur", "Échec de l'enregistrement du prêt");
            }
            
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Montant invalide");
        }
    }
    
    private void showRemboursements() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Remboursements de prêts");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        // Formulaire de remboursement
        TitledPane remboursementPane = new TitledPane();
        remboursementPane.setText("Enregistrer un remboursement");
        remboursementPane.setExpanded(true);
        
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(10);
        form.setPadding(new Insets(15));
        
        Label pretLabel = new Label("Prêt à rembourser:");
        pretLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<Pret> pretCombo = new ComboBox<>();
        pretCombo.setPromptText("Sélectionner un prêt");
        pretCombo.setStyle(Styles.CHAMP_TEXTE);
        
        List<Pret> pretsActifs = pretService.listerPretsActifsParAvecId(tresorier.getAvecId());
        pretCombo.setItems(FXCollections.observableArrayList(pretsActifs));
        
        Label montantLabel = new Label("Montant remboursé:");
        montantLabel.setStyle("-fx-font-weight: bold;");
        TextField montantField = new TextField();
        montantField.setPromptText("Montant en FCFA");
        montantField.setStyle(Styles.CHAMP_TEXTE);
        
        Label resteLabel = new Label("Reste à payer:");
        resteLabel.setStyle("-fx-font-weight: bold;");
        Label resteValue = new Label("0 FCFA");
        resteValue.setStyle("-fx-text-fill: " + Styles.VERT_PRINCIPAL + "; -fx-font-weight: bold;");
        
        // Calcul automatique du reste
        pretCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getMontantRestantDu() != null) {
                resteValue.setText(formatMontant(newVal.getMontantRestantDu()));
            } else {
                resteValue.setText("0 FCFA");
            }
        });
        
        form.add(pretLabel, 0, 0);
        form.add(pretCombo, 1, 0);
        form.add(montantLabel, 0, 1);
        form.add(montantField, 1, 1);
        form.add(resteLabel, 0, 2);
        form.add(resteValue, 1, 2);
        
        remboursementPane.setContent(form);
        
        // Liste des remboursements récents
        TitledPane historiquePane = new TitledPane();
        historiquePane.setText("Historique des remboursements");
        historiquePane.setExpanded(true);
        
        remboursementsTable = new TableView<>();
        remboursementsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        remboursementsTable.setPrefHeight(200);
        
        TableColumn<Remboursement, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateRemboursementFormatted()));
        
        TableColumn<Remboursement, String> colEmprunteur = new TableColumn<>("Emprunteur");
        colEmprunteur.setCellValueFactory(cellData -> {
            Pret pret = cellData.getValue().getPret();
            if (pret != null && pret.getEmprunteur() != null) {
                return new javafx.beans.property.SimpleStringProperty(pret.getEmprunteur().getNomComplet());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        TableColumn<Remboursement, String> colMontant = new TableColumn<>("Montant");
        colMontant.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(formatMontant(cellData.getValue().getMontant())));
        
        remboursementsTable.getColumns().addAll(colDate, colEmprunteur, colMontant);
        
        historiquePane.setContent(remboursementsTable);
        
        // Boutons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button rembourserButton = new Button("💰 Enregistrer remboursement");
        rembourserButton.setStyle(Styles.BOUTON_PRINCIPAL);
        rembourserButton.setOnAction(e -> enregistrerRemboursement(pretCombo, montantField));
        
        Button annulerButton = new Button("❌ Annuler");
        annulerButton.setStyle(Styles.BOUTON_SECONDAIRE);
        
        buttonBox.getChildren().addAll(rembourserButton, annulerButton);
        
        view.getChildren().addAll(title, remboursementPane, historiquePane, buttonBox);
        root.setCenter(view);
        
        // Charger l'historique
        chargerRemboursements();
    }
    
    private void chargerRemboursements() {
        List<Remboursement> remboursements = remboursementService.listerRemboursement();
        remboursementsTable.setItems(FXCollections.observableArrayList(remboursements));
    }
    
    private void enregistrerRemboursement(ComboBox<Pret> pretCombo, TextField montantField) {
        try {
            Pret pret = pretCombo.getValue();
            if (pret == null) {
                showAlert("Erreur", "Veuillez sélectionner un prêt");
                return;
            }
            
            String montantText = montantField.getText().trim();
            if (montantText.isEmpty()) {
                showAlert("Erreur", "Veuillez saisir un montant");
                return;
            }
            
            BigDecimal montant = new BigDecimal(montantText);
            if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Erreur", "Le montant doit être supérieur à 0");
                return;
            }
            
            if (montant.compareTo(pret.getMontantRestantDu()) > 0) {
                showAlert("Erreur", "Le montant remboursé dépasse le reste dû (" + 
                         formatMontant(pret.getMontantRestantDu()) + ")");
                return;
            }
            
            // Vérifier la réunion en cours
            if (reunionEnCours == null) {
                showAlert("Erreur", "Aucune réunion en cours. Veuillez d'abord créer une réunion.");
                return;
            }
            
            // Créer le remboursement
            Remboursement remboursement = new Remboursement();
            remboursement.setMontant(montant);
            remboursement.setPret(pret);
            remboursement.setPretId(pret.getId());
            remboursement.setReunion(reunionEnCours);
            remboursement.setReunionId(reunionEnCours.getId());
            remboursement.setDateRemboursement(LocalDate.now().atStartOfDay());
            
            // Enregistrer le remboursement
            boolean success = remboursementService.enregistreRemboursement(remboursement);
            
            if (success) {
                BigDecimal nouveauRestant = pret.getMontantRestantDu().subtract(montant);
                showInfo("Succès", "Remboursement enregistré avec succès!\n" +
                        "Montant: " + formatMontant(montant) + "\n" +
                        "Reste dû: " + formatMontant(nouveauRestant));
                
                // Réinitialiser le formulaire
                montantField.clear();
                pretCombo.setValue(null);
                
                // Rafraîchir les listes
                chargerPrets();
                chargerRemboursements();
                
                // Mettre à jour le dashboard
                showDashboard();
            } else {
                showAlert("Erreur", "Échec de l'enregistrement du remboursement");
            }
            
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Montant invalide");
        }
    }
    
    private void showHistorique() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Historique des transactions");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        // Filtres
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        DatePicker debutPicker = new DatePicker(LocalDate.now().minusMonths(1));
        debutPicker.setPromptText("Date début");
        debutPicker.setStyle(Styles.CHAMP_TEXTE);
        
        DatePicker finPicker = new DatePicker(LocalDate.now());
        finPicker.setPromptText("Date fin");
        finPicker.setStyle(Styles.CHAMP_TEXTE);
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Toutes", "Décaissements", "Remboursements", "Cotisations");
        typeCombo.setValue("Toutes");
        typeCombo.setStyle(Styles.CHAMP_TEXTE);
        
        Button filtrerButton = new Button("🔍 Filtrer");
        filtrerButton.setStyle(Styles.BOUTON_PRINCIPAL);
        
        filterBox.getChildren().addAll(
            new Label("Du:"), debutPicker,
            new Label("Au:"), finPicker,
            new Label("Type:"), typeCombo,
            filtrerButton
        );
        
        // Tableau des transactions
        transactionsTable = new TableView<>();
        transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Object, String> colDate = new TableColumn<>("Date");
        colDate.setPrefWidth(100);
        TableColumn<Object, String> colType = new TableColumn<>("Type");
        colType.setPrefWidth(120);
        TableColumn<Object, String> colMembre = new TableColumn<>("Membre");
        colMembre.setPrefWidth(150);
        TableColumn<Object, String> colMontant = new TableColumn<>("Montant");
        colMontant.setPrefWidth(100);
        TableColumn<Object, String> colDescription = new TableColumn<>("Description");
        colDescription.setPrefWidth(300);
        
        transactionsTable.getColumns().addAll(colDate, colType, colMembre, colMontant, colDescription);
        
        view.getChildren().addAll(title, filterBox, transactionsTable);
        root.setCenter(view);
    }
    
    private void showRapports() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Rapports financiers");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        Button rapportMensuel = new Button("📊 Rapport mensuel");
        rapportMensuel.setStyle(Styles.BOUTON_PRINCIPAL);
        rapportMensuel.setPrefWidth(250);
        rapportMensuel.setOnAction(e -> genererRapport("mensuel"));
        
        Button rapportTrimestriel = new Button("📈 Rapport trimestriel");
        rapportTrimestriel.setStyle(Styles.BOUTON_SECONDAIRE);
        rapportTrimestriel.setPrefWidth(250);
        rapportTrimestriel.setOnAction(e -> genererRapport("trimestriel"));
        
        Button rapportAnnuel = new Button("📅 Rapport annuel");
        rapportAnnuel.setStyle(Styles.BOUTON_ACCENT);
        rapportAnnuel.setPrefWidth(250);
        rapportAnnuel.setOnAction(e -> genererRapport("annuel"));
        
        Button bilanPrets = new Button("💳 Bilan des prêts");
        bilanPrets.setStyle(Styles.BOUTON_PRINCIPAL);
        bilanPrets.setPrefWidth(250);
        bilanPrets.setOnAction(e -> genererBilanPrets());
        
        Button etatCaisse = new Button("💰 État de caisse");
        etatCaisse.setStyle(Styles.BOUTON_SECONDAIRE);
        etatCaisse.setPrefWidth(250);
        etatCaisse.setOnAction(e -> genererEtatCaisse());
        
        Button evolution = new Button("📉 Évolution du fonds");
        evolution.setStyle(Styles.BOUTON_ACCENT);
        evolution.setPrefWidth(250);
        evolution.setOnAction(e -> genererEvolution());
        
        grid.add(rapportMensuel, 0, 0);
        grid.add(rapportTrimestriel, 1, 0);
        grid.add(rapportAnnuel, 2, 0);
        grid.add(bilanPrets, 0, 1);
        grid.add(etatCaisse, 1, 1);
        grid.add(evolution, 2, 1);
        
        view.getChildren().addAll(title, grid);
        root.setCenter(view);
    }
    
    private void genererRapport(String periode) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rapport " + periode);
        alert.setHeaderText("Rapport financier " + periode);
        
        StringBuilder content = new StringBuilder();
        content.append("=== RAPPORT FINANCIER ===\n\n");
        content.append("Période: ").append(periode).append("\n");
        content.append("AVEC: ").append(avec != null ? avec.getNom() : "N/A").append("\n");
        content.append("Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
        
        content.append("Fonds de crédit: ").append(formatMontant(avec.getTotalCredit())).append("\n");
        content.append("Caisse solidarité: ").append(formatMontant(avec.getCotisationCaisseSolidarite())).append("\n");
        
        int totalPrets = pretService.compterPrets();
        content.append("Total prêts décaissés: ").append(totalPrets).append("\n");
        content.append("Prêts en cours: ").append(pretService.compterPretsActifs()).append("\n");
        
        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setPrefHeight(300);
        textArea.setPrefWidth(400);
        
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
    
    private void genererBilanPrets() {
        List<Pret> prets = pretService.listerPretsParAvecId(tresorier.getAvecId());
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bilan des prêts");
        alert.setHeaderText("Bilan des prêts - " + (avec != null ? avec.getNom() : "AVEC"));
        
        StringBuilder content = new StringBuilder();
        content.append("=== BILAN DES PRÊTS ===\n\n");
        content.append("Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
        
        BigDecimal totalDecaisse = BigDecimal.ZERO;
        BigDecimal totalRembourse = BigDecimal.ZERO;
        
        for (Pret pret : prets) {
            content.append("Prêt #").append(pret.getNumeroPret()).append("\n");
            content.append("  Emprunteur: ").append(pret.getEmprunteur().getNomComplet()).append("\n");
            content.append("  Montant: ").append(formatMontant(pret.getMontantInitial())).append("\n");
            content.append("  Restant: ").append(formatMontant(pret.getMontantRestantDu())).append("\n");
            content.append("  Statut: ").append(pret.getStatut().getLibelle()).append("\n\n");
            
            totalDecaisse = totalDecaisse.add(pret.getMontantInitial());
            BigDecimal rembourse = pret.getMontantInitial().subtract(pret.getMontantRestantDu());
            totalRembourse = totalRembourse.add(rembourse);
        }
        
        content.append("=== RÉCAPITULATIF ===\n");
        content.append("Total décaissé: ").append(formatMontant(totalDecaisse)).append("\n");
        content.append("Total remboursé: ").append(formatMontant(totalRembourse)).append("\n");
        
        if (totalDecaisse.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taux = totalRembourse.multiply(new BigDecimal(100))
                    .divide(totalDecaisse, 2, BigDecimal.ROUND_HALF_UP);
            content.append("Taux de recouvrement: ").append(taux).append("%\n");
        }
        
        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setPrefHeight(400);
        textArea.setPrefWidth(500);
        
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
    
    private void genererEtatCaisse() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("État de caisse");
        alert.setHeaderText("État de caisse - " + (avec != null ? avec.getNom() : "AVEC"));
        
        BigDecimal fondCredit = avec != null ? avec.getTotalCredit() : BigDecimal.ZERO;
        BigDecimal fondSolidarite = avec != null ? avec.getCotisationCaisseSolidarite() : BigDecimal.ZERO;
        
        StringBuilder content = new StringBuilder();
        content.append("=== ÉTAT DE CAISSE ===\n\n");
        content.append("Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");
        
        content.append("FONDS DE CRÉDIT\n");
        content.append("  Montant: ").append(formatMontant(fondCredit)).append("\n\n");
        
        content.append("CAISSE SOLIDARITÉ\n");
        content.append("  Montant: ").append(formatMontant(fondSolidarite)).append("\n\n");
        
        content.append("TOTAL GÉNÉRAL\n");
        content.append("  ").append(formatMontant(fondCredit.add(fondSolidarite))).append("\n\n");
        
        content.append("Vérifié par: ").append(tresorier.getNomComplet()).append("\n");
        content.append("Signature: _______________");
        
        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setPrefHeight(300);
        textArea.setPrefWidth(400);
        
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
    
    private void genererEvolution() {
        showInfo("Information", "Graphique d'évolution à implémenter");
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
        valueLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");
        
        card.getChildren().addAll(iconLabel, valueLabel, labelLabel);
        
        return card;
    }
    
    private String formatMontant(BigDecimal montant) {
        if (montant == null) {
            return "0 FCFA";
        }
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