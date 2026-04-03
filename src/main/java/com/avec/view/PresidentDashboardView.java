package com.avec.view;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.enums.RoleComite;
import com.avec.enums.StatutMembre;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

public class PresidentDashboardView {

	private MainApp mainApp;
	private SessionUtilisateur session;
	private MembreService membreService;
	private AvecService avecService;
	private BorderPane root;

	private Membre president;
	private Avec avec;

	private TableView<Membre> membresTable;
	private TableView<Membre> comiteTable;
	private TableView<Membre> gardiensTable;

	private static final String ICONE_TABLEAU_BORD = "📊";
	private static final String ICONE_MEMBRES = "👥";
	private static final String ICONE_COMITE = "👤";
	private static final String ICONE_GARDIENS = "🔑";
	private static final String ICONE_ELECTION = "🗳️";
	private static final String ICONE_REUNIONS = "📅";
	private static final String ICONE_RAPPORTS = "📋";
	private static final String ICONE_DECONNEXION = "🚪";

	public PresidentDashboardView(MainApp mainApp) {

		this.mainApp = mainApp;
		this.session = SessionUtilisateur.getInstance();
		this.membreService = new MembreService();
		this.avecService = new AvecService();
		this.president = session.getMembre();

		initData();
		createView();

	}

	private void initData() {
		try {
			if (president != null && president.getAvecId() != null) {
				this.avec = avecService.getAvecById(president.getAvecId());
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
		header.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-border-color: " + Styles.GRIS_CLAIR + ";"
				+ "-fx-border-width: 0 0 2 0;");

		HBox titleBox = new HBox(10);
		titleBox.setAlignment(Pos.CENTER_LEFT);

		Label logoLabel = new Label("👑");
		logoLabel.setStyle("-fx-font-size: 24px;");

		String titre = avec != null ? "PRÉSIDENT - " + avec.getNom() : "PRÉSIDENT";
		Label titleLabel = new Label(titre);
		titleLabel.setStyle(Styles.TITRE_PRINCIPAL);

		titleBox.getChildren().addAll(logoLabel, titleLabel);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox userBox = new HBox(15);
		userBox.setAlignment(Pos.CENTER_RIGHT);

		Label userIcon = new Label("👤");
		userIcon.setStyle("-fx-font-size: 20px;");

		Label userLabel = new Label(president.getPrenom() + " " + president.getNom());
		userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");

		Label roleLabel = new Label("(Président)");
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
		sidebar.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-border-color: " + Styles.GRIS_CLAIR + ";"
				+ "-fx-border-width: 0 2 0 0;");

		// Profil AVEC
		VBox profileBox = new VBox(10);
		profileBox.setAlignment(Pos.CENTER);
		profileBox.setPadding(new Insets(0, 0, 20, 0));
		profileBox.setStyle("-fx-border-color: " + Styles.GRIS_CLAIR + ";" + "-fx-border-width: 0 0 2 0;");

		Label avatarLabel = new Label("👥");
		avatarLabel.setStyle("-fx-font-size: 48px;");

		String nomAvec = avec != null ? avec.getNom() : "AVEC";
		Label avecLabel = new Label(nomAvec);
		avecLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

		if (avec != null) {
			Label infoLabel = new Label("Cycle: " + avec.getPhaseCourante());
			infoLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + "; -fx-font-size: 12px;");
			profileBox.getChildren().addAll(avatarLabel, avecLabel, infoLabel);
		} else {
			profileBox.getChildren().addAll(avatarLabel, avecLabel);
		}

		// Menu
		VBox menuBox = new VBox(5);
		menuBox.setPadding(new Insets(20, 0, 0, 0));

		menuBox.getChildren().addAll(createMenuButton(ICONE_TABLEAU_BORD, "Tableau de bord", this::showDashboard),
				createMenuButton(ICONE_MEMBRES, "Liste des membres", this::showMembres),
				createMenuButton(ICONE_COMITE, "Comité de gestion", this::showComite),
				createMenuButton(ICONE_GARDIENS, "Gardiens de clés", this::showGardiens),
				createMenuButton(ICONE_ELECTION, "Organiser élection", this::showElection),
				createMenuButton(ICONE_REUNIONS, "Calendrier réunions", this::showReunions),
				createMenuButton(ICONE_RAPPORTS, "Rapports", this::showRapports));

		sidebar.getChildren().addAll(profileBox, menuBox);

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

	private void showDashboard() {
		VBox dashboard = new VBox(20);
		dashboard.setPadding(new Insets(20));
		dashboard.setAlignment(Pos.TOP_CENTER);

		try {
			int totalMembres = membreService.getMembresByAvecId(president.getAvecId()).size();
			List<Membre> comite = membreService.getComiteGestion(president.getAvecId());
			List<Membre> gardiens = membreService.getGardiensCles(president.getAvecId());
			int membresActifs = (int) membreService.getMembresByAvecId(president.getAvecId()).stream()
					.filter(m -> m.getStatut() == StatutMembre.ACTIF).count();

			Label welcomeLabel = new Label("Tableau de bord du Président");
			welcomeLabel.setStyle(Styles.TITRE_PRINCIPAL);

			// Cartes statistiques
			HBox statsBox = new HBox(20);
			statsBox.setAlignment(Pos.CENTER);
			statsBox.setPadding(new Insets(20, 0, 20, 0));

			VBox carte1 = createStatCard("👥", "Membres", String.valueOf(totalMembres), Styles.VERT_PRINCIPAL);
			VBox carte2 = createStatCard("✅", "Actifs", String.valueOf(membresActifs), Styles.VERT_SUCCES);
			VBox carte3 = createStatCard("👤", "Comité", String.valueOf(comite.size()), Styles.BLEU_SECONDAIRE);
			VBox carte4 = createStatCard("🔑", "Gardiens", String.valueOf(gardiens.size()), Styles.ACCENT_DORE);

			statsBox.getChildren().addAll(carte1, carte2, carte3, carte4);

			// État du comité
			VBox comiteBox = new VBox(10);
			comiteBox.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;"
					+ "-fx-padding: 20;" + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

			Label comiteTitle = new Label("Comité de gestion en cours");
			comiteTitle.setStyle(Styles.TITRE_SECONDAIRE);

			GridPane comiteGrid = new GridPane();
			comiteGrid.setHgap(20);
			comiteGrid.setVgap(10);
			comiteGrid.setPadding(new Insets(10, 0, 0, 0));

			int row = 0;
			for (Membre m : comite) {
				Label roleLabel = new Label(m.getRoleComite().getDescription() + ":");
				roleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
				Label nomLabel = new Label(m.getPrenom() + " " + m.getNom());
				comiteGrid.add(roleLabel, 0, row);
				comiteGrid.add(nomLabel, 1, row);
				row++;
			}

			comiteBox.getChildren().addAll(comiteTitle, comiteGrid);

			dashboard.getChildren().addAll(welcomeLabel, statsBox, comiteBox);

		} catch (SQLException e) {
			showAlert("Erreur", "Erreur chargement données: " + e.getMessage());
		}

		root.setCenter(dashboard);
	}

	private void showMembres() {
		VBox view = new VBox(15);
		view.setPadding(new Insets(20));

		Label title = new Label("Liste des membres");
		title.setStyle(Styles.TITRE_PRINCIPAL);

		// Barre de recherche
		HBox searchBox = new HBox(10);
		TextField searchField = new TextField();
		searchField.setPromptText("Rechercher par nom...");
		searchField.setPrefWidth(300);
		searchField.setStyle(Styles.CHAMP_TEXTE);

		Button searchButton = new Button("🔍 Rechercher");
		searchButton.setStyle(Styles.BOUTON_PRINCIPAL);

		// Filtre par statut
		ComboBox<String> statutFilter = new ComboBox<>();
		statutFilter.getItems().addAll("Tous", "Actifs", "Inactifs");
		statutFilter.setValue("Tous");
		statutFilter.setStyle(Styles.CHAMP_TEXTE);

		searchBox.getChildren().addAll(searchField, statutFilter, searchButton);

		// Tableau des membres
		membresTable = new TableView<>();
		membresTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<Membre, String> colNom = new TableColumn<>("Nom");
		colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
		colNom.setPrefWidth(120);

		TableColumn<Membre, String> colPrenom = new TableColumn<>("Prénom");
		colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
		colPrenom.setPrefWidth(120);

		TableColumn<Membre, String> colCarte = new TableColumn<>("N° Carte");
		colCarte.setCellValueFactory(new PropertyValueFactory<>("numeroCarte"));
		colCarte.setPrefWidth(150);

		TableColumn<Membre, String> colRole = new TableColumn<>("Rôle");
		colRole.setCellValueFactory(cellData -> {
			Membre m = cellData.getValue();
			return new javafx.beans.property.SimpleStringProperty(
					m.getRoleComite() != null ? m.getRoleComite().getDescription() : "Membre");
		});
		colRole.setPrefWidth(120);

		TableColumn<Membre, Integer> colParts = new TableColumn<>("Parts");
		colParts.setCellValueFactory(new PropertyValueFactory<>("nombreParts"));
		colParts.setPrefWidth(80);

		TableColumn<Membre, String> colStatut = new TableColumn<>("Statut");
		colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
		colStatut.setPrefWidth(100);

		TableColumn<Membre, String> colTelephone = new TableColumn<>("Téléphone");
		colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
		colTelephone.setPrefWidth(120);

		membresTable.getColumns().addAll(colNom, colPrenom, colCarte, colRole, colParts, colStatut, colTelephone);

		// Charger les données
		loadMembresData();

		// Actions
		searchButton.setOnAction(e -> {
			try {
				String recherche = searchField.getText();
				List<Membre> resultats;
				if (recherche.isEmpty()) {
					resultats = membreService.getMembresByAvecId(president.getAvecId());
				} else {
					resultats = membreService.rechercherMembres(president.getAvecId(), recherche);
				}

				String filter = statutFilter.getValue();
				if ("Actifs".equals(filter)) {
					resultats = resultats.stream().filter(m -> m.getStatut() == StatutMembre.ACTIF).toList();
				} else if ("Inactifs".equals(filter)) {
					resultats = resultats.stream().filter(m -> m.getStatut() == StatutMembre.INACTIF).toList();
				}

				membresTable.setItems(FXCollections.observableArrayList(resultats));
			} catch (SQLException ex) {
				showAlert("Erreur", "Erreur recherche: " + ex.getMessage());
			}
		});

		statutFilter.setOnAction(e -> {
			try {
				List<Membre> membres = membreService.getMembresByAvecId(president.getAvecId());
				String filter = statutFilter.getValue();
				if ("Actifs".equals(filter)) {
					membres = membres.stream().filter(m -> m.getStatut() == StatutMembre.ACTIF).toList();
				} else if ("Inactifs".equals(filter)) {
					membres = membres.stream().filter(m -> m.getStatut() == StatutMembre.INACTIF).toList();
				}
				membresTable.setItems(FXCollections.observableArrayList(membres));
			} catch (SQLException ex) {
				showAlert("Erreur", "Erreur filtrage: " + ex.getMessage());
			}
		});

		view.getChildren().addAll(title, searchBox, membresTable);
		root.setCenter(view);
	}

	private void loadMembresData() {
		try {
			List<Membre> membres = membreService.getMembresByAvecId(president.getAvecId());
			membresTable.setItems(FXCollections.observableArrayList(membres));
		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
		}
	}

	private void showComite() {
		VBox view = new VBox(15);
		view.setPadding(new Insets(20));

		Label title = new Label("Comité de gestion");
		title.setStyle(Styles.TITRE_PRINCIPAL);

		try {
			List<Membre> comite = membreService.getComiteGestion(president.getAvecId());

			GridPane grid = new GridPane();
			grid.setHgap(20);
			grid.setVgap(15);
			grid.setPadding(new Insets(20));
			grid.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;");

			// En-têtes
			Label roleHeader = new Label("Rôle");
			roleHeader.setStyle(
					"-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
			Label nomHeader = new Label("Nom");
			nomHeader.setStyle(
					"-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
			Label contactHeader = new Label("Contact");
			contactHeader.setStyle(
					"-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");

			grid.add(roleHeader, 0, 0);
			grid.add(nomHeader, 1, 0);
			grid.add(contactHeader, 2, 0);

			int row = 1;
			for (Membre m : comite) {
				Label roleLabel = new Label(m.getRoleComite().getDescription());
				roleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.BLEU_SECONDAIRE + ";");

				Label nomLabel = new Label(m.getPrenom() + " " + m.getNom());

				Label contactLabel = new Label(m.getTelephone() != null ? m.getTelephone() : "Non renseigné");

				grid.add(roleLabel, 0, row);
				grid.add(nomLabel, 1, row);
				grid.add(contactLabel, 2, row);

				row++;
			}

			view.getChildren().addAll(title, grid);

		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de charger le comité: " + e.getMessage());
		}

		root.setCenter(view);
	}

	private void showGardiens() {
		VBox view = new VBox(15);
		view.setPadding(new Insets(20));

		Label title = new Label("Gardiens de clés");
		title.setStyle(Styles.TITRE_PRINCIPAL);

		try {
			List<Membre> gardiens = membreService.getGardiensCles(president.getAvecId());

			GridPane grid = new GridPane();
			grid.setHgap(20);
			grid.setVgap(15);
			grid.setPadding(new Insets(20));
			grid.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;");

			// En-têtes
			Label roleHeader = new Label("Rôle");
			roleHeader.setStyle(
					"-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
			Label nomHeader = new Label("Nom");
			nomHeader.setStyle(
					"-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
			Label contactHeader = new Label("Contact");
			contactHeader.setStyle(
					"-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");

			grid.add(roleHeader, 0, 0);
			grid.add(nomHeader, 1, 0);
			grid.add(contactHeader, 2, 0);

			int row = 1;
			for (Membre m : gardiens) {
				Label roleLabel = new Label(m.getRoleCle().getLibelle());
				roleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.ACCENT_DORE + ";");

				Label nomLabel = new Label(m.getPrenom() + " " + m.getNom());

				Label contactLabel = new Label(m.getTelephone() != null ? m.getTelephone() : "Non renseigné");

				grid.add(roleLabel, 0, row);
				grid.add(nomLabel, 1, row);
				grid.add(contactLabel, 2, row);

				row++;
			}

			view.getChildren().addAll(title, grid);

		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de charger les gardiens: " + e.getMessage());
		}

		root.setCenter(view);
	}

	private void showElection() {
		VBox view = new VBox(15);
		view.setPadding(new Insets(20));

		Label title = new Label("Organiser une élection");
		title.setStyle(Styles.TITRE_PRINCIPAL);

		// Procédure d'élection selon le guide
		VBox procedureBox = new VBox(10);
		procedureBox.setStyle(
				"-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;" + "-fx-padding: 20;");

		Label procedureTitle = new Label("Procédure d'élection (vote secret avec cailloux)");
		procedureTitle.setStyle(Styles.TITRE_SECONDAIRE);

		Label etape1 = new Label("1. Chaque poste nécessite au moins 2 candidats");
		Label etape2 = new Label("2. Utiliser 3 récipients de couleurs différentes");
		Label etape3 = new Label("3. Les membres votent derrière un paravent");
		Label etape4 = new Label("4. Déposer un caillou dans l'urne du candidat choisi");
		Label etape5 = new Label("5. Dépouillement public par l'Agent Villageois");

		procedureBox.getChildren().addAll(procedureTitle, etape1, etape2, etape3, etape4, etape5);

		// Liste des membres éligibles
		Label eligibleTitle = new Label("Membres éligibles pour le comité");
		eligibleTitle.setStyle(Styles.TITRE_SECONDAIRE);
		eligibleTitle.setPadding(new Insets(20, 0, 10, 0));

		try {
			List<Membre> membres = membreService.getMembresByAvecId(president.getAvecId());
			List<Membre> eligibles = membres.stream().filter(m -> m.getStatut() == StatutMembre.ACTIF).toList();

			ListView<String> eligibleList = new ListView<>();
			for (Membre m : eligibles) {
				eligibleList.getItems().add(m.getPrenom() + " " + m.getNom());
			}
			eligibleList.setPrefHeight(150);

			// Bouton pour démarrer l'élection
			Button startElection = new Button("🗳️ Démarrer l'élection");
			startElection.setStyle(Styles.BOUTON_PRINCIPAL);
			startElection.setPrefWidth(300);
			startElection.setPadding(new Insets(10));

			startElection.setOnAction(e -> {
				showElectionDialog(eligibles);
			});

			view.getChildren().addAll(title, procedureBox, eligibleTitle, eligibleList, startElection);

		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
		}

		root.setCenter(view);
	}

	private void showElectionDialog(List<Membre> eligibles) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Nouvelle élection");
		dialog.setHeaderText("Élection du comité de gestion");

		VBox content = new VBox(15);
		content.setPadding(new Insets(20));
		content.setPrefWidth(400);

		Label infoLabel = new Label("Sélectionnez les membres pour chaque poste:");
		infoLabel.setStyle("-fx-font-weight: bold;");

		// Combobox pour chaque poste
		ComboBox<Membre> presidentCombo = new ComboBox<>();
		presidentCombo.setPromptText("Choisir le Président");
		presidentCombo.setItems(FXCollections.observableArrayList(eligibles));

		ComboBox<Membre> secretaireCombo = new ComboBox<>();
		secretaireCombo.setPromptText("Choisir le Secrétaire");
		secretaireCombo.setItems(FXCollections.observableArrayList(eligibles));

		ComboBox<Membre> tresorierCombo = new ComboBox<>();
		tresorierCombo.setPromptText("Choisir le Trésorier");
		tresorierCombo.setItems(FXCollections.observableArrayList(eligibles));

		ComboBox<Membre> compteur1Combo = new ComboBox<>();
		compteur1Combo.setPromptText("Choisir le 1er Compteur");
		compteur1Combo.setItems(FXCollections.observableArrayList(eligibles));

		ComboBox<Membre> compteur2Combo = new ComboBox<>();
		compteur2Combo.setPromptText("Choisir le 2ème Compteur");
		compteur2Combo.setItems(FXCollections.observableArrayList(eligibles));

		content.getChildren().addAll(infoLabel, new Label("Président:"), presidentCombo, new Label("Secrétaire:"),
				secretaireCombo, new Label("Trésorier:"), tresorierCombo, new Label("Compteur 1:"), compteur1Combo,
				new Label("Compteur 2:"), compteur2Combo);

		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				try {
					List<MembreService.ResultatElection> resultats = new ArrayList<>();
					resultats.add(new MembreService.ResultatElection(presidentCombo.getValue().getId(),
							RoleComite.PRESIDENT));
					resultats.add(new MembreService.ResultatElection(secretaireCombo.getValue().getId(),
							RoleComite.SECRETAIRE));
					resultats.add(new MembreService.ResultatElection(tresorierCombo.getValue().getId(),
							RoleComite.TRESORIER));
					resultats.add(
							new MembreService.ResultatElection(compteur1Combo.getValue().getId(), RoleComite.COMPTEUR));
					resultats.add(
							new MembreService.ResultatElection(compteur2Combo.getValue().getId(), RoleComite.COMPTEUR));

					if (membreService.organiserElection(president.getAvecId(), resultats)) {
						showInfo("Succès", "Élection organisée avec succès!");
						showComite();
					} else {
						showAlert("Erreur", "Échec de l'organisation de l'élection");
					}
				} catch (Exception e) {
					showAlert("Erreur", "Erreur: " + e.getMessage());
				}
			}
			return null;
		});

		dialog.showAndWait();
	}

	private void showReunions() {
		VBox view = new VBox(15);
		view.setPadding(new Insets(20));

		Label title = new Label("Calendrier des réunions");
		title.setStyle(Styles.TITRE_PRINCIPAL);

		// Calendrier simplifié
		VBox calendarBox = new VBox(10);
		calendarBox.setStyle(
				"-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;" + "-fx-padding: 20;");

		Label nextReunionLabel = new Label("Prochaine réunion");
		nextReunionLabel.setStyle(Styles.TITRE_SECONDAIRE);

		GridPane infoGrid = new GridPane();
		infoGrid.setHgap(15);
		infoGrid.setVgap(10);

		infoGrid.add(new Label("Date:"), 0, 0);
		infoGrid.add(new Label("À déterminer"), 1, 0);
		infoGrid.add(new Label("Type:"), 0, 1);
		infoGrid.add(new Label("Réunion d'épargne"), 1, 1);
		infoGrid.add(new Label("Lieu:"), 0, 2);
		infoGrid.add(new Label("Siège de l'AVEC"), 1, 2);

		calendarBox.getChildren().addAll(nextReunionLabel, infoGrid);

		view.getChildren().addAll(title, calendarBox);
		root.setCenter(view);
	}

	private void showRapports() {
		VBox view = new VBox(15);
		view.setPadding(new Insets(20));

		Label title = new Label("Rapports");
		title.setStyle(Styles.TITRE_PRINCIPAL);

		GridPane grid = new GridPane();
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setPadding(new Insets(20));

		Button rapportMembres = new Button("📊 Rapport des membres");
		rapportMembres.setStyle(Styles.BOUTON_PRINCIPAL);
		rapportMembres.setPrefWidth(250);

		Button rapportComite = new Button("👤 Rapport du comité");
		rapportComite.setStyle(Styles.BOUTON_SECONDAIRE);
		rapportComite.setPrefWidth(250);

		Button rapportFinancier = new Button("💰 Rapport financier");
		rapportFinancier.setStyle(Styles.BOUTON_ACCENT);
		rapportFinancier.setPrefWidth(250);

		Button rapportActivites = new Button("📋 Rapport d'activités");
		rapportActivites.setStyle(Styles.BOUTON_PRINCIPAL);
		rapportActivites.setPrefWidth(250);

		grid.add(rapportMembres, 0, 0);
		grid.add(rapportComite, 1, 0);
		grid.add(rapportFinancier, 0, 1);
		grid.add(rapportActivites, 1, 1);

		view.getChildren().addAll(title, grid);
		root.setCenter(view);
	}

	private VBox createStatCard(String icon, String label, String value, String color) {
		VBox card = new VBox(10);
		card.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;" + "-fx-padding: 20;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
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
