package com.avec.view;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.enums.JourReunion;
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
			tabPane.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";");

			
			// POUR QUE LE TABPANE PRENNE TOUT L'ESPACE
	        tabPane.setPrefHeight(Double.MAX_VALUE);
	        tabPane.setPrefWidth(Double.MAX_VALUE);
	        
	        // ✅ FORCER LA VISIBILITÉ
	        VBox.setVgrow(tabPane, Priority.ALWAYS);
	        HBox.setHgrow(tabPane, Priority.ALWAYS);
	        
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
		sidebar.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-border-color: " + Styles.GRIS_CLAIR + ";"
				+ "-fx-border-width: 0 2 0 0;");

		VBox menuBox = new VBox(5);
		menuBox.setPadding(new Insets(20, 0, 0, 0));

		Button btnDashboard = new Button(ICONE_TABLEAU_BORD + "  Tableau de bord");
		btnDashboard.setMaxWidth(Double.MAX_VALUE);
		btnDashboard.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Styles.NOIR
				+ "; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
		btnDashboard.setOnAction(e -> tabPane.getSelectionModel().select(0));

		Button btnUtilisateurs = new Button(ICONE_UTILISATEURS + "  Utilisateurs");
		btnUtilisateurs.setMaxWidth(Double.MAX_VALUE);
		btnUtilisateurs.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Styles.NOIR
				+ "; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
		btnUtilisateurs.setOnAction(e -> tabPane.getSelectionModel().select(1));

		Button btnAgentsTerrain = new Button(ICONE_AGENTS_TERRAIN + "  Agents Terrain");
		btnAgentsTerrain.setMaxWidth(Double.MAX_VALUE);
		btnAgentsTerrain.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Styles.NOIR
				+ "; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
		btnAgentsTerrain.setOnAction(e -> tabPane.getSelectionModel().select(2));

		Button btnAgentsVillageois = new Button(ICONE_AGENTS_VILLAGEOIS + "  Agents Villageois");
		btnAgentsVillageois.setMaxWidth(Double.MAX_VALUE);
		btnAgentsVillageois.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Styles.NOIR
				+ "; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
		btnAgentsVillageois.setOnAction(e -> {
			System.out.println("Clic sur Agents Villageois - Onglet index 3");
			tabPane.getSelectionModel().select(3);
		});

		Button btnAvec = new Button(ICONE_AVEC + "  AVEC");
		btnAvec.setMaxWidth(Double.MAX_VALUE);
		btnAvec.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Styles.NOIR
				+ "; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
		btnAvec.setOnAction(e -> tabPane.getSelectionModel().select(4));

		Button btnMembres = new Button(ICONE_MEMBRES + "  Membres");
		btnMembres.setMaxWidth(Double.MAX_VALUE);
		btnMembres.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Styles.NOIR
				+ "; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
		btnMembres.setOnAction(e -> tabPane.getSelectionModel().select(5));

		Button btnStats = new Button(ICONE_STATISTIQUES + "  Statistiques");
		btnStats.setMaxWidth(Double.MAX_VALUE);
		btnStats.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Styles.NOIR
				+ "; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
		btnStats.setOnAction(e -> tabPane.getSelectionModel().select(6));

		menuBox.getChildren().addAll(btnDashboard, btnUtilisateurs, btnAgentsTerrain, btnAgentsVillageois, btnAvec,
				btnMembres, btnStats);

		sidebar.getChildren().add(menuBox);

		return sidebar;
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
			totalEpargne = membreService.getTotalEpargne();
			totalPrets = membreService.getTotalPretEnCours();
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
			BigDecimal totalEpargne = membreService.getTotalEpargne();
			BigDecimal totalPrets = membreService.getTotalPretEnCours();

			statsGrid.add(createStatItem("👤", "Utilisateurs", String.valueOf(nbUtilisateurs)), 0, 0);
			statsGrid.add(createStatItem("🏞️", "Agents Terrain", String.valueOf(nbAgentsTerrain)), 1, 0);
			statsGrid.add(createStatItem("🌾", "Agents Villageois", String.valueOf(nbAgentsVillageois)), 2, 0);
			statsGrid.add(createStatItem("🤝", "AVEC", String.valueOf(nbAvec)), 3, 0);
			statsGrid.add(createStatItem("👥", "Membres", String.valueOf(nbMembres)), 0, 1);
			statsGrid.add(createStatItem("✅", "Membres actifs", String.valueOf(nbMembresActifs)), 1, 1);
			statsGrid.add(createStatItem("💰", "Épargne totale", formatMontant(totalEpargne)), 2, 1);
			statsGrid.add(createStatItem("💳", "Prêts en cours", formatMontant(totalPrets)), 3, 1);

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
				resultats.addAll(membreService.rechercherMembres(avec.getId(), recherche));
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

		TextField professionField = new TextField();
		professionField.setPromptText("Profession");
		professionField.setStyle(Styles.CHAMP_TEXTE);

		TextField villageField = new TextField();
		villageField.setPromptText("Village");
		villageField.setStyle(Styles.CHAMP_TEXTE);

		TextField telephoneField = new TextField();
		telephoneField.setPromptText("Téléphone");
		telephoneField.setStyle(Styles.CHAMP_TEXTE);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Mot de passe");
		passwordField.setStyle(Styles.CHAMP_TEXTE);

		content.getChildren().addAll(new Label("Nom:"), nomField, new Label("Prénom:"), prenomField, new Label("AVEC:"),
				avecCombo, new Label("Profession:"), professionField, new Label("Village:"), villageField,
				new Label("Téléphone:"), telephoneField, new Label("Mot de passe:"), passwordField);

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

					Membre membre = membreService.creerMembre(nomField.getText().trim(), prenomField.getText().trim(),
							selectedAvec.getId(), professionField.getText().trim(), villageField.getText().trim(),
							telephoneField.getText().trim());

					// Définir le mot de passe (si votre modèle Membre a un champ motDePasse)
					// membre.setMotDePasse(passwordField.getText().trim());

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

		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		content.setPrefWidth(450);

		// Nom de l'AVEC
		Label nomLabel = new Label("Nom de l'AVEC *");
		nomLabel.setStyle("-fx-font-weight: bold;");
		TextField nomField = new TextField();
		nomField.setPromptText("Ex: AVEC Ndiarème, AVEC Bambey, ...");
		nomField.setStyle(Styles.CHAMP_TEXTE);

		// Prix de la part
		Label prixLabel = new Label("Prix de la part (FCFA) *");
		prixLabel.setStyle("-fx-font-weight: bold;");
		TextField prixPartField = new TextField();
		prixPartField.setPromptText("Ex: 500, 1000, 2000");
		prixPartField.setStyle(Styles.CHAMP_TEXTE);

		// Lieu de réunion
		Label lieuLabel = new Label("Lieu de réunion *");
		lieuLabel.setStyle("-fx-font-weight: bold;");
		TextField lieuField = new TextField();
		lieuField.setPromptText("Ex: Chez le président, École du village, ...");
		lieuField.setStyle(Styles.CHAMP_TEXTE);

		// Jour de réunion
		Label jourLabel = new Label("Jour de réunion *");
		jourLabel.setStyle("-fx-font-weight: bold;");
		ComboBox<JourReunion> jourCombo = new ComboBox<>();
		jourCombo.setItems(FXCollections.observableArrayList(JourReunion.values()));
		jourCombo.setPromptText("Sélectionner le jour de réunion");
		jourCombo.setStyle(Styles.CHAMP_TEXTE);

		// Agent villageois
		Label agentLabel = new Label("Agent Villageois *");
		agentLabel.setStyle("-fx-font-weight: bold;");
		ComboBox<AgentVillageois> agentCombo = new ComboBox<>();
		agentCombo.setPromptText("Sélectionner un agent villageois");
		agentCombo.setStyle(Styles.CHAMP_TEXTE);

		// Charger les agents villageois
		try {
			List<AgentVillageois> agents = agentVillageoisService.listerAgentVillageois();
			if (agents != null && !agents.isEmpty()) {
				agentCombo.setItems(FXCollections.observableArrayList(agents));
			} else {
				agentCombo.setPromptText("Aucun agent villageois disponible");
				agentCombo.setDisable(true);
			}
		} catch (Exception e) {
			System.err.println("Erreur chargement agents: " + e.getMessage());
			agentCombo.setPromptText("Erreur chargement");
			agentCombo.setDisable(true);
		}

		// Informations supplémentaires
		Label infoLabel = new Label("Informations complémentaires");
		infoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.BLEU_SECONDAIRE + ";");

		// Nombre de membres max
		Label nbMembresLabel = new Label("Nombre max de membres");
		Spinner<Integer> nbMembresSpinner = new Spinner<>(10, 30, 15);
		nbMembresSpinner.setStyle(Styles.CHAMP_TEXTE);
		nbMembresSpinner.setEditable(true);

		// Taux frais service
		Label tauxLabel = new Label("Taux frais service mensuel (%)");
		TextField tauxField = new TextField("5");
		tauxField.setPromptText("Entre 5% et 10%");
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

		// Durée max prêt
		Label dureeLabel = new Label("Durée max prêt (semaines)");
		Spinner<Integer> dureeSpinner = new Spinner<>(4, 24, 12);
		dureeSpinner.setStyle(Styles.CHAMP_TEXTE);

		// Organisation du contenu
		VBox formFields = new VBox(8);
		formFields.getChildren().addAll(nomLabel, nomField, prixLabel, prixPartField, lieuLabel, lieuField, jourLabel,
				jourCombo, agentLabel, agentCombo, new Separator(), infoLabel, nbMembresLabel, nbMembresSpinner,
				tauxLabel, tauxField, caisseSolidariteCheck, cotisationBox, dureeLabel, dureeSpinner);

		ScrollPane scrollPane = new ScrollPane(formFields);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefHeight(500);
		scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

		content.getChildren().add(scrollPane);

		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Désactiver OK si les champs obligatoires sont vides
		Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setDisable(true);

		// Validation en temps réel
		nomField.textProperty().addListener((obs, oldVal, newVal) -> {
			okButton.setDisable(newVal == null || newVal.trim().isEmpty() || prixPartField.getText().trim().isEmpty()
					|| lieuField.getText().trim().isEmpty() || jourCombo.getValue() == null
					|| agentCombo.getValue() == null);
		});

		prixPartField.textProperty().addListener((obs, oldVal, newVal) -> {
			okButton.setDisable(nomField.getText().trim().isEmpty() || newVal == null || newVal.trim().isEmpty()
					|| lieuField.getText().trim().isEmpty() || jourCombo.getValue() == null
					|| agentCombo.getValue() == null);
		});

		lieuField.textProperty().addListener((obs, oldVal, newVal) -> {
			okButton.setDisable(nomField.getText().trim().isEmpty() || prixPartField.getText().trim().isEmpty()
					|| newVal == null || newVal.trim().isEmpty() || jourCombo.getValue() == null
					|| agentCombo.getValue() == null);
		});

		jourCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
			okButton.setDisable(nomField.getText().trim().isEmpty() || prixPartField.getText().trim().isEmpty()
					|| lieuField.getText().trim().isEmpty() || newVal == null || agentCombo.getValue() == null);
		});

		agentCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
			okButton.setDisable(nomField.getText().trim().isEmpty() || prixPartField.getText().trim().isEmpty()
					|| lieuField.getText().trim().isEmpty() || jourCombo.getValue() == null || newVal == null);
		});

		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				try {
					String nom = nomField.getText().trim();
					BigDecimal prixPart = new BigDecimal(prixPartField.getText().trim());
					String lieuReunion = lieuField.getText().trim();
					JourReunion jourReunion = jourCombo.getValue();
					Long agentVillageoisId = agentCombo.getValue().getId();

					// Valider le prix
					if (prixPart.compareTo(BigDecimal.ZERO) <= 0) {
						showAlert("Erreur", "Le prix de la part doit être supérieur à 0");
						return null;
					}

					// Créer l'AVEC avec la méthode creerAvec
					Avec avec = avecService.creerAvec(nom, prixPart, agentVillageoisId);

					// Mettre à jour les paramètres supplémentaires
					avec.setNombreMembresMax(nbMembresSpinner.getValue());
					avec.setTauxFraisServiceMensuel(new BigDecimal(tauxField.getText().trim()));
					avec.setCaisseSolidariteActive(caisseSolidariteCheck.isSelected());
					if (caisseSolidariteCheck.isSelected()) {
						avec.setCotisationCaisseSolidarite(new BigDecimal(cotisationField.getText().trim()));
					}
					// avec.setDureeMaxPretSemaines(dureeSpinner.getValue());

					// Sauvegarder les modifications supplémentaires
					avecService.modifierAvec(avec);

					showInfo("Succès",
							"AVEC créée avec succès!\n" + "Nom: " + avec.getNom() + "\n" + "Code: "
									+ avec.getCodeUnique() + "\n" + "Prix de la part: " + formatMontant(prixPart) + "\n"
									+ "Agent: " + agentCombo.getValue().getNomComplet());

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