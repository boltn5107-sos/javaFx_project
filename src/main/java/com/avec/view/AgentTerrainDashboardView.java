package com.avec.view;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.enums.PhaseCycle;
import com.avec.enums.StatutAvec;
import com.avec.model.AgentTerrain;
import com.avec.model.AgentVillageois;
import com.avec.model.Avec;
import com.avec.model.Membre;
import com.avec.model.SessionUtilisateur;
import com.avec.model.Visite;
import com.avec.service.AgentVillageoisService;
import com.avec.service.AvecService;
import com.avec.service.MembreService;
import com.avec.service.UtilisateurService;
import com.avec.service.VisiteService;

import javafx.application.Platform;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class AgentTerrainDashboardView {

	private MainApp mainApp;
	private SessionUtilisateur session;
	private AgentTerrain agentTerrain;
	private AgentVillageoisService agentVillageoisService;
	private AvecService avecService;
	private VisiteService visiteService;
	private MembreService membreService;
	private BorderPane root;

	// Tables
	private TableView<Avec> avecTable;
	private TableView<AgentVillageois> avTable;
	private TableView<Visite> visiteTable;

	// Onglets
	private TabPane tabPane;

	private static final String ICONE_TABLEAU_BORD = "📊";
	private static final String ICONE_AVEC = "🤝";
	private static final String ICONE_AGENTS = "🌾";
	private static final String ICONE_VISITES = "📍";
	private static final String ICONE_RAPPORTS = "📋";
	private static final String ICONE_DECONNEXION = "🚪";

	public AgentTerrainDashboardView(MainApp mainApp) {
		this.mainApp = mainApp;
		this.session = SessionUtilisateur.getInstance();
		this.agentTerrain = session.getAgentTerrain();
		this.agentVillageoisService = new AgentVillageoisService();
		this.avecService = new AvecService();
		this.visiteService = new VisiteService();
		createView();
		loadData();
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

		// Onglets centraux
		tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		tabPane.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";");

		// Tableau de bord
		Tab dashboardTab = new Tab("Tableau de bord");
		dashboardTab.setContent(createDashboardContent());
		dashboardTab.setClosable(false);

		// AVEC supervisées
		Tab avecTab = new Tab("AVEC supervisées");
		avecTab.setContent(createAvecContent());
		avecTab.setClosable(false);

		// Agents Villageois
		Tab agentsTab = new Tab("Agents Villageois");
		agentsTab.setContent(createAgentsContent());
		agentsTab.setClosable(false);

		// Visites de supervision
		Tab visitesTab = new Tab("Visites de supervision");
		visitesTab.setContent(createVisitesContent());
		visitesTab.setClosable(false);

		

		// Rapports
		Tab rapportsTab = new Tab("Rapports");
		rapportsTab.setContent(createRapportsContent());
		rapportsTab.setClosable(false);

		tabPane.getTabs().addAll(dashboardTab, avecTab, agentsTab, visitesTab, rapportsTab);

		root.setCenter(tabPane);
	}

	private HBox createHeader() {
		HBox header = new HBox();
		header.setAlignment(Pos.CENTER_RIGHT);
		header.setPadding(new Insets(15, 20, 15, 20));
		header.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-border-color: " + Styles.GRIS_CLAIR + ";"
				+ "-fx-border-width: 0 0 2 0;");

		HBox titleBox = new HBox(10);
		titleBox.setAlignment(Pos.CENTER_LEFT);

		Label logoLabel = new Label("🏞️");
		logoLabel.setStyle("-fx-font-size: 24px;");

		Label titleLabel = new Label("AGENT DE TERRAIN - Supervision");
		titleLabel.setStyle(Styles.TITRE_PRINCIPAL);

		titleBox.getChildren().addAll(logoLabel, titleLabel);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox userBox = new HBox(15);
		userBox.setAlignment(Pos.CENTER_RIGHT);

		Label userIcon = new Label("👤");
		userIcon.setStyle("-fx-font-size: 20px;");

		Label userLabel = new Label(agentTerrain.getPrenom() + " " + agentTerrain.getNom());
		userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");

		Label roleLabel = new Label("(Agent Terrain)");
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

		VBox profileBox = new VBox(10);
		profileBox.setAlignment(Pos.CENTER);
		profileBox.setPadding(new Insets(0, 0, 20, 0));
		profileBox.setStyle("-fx-border-color: " + Styles.GRIS_CLAIR + ";" + "-fx-border-width: 0 0 2 0;");

		Label avatarLabel = new Label("🏞️");
		avatarLabel.setStyle("-fx-font-size: 48px;");

		Label nameLabel = new Label(agentTerrain.getNomComplet());
		nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

		Label zoneLabel = new Label(
				"Zone: " + (agentTerrain.getZone() != null ? agentTerrain.getZone() : "Non définie"));
		zoneLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + "; -fx-font-size: 12px;");

		profileBox.getChildren().addAll(avatarLabel, nameLabel, zoneLabel);

		VBox menuBox = new VBox(5);
		menuBox.setPadding(new Insets(20, 0, 0, 0));

		menuBox.getChildren().addAll(
				createMenuButton(ICONE_TABLEAU_BORD, "Tableau de bord", () -> tabPane.getSelectionModel().select(0)),
				createMenuButton(ICONE_AVEC, "AVEC supervisées", () -> tabPane.getSelectionModel().select(1)),
				createMenuButton(ICONE_AGENTS, "Agents Villageois", () -> tabPane.getSelectionModel().select(2)),
				createMenuButton(ICONE_VISITES, "Visites de supervision", () -> tabPane.getSelectionModel().select(3)),
				createMenuButton(ICONE_RAPPORTS, "Rapports", () -> tabPane.getSelectionModel().select(4)));
		
		// Dans le header de chaque dashboard
		Button btnChangerMdp = new Button("🔒 Changer mot de passe");
		btnChangerMdp.setStyle(Styles.BOUTON_ACCENT);
		btnChangerMdp.setOnAction(e -> showChangerMotDePasse());

		

		sidebar.getChildren().addAll(profileBox, menuBox,btnChangerMdp);

		return sidebar;
	}

	private Button createMenuButton(String icon, String text, Runnable action) {
		Button button = new Button(icon + "  " + text);
		button.setStyle(
				"-fx-background-color: transparent; " + "-fx-text-fill: " + Styles.NOIR + "; " + "-fx-font-size: 14px; "
						+ "-fx-padding: 10 15; " + "-fx-alignment: CENTER_LEFT; " + "-fx-cursor: hand;");
		button.setMaxWidth(Double.MAX_VALUE);

		button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + "; "
				+ "-fx-text-fill: " + Styles.VERT_PRINCIPAL + "; " + "-fx-font-size: 14px; " + "-fx-padding: 10 15; "
				+ "-fx-alignment: CENTER_LEFT; " + "-fx-cursor: hand;"));

		button.setOnMouseExited(e -> button.setStyle(
				"-fx-background-color: transparent; " + "-fx-text-fill: " + Styles.NOIR + "; " + "-fx-font-size: 14px; "
						+ "-fx-padding: 10 15; " + "-fx-alignment: CENTER_LEFT; " + "-fx-cursor: hand;"));

		button.setOnAction(e -> action.run());

		return button;
	}

	// ==================== TABLEAU DE BORD ====================

	private VBox createDashboardContent() {
		VBox dashboard = new VBox(20);
		dashboard.setPadding(new Insets(20));
		dashboard.setAlignment(Pos.TOP_CENTER);

		Label welcomeLabel = new Label("Bienvenue, " + agentTerrain.getPrenom());
		welcomeLabel.setStyle(Styles.TITRE_PRINCIPAL);

		// Cartes statistiques
		HBox statsBox = new HBox(20);
		statsBox.setAlignment(Pos.CENTER);
		statsBox.setPadding(new Insets(20, 0, 20, 0));

		try {
			List<Avec> avecs = avecService.getAvecsByAgentTerrainId(agentTerrain.getId());
			List<AgentVillageois> agents = agentVillageoisService.chercherAvParAt(agentTerrain.getId());
			int nbVisites = visiteService.countVisitesByAgentTerrain(agentTerrain.getId());

			VBox carte1 = createStatCard("🤝", "AVEC supervisées", String.valueOf(avecs != null ? avecs.size() : 0),
					Styles.VERT_PRINCIPAL);
			VBox carte2 = createStatCard("🌾", "Agents Villageois", String.valueOf(agents != null ? agents.size() : 0),
					Styles.BLEU_SECONDAIRE);
			VBox carte3 = createStatCard("📍", "Visites ce mois", String.valueOf(nbVisites), Styles.ACCENT_DORE);
			VBox carte4 = createStatCard("📚", "AVEC en formation", "0", Styles.VERT_PRINCIPAL);

			statsBox.getChildren().addAll(carte1, carte2, carte3, carte4);

		} catch (SQLException e) {
			System.err.println("Erreur chargement stats: " + e.getMessage());
		}

		// Prochaines visites
		VBox visitesBox = new VBox(10);
		visitesBox.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;"
				+ "-fx-padding: 20;" + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

		Label visitesTitle = new Label("Prochaines visites de supervision");
		visitesTitle.setStyle(Styles.TITRE_SECONDAIRE);

		TableView<Object> calendrierTable = new TableView<>();
		calendrierTable.setPrefHeight(200);

		TableColumn<Object, String> colDate = new TableColumn<>("Date");
		colDate.setPrefWidth(100);
		TableColumn<Object, String> colAvec = new TableColumn<>("AVEC");
		colAvec.setPrefWidth(150);
		TableColumn<Object, String> colAgent = new TableColumn<>("Agent Villageois");
		colAgent.setPrefWidth(150);
		TableColumn<Object, String> colType = new TableColumn<>("Type visite");
		colType.setPrefWidth(120);

		calendrierTable.getColumns().addAll(colDate, colAvec, colAgent, colType);

		visitesBox.getChildren().addAll(visitesTitle, calendrierTable);

		dashboard.getChildren().addAll(welcomeLabel, statsBox, visitesBox);

		return dashboard;
	}

	private VBox createStatCard(String icon, String label, String value, String color) {
		VBox card = new VBox(10);
		card.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;" + "-fx-padding: 20;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
		card.setPrefWidth(180);
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

	// ==================== AVEC SUPERVISÉES ====================

	private VBox createAvecContent() {
		VBox content = new VBox(15);
		content.setPadding(new Insets(20));

		Label title = new Label("AVEC sous votre supervision");
		title.setStyle(Styles.TITRE_SECONDAIRE);

		HBox toolbar = new HBox(10);

		// ✅ Bouton pour créer une AVEC (affectée à un agent villageois)
		Button creerAvecBtn = new Button("➕ Créer une AVEC");
		creerAvecBtn.setStyle(Styles.BOUTON_PRINCIPAL);
		creerAvecBtn.setOnAction(e -> showCreerAvec());

		Button actualiserBtn = new Button("🔄 Actualiser");
		actualiserBtn.setStyle(Styles.BOUTON_PRINCIPAL);
		actualiserBtn.setOnAction(e -> chargerAvecs());

		toolbar.getChildren().addAll(creerAvecBtn,actualiserBtn);

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
		colPhase.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
				cellData.getValue().getPhaseCourante().getLibelle()));
		colPhase.setPrefWidth(120);

		TableColumn<Avec, String> colAgent = new TableColumn<>("Agent Villageois");
		colAgent.setCellValueFactory(cellData -> {
			Avec a = cellData.getValue();
			if (a.getAgentVillageois() != null) {
				return new javafx.beans.property.SimpleStringProperty(a.getAgentVillageois().getNomComplet());
			} else if (a.getAgentVillageoisId() != null) {
	            // Si l'objet n'est pas chargé mais l'ID existe
	            return new javafx.beans.property.SimpleStringProperty("ID: " + a.getAgentVillageoisId());
	        } else {
			return new javafx.beans.property.SimpleStringProperty("");
	        }
		});
		colAgent.setPrefWidth(150);

		TableColumn<Avec, Integer> colMembres = new TableColumn<>("Membres");
		colMembres.setCellValueFactory(new PropertyValueFactory<>("nombreMembreMax"));
		colMembres.setPrefWidth(80);

		TableColumn<Avec, String> colAction = new TableColumn<>("Action");
		colAction.setCellFactory(col -> new TableCell<Avec, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					Button btn = new Button("📋 Détails");
					btn.setStyle(Styles.BOUTON_SECONDAIRE);
					btn.setOnAction(e -> {
						Avec avec = getTableView().getItems().get(getIndex());
						voirDetailsAvec(avec);
					});
					setGraphic(btn);
				}
			}
		});
		colAction.setPrefWidth(100);

		avecTable.getColumns().addAll(colId, colNom, colCode, colPhase, colAgent, colMembres, colAction);

		chargerAvecs();

		content.getChildren().addAll(title, toolbar, avecTable);
		VBox.setVgrow(avecTable, Priority.ALWAYS);

		return content;
	}

	private void chargerAvecs() {
		try {
			List<Avec> avecs = avecService.getAvecsByAgentTerrainId(agentTerrain.getId());
			 if (avecs != null && !avecs.isEmpty()) {
		            System.out.println(">>> Nombre d'AVEC trouvées: " + avecs.size());
		            for (Avec avec : avecs) {
		                System.out.println(">>> - " + avec.getNom() + 
		                    ", Agent villageois: " + (avec.getAgentVillageois() != null ? 
		                    avec.getAgentVillageois().getNomComplet() : "Non désigné"));
		            }
		            avecTable.setItems(FXCollections.observableArrayList(avecs));
		        } else {
		            System.out.println(">>> Aucune AVEC trouvée");
		            avecTable.setItems(FXCollections.observableArrayList());
		        }
		    } catch (SQLException e) {
		        System.err.println("Erreur chargement AVEC: " + e.getMessage());
		        e.printStackTrace();
		        showAlert("Erreur", "Impossible de charger les AVEC: " + e.getMessage());
		        avecTable.setItems(FXCollections.observableArrayList());
		    }
		
	}

	private void voirDetailsAvec(Avec avec) {
		
		 // ✅ Charger l'agent villageois si nécessaire
	    if (avec.getAgentVillageois() == null && avec.getAgentVillageoisId() != null) {
	        try {
	            AgentVillageois agent = agentVillageoisService.chercherAvParId(avec.getAgentVillageoisId());
	            avec.setAgentVillageois(agent);
	        } catch (Exception e) {
	            System.err.println("Erreur chargement agent: " + e.getMessage());
	        }
	    }
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Détails AVEC");
		alert.setHeaderText(avec.getNom());

		StringBuilder content = new StringBuilder();
		content.append("Code: ").append(avec.getCodeUnique()).append("\n");
		content.append("Phase: ").append(avec.getPhaseCourante().getLibelle()).append("\n");
		content.append("Statut: ").append(avec.getStatut().getLibelle()).append("\n");
		content.append("Membres max: ").append(avec.getNombreMembresMax()).append("\n");
		content.append("Prix part: ").append(avec.getPrixPart()).append(" FCFA\n");
		content.append("Agent Villageois: ");
		// ✅ Affichage correct de l'agent villageois
	    if (avec.getAgentVillageois() != null) {
	        content.append(avec.getAgentVillageois().getNomComplet());
	        content.append(" (ID: ").append(avec.getAgentVillageois().getId()).append(")");
	    } else if (avec.getAgentVillageoisId() != null) {
	        content.append("ID: ").append(avec.getAgentVillageoisId()).append(" (non chargé)");
	    } else {
	        content.append("Non désigné");
	    }
	    
	    content.append("\n\nAgent Terrain: ");
	    if (avec.getAgentTerrain() != null) {
	        content.append(avec.getAgentTerrain().getNomComplet());
	    } else if (avec.getAgentTerrainId() != null) {
	        content.append("ID: ").append(avec.getAgentTerrainId());
	    } else {
	        content.append("Non assigné");
	    }

		alert.setContentText(content.toString());
		alert.showAndWait();
	}

	// ==================== AGENTS VILLAGEOIS ====================

	private VBox createAgentsContent() {
		VBox content = new VBox(15);
		content.setPadding(new Insets(20));

		Label title = new Label("Agents Villageois sous votre supervision");
		title.setStyle(Styles.TITRE_SECONDAIRE);

		HBox toolbar = new HBox(10);
		
		 // ✅ Bouton pour désigner un agent villageois
        Button designerBtn = new Button("👨‍🏫 Désigner agent villageois");
        designerBtn.setStyle(Styles.BOUTON_PRINCIPAL);
        designerBtn.setOnAction(e -> showAjoutAgentVillageois());
        
		Button actualiserBtn = new Button("🔄 Actualiser");
		actualiserBtn.setStyle(Styles.BOUTON_PRINCIPAL);
		actualiserBtn.setOnAction(e -> chargerAgentsVillageois());

		toolbar.getChildren().addAll(designerBtn,actualiserBtn);

		avTable = new TableView<>();
		avTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

		TableColumn<AgentVillageois, Integer> colNbAvec = new TableColumn<>("AVEC formées");
		colNbAvec.setCellValueFactory(cellData -> {
			try {
				List<Avec> avecs = avecService.getAvecsByAgentVillageois(cellData.getValue().getId());
				return new javafx.beans.property.SimpleIntegerProperty(avecs != null ? avecs.size() : 0).asObject();
			} catch (SQLException e) {
				return new javafx.beans.property.SimpleIntegerProperty(0).asObject();
			}
		});
		colNbAvec.setPrefWidth(100);

		TableColumn<AgentVillageois, String> colAction = new TableColumn<>("Action");
		colAction.setCellFactory(col -> new TableCell<AgentVillageois, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					Button btn = new Button("📊 Évaluer");
					btn.setStyle(Styles.BOUTON_SECONDAIRE);
					btn.setOnAction(e -> {
						AgentVillageois av = getTableView().getItems().get(getIndex());
						evaluerAgent(av);
					});
					setGraphic(btn);
				}
			}
		});
		colAction.setPrefWidth(80);

		avTable.getColumns().addAll(colId, colNom, colPrenom, colEmail, colTelephone, colNbAvec, colAction);

		chargerAgentsVillageois();

		content.getChildren().addAll(title, toolbar, avTable);
		VBox.setVgrow(avTable, Priority.ALWAYS);

		return content;
	}

	private void chargerAgentsVillageois() {
		try {
			List<AgentVillageois> agents = agentVillageoisService.chercherAvParAt(agentTerrain.getId());
			if (agents != null && !agents.isEmpty()) {
				avTable.setItems(FXCollections.observableArrayList(agents));
			} else {
				avTable.setItems(FXCollections.observableArrayList());
			}
		} catch (Exception e) { // ✅ Utiliser Exception au lieu de SQLException
			showAlert("Erreur", "Impossible de charger les agents villageois: " + e.getMessage());
		}
	}

	private void evaluerAgent(AgentVillageois av) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Évaluation de l'agent villageois");
		dialog.setHeaderText("Évaluation de " + av.getNomComplet());

		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		content.setPrefWidth(400);

		Label noteLabel = new Label("Note (1-5):");
		Spinner<Integer> noteSpinner = new Spinner<>(1, 5, 3);
		noteSpinner.setStyle(Styles.CHAMP_TEXTE);

		Label commentaireLabel = new Label("Commentaire:");
		TextArea commentaireArea = new TextArea();
		commentaireArea.setPromptText("Observations sur le travail de l'agent...");
		commentaireArea.setPrefRowCount(4);
		commentaireArea.setStyle(Styles.CHAMP_TEXTE);

		content.getChildren().addAll(noteLabel, noteSpinner, commentaireLabel, commentaireArea);

		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				showInfo("Évaluation enregistrée",
						"Note: " + noteSpinner.getValue() + "/5\n" + "Commentaire: " + commentaireArea.getText());
			}
			return null;
		});

		dialog.showAndWait();
	}

	// ==================== VISITES DE SUPERVISION ====================

	private VBox createVisitesContent() {
		VBox content = new VBox(15);
		content.setPadding(new Insets(20));

		Label title = new Label("Planification des visites de supervision");
		title.setStyle(Styles.TITRE_SECONDAIRE);

		// Formulaire de planification
		TitledPane planificationPane = new TitledPane();
		planificationPane.setText("Planifier une visite");
		planificationPane.setExpanded(true);

		GridPane form = new GridPane();
		form.setHgap(15);
		form.setVgap(10);
		form.setPadding(new Insets(15));

		Label avecLabel = new Label("AVEC:");
		avecLabel.setStyle("-fx-font-weight: bold;");
		ComboBox<Avec> avecCombo = new ComboBox<>();
		avecCombo.setPromptText("Sélectionner une AVEC");
		avecCombo.setStyle(Styles.CHAMP_TEXTE);

		try {
			List<Avec> avecs = avecService.getAvecsByAgentTerrainId(agentTerrain.getId());
			avecCombo.setItems(FXCollections.observableArrayList(avecs));
		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de charger les AVEC: " + e.getMessage());
		}

		Label dateLabel = new Label("Date:");
		dateLabel.setStyle("-fx-font-weight: bold;");
		DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(7));
		datePicker.setStyle(Styles.CHAMP_TEXTE);

		Label typeLabel = new Label("Type de visite:");
		typeLabel.setStyle("-fx-font-weight: bold;");
		ComboBox<String> typeCombo = new ComboBox<>();
		typeCombo.getItems().addAll("Supervision", "Évaluation de phase", "Formation", "Réunion de bilan");
		typeCombo.setValue("Supervision");
		typeCombo.setStyle(Styles.CHAMP_TEXTE);

		Label observationsLabel = new Label("Observations:");
		observationsLabel.setStyle("-fx-font-weight: bold;");
		TextArea observationsArea = new TextArea();
		observationsArea.setPromptText("Objectifs de la visite...");
		observationsArea.setPrefRowCount(3);
		observationsArea.setStyle(Styles.CHAMP_TEXTE);

		Button planifierBtn = new Button("📅 Planifier la visite");
		planifierBtn.setStyle(Styles.BOUTON_PRINCIPAL);

		form.add(avecLabel, 0, 0);
		form.add(avecCombo, 1, 0);
		form.add(dateLabel, 0, 1);
		form.add(datePicker, 1, 1);
		form.add(typeLabel, 0, 2);
		form.add(typeCombo, 1, 2);
		form.add(observationsLabel, 0, 3);
		form.add(observationsArea, 1, 3);
		form.add(planifierBtn, 1, 4);

		planificationPane.setContent(form);

		// Liste des visites planifiées
		TitledPane visitesPane = new TitledPane();
		visitesPane.setText("Visites planifiées");
		visitesPane.setExpanded(true);

		visiteTable = new TableView<>();
		visiteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		visiteTable.setPrefHeight(200);

		TableColumn<Visite, String> colDate = new TableColumn<>("Date");
		colDate.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().toString()));
		colDate.setPrefWidth(100);

		TableColumn<Visite, String> colAvec = new TableColumn<>("AVEC");
		colAvec.setCellValueFactory(cellData -> {
			Avec a = cellData.getValue().getAvec();
			return new javafx.beans.property.SimpleStringProperty(a != null ? a.getNom() : "");
		});
		colAvec.setPrefWidth(150);

		TableColumn<Visite, String> colAgent = new TableColumn<>("Agent Villageois");
		colAgent.setCellValueFactory(cellData -> {
			AgentVillageois av = cellData.getValue().getAgentVillageois();
			return new javafx.beans.property.SimpleStringProperty(av != null ? av.getNomComplet() : "");
		});
		colAgent.setPrefWidth(150);

		TableColumn<Visite, String> colType = new TableColumn<>("Type");
		colType.setCellValueFactory(new PropertyValueFactory<>("module"));
		colType.setPrefWidth(120);

		TableColumn<Visite, String> colStatut = new TableColumn<>("Statut");
		colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
		colStatut.setPrefWidth(80);

		visiteTable.getColumns().addAll(colDate, colAvec, colAgent, colType, colStatut);

		visitesPane.setContent(visiteTable);

		content.getChildren().addAll(title, planificationPane, visitesPane);
		VBox.setVgrow(visiteTable, Priority.ALWAYS);

		return content;
	}

	// ==================== VALIDATION DE PHASE ====================


	
	// ==================== RAPPORTS ====================

	private VBox createRapportsContent() {
		VBox content = new VBox(15);
		content.setPadding(new Insets(20));

		Label title = new Label("Rapports de supervision");
		title.setStyle(Styles.TITRE_PRINCIPAL);

		GridPane grid = new GridPane();
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setPadding(new Insets(20));

		Button rapportGlobal = new Button("📊 Rapport global AVEC");
		rapportGlobal.setStyle(Styles.BOUTON_PRINCIPAL);
		rapportGlobal.setPrefWidth(250);
		rapportGlobal.setOnAction(e -> genererRapportGlobal());

		Button rapportAgents = new Button("🌾 Performance des agents");
		rapportAgents.setStyle(Styles.BOUTON_SECONDAIRE);
		rapportAgents.setPrefWidth(250);
		rapportAgents.setOnAction(e -> genererRapportAgents());

		Button rapportVisites = new Button("📍 Rapport des visites");
		rapportVisites.setStyle(Styles.BOUTON_ACCENT);
		rapportVisites.setPrefWidth(250);
		rapportVisites.setOnAction(e -> genererRapportVisites());

		Button rapportFormation = new Button("📚 Progression formation");
		rapportFormation.setStyle(Styles.BOUTON_PRINCIPAL);
		rapportFormation.setPrefWidth(250);
		rapportFormation.setOnAction(e -> genererRapportFormation());

		grid.add(rapportGlobal, 0, 0);
		grid.add(rapportAgents, 1, 0);
		grid.add(rapportVisites, 0, 1);
		grid.add(rapportFormation, 1, 1);

		content.getChildren().addAll(title, grid);

		return content;
	}

	private void genererRapportGlobal() {
		try {
			List<Avec> avecs = avecService.getAvecsByAgentTerrainId(agentTerrain.getId());

			StringBuilder rapport = new StringBuilder();
			rapport.append("=== RAPPORT GLOBAL AVEC ===\n\n");
			rapport.append("Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
					.append("\n");
			rapport.append("Agent Terrain: ").append(agentTerrain.getNomComplet()).append("\n\n");
			rapport.append("=== STATISTIQUES ===\n");
			rapport.append("Nombre d'AVEC supervisées: ").append(avecs.size()).append("\n\n");

			int preparatoire = 0, intensive = 0, developpement = 0, maturite = 0;
			for (Avec avec : avecs) {
				switch (avec.getPhaseCourante()) {
				case PREPARATOIRE:
					preparatoire++;
					break;
				case INTENSIVE:
					intensive++;
					break;
				case DEVELOPPEMENT:
					developpement++;
					break;
				case MATURITE:
					maturite++;
					break;
				default:
					break;
				}
			}

			rapport.append("Répartition par phase:\n");
			rapport.append("  - Préparatoire: ").append(preparatoire).append("\n");
			rapport.append("  - Intensive: ").append(intensive).append("\n");
			rapport.append("  - Développement: ").append(developpement).append("\n");
			rapport.append("  - Maturité: ").append(maturite).append("\n");

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Rapport global");
			alert.setHeaderText("Rapport des AVEC supervisées");

			TextArea textArea = new TextArea(rapport.toString());
			textArea.setEditable(false);
			textArea.setPrefHeight(300);
			textArea.setPrefWidth(400);

			alert.getDialogPane().setContent(textArea);
			alert.showAndWait();

		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de générer le rapport: " + e.getMessage());
		}
	}

	private void genererRapportAgents() {
		try {
			List<AgentVillageois> agents = agentVillageoisService.chercherAvParAt(agentTerrain.getId());

			StringBuilder rapport = new StringBuilder();
			rapport.append("=== RAPPORT DES AGENTS VILLAGEOIS ===\n\n");
			rapport.append("Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
					.append("\n\n");

			for (AgentVillageois av : agents) {
				List<Avec> avecs = avecService.getAvecsByAgentVillageois(av.getId());
				rapport.append("Agent: ").append(av.getNomComplet()).append("\n");
				rapport.append("  - AVEC formées: ").append(avecs != null ? avecs.size() : 0).append("\n");
				rapport.append("  - Contact: ").append(av.getTelephone()).append("\n\n");
			}

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Rapport des agents");
			alert.setHeaderText("Performance des agents villageois");

			TextArea textArea = new TextArea(rapport.toString());
			textArea.setEditable(false);
			textArea.setPrefHeight(300);
			textArea.setPrefWidth(400);

			alert.getDialogPane().setContent(textArea);
			alert.showAndWait();

		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de générer le rapport: " + e.getMessage());
		}
	}

	private void genererRapportVisites() {
		showInfo("Information", "Rapport des visites à implémenter");
	}

	private void genererRapportFormation() {
		showInfo("Information", "Rapport de progression formation à implémenter");
	}

	// ==================== MÉTHODES UTILITAIRES ====================

	/**
	 * ✅ Formulaire de création d'AVEC par l'agent terrain
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

		// Agent Villageois responsable
		Label agentLabel = new Label("Agent Villageois *");
		agentLabel.setStyle("-fx-font-weight: bold;");
		ComboBox<AgentVillageois> agentCombo = new ComboBox<>();
		agentCombo.setPromptText("Sélectionner un agent villageois");
		agentCombo.setStyle(Styles.CHAMP_TEXTE);

		try {
			List<AgentVillageois> agents = agentVillageoisService.chercherAvParAt(agentTerrain.getId());
			agentCombo.setItems(FXCollections.observableArrayList(agents));
		} catch (Exception e) {
			showAlert("Erreur", "Impossible de charger les agents villageois: " + e.getMessage());
		}

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
	            agentLabel, agentCombo,
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
	                AgentVillageois agent = agentCombo.getValue();
	                BigDecimal prixPart = new BigDecimal(prixPartField.getText().trim());
	                
					

					if (nom.isEmpty() || agent == null || prixPart.compareTo(BigDecimal.ZERO) <= 0) {
						showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
						return null;
					}

					// ✅ Créer l'AVEC avec l'agent villageois
					Avec avec = avecService.creerAvec(nom, prixPart, agent.getId());

					// ✅ Lier l'agent terrain comme superviseur
					avec.setAgentTerrainId(agentTerrain.getId());
					avecService.modifierAvec(avec);

					showInfo("Succès", "AVEC créée avec succès!\n" + "Nom: " + avec.getNom() + "\n" + "Code: "
							+ avec.getCodeUnique() + "\n" + "Agent Villageois: " + agent.getNomComplet());

					chargerAvecs();

				} catch (Exception e) {
					showAlert("Erreur", "Erreur: " + e.getMessage());
				}
			}
			return null;
		});

		dialog.showAndWait();
	}

	/**
	 * ✅ Formulaire d'ajout d'agent villageois par l'agent terrain
	 */
	 private void showAjoutAgentVillageois() {
	        Dialog<ButtonType> dialog = new Dialog<>();
	        dialog.setTitle("Désigner un agent villageois");
	        dialog.setHeaderText("Désigner un membre d'une AVEC terminée comme Agent Villageois");
	        
	        VBox content = new VBox(10);
	        content.setPadding(new Insets(20));
	        content.setPrefWidth(450);
	        
	        try {
	            // 1. Sélectionner une AVEC terminée
	            Label avecLabel = new Label("Sélectionner une AVEC terminée:");
	            avecLabel.setStyle("-fx-font-weight: bold;");
	            ComboBox<Avec> avecCombo = new ComboBox<>();
	            avecCombo.setPromptText("Choisir une AVEC qui a terminé son cycle");
	            avecCombo.setStyle(Styles.CHAMP_TEXTE);
	            
	            // Charger les AVEC terminées
	            List<Avec> avecsTerminees = avecService.getAvecsByStatut(StatutAvec.TERMINE);
	            avecCombo.setItems(FXCollections.observableArrayList(avecsTerminees));
	            
	            // 2. Sélectionner un membre de cette AVEC
	            Label membreLabel = new Label("Sélectionner un membre:");
	            membreLabel.setStyle("-fx-font-weight: bold;");
	            ComboBox<Membre> membreCombo = new ComboBox<>();
	            membreCombo.setPromptText("Choisir un membre");
	            membreCombo.setDisable(true);
	            membreCombo.setStyle(Styles.CHAMP_TEXTE);
	            
	            // 3. Informations complémentaires
	            Label infoLabel = new Label("Informations pour l'agent villageois:");
	            infoLabel.setStyle("-fx-font-weight: bold;");
	            
	            TextField emailField = new TextField();
	            emailField.setPromptText("Email (pour la connexion)");
	            emailField.setStyle(Styles.CHAMP_TEXTE);
	            
	            PasswordField passwordField = new PasswordField();
	            passwordField.setPromptText("Mot de passe (pour la connexion)");
	            passwordField.setStyle(Styles.CHAMP_TEXTE);
	            
	            TextField telephoneField = new TextField();
	            telephoneField.setPromptText("Téléphone");
	            telephoneField.setStyle(Styles.CHAMP_TEXTE);
	            
	            // Remplir les membres quand une AVEC est sélectionnée
	            avecCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
	                if (newVal != null) {
	                    try {
	                        List<Membre> membres = membreService.getMembresByAvecId(newVal.getId());
	                        membreCombo.setItems(FXCollections.observableArrayList(membres));
	                        membreCombo.setDisable(false);
	                        membreCombo.setPromptText("Sélectionner un membre");
	                    } catch (SQLException e) {
	                        showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
	                    }
	                }
	            });
	            
	            content.getChildren().addAll(
	                avecLabel, avecCombo,
	                membreLabel, membreCombo,
	                new Separator(),
	                infoLabel,
	                new Label("Email:"), emailField,
	                new Label("Mot de passe:"), passwordField,
	                new Label("Téléphone:"), telephoneField
	            );
	            
	            dialog.getDialogPane().setContent(content);
	            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
	            
	            dialog.setResultConverter(button -> {
	                if (button == ButtonType.OK) {
	                    try {
	                        Avec avec = avecCombo.getValue();
	                        Membre membre = membreCombo.getValue();
	                        
	                        if (avec == null || membre == null) {
	                            showAlert("Erreur", "Veuillez sélectionner une AVEC et un membre");
	                            return null;
	                        }
	                        
	                        String email = emailField.getText().trim();
	                        String password = passwordField.getText().trim();
	                        String telephone = telephoneField.getText().trim();
	                        
	                        if (email.isEmpty() || password.isEmpty()) {
	                            showAlert("Erreur", "L'email et le mot de passe sont obligatoires");
	                            return null;
	                        }
	                        
	                        // ✅ Créer l'agent villageois à partir du membre
	                        AgentVillageois agent = new AgentVillageois();
	                        agent.setNom(membre.getNom());
	                        agent.setPrenom(membre.getPrenom());
	                        agent.setEmail(email);
	                        agent.setMotDePasse(password);
	                        agent.setTelephone(telephone);
	                        agent.setAgentTerrain(agentTerrain);
	                        
	                        // Ajouter l'agent villageois
	                        if (agentVillageoisService.enregistrerAgentVillageoisParAt(agent)) {
	                            showInfo("Succès", 
	                                "Agent villageois désigné avec succès!\n\n" +
	                                "Nom: " + membre.getNomComplet() + "\n" +
	                                "AVEC d'origine: " + avec.getNom() + "\n" +
	                                "Email: " + email + "\n\n" +
	                                "Cet agent pourra maintenant former de nouvelles AVEC.");
	                            chargerAgentsVillageois();
	                        } else {
	                            showAlert("Erreur", "Échec de la désignation");
	                        }
	                        
	                    } catch (Exception e) {
	                        showAlert("Erreur", "Erreur: " + e.getMessage());
	                        e.printStackTrace();
	                    }
	                }
	                return null;
	            });
	            
	            dialog.showAndWait();
	            
	        } catch (SQLException e) {
	            showAlert("Erreur", "Impossible de charger les données: " + e.getMessage());
	        }
	    }
	

	private void loadData() {
		chargerAvecs();
		chargerAgentsVillageois();
	}
	
	/**
     * Affiche le dialogue de changement de mot de passe
     */
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