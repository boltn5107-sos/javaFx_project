package com.avec.view;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.AgentTerrain;
import com.avec.model.AgentVillageois;
import com.avec.model.Avec;
import com.avec.model.Membre;
import com.avec.model.SessionUtilisateur;
import com.avec.model.Utilisateur;
import com.avec.service.AgentTerrainService;
import com.avec.service.AgentVillageoisService;
import com.avec.service.AvecService;
import com.avec.service.AvecService.StatistiquesAvec;
import com.avec.service.MembreService;
import com.avec.service.UtilisateurService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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

public class AdminDashboardView {

	private MainApp mainApp;
	private SessionUtilisateur session;
	private UtilisateurService utilisateurService;
	private AgentTerrainService agentTerrainService;
	private AgentVillageoisService agentVillageoisService;
	private AvecService avecService;
	private MembreService membreService;
	private BorderPane root;

	// Tables
	private TableView<Utilisateur> utilisateurTable;
	private TableView<AgentTerrain> agentTerrainTable;
	private TableView<AgentVillageois> agentVillageoisTable;
	private TableView<Avec> avecTable;
	private TableView<Membre> membreTable;

	// Onglets
	private TabPane tabPane;
	private Tab dashboardTab;
	private Tab utilisateursTab;
	private Tab agentsTerrainTab;
	private Tab agentsVillageoisTab;
	private Tab avecTab;
	private Tab membresTab;
	private Tab statsTab;

	private static final String ICONE_TABLEAU_BORD = "📊";
	private static final String ICONE_UTILISATEURS = "👤";
	private static final String ICONE_AGENTS_TERRAIN = "🏞️";
	private static final String ICONE_AGENTS_VILLAGEOIS = "🌾";
	private static final String ICONE_AVEC = "🤝";
	private static final String ICONE_MEMBRES = "👥";
	private static final String ICONE_STATISTIQUES = "📈";
	private static final String ICONE_DECONNEXION = "🚪";

	public AdminDashboardView(MainApp mainApp) {
		this.mainApp = mainApp;
		this.session = SessionUtilisateur.getInstance();
		this.utilisateurService = new UtilisateurService();
		this.agentTerrainService = new AgentTerrainService();
		this.agentVillageoisService = new AgentVillageoisService();
		this.avecService = new AvecService();
		this.membreService = new MembreService();
		createView();
	}

	private void createView() {
		try {
			root = new BorderPane();
			root.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";");

			// En-tête
			HBox header = createHeader();
			root.setTop(header);

			// Menu latéral
			VBox sidebar = createSidebar();
			root.setLeft(sidebar);

			// Créer le TabPane
			tabPane = new TabPane();
			tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
//			tabPane.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";");
//
//			// POUR QUE LE TABPANE PRENNE TOUT L'ESPACE
//			tabPane.setPrefHeight(Double.MAX_VALUE);
//			tabPane.setPrefWidth(Double.MAX_VALUE);
//
//			// ✅ FORCER LA VISIBILITÉ
//			VBox.setVgrow(tabPane, Priority.ALWAYS);
//			HBox.setHgrow(tabPane, Priority.ALWAYS);
//			
			// ✅ CACHER LES ONGLETS (les rendre invisibles)
	        tabPane.setTabMinHeight(0);
	        tabPane.setTabMaxHeight(0);
	        tabPane.setStyle("-fx-tab-min-height: 0; -fx-tab-max-height: 0;");

			// Tableau de bord
			dashboardTab = new Tab("Tableau de bord");
			dashboardTab.setContent(createDashboardContent());
			dashboardTab.setClosable(false);

			// Utilisateurs
			utilisateursTab = new Tab("Utilisateurs");
			utilisateursTab.setContent(createUtilisateursContent());
			utilisateursTab.setClosable(false);

			// Agents Terrain
			agentsTerrainTab = new Tab("Agents Terrain");
			agentsTerrainTab.setContent(createAgentsTerrainContent());
			agentsTerrainTab.setClosable(false);

			// Agents Villageois - IMPORTANT: Même structure que Agents Terrain
			agentsVillageoisTab = new Tab("Agents Villageois");
			agentsVillageoisTab.setContent(createAgentsVillageoisContent());
			agentsVillageoisTab.setClosable(false);

			// AVEC
			avecTab = new Tab("AVEC");
			avecTab.setContent(createAvecContent());
			avecTab.setClosable(false);

			// Membres
			membresTab = new Tab("Membres");
			membresTab.setContent(createMembresContent());
			membresTab.setClosable(false);

			// Statistiques
			statsTab = new Tab("Statistiques");
			statsTab.setContent(createStatsContent());
			statsTab.setClosable(false);

			// Ajouter tous les onglets
			tabPane.getTabs().addAll(dashboardTab, utilisateursTab, agentsTerrainTab, agentsVillageoisTab, avecTab,
					membresTab, statsTab);

			// ✅ SÉLECTIONNER LE PREMIER ONGLET PAR DÉFAUT
			tabPane.getSelectionModel().select(0);

			root.setCenter(tabPane);

		} catch (Exception e) {
			System.err.println("ERREUR CRITIQUE: " + e.getMessage());
			e.printStackTrace();

			VBox errorBox = new VBox(10);
			errorBox.setAlignment(Pos.CENTER);
			errorBox.setPadding(new Insets(20));

			Label errorLabel = new Label("Erreur lors du chargement:");
			errorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");

			Label errorMsg = new Label(e.getMessage());
			errorMsg.setStyle("-fx-text-fill: red;");
			errorMsg.setWrapText(true);

			errorBox.getChildren().addAll(errorLabel, errorMsg);
			root.setCenter(errorBox);
		}
	}

	private HBox createHeader() {
		HBox header = new HBox();
		header.setAlignment(Pos.CENTER_RIGHT);
		header.setPadding(new Insets(15, 20, 15, 20));
		header.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-border-color: " + Styles.GRIS_CLAIR + ";"
				+ "-fx-border-width: 0 0 2 0;");

		HBox titleBox = new HBox(10);
		titleBox.setAlignment(Pos.CENTER_LEFT);

		Label logoLabel = new Label("👑");
		logoLabel.setStyle("-fx-font-size: 24px;");

		Label titleLabel = new Label("ADMINISTRATEUR");
		titleLabel.setStyle(Styles.TITRE_PRINCIPAL);

		titleBox.getChildren().addAll(logoLabel, titleLabel);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox userBox = new HBox(15);
		userBox.setAlignment(Pos.CENTER_RIGHT);

		Label userIcon = new Label("👤");
		userIcon.setStyle("-fx-font-size: 20px;");

		Label userLabel = new Label(session.getNomUtilisateur());
		userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");

		Label roleLabel = new Label("(Administrateur)");
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

	    VBox menuBox = new VBox(5);
	    menuBox.setPadding(new Insets(20, 0, 0, 0));

	    // ✅ Styles pour les boutons
	    String styleNormal = "-fx-background-color: transparent; " +
	                         "-fx-text-fill: white; " +
	                         "-fx-alignment: CENTER_LEFT; " +
	                         "-fx-padding: 10 15; " +
	                         "-fx-font-size: 14px; " +
	                         "-fx-cursor: hand;";
	    
	    String styleActif = "-fx-background-color: " + Styles.BLANC + "; " +
	                        "-fx-text-fill: " + Styles.VERT_PRINCIPAL + "; " +
	                        "-fx-alignment: CENTER_LEFT; " +
	                        "-fx-padding: 10 15; " +
	                        "-fx-font-size: 14px; " +
	                        "-fx-font-weight: bold; " +
	                        "-fx-background-radius: 8; " +
	                        "-fx-cursor: hand;";
	    
	    String styleSurvol = "-fx-background-color: rgba(255,255,255,0.2); " +
	                         "-fx-text-fill: white; " +
	                         "-fx-alignment: CENTER_LEFT; " +
	                         "-fx-padding: 10 15; " +
	                         "-fx-font-size: 14px; " +
	                         "-fx-cursor: hand;";

	    // ✅ Création des boutons
	    ToggleButton btnDashboard = new ToggleButton(ICONE_TABLEAU_BORD + "  Tableau de bord");
	    btnDashboard.setMaxWidth(Double.MAX_VALUE);
	    btnDashboard.setStyle(styleNormal);
	    
	    ToggleButton btnUtilisateurs = new ToggleButton(ICONE_UTILISATEURS + "  Utilisateurs");
	    btnUtilisateurs.setMaxWidth(Double.MAX_VALUE);
	    btnUtilisateurs.setStyle(styleNormal);
	    
	    ToggleButton btnAgentsTerrain = new ToggleButton(ICONE_AGENTS_TERRAIN + "  Agents Terrain");
	    btnAgentsTerrain.setMaxWidth(Double.MAX_VALUE);
	    btnAgentsTerrain.setStyle(styleNormal);
	    
	    ToggleButton btnAgentsVillageois = new ToggleButton(ICONE_AGENTS_VILLAGEOIS + "  Agents Villageois");
	    btnAgentsVillageois.setMaxWidth(Double.MAX_VALUE);
	    btnAgentsVillageois.setStyle(styleNormal);
	    
	    ToggleButton btnAvec = new ToggleButton(ICONE_AVEC + "  AVEC");
	    btnAvec.setMaxWidth(Double.MAX_VALUE);
	    btnAvec.setStyle(styleNormal);
	    
	    ToggleButton btnMembres = new ToggleButton(ICONE_MEMBRES + "  Membres");
	    btnMembres.setMaxWidth(Double.MAX_VALUE);
	    btnMembres.setStyle(styleNormal);
	    
	    ToggleButton btnStats = new ToggleButton(ICONE_STATISTIQUES + "  Statistiques");
	    btnStats.setMaxWidth(Double.MAX_VALUE);
	    btnStats.setStyle(styleNormal);
	    
	    // ✅ Ajout des effets de survol pour tous les boutons
	    ToggleButton[] allButtons = {btnDashboard, btnUtilisateurs, btnAgentsTerrain, 
	                                  btnAgentsVillageois, btnAvec, btnMembres, btnStats};
	    
	    for (ToggleButton btn : allButtons) {
	        btn.setOnMouseEntered(e -> {
	            if (!btn.isSelected()) {
	                btn.setStyle(styleSurvol);
	            }
	        });
	        btn.setOnMouseExited(e -> {
	            if (!btn.isSelected()) {
	                btn.setStyle(styleNormal);
	            }
	        });
	    }
	    
	    // ✅ Actions des boutons avec gestion du style
	    btnDashboard.setOnAction(e -> {
	        resetAllButtonsStyle(allButtons, styleNormal);
	        btnDashboard.setStyle(styleActif);
	        tabPane.getSelectionModel().select(0);
	    });
	    
	    btnUtilisateurs.setOnAction(e -> {
	        resetAllButtonsStyle(allButtons, styleNormal);
	        btnUtilisateurs.setStyle(styleActif);
	        tabPane.getSelectionModel().select(1);
	    });
	    
	    btnAgentsTerrain.setOnAction(e -> {
	        resetAllButtonsStyle(allButtons, styleNormal);
	        btnAgentsTerrain.setStyle(styleActif);
	        tabPane.getSelectionModel().select(2);
	    });
	    
	    btnAgentsVillageois.setOnAction(e -> {
	        resetAllButtonsStyle(allButtons, styleNormal);
	        btnAgentsVillageois.setStyle(styleActif);
	        tabPane.getSelectionModel().select(3);
	    });
	    
	    btnAvec.setOnAction(e -> {
	        resetAllButtonsStyle(allButtons, styleNormal);
	        btnAvec.setStyle(styleActif);
	        tabPane.getSelectionModel().select(4);
	    });
	    
	    btnMembres.setOnAction(e -> {
	        resetAllButtonsStyle(allButtons, styleNormal);
	        btnMembres.setStyle(styleActif);
	        tabPane.getSelectionModel().select(5);
	    });
	    
	    btnStats.setOnAction(e -> {
	        resetAllButtonsStyle(allButtons, styleNormal);
	        btnStats.setStyle(styleActif);
	        tabPane.getSelectionModel().select(6);
	    });
	    
	    // ✅ Groupe de toggle (un seul sélectionné à la fois)
	    ToggleGroup group = new ToggleGroup();
	    for (ToggleButton btn : allButtons) {
	        btn.setToggleGroup(group);
	    }
	    
	    // ✅ Sélectionner le premier bouton par défaut
	    btnDashboard.setSelected(true);
	    btnDashboard.setStyle(styleActif);
	    
	    menuBox.getChildren().addAll(btnDashboard, btnUtilisateurs, btnAgentsTerrain, 
	                                  btnAgentsVillageois, btnAvec, btnMembres, btnStats);
	    
	    // Bouton changer mot de passe
	    Button btnChangerMdp = new Button("🔒 Changer mot de passe");
	    btnChangerMdp.setStyle(Styles.BOUTON_ACCENT);
	    btnChangerMdp.setMaxWidth(Double.MAX_VALUE);
	    btnChangerMdp.setOnAction(e -> showChangerMotDePasse());
	    
	    // ✅ Ajout d'un espace avant le bouton changer mot de passe
	    Region spacer = new Region();
	    VBox.setVgrow(spacer, Priority.ALWAYS);
	    
	    sidebar.getChildren().addAll(menuBox, spacer, btnChangerMdp);

	    return sidebar;
	}

	/**
	 * Réinitialise le style de tous les boutons
	 */
	private void resetAllButtonsStyle(ToggleButton[] buttons, String style) {
	    for (ToggleButton btn : buttons) {
	        btn.setStyle(style);
	        // Réactiver l'effet de survol
	        final ToggleButton currentBtn = btn;
	        btn.setOnMouseEntered(e -> {
	            if (!currentBtn.isSelected()) {
	                currentBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); " +
	                                   "-fx-text-fill: white; " +
	                                   "-fx-alignment: CENTER_LEFT; " +
	                                   "-fx-padding: 10 15; " +
	                                   "-fx-font-size: 14px; " +
	                                   "-fx-cursor: hand;");
	            }
	        });
	        btn.setOnMouseExited(e -> {
	            if (!currentBtn.isSelected()) {
	                currentBtn.setStyle(style);
	            }
	        });
	    }
	}
	// ==================== CONTENU DES ONGLETS ====================

	private VBox createDashboardContent() {
		VBox dashboard = new VBox(20);
		dashboard.setPadding(new Insets(20));
		dashboard.setAlignment(Pos.TOP_CENTER);

		Label welcomeLabel = new Label("Tableau de bord Administrateur");
		welcomeLabel.setStyle(Styles.TITRE_PRINCIPAL);

		HBox statsBox = new HBox(20);
		statsBox.setAlignment(Pos.CENTER);
		statsBox.setPadding(new Insets(20, 0, 20, 0));

		int nbUtilisateurs = utilisateurService.getNombreUtilisateurs();
		int nbAgentsTerrain = agentTerrainService.getNombreAgentTerrain();
		int nbAgentsVillageois = agentVillageoisService.getNombreAgentVillageois();
		int nbAvec = 0;
		int nbMembres = 0;
		int nbMembresActifs = 0;
		BigDecimal totalEpargne = BigDecimal.ZERO;
		BigDecimal totalPrets = BigDecimal.ZERO;

		try {
			StatistiquesAvec stats = avecService.getStatistiques();
			nbAvec = stats.getTotalAvecs();
			nbMembres = membreService.getNombreTotalMembres();
			nbMembresActifs = membreService.getNombreMembresActifs();

		} catch (SQLException e) {
			System.err.println("Erreur chargement stats: " + e.getMessage());
		}

		VBox carte1 = createStatCard("👤", "Utilisateurs", String.valueOf(nbUtilisateurs), Styles.VERT_PRINCIPAL);
		VBox carte2 = createStatCard("🏞️", "Agents Terrain", String.valueOf(nbAgentsTerrain), Styles.BLEU_SECONDAIRE);
		VBox carte3 = createStatCard("🌾", "Agents Villageois", String.valueOf(nbAgentsVillageois), Styles.ACCENT_DORE);
		VBox carte4 = createStatCard("🤝", "AVEC", String.valueOf(nbAvec), Styles.VERT_PRINCIPAL);
		VBox carte5 = createStatCard("👥", "Membres", String.valueOf(nbMembres), Styles.BLEU_SECONDAIRE);
		VBox carte6 = createStatCard("✅", "Actifs", String.valueOf(nbMembresActifs), Styles.VERT_SUCCES);
		VBox carte7 = createStatCard("💰", "Épargne", formatMontant(totalEpargne), Styles.ACCENT_DORE);
		VBox carte8 = createStatCard("💳", "Prêts", formatMontant(totalPrets), Styles.VERT_PRINCIPAL);

		statsBox.getChildren().addAll(carte1, carte2, carte3, carte4, carte5, carte6, carte7, carte8);

		VBox actionsBox = new VBox(10);
		actionsBox.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;"
				+ "-fx-padding: 20;" + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

		Label actionsTitle = new Label("Actions rapides");
		actionsTitle.setStyle(Styles.TITRE_SECONDAIRE);

		GridPane actionsGrid = new GridPane();
		actionsGrid.setHgap(15);
		actionsGrid.setVgap(10);

		Button ajoutUtilisateur = new Button("➕ Ajouter utilisateur");
		ajoutUtilisateur.setStyle(Styles.BOUTON_PRINCIPAL);
		ajoutUtilisateur.setOnAction(e -> showAjoutUtilisateur());

		Button ajoutAgentTerrain = new Button("🏞️ Ajouter agent terrain");
		ajoutAgentTerrain.setStyle(Styles.BOUTON_SECONDAIRE);
		ajoutAgentTerrain.setOnAction(e -> showAjoutAgentTerrain());

		Button ajoutAvec = new Button("🤝 Créer une AVEC");
		ajoutAvec.setStyle(Styles.BOUTON_ACCENT);
		ajoutAvec.setOnAction(e -> showAjoutAvec());

		Button ajoutMembre = new Button("👥 Ajouter membre");
		ajoutMembre.setStyle(Styles.BOUTON_PRINCIPAL);
		ajoutMembre.setOnAction(e -> showAjoutMembre());

		actionsGrid.add(ajoutUtilisateur, 0, 0);
		actionsGrid.add(ajoutAgentTerrain, 1, 0);
		actionsGrid.add(ajoutAvec, 2, 0);
		actionsGrid.add(ajoutMembre, 3, 0);

		actionsBox.getChildren().addAll(actionsTitle, actionsGrid);

		dashboard.getChildren().addAll(welcomeLabel, statsBox, actionsBox);

		return dashboard;
	}

	private VBox createStatCard(String icon, String label, String value, String color) {
		VBox card = new VBox(10);
		card.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;" + "-fx-padding: 15;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
		card.setPrefWidth(120);
		card.setAlignment(Pos.CENTER);

		Label iconLabel = new Label(icon);
		iconLabel.setStyle("-fx-font-size: 28px;");

		Label valueLabel = new Label(value);
		valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

		Label labelLabel = new Label(label);
		labelLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");

		card.getChildren().addAll(iconLabel, valueLabel, labelLabel);

		return card;
	}

	// ==================== AGENTS TERRAIN (EXEMPLE) ====================

	private VBox createAgentsTerrainContent() {
		VBox content = new VBox(15);
		content.setPadding(new Insets(20));

		Label title = new Label("Liste des agents terrain");
		title.setStyle(Styles.TITRE_SECONDAIRE);

		HBox toolbar = new HBox(10);
		Button ajouterBtn = new Button("➕ Ajouter agent terrain");
		ajouterBtn.setStyle(Styles.BOUTON_PRINCIPAL);
		ajouterBtn.setOnAction(e -> showAjoutAgentTerrain());

		Button supprimerBtn = new Button("🗑️ Supprimer");
		supprimerBtn
				.setStyle("-fx-background-color: " + Styles.ROUGE_ERREUR + "; -fx-text-fill: white; -fx-cursor: hand;");
		supprimerBtn.setOnAction(e -> supprimerAgentTerrain());

		Button actualiserBtn = new Button("🔄 Actualiser");
		actualiserBtn.setStyle(Styles.BOUTON_SECONDAIRE);
		actualiserBtn.setOnAction(e -> chargerAgentsTerrain());

		toolbar.getChildren().addAll(ajouterBtn, supprimerBtn, actualiserBtn);

		agentTerrainTable = new TableView<>();
		agentTerrainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<AgentTerrain, Long> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colId.setPrefWidth(50);

		TableColumn<AgentTerrain, String> colNom = new TableColumn<>("Nom");
		colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
		colNom.setPrefWidth(100);

		TableColumn<AgentTerrain, String> colPrenom = new TableColumn<>("Prénom");
		colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
		colPrenom.setPrefWidth(100);

		TableColumn<AgentTerrain, String> colEmail = new TableColumn<>("Email");
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colEmail.setPrefWidth(200);

		TableColumn<AgentTerrain, String> colTelephone = new TableColumn<>("Téléphone");
		colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
		colTelephone.setPrefWidth(120);

		agentTerrainTable.getColumns().addAll(colId, colNom, colPrenom, colEmail, colTelephone);

		chargerAgentsTerrain();

		content.getChildren().addAll(title, toolbar, agentTerrainTable);
		return content;
	}

	// ==================== AGENTS VILLAGEOIS (MÊME STRUCTURE QUE AGENTS TERRAIN)
	// ====================

	private VBox createAgentsVillageoisContent() {
		VBox content = new VBox(15);
		content.setPadding(new Insets(20));
		content.setPrefHeight(Double.MAX_VALUE);
		content.setPrefWidth(Double.MAX_VALUE);

		Label title = new Label("Liste des agents villageois");
		title.setStyle(Styles.TITRE_SECONDAIRE);

		HBox toolbar = new HBox(10);
		Button ajouterBtn = new Button("➕ Ajouter agent villageois");
		ajouterBtn.setStyle(Styles.BOUTON_PRINCIPAL);
		ajouterBtn.setOnAction(e -> showAjoutAgentVillageois());

		Button supprimerBtn = new Button("🗑️ Supprimer");
		supprimerBtn
				.setStyle("-fx-background-color: " + Styles.ROUGE_ERREUR + "; -fx-text-fill: white; -fx-cursor: hand;");
		supprimerBtn.setOnAction(e -> supprimerAgentVillageois());

		Button actualiserBtn = new Button("🔄 Actualiser");
		actualiserBtn.setStyle(Styles.BOUTON_SECONDAIRE);
		actualiserBtn.setOnAction(e -> chargerAgentsVillageois());

		toolbar.getChildren().addAll(ajouterBtn, supprimerBtn, actualiserBtn);

		agentVillageoisTable = new TableView<>();
		agentVillageoisTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// ✅ AJOUTER UNE HAUTEUR MINIMALE
		agentVillageoisTable.setPrefHeight(400);

		TableColumn<AgentVillageois, Long> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colId.setPrefWidth(50);

		TableColumn<AgentVillageois, String> colNom = new TableColumn<>("Nom");
		colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
		colNom.setPrefWidth(100);

		TableColumn<AgentVillageois, String> colPrenom = new TableColumn<>("Prénom");
		colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
		colPrenom.setPrefWidth(100);

		TableColumn<AgentVillageois, String> colEmail = new TableColumn<>("Email");
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colEmail.setPrefWidth(200);

		TableColumn<AgentVillageois, String> colTelephone = new TableColumn<>("Téléphone");
		colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
		colTelephone.setPrefWidth(120);

		TableColumn<AgentVillageois, String> colAgentTerrain = new TableColumn<>("Agent Terrain");
		colAgentTerrain.setCellValueFactory(cellData -> {
			AgentVillageois av = cellData.getValue();
			if (av.getAgentTerrain() != null) {
				return new javafx.beans.property.SimpleStringProperty(
						av.getAgentTerrain().getPrenom() + " " + av.getAgentTerrain().getNom());
			}
			return new javafx.beans.property.SimpleStringProperty("");
		});
		colAgentTerrain.setPrefWidth(150);

		agentVillageoisTable.getColumns().addAll(colId, colNom, colPrenom, colEmail, colTelephone, colAgentTerrain);

		chargerAgentsVillageois();

		content.getChildren().addAll(title, toolbar, agentVillageoisTable);

		// UN ESPACEUR POUR POUSSER LE CONTENU VERS LE HAUT
		VBox.setVgrow(agentVillageoisTable, Priority.ALWAYS);
		return content;
	}

	// ==================== AUTRES CONTENUS ====================

	private VBox createUtilisateursContent() {
		VBox content = new VBox(15);
		content.setPadding(new Insets(20));

		HBox toolbar = new HBox(10);
		Button ajouterBtn = new Button("➕ Ajouter utilisateur");
		ajouterBtn.setStyle(Styles.BOUTON_PRINCIPAL);
		ajouterBtn.setOnAction(e -> showAjoutUtilisateur());

		Button supprimerBtn = new Button("🗑️ Supprimer");
		supprimerBtn
				.setStyle("-fx-background-color: " + Styles.ROUGE_ERREUR + "; -fx-text-fill: white; -fx-cursor: hand;");
		supprimerBtn.setOnAction(e -> supprimerUtilisateur());

		toolbar.getChildren().addAll(ajouterBtn, supprimerBtn);

		utilisateurTable = new TableView<>();
		utilisateurTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<Utilisateur, Long> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		TableColumn<Utilisateur, String> colNom = new TableColumn<>("Nom");
		colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
		TableColumn<Utilisateur, String> colPrenom = new TableColumn<>("Prénom");
		colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
		TableColumn<Utilisateur, String> colEmail = new TableColumn<>("Email");
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		TableColumn<Utilisateur, String> colTelephone = new TableColumn<>("Téléphone");
		colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));

		utilisateurTable.getColumns().addAll(colId, colNom, colPrenom, colEmail, colTelephone);

		chargerUtilisateurs();

		content.getChildren().addAll(toolbar, utilisateurTable);
		return content;
	}

	private VBox createAvecContent() {
		VBox content = new VBox(15);
		content.setPadding(new Insets(20));

		HBox toolbar = new HBox(10);
		Button ajouterBtn = new Button("➕ Créer une AVEC");
		ajouterBtn.setStyle(Styles.BOUTON_PRINCIPAL);
		ajouterBtn.setOnAction(e -> showAjoutAvec());

		Button supprimerBtn = new Button("🗑️ Supprimer");
		supprimerBtn
				.setStyle("-fx-background-color: " + Styles.ROUGE_ERREUR + "; -fx-text-fill: white; -fx-cursor: hand;");
		supprimerBtn.setOnAction(e -> supprimerAvec());

		toolbar.getChildren().addAll(ajouterBtn, supprimerBtn);

		avecTable = new TableView<>();
		avecTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<Avec, Long> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		TableColumn<Avec, String> colNom = new TableColumn<>("Nom");
		colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
		TableColumn<Avec, String> colCode = new TableColumn<>("Code");
		colCode.setCellValueFactory(new PropertyValueFactory<>("codeUnique"));
		TableColumn<Avec, String> colPhase = new TableColumn<>("Phase");
		colPhase.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
				cellData.getValue().getPhaseCourante().getLibelle()));
		TableColumn<Avec, String> colAgent = new TableColumn<>("Agent Villageois");
		colAgent.setCellValueFactory(cellData -> {
			Avec a = cellData.getValue();
			if (a.getAgentVillageois() != null) {
				return new javafx.beans.property.SimpleStringProperty(a.getAgentVillageois().getNomComplet());
			}
			return new javafx.beans.property.SimpleStringProperty("");
		});

		avecTable.getColumns().addAll(colId, colNom, colCode, colPhase, colAgent);

		chargerAvec();

		content.getChildren().addAll(toolbar, avecTable);
		return content;
	}

	private VBox createMembresContent() {
		VBox content = new VBox(15);
		content.setPadding(new Insets(20));

		HBox toolbar = new HBox(10);
		TextField searchField = new TextField();
		searchField.setPromptText("Rechercher un membre...");
		searchField.setPrefWidth(300);
		searchField.setStyle(Styles.CHAMP_TEXTE);

		Button searchButton = new Button("🔍 Rechercher");
		searchButton.setStyle(Styles.BOUTON_PRINCIPAL);
		searchButton.setOnAction(e -> rechercherMembres(searchField.getText()));

		toolbar.getChildren().addAll(searchField, searchButton);

		membreTable = new TableView<>();
		membreTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<Membre, Long> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		TableColumn<Membre, String> colNom = new TableColumn<>("Nom");
		colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
		TableColumn<Membre, String> colPrenom = new TableColumn<>("Prénom");
		colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
		TableColumn<Membre, String> colCarte = new TableColumn<>("N° Carte");
		colCarte.setCellValueFactory(new PropertyValueFactory<>("numeroCarte"));

		membreTable.getColumns().addAll(colId, colNom, colPrenom, colCarte);

		chargerMembres();

		content.getChildren().addAll(toolbar, membreTable);
		return content;
	}

	private VBox createStatsContent() {
		VBox content = new VBox(15);
		content.setPadding(new Insets(20));

		Label title = new Label("Statistiques générales");
		title.setStyle(Styles.TITRE_PRINCIPAL);

		GridPane statsGrid = new GridPane();
		statsGrid.setHgap(20);
		statsGrid.setVgap(15);
		statsGrid.setPadding(new Insets(20));
		statsGrid.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;");

		try {
			int nbUtilisateurs = utilisateurService.getNombreUtilisateurs();
			int nbAgentsTerrain = agentTerrainService.getNombreAgentTerrain();
			int nbAgentsVillageois = agentVillageoisService.getNombreAgentVillageois();
			StatistiquesAvec stats = avecService.getStatistiques();
			int nbAvec = stats.getTotalAvecs();
			int nbMembres = membreService.getNombreTotalMembres();
			int nbMembresActifs = membreService.getNombreMembresActifs();

			statsGrid.add(createStatItem("👤", "Utilisateurs", String.valueOf(nbUtilisateurs)), 0, 0);
			statsGrid.add(createStatItem("🏞️", "Agents Terrain", String.valueOf(nbAgentsTerrain)), 1, 0);
			statsGrid.add(createStatItem("🌾", "Agents Villageois", String.valueOf(nbAgentsVillageois)), 2, 0);
			statsGrid.add(createStatItem("🤝", "AVEC", String.valueOf(nbAvec)), 3, 0);
			statsGrid.add(createStatItem("👥", "Membres", String.valueOf(nbMembres)), 0, 1);
			statsGrid.add(createStatItem("✅", "Membres actifs", String.valueOf(nbMembresActifs)), 1, 1);

		} catch (SQLException e) {
			System.err.println("Erreur chargement stats: " + e.getMessage());
		}

		content.getChildren().addAll(title, statsGrid);
		return content;
	}

	private VBox createStatItem(String icon, String label, String value) {
		VBox item = new VBox(5);
		item.setAlignment(Pos.CENTER);
		item.setPadding(new Insets(15));
		item.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";" + "-fx-background-radius: 10;");
		item.setPrefWidth(180);

		Label iconLabel = new Label(icon);
		iconLabel.setStyle("-fx-font-size: 24px;");

		Label valueLabel = new Label(value);
		valueLabel
				.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");

		Label labelLabel = new Label(label);
		labelLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");

		item.getChildren().addAll(iconLabel, valueLabel, labelLabel);
		return item;
	}

	// ==================== MÉTHODES DE CHARGEMENT ====================

	private void chargerUtilisateurs() {
		List<Utilisateur> utilisateurs = utilisateurService.listerUtilisateur();
		utilisateurTable.setItems(FXCollections.observableArrayList(utilisateurs));
	}

	private void chargerAgentsTerrain() {
		List<AgentTerrain> agents = agentTerrainService.listerAgentTerrain();
		agentTerrainTable.setItems(FXCollections.observableArrayList(agents));
	}

	private void chargerAgentsVillageois() {
		try {
			List<AgentVillageois> agents = agentVillageoisService.listerAgentVillageois();
			if (agents != null) {
				agentVillageoisTable.setItems(FXCollections.observableArrayList(agents));
			} else {
				agentVillageoisTable.setItems(FXCollections.observableArrayList());
			}
		} catch (Exception e) {
			showAlert("Erreur", "Impossible de charger les agents villageois: " + e.getMessage());
		}
	}

	private void chargerAvec() {
		try {
			List<Avec> avecs = avecService.getAllAvecs();
			avecTable.setItems(FXCollections.observableArrayList(avecs));
		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de charger les AVEC: " + e.getMessage());
		}
	}

	private void chargerMembres() {
		try {
			List<Membre> membres = membreService.getAllMembres();
			membreTable.setItems(FXCollections.observableArrayList(membres));
		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
		}
	}

	private void rechercherMembres(String recherche) {
		if (recherche == null || recherche.trim().isEmpty()) {
			chargerMembres();
			return;
		}

		try {
			List<Avec> avecs = avecService.getAllAvecs();
			List<Membre> resultats = new ArrayList<>();

			for (Avec avec : avecs) {
				resultats.addAll(membreService.getMembresByAvecId(avec.getId()));
			}

			membreTable.setItems(FXCollections.observableArrayList(resultats));
		} catch (SQLException e) {
			showAlert("Erreur", "Erreur de recherche: " + e.getMessage());
		}
	}

	// Dialogue d'ajout de membre
	private void showAjoutMembre() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Ajouter un membre");
		dialog.setHeaderText("Créer un nouveau membre");

		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		content.setPrefWidth(400);

		TextField nomField = new TextField();
		nomField.setPromptText("Nom");
		nomField.setStyle(Styles.CHAMP_TEXTE);

		TextField prenomField = new TextField();
		prenomField.setPromptText("Prénom");
		prenomField.setStyle(Styles.CHAMP_TEXTE);

		ComboBox<Avec> avecCombo = new ComboBox<>();
		avecCombo.setPromptText("Sélectionner une AVEC");
		avecCombo.setStyle(Styles.CHAMP_TEXTE);

		try {
			List<Avec> avecs = avecService.getAllAvecs();
			avecCombo.setItems(FXCollections.observableArrayList(avecs));
		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de charger les AVEC: " + e.getMessage());
		}

		TextField numeroCarteField = new TextField();
		numeroCarteField.setPromptText("Numéro de carte");
		numeroCarteField.setStyle(Styles.CHAMP_TEXTE);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Mot de passe");
		passwordField.setStyle(Styles.CHAMP_TEXTE);

		TextField telephoneField = new TextField();
		telephoneField.setPromptText("Téléphone");
		telephoneField.setStyle(Styles.CHAMP_TEXTE);

		content.getChildren().addAll(new Label("Nom:"), nomField, new Label("Prénom:"), prenomField, new Label("AVEC:"),
				avecCombo, new Label("Profession:"), numeroCarteField, telephoneField, new Label("Mot de passe:"),
				passwordField, new Label("Téléphone:"));

		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				try {
					Avec selectedAvec = avecCombo.getValue();
					if (selectedAvec == null) {
						showAlert("Erreur", "Veuillez sélectionner une AVEC");
						return null;
					}
					String nom = nomField.getText().trim();
					String prenom = prenomField.getText().trim();
					String password = passwordField.getText().trim();
					String telephone = telephoneField.getText().trim();

					if (nom.isEmpty() || prenom.isEmpty() || password.isEmpty() || telephone.isEmpty()) {
						showAlert("Erreur", "Tous les champs sont obligatoires");
						return null;
					}

					Membre membre = membreService.creerMembre(nom, prenom, selectedAvec.getId(), password, telephone);
					showInfo("Succès", "Membre ajouté avec succès!\nNuméro de carte: " + membre.getNumeroCarte());
					chargerMembres();

				} catch (SQLException | IllegalArgumentException e) {
					showAlert("Erreur", "Erreur: " + e.getMessage());
				}
			}
			return null;
		});

		dialog.showAndWait();
	}

	// Dialogue d'ajout d'utilisateur
	private void showAjoutUtilisateur() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Ajouter un utilisateur");
		dialog.setHeaderText("Créer un nouvel utilisateur");

		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		content.setPrefWidth(400);

		TextField nomField = new TextField();
		nomField.setPromptText("Nom");
		TextField prenomField = new TextField();
		prenomField.setPromptText("Prénom");
		TextField emailField = new TextField();
		emailField.setPromptText("Email");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Mot de passe");
		TextField telephoneField = new TextField();
		telephoneField.setPromptText("Téléphone");

		content.getChildren().addAll(new Label("Nom:"), nomField, new Label("Prénom:"), prenomField,
				new Label("Email:"), emailField, new Label("Mot de passe:"), passwordField, new Label("Téléphone:"),
				telephoneField);

		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				Utilisateur utilisateur = new Utilisateur();
				utilisateur.setNom(nomField.getText().trim());
				utilisateur.setPrenom(prenomField.getText().trim());
				utilisateur.setEmail(emailField.getText().trim());
				utilisateur.setMotDePasse(passwordField.getText().trim());
				utilisateur.setTelephone(telephoneField.getText().trim());

				if (utilisateurService.AjouterUtilisateur(utilisateur)) {
					showInfo("Succès", "Utilisateur ajouté avec succès!");
					chargerUtilisateurs();
				} else {
					showAlert("Erreur", "Échec de l'ajout");
				}
			}
			return null;
		});

		dialog.showAndWait();
	}

	private void showAjoutAgentTerrain() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Ajouter un agent terrain");
		dialog.setHeaderText("Créer un nouvel agent terrain");

		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		content.setPrefWidth(400);

		TextField nomField = new TextField();
		nomField.setPromptText("Nom");
		TextField prenomField = new TextField();
		prenomField.setPromptText("Prénom");
		TextField emailField = new TextField();
		emailField.setPromptText("Email");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Mot de passe");
		TextField telephoneField = new TextField();
		telephoneField.setPromptText("Téléphone");

		content.getChildren().addAll(new Label("Nom:"), nomField, new Label("Prénom:"), prenomField,
				new Label("Email:"), emailField, new Label("Mot de passe:"), passwordField, new Label("Téléphone:"),
				telephoneField);

		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				AgentTerrain agent = new AgentTerrain();
				agent.setNom(nomField.getText().trim());
				agent.setPrenom(prenomField.getText().trim());
				agent.setEmail(emailField.getText().trim());
				agent.setMotDePasse(passwordField.getText().trim());
				agent.setTelephone(telephoneField.getText().trim());

				if (agentTerrainService.enregistrerAgentTerrain(agent)) {
					showInfo("Succès", "Agent terrain ajouté avec succès!");
					chargerAgentsTerrain();
				} else {
					showAlert("Erreur", "Échec de l'ajout");
				}
			}
			return null;
		});

		dialog.showAndWait();
	}

	private void showAjoutAgentVillageois() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Ajouter un agent villageois");
		dialog.setHeaderText("Créer un nouvel agent villageois");

		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		content.setPrefWidth(400);

		TextField nomField = new TextField();
		nomField.setPromptText("Nom");
		TextField prenomField = new TextField();
		prenomField.setPromptText("Prénom");
		TextField emailField = new TextField();
		emailField.setPromptText("Email");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Mot de passe");
		TextField telephoneField = new TextField();
		telephoneField.setPromptText("Téléphone");

		ComboBox<AgentTerrain> agentTerrainCombo = new ComboBox<>();
		agentTerrainCombo.setPromptText("Agent terrain superviseur");
		agentTerrainCombo.setItems(FXCollections.observableArrayList(agentTerrainService.listerAgentTerrain()));
		agentTerrainCombo.setStyle(Styles.CHAMP_TEXTE);

		content.getChildren().addAll(new Label("Nom:"), nomField, new Label("Prénom:"), prenomField,
				new Label("Email:"), emailField, new Label("Mot de passe:"), passwordField, new Label("Téléphone:"),
				telephoneField, new Label("Agent terrain:"), agentTerrainCombo);

		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				AgentVillageois agent = new AgentVillageois();
				agent.setNom(nomField.getText().trim());
				agent.setPrenom(prenomField.getText().trim());
				agent.setEmail(emailField.getText().trim());
				agent.setMotDePasse(passwordField.getText().trim());
				agent.setTelephone(telephoneField.getText().trim());
				agent.setAgentTerrain(agentTerrainCombo.getValue());

				if (agentVillageoisService.enregistrerAgentVillageois(agent)) {
					showInfo("Succès", "Agent villageois ajouté avec succès!");
					chargerAgentsVillageois();
				} else {
					showAlert("Erreur", "Échec de l'ajout");
				}
			}
			return null;
		});

		dialog.showAndWait();
	}

	private void showAjoutAvec() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Créer une AVEC");
		dialog.setHeaderText("Nouvelle Association Villageoise d'Épargne et de Crédit");
		
		  // ✅ Appliquer un style global au DialogPane
	    DialogPane dialogPane = dialog.getDialogPane();
	    dialogPane.setStyle("-fx-background-color: " + Styles.BLANC + ";");

		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		content.setPrefWidth(450);

		// Nom de l'AVEC
		Label nomLabel = new Label("Nom de l'AVEC *");
		nomLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.NOIR + ";" );
		TextField nomField = new TextField();
		nomField.setPromptText("Ex: AVEC Ndiarème");
		nomField.setStyle(Styles.CHAMP_TEXTE);

		// Agent Terrain (superviseur)
		Label agentTerrainLabel = new Label("Agent Terrain (Superviseur) *");
		agentTerrainLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.NOIR + ";" );
		ComboBox<AgentTerrain> agentTerrainCombo = new ComboBox<>();
		agentTerrainCombo.setPromptText("Sélectionner un agent terrain");
		agentTerrainCombo.setStyle(Styles.CHAMP_TEXTE);

		try {
			List<AgentTerrain> agentsTerrain = agentTerrainService.listerAgentTerrain();
			agentTerrainCombo.setItems(FXCollections.observableArrayList(agentsTerrain));
		} catch (Exception e) {
			showAlert("Erreur", "Impossible de charger les agents terrain: " + e.getMessage());
		}

		// Agent Villageois (formateur)
		Label agentVillageoisLabel = new Label("Agent Villageois (Formateur) *");
		agentVillageoisLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.NOIR + ";" );
		ComboBox<AgentVillageois> agentVillageoisCombo = new ComboBox<>();
		agentVillageoisCombo.setPromptText("Sélectionner un agent villageois");
		agentVillageoisCombo.setStyle(Styles.CHAMP_TEXTE);
		agentVillageoisCombo.setDisable(true); // Désactivé tant qu'aucun agent terrain n'est sélectionné

		// Prix de la part
		Label prixLabel = new Label("Prix de la part (FCFA) *");
		prixLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.NOIR + ";" );
		TextField prixPartField = new TextField();
		prixPartField.setPromptText("Ex: 500");
		prixPartField.setStyle(Styles.CHAMP_TEXTE);


		// Nombre max de membres
		Label nbMembresLabel = new Label("Nombre max de membres");
		nbMembresLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.NOIR + ";" );
		Spinner<Integer> nbMembresSpinner = new Spinner<>(10, 30, 15);
		nbMembresSpinner.setStyle(Styles.CHAMP_TEXTE);

		// Taux frais service
		Label tauxLabel = new Label("Taux frais service mensuel (%)");
		tauxLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.NOIR + ";" );
		TextField tauxField = new TextField("5");
		tauxField.setStyle(Styles.CHAMP_TEXTE);

		// Caisse solidarité
		CheckBox caisseSolidariteCheck = new CheckBox("Activer la Caisse de Solidarité");
		caisseSolidariteCheck.setSelected(true);

		TextField cotisationField = new TextField("100");
		cotisationField.setPromptText("Montant cotisation");
		cotisationField.setStyle(Styles.CHAMP_TEXTE);
		cotisationField.setDisable(false);

		caisseSolidariteCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
			cotisationField.setDisable(!newVal);
		});

		HBox cotisationBox = new HBox(10);
		cotisationBox.setAlignment(Pos.CENTER_LEFT);
		cotisationBox.getChildren().addAll(new Label("Cotisation:"), cotisationField, new Label("FCFA"));

		// Remplir les agents villageois quand un agent terrain est sélectionné
		agentTerrainCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				try {
					List<AgentVillageois> agents = agentVillageoisService.chercherAvParAt(newVal.getId());
					agentVillageoisCombo.setItems(FXCollections.observableArrayList(agents));
					agentVillageoisCombo.setDisable(false);
					agentVillageoisCombo.setPromptText("Sélectionner un agent villageois");
				} catch (Exception e) {
					showAlert("Erreur", "Impossible de charger les agents villageois: " + e.getMessage());
				}
			} else {
				agentVillageoisCombo.setDisable(true);
				agentVillageoisCombo.setItems(FXCollections.observableArrayList());
			}
		});

		content.getChildren().addAll(nomLabel, nomField, agentTerrainLabel, agentTerrainCombo, agentVillageoisLabel,
				agentVillageoisCombo, prixLabel, prixPartField,
				nbMembresLabel, nbMembresSpinner, tauxLabel, tauxField, caisseSolidariteCheck, cotisationBox);

		ScrollPane scrollPane = new ScrollPane(content);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefHeight(500);
		scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

		dialog.getDialogPane().setContent(scrollPane);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Désactiver OK si les champs obligatoires sont vides
		Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setDisable(true);

		// Validation en temps réel
		nomField.textProperty().addListener((obs, oldVal, newVal) -> {
			okButton.setDisable(newVal == null || newVal.trim().isEmpty() || agentTerrainCombo.getValue() == null
					|| agentVillageoisCombo.getValue() == null || prixPartField.getText().trim().isEmpty());
		});

		agentTerrainCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
			okButton.setDisable(nomField.getText().trim().isEmpty() || newVal == null
					|| agentVillageoisCombo.getValue() == null || prixPartField.getText().trim().isEmpty());
		});

		agentVillageoisCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
			okButton.setDisable(nomField.getText().trim().isEmpty() || agentTerrainCombo.getValue() == null
					|| newVal == null || prixPartField.getText().trim().isEmpty());
		});

		prixPartField.textProperty().addListener((obs, oldVal, newVal) -> {
			okButton.setDisable(nomField.getText().trim().isEmpty() || agentTerrainCombo.getValue() == null
					|| agentVillageoisCombo.getValue() == null || newVal == null || newVal.trim().isEmpty());
		});

		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				try {
					String nom = nomField.getText().trim();
					AgentTerrain agentTerrain = agentTerrainCombo.getValue();
					AgentVillageois agentVillageois = agentVillageoisCombo.getValue();
					BigDecimal prixPart = new BigDecimal(prixPartField.getText().trim());
					
					int nombreMembresMax = nbMembresSpinner.getValue();
					BigDecimal tauxFrais = new BigDecimal(tauxField.getText().trim());

					if (nom.isEmpty() || agentTerrain == null || agentVillageois == null
							|| prixPart.compareTo(BigDecimal.ZERO) <= 0) {
						showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
						return null;
					}

					// ✅ Créer l'AVEC avec l'agent villageois
					Avec avec = avecService.creerAvec(nom, prixPart, agentVillageois.getId());

					// ✅ Lier l'agent terrain comme superviseur
					avec.setAgentTerrainId(agentTerrain.getId());
					avec.setNombreMembresMax(nombreMembresMax);
					avec.setTauxFraisServiceMensuel(tauxFrais);
					avec.setCaisseSolidariteActive(caisseSolidariteCheck.isSelected());
					if (caisseSolidariteCheck.isSelected()) {
						avec.setCotisationCaisseSolidarite(new BigDecimal(cotisationField.getText().trim()));
					}

					avecService.modifierAvec(avec);

					showInfo("Succès",
							"AVEC créée avec succès!\n" + "Nom: " + avec.getNom() + "\n" + "Code: "
									+ avec.getCodeUnique() + "\n" + "Agent Terrain: " + agentTerrain.getNomComplet()
									+ "\n" + "Agent Villageois: " + agentVillageois.getNomComplet());

					chargerAvec();

				} catch (NumberFormatException e) {
					showAlert("Erreur", "Format de nombre invalide: " + e.getMessage());
				} catch (IllegalArgumentException e) {
					showAlert("Erreur", e.getMessage());
				} catch (SQLException e) {
					showAlert("Erreur", "Erreur base de données: " + e.getMessage());
				} catch (Exception e) {
					showAlert("Erreur", "Erreur: " + e.getMessage());
					e.printStackTrace();
				}
			}
			return null;
		});

		dialog.showAndWait();
	}

	// Méthodes de suppression
	private void supprimerUtilisateur() {
		Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Sélection requise", "Veuillez sélectionner un utilisateur à supprimer.");
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Confirmation");
		confirm.setHeaderText("Supprimer l'utilisateur");
		confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNomComplet() + " ?");

		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				if (utilisateurService.supprimerUtilisateur(selected.getId())) {
					showInfo("Succès", "Utilisateur supprimé");
					chargerUtilisateurs();
				} else {
					showAlert("Erreur", "Échec de la suppression");
				}
			}
		});
	}

	private void supprimerAgentTerrain() {
		AgentTerrain selected = agentTerrainTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Sélection requise", "Veuillez sélectionner un agent terrain à supprimer.");
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Confirmation");
		confirm.setHeaderText("Supprimer l'agent terrain");
		confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNomComplet() + " ?");

		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				if (agentTerrainService.supprimerAgentTerrain(selected.getId())) {
					showInfo("Succès", "Agent terrain supprimé");
					chargerAgentsTerrain();
				} else {
					showAlert("Erreur", "Échec de la suppression");
				}
			}
		});
	}

	private void supprimerAgentVillageois() {
		AgentVillageois selected = agentVillageoisTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Sélection requise", "Veuillez sélectionner un agent villageois à supprimer.");
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Confirmation");
		confirm.setHeaderText("Supprimer l'agent villageois");
		confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNomComplet() + " ?");

		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				if (agentVillageoisService.supprimerAgentVillageois(selected.getId())) {
					showInfo("Succès", "Agent villageois supprimé");
					chargerAgentsVillageois();
				} else {
					showAlert("Erreur", "Échec de la suppression");
				}
			}
		});
	}

	private void supprimerAvec() {
		Avec selected = avecTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Sélection requise", "Veuillez sélectionner une AVEC à supprimer.");
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Confirmation");
		confirm.setHeaderText("Supprimer l'AVEC");
		confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNom() + " ?");

		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try {
					if (avecService.supprimerAvec(selected.getId())) {
						showInfo("Succès", "AVEC supprimée");
						chargerAvec();
					} else {
						showAlert("Erreur", "Échec de la suppression");
					}
				} catch (SQLException e) {
					showAlert("Erreur", "Erreur: " + e.getMessage());
				}
			}
		});
	}

	private String formatMontant(BigDecimal montant) {
		if (montant == null)
			return "0 FCFA";
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

		content.getChildren().addAll(infoLabel, new Separator(), new Label("Ancien mot de passe :"), ancienMdpField,
				new Label("Nouveau mot de passe :"), nouveauMdpField, new Label("Confirmation :"), confirmationMdpField,
				messageLabel);

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
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle(title);
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.showAndWait();
		});
	}

	private void showInfo(String title, String message) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle(title);
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.showAndWait();
		});
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