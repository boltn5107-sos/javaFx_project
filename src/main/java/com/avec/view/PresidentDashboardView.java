package com.avec.view;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.enums.RoleComite;
import com.avec.enums.StatutMembre;
import com.avec.model.Amende;
import com.avec.model.Avec;
import com.avec.model.Membre;
import com.avec.model.Pret;
import com.avec.model.Reunion;
import com.avec.model.SessionUtilisateur;
import com.avec.model.TypeInfraction;
import com.avec.service.AmendeService;
import com.avec.service.AvecService;
import com.avec.service.CycleService;
import com.avec.service.MembreService;
import com.avec.service.PretService;
import com.avec.service.ReunionService;
import com.avec.service.TypeInfractionService;
import com.avec.service.UtilisateurService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
	private CycleService cycleService;
	private ReunionService reunionService;
	private BorderPane root;

	private Membre president;
	private Avec avec;
	private PretService pretService;

	private TableView<Membre> membresTable;
	private TableView<Membre> amendeTable;
	
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

	private static final String ICONE_MEMBRES = "👥";
	private static final String ICONE_COMITE = "👤";
	private static final String ICONE_DECONNEXION = "🚪";
	private static final String ICONE_AMENDES = "💰";
	private static final String ICONE_REUNION = "📅";
	 private static final String ICONE_LISTE = "📋";
	private static final String ICONE_PRET = "💳";

	public PresidentDashboardView(MainApp mainApp) {
		this.mainApp = mainApp;
		this.session = SessionUtilisateur.getInstance();
		this.membreService = new MembreService();
		this.avecService = new AvecService();
		this.cycleService = new CycleService();
		this.reunionService = new ReunionService();
		this.pretService = new PretService();
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
		showMembres();
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

		Button logoutButton = new Button(ICONE_DECONNEXION + " Déconnexion");
		logoutButton.setStyle(Styles.BOUTON_SECONDAIRE);
		logoutButton.setOnAction(e -> logout());

		userBox.getChildren().addAll(userIcon, userLabel, roleLabel, logoutButton);

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
	        
	        Label avatarLabel = new Label("👑");
	        avatarLabel.setStyle("-fx-font-size: 48px;");
	        
	        Label nameLabel = new Label(president.getNomComplet());
	        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
	        
	        profileBox.getChildren().addAll(avatarLabel, nameLabel);
	        
	        VBox menuBox = new VBox(5);
	        menuBox.setPadding(new Insets(20, 0, 0, 0));
	        
	        // ✅ Création des ToggleButton
	        ToggleButton btnMembres = new ToggleButton(ICONE_MEMBRES + "  Gestion des membres");
	        btnMembres.setMaxWidth(Double.MAX_VALUE);
	        btnMembres.setStyle(STYLE_BOUTON_NORMAL);
	        
	        ToggleButton btnReunions = new ToggleButton(ICONE_REUNION + "  Réunions");
	        btnReunions.setMaxWidth(Double.MAX_VALUE);
	        btnReunions.setStyle(STYLE_BOUTON_NORMAL);
	        
	        ToggleButton btnDemandesPrets = new ToggleButton(ICONE_PRET + "  Demandes de prêts");
	        btnDemandesPrets.setMaxWidth(Double.MAX_VALUE);
	        btnDemandesPrets.setStyle(STYLE_BOUTON_NORMAL);
	        
	        ToggleButton btnComite = new ToggleButton(ICONE_COMITE + "  Comité de gestion");
	        btnComite.setMaxWidth(Double.MAX_VALUE);
	        btnComite.setStyle(STYLE_BOUTON_NORMAL);
	        
	        ToggleButton btnAmendes = new ToggleButton(ICONE_AMENDES + "  Gestion des amendes");
	        btnAmendes.setMaxWidth(Double.MAX_VALUE);
	        btnAmendes.setStyle(STYLE_BOUTON_NORMAL);
	        
	        ToggleButton btnListeMembres = new ToggleButton(ICONE_LISTE + "  Liste des membres");
	        btnListeMembres.setMaxWidth(Double.MAX_VALUE);
	        btnListeMembres.setStyle(STYLE_BOUTON_NORMAL);
	        
	        // ✅ Ajout des effets de survol
	        ToggleButton[] allButtons = {btnMembres, btnReunions, btnDemandesPrets, btnComite, btnAmendes, btnListeMembres};
	        
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
	        btnMembres.setOnAction(e -> {
	            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
	            btnMembres.setStyle(STYLE_BOUTON_ACTIF);
	            showMembres();
	        });
	        
	        btnReunions.setOnAction(e -> {
	            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
	            btnReunions.setStyle(STYLE_BOUTON_ACTIF);
	            showGestionReunion();
	        });
	        
	        btnDemandesPrets.setOnAction(e -> {
	            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
	            btnDemandesPrets.setStyle(STYLE_BOUTON_ACTIF);
	            showDemandesPrets();
	        });
	        
	        btnComite.setOnAction(e -> {
	            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
	            btnComite.setStyle(STYLE_BOUTON_ACTIF);
	            showComite();
	        });
	        
	        btnAmendes.setOnAction(e -> {
	            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
	            btnAmendes.setStyle(STYLE_BOUTON_ACTIF);
	            showAmendes();
	        });
	        
	        btnListeMembres.setOnAction(e -> {
	            resetAllButtonsStyle(allButtons, STYLE_BOUTON_NORMAL);
	            btnListeMembres.setStyle(STYLE_BOUTON_ACTIF);
	            showMembre();
	        });
	        
	        // ✅ Groupe de toggle (un seul sélectionné à la fois)
	        ToggleGroup group = new ToggleGroup();
	        for (ToggleButton btn : allButtons) {
	            btn.setToggleGroup(group);
	        }
	        
	        // ✅ Sélectionner le premier bouton par défaut
	        btnMembres.setSelected(true);
	        btnMembres.setStyle(STYLE_BOUTON_ACTIF);
	        
	        menuBox.getChildren().addAll(btnMembres, btnReunions, btnDemandesPrets, btnComite, btnAmendes, btnListeMembres);
	        
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
	    
	
	    private void showMembre() {
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
	            List<Membre> membres = membreService.getMembresByAvecId(president.getAvecId());
	            table.setItems(FXCollections.observableArrayList(membres));
	        } catch (SQLException e) {
	        	showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
	        }
	        
	        view.getChildren().addAll(title, table);
	        root.setCenter(view);
		}


private void showGestionReunion() {
		if (president != null && president.getAvecId() != null) {
			System.out.println(">>> President avecId: " + president.getAvecId());
			ReunionView rv = new ReunionView(president.getAvecId());
			rv.afficher();
		} else {
			showAlert("Erreur", "Aucune AVEC associée au président\n" +
				"-president: " + president + "\n" +
				"-avecId: " + (president != null ? president.getAvecId() : "null"));
		}
	}

	/**
	 * ✅ Gestion des demandes de prêts
	 */
	private void showDemandesPrets() {
		VBox view = new VBox(15);
		view.setPadding(new Insets(20));

		Label title = new Label("Demandes de prêts en attente");
		title.setStyle(Styles.TITRE_PRINCIPAL);

		TableView<Pret> table = new TableView<>();
		table.setPrefHeight(400);

		TableColumn<Pret, String> colNumero = new TableColumn<>("N° Prêt");
		colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroPret"));
		colNumero.setPrefWidth(120);

		TableColumn<Pret, String> colMembre = new TableColumn<>("Membre");
		colMembre.setCellValueFactory(cellData -> {
			if (cellData.getValue().getEmprunteur() != null) {
				return new javafx.beans.property.SimpleStringProperty(
					cellData.getValue().getEmprunteur().getNomComplet());
			}
			return new javafx.beans.property.SimpleStringProperty("-");
		});
		colMembre.setPrefWidth(150);

		TableColumn<Pret, String> colMontant = new TableColumn<>("Montant");
		colMontant.setCellValueFactory(cellData -> {
			java.math.BigDecimal montant = cellData.getValue().getMontantInitial();
			String formatted = montant != null ? montant.toString() + " XAF" : "-";
			return new javafx.beans.property.SimpleStringProperty(formatted);
		});
		colMontant.setPrefWidth(120);

		TableColumn<Pret, String> colDuree = new TableColumn<>("Durée");
		colDuree.setCellValueFactory(cellData -> 
			new javafx.beans.property.SimpleStringProperty(
				cellData.getValue().getDureeEnSemaines() + " sem.")
		);
		colDuree.setPrefWidth(80);

		TableColumn<Pret, Void> colAction = new TableColumn<>("Actions");
		colAction.setPrefWidth(200);
		colAction.setCellFactory(col -> new javafx.scene.control.TableCell<Pret, Void>() {
			private final HBox buttons = new HBox(5);
			private final Button btnApprouver = new Button("✅ Approuver");
			private final Button btnRejeter = new Button("❌ Rejeter");

			{
				btnApprouver.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 5 10;");
				btnRejeter.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 10;");
				
				btnApprouver.setOnAction(e -> {
					Pret pret = getTableView().getItems().get(getIndex());
					approuverDemandePret(pret);
				});
				
				btnRejeter.setOnAction(e -> {
					Pret pret = getTableView().getItems().get(getIndex());
					rejeterDemandePret(pret);
				});
				
				buttons.getChildren().addAll(btnApprouver, btnRejeter);
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : buttons);
			}
		});

		table.getColumns().addAll(colNumero, colMembre, colMontant, colDuree, colAction);

		try {
			List<Pret> demandes = pretService.listerDemandesEnAttenteParAvecId(president.getAvecId());
			
			// Charger les membres emprunteurs
			for (Pret pret : demandes) {
				if (pret.getEmprunteurId() != null) {
					try {
						Membre membre = membreService.getMembreById(pret.getEmprunteurId());
						pret.setEmprunteur(membre);
					} catch (Exception e) {
						System.err.println("Erreur chargement membre: " + e.getMessage());
					}
				}
			}
			
			table.setItems(javafx.collections.FXCollections.observableArrayList(demandes));
			
			if (demandes.isEmpty()) {
				Label emptyLabel = new Label("Aucune demande de prêt en attente");
				emptyLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
				view.getChildren().add(emptyLabel);
			}
		} catch (Exception e) {
			showAlert("Erreur", "Impossible de charger les demandes: " + e.getMessage());
			e.printStackTrace();
		}

		view.getChildren().addAll(title, table);
		root.setCenter(view);
	}

	private void approuverDemandePret(Pret pret) {
		// Vérifier que le prêt a une réunion de décaissement associée
		if (pret.getReunionDecaissementId() == null) {
			showAlert("Erreur", "Aucune réunion associée à cette demande de prêt.");
			return;
		}
		
		String nomMembre = (pret.getEmprunteur() != null) ? pret.getEmprunteur().getNomComplet() : "Membre #" + pret.getEmprunteurId();
		
		boolean confirm = showConfirmation("Confirmer", "Approuver la demande",
			"Êtes-vous sûr d'approuver ce prêt de " + pret.getMontantInitial() + " XAF pour " +
			nomMembre + "?");
		
		if (confirm) {
			// Utiliser la réunion déjà associée au prêt
			boolean success = pretService.approuverPret(pret.getId(), pret.getReunionDecaissementId(), president.getId());
			if (success) {
				showInfo("Succès", "Prêt approuvé avec succès!");
				showDemandesPrets();
			} else {
				showAlert("Erreur", "Impossible d'approuver le prêt");
			}
		}
	}

	private void rejeterDemandePret(Pret pret) {
		String nomMembre = (pret.getEmprunteur() != null) ? pret.getEmprunteur().getNomComplet() : "Membre #" + pret.getEmprunteurId();
		
		boolean confirm = showConfirmation("Confirmer", "Rejeter la demande",
			"Êtes-vous sûr de rejeter ce prêt pour " + nomMembre + "?");
		
		if (confirm) {
			boolean success = pretService.rejeterPret(pret.getId());
			if (success) {
				showInfo("Succès", "Demande de prêt rejetée");
				showDemandesPrets();
			} else {
				showAlert("Erreur", "Impossible de rejeter le prêt");
			}
		}
	}

	private boolean showConfirmation(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		Optional<ButtonType> result = alert.showAndWait();
		return result.isPresent() && result.get() == ButtonType.OK;
	}

	/**
	 * ✅ Gestion des membres
	 */
	private void showMembres() {
		VBox view = new VBox(15);
		view.setPadding(new Insets(20));

		Label title = new Label("Gestion des membres");
		title.setStyle(Styles.TITRE_PRINCIPAL);

		HBox toolbar = new HBox(10);

		Button ajouterBtn = new Button("➕ Ajouter un membre");
		ajouterBtn.setStyle(Styles.BOUTON_PRINCIPAL);
		ajouterBtn.setOnAction(e -> showAjoutMembre());

		Button modifierBtn = new Button("✏️ Modifier rôle");
		modifierBtn.setStyle(Styles.BOUTON_SECONDAIRE);
		modifierBtn.setOnAction(e -> modifierRoleMembre());

		Button desactiverBtn = new Button("🗑️ Désactiver");
		desactiverBtn
				.setStyle("-fx-background-color: " + Styles.ROUGE_ERREUR + "; -fx-text-fill: white; -fx-cursor: hand;");
		desactiverBtn.setOnAction(e -> desactiverMembre());

		Button actualiserBtn = new Button("🔄 Actualiser");
		actualiserBtn.setStyle(Styles.BOUTON_ACCENT);
		actualiserBtn.setOnAction(e -> chargerMembres());

		toolbar.getChildren().addAll(ajouterBtn, modifierBtn, desactiverBtn, actualiserBtn);

		membresTable = new TableView<>();
		membresTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// ✅ Colonnes correspondant aux champs de la base
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

		TableColumn<Membre, String> colRole = new TableColumn<>("Rôle Comité");
		colRole.setCellValueFactory(cellData -> {
			Membre m = cellData.getValue();
			String role = m.getRoleComite() != null ? m.getRoleComite().getDescription() : "Aucun";
			return new javafx.beans.property.SimpleStringProperty(role);
		});
		colRole.setPrefWidth(120);

		TableColumn<Membre, String> colCle = new TableColumn<>("Rôle Clé");
		colCle.setCellValueFactory(cellData -> {
			Membre m = cellData.getValue();
			String role = m.getRoleCle() != null ? m.getRoleCle().getLibelle() : "Aucun";
			return new javafx.beans.property.SimpleStringProperty(role);
		});
		colCle.setPrefWidth(100);

		TableColumn<Membre, String> colStatut = new TableColumn<>("Statut");
		colStatut.setCellValueFactory(cellData -> {
			Membre m = cellData.getValue();
			String statut = m.getEstActif() != null ? m.getEstActif().getLibelle() : "Inactif";
			return new javafx.beans.property.SimpleStringProperty(statut);
		});
		colStatut.setPrefWidth(80);

		TableColumn<Membre, String> colDate = new TableColumn<>("Date adhésion");
		colDate.setCellValueFactory(cellData -> {
			Membre m = cellData.getValue();
			String date = m.getDateAdhesion() != null
					? m.getDateAdhesion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
					: "";
			return new javafx.beans.property.SimpleStringProperty(date);
		});
		colDate.setPrefWidth(100);

		membresTable.getColumns().addAll(colId, colNom, colPrenom, colCarte, colRole, colCle, colStatut, colDate);

		chargerMembres();

		view.getChildren().addAll(title, toolbar, membresTable);
		VBox.setVgrow(membresTable, Priority.ALWAYS);

		root.setCenter(view);
	}
	
	/**
	 * Affiche la vue de gestion des amendes
	 */
	private void showAmendes() {
	    VBox view = new VBox(15);
	    view.setPadding(new Insets(20));
	    
	    Label title = new Label("Gestion des amendes");
	    title.setStyle(Styles.TITRE_PRINCIPAL);
	    
	    // Sélection de la réunion
	    HBox reunionBox = new HBox(10);
	    reunionBox.setAlignment(Pos.CENTER_LEFT);
	    
	    Label reunionLabel = new Label("Sélectionner une réunion:");
	    reunionLabel.setStyle("-fx-font-weight: bold;");
	    
	    ComboBox<Reunion> reunionCombo = new ComboBox<>();
	    reunionCombo.setPromptText("Choisir une réunion");
	    reunionCombo.setStyle(Styles.CHAMP_TEXTE);
	    reunionCombo.setPrefWidth(250);
	    
	    try {
	        // Charger les réunions de l'AVEC
	        List<Reunion> reunions = reunionService.getReunionsByAvecId(avec.getId());
	        reunionCombo.setItems(FXCollections.observableArrayList(reunions));
	    } catch (SQLException e) {
	        showAlert("Erreur", "Impossible de charger les réunions: " + e.getMessage());
	    }
	    
	    Button chargerBtn = new Button("🔍 Charger");
	    chargerBtn.setStyle(Styles.BOUTON_SECONDAIRE);
	    
	    reunionBox.getChildren().addAll(reunionLabel, reunionCombo, chargerBtn);
	    
	    // Tableau des amendes
	    TableView<Amende> amendesTable = new TableView<>();
	    amendesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	    amendesTable.setPrefHeight(300);
	    
	    TableColumn<Amende, String> colMembre = new TableColumn<>("Membre");
	    colMembre.setCellValueFactory(cellData -> {
	        Amende a = cellData.getValue();
	        if (a.getMembre() != null) {
	            return new javafx.beans.property.SimpleStringProperty(a.getMembre().getNomComplet());
	        }
	        return new javafx.beans.property.SimpleStringProperty("");
	    });
	    colMembre.setPrefWidth(150);
	    
	    TableColumn<Amende, String> colType = new TableColumn<>("Type d'infraction");
	    colType.setCellValueFactory(cellData -> {
	        Amende a = cellData.getValue();
	        if (a.getTypeInfraction() != null) {
	            return new javafx.beans.property.SimpleStringProperty(a.getTypeInfraction().getLibelle());
	        }
	        return new javafx.beans.property.SimpleStringProperty("");
	    });
	    colType.setPrefWidth(200);
	    
	    TableColumn<Amende, String> colMontant = new TableColumn<>("Montant");
	    colMontant.setCellValueFactory(cellData -> {
	        Amende a = cellData.getValue();
	        return new javafx.beans.property.SimpleStringProperty(a.getMontantFormatted());
	    });
	    colMontant.setPrefWidth(100);
	    
	    TableColumn<Amende, String> colStatut = new TableColumn<>("Statut");
	    colStatut.setCellValueFactory(cellData -> {
	        Amende a = cellData.getValue();
	        String statut = a.getEstPaye() ? "✅ Payé" : "⏳ Non payé";
	        return new javafx.beans.property.SimpleStringProperty(statut);
	    });
	    colStatut.setPrefWidth(100);
	    
	    TableColumn<Amende, String> colDate = new TableColumn<>("Date paiement");
	    colDate.setCellValueFactory(cellData -> {
	        Amende a = cellData.getValue();
	        return new javafx.beans.property.SimpleStringProperty(a.getDatePaiementFormatted());
	    });
	    colDate.setPrefWidth(120);
	    
	    TableColumn<Amende, String> colAction = new TableColumn<>("Action");
	    colAction.setCellFactory(col -> new TableCell<Amende, String>() {
	        @Override
	        protected void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty);
	            if (empty) {
	                setGraphic(null);
	            } else {
	                Amende amende = getTableView().getItems().get(getIndex());
	                if (!amende.getEstPaye()) {
	                    Button payerBtn = new Button("💰 Marquer payé");
	                    payerBtn.setStyle(Styles.BOUTON_ACCENT);
	                    payerBtn.setOnAction(e -> marquerAmendePayee(amende, amendesTable));
	                    setGraphic(payerBtn);
	                } else {
	                    Label payeLabel = new Label("Payé");
	                    payeLabel.setStyle("-fx-text-fill: " + Styles.VERT_PRINCIPAL + "; -fx-font-weight: bold;");
	                    setGraphic(payeLabel);
	                }
	            }
	        }
	    });
	    colAction.setPrefWidth(120);
	    
	    amendesTable.getColumns().addAll(colMembre, colType, colMontant, colStatut, colDate, colAction);
	    
	    // Formulaire pour ajouter une nouvelle amende
	    TitledPane ajoutPane = new TitledPane();
	    ajoutPane.setText("➕ Ajouter une nouvelle amende");
	    ajoutPane.setExpanded(false);
	    
	    GridPane form = new GridPane();
	    form.setHgap(15);
	    form.setVgap(10);
	    form.setPadding(new Insets(15));
	    
	    // Sélection du membre
	    Label membreAmendeLabel = new Label("Membre:");
	    membreAmendeLabel.setStyle("-fx-font-weight: bold;");
	    ComboBox<Membre> membreAmendeCombo = new ComboBox<>();
	    membreAmendeCombo.setPromptText("Sélectionner un membre");
	    membreAmendeCombo.setStyle(Styles.CHAMP_TEXTE);
	    membreAmendeCombo.setPrefWidth(200);
	    
	    try {
	        List<Membre> membres = membreService.getMembresByAvecId(avec.getId());
	        membreAmendeCombo.setItems(FXCollections.observableArrayList(membres));
	    } catch (SQLException e) {
	        showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
	    }
	    
	    // Type d'infraction
	    Label typeAmendeLabel = new Label("Type d'infraction:");
	    typeAmendeLabel.setStyle("-fx-font-weight: bold;");
	    ComboBox<TypeInfraction> typeInfractionCombo = new ComboBox<>();
	    typeInfractionCombo.setPromptText("Sélectionner le type");
	    typeInfractionCombo.setStyle(Styles.CHAMP_TEXTE);
	    typeInfractionCombo.setPrefWidth(200);
	    
	    // Charger les types d'infractions
	    TypeInfractionService typeInfractionService = new TypeInfractionService();
	    List<TypeInfraction> types = typeInfractionService.getAllTypes();
	    typeInfractionCombo.setItems(FXCollections.observableArrayList(types));
	    
	    // Montant (modifiable)
	    Label montantAmendeLabel = new Label("Montant (FCFA):");
	    montantAmendeLabel.setStyle("-fx-font-weight: bold;");
	    TextField montantAmendeField = new TextField();
	    montantAmendeField.setPromptText("Montant");
	    montantAmendeField.setStyle(Styles.CHAMP_TEXTE);
	    montantAmendeField.setPrefWidth(150);
	    
	    // Remplir automatiquement le montant selon le type sélectionné
	    typeInfractionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
	        if (newVal != null && newVal.getMontantDefaut() != null) {
	            montantAmendeField.setText(newVal.getMontantDefaut().toString());
	        }
	    });
	    
	    // Observations
	    Label observationsLabel = new Label("Observations:");
	    observationsLabel.setStyle("-fx-font-weight: bold;");
	    TextArea observationsArea = new TextArea();
	    observationsArea.setPromptText("Motif de l'amende...");
	    observationsArea.setPrefRowCount(2);
	    observationsArea.setStyle(Styles.CHAMP_TEXTE);
	    
	    // Réunion (optionnel)
	    Label reunionAmendeLabel = new Label("Réunion:");
	    reunionAmendeLabel.setStyle("-fx-font-weight: bold;");
	    ComboBox<Reunion> reunionAmendeCombo = new ComboBox<>();
	    reunionAmendeCombo.setPromptText("Réunion (optionnel)");
	    reunionAmendeCombo.setStyle(Styles.CHAMP_TEXTE);
	    reunionAmendeCombo.setPrefWidth(200);
	    
	    try {
	        List<Reunion> reunions = reunionService.getReunionsByAvecId(avec.getId());
	        reunionAmendeCombo.setItems(FXCollections.observableArrayList(reunions));
	    } catch (SQLException e) {
	        showAlert("Erreur", "Impossible de charger les réunions: " + e.getMessage());
	    }
	    
	    form.add(membreAmendeLabel, 0, 0);
	    form.add(membreAmendeCombo, 1, 0);
	    form.add(typeAmendeLabel, 2, 0);
	    form.add(typeInfractionCombo, 3, 0);
	    form.add(montantAmendeLabel, 0, 1);
	    form.add(montantAmendeField, 1, 1);
	    form.add(reunionAmendeLabel, 2, 1);
	    form.add(reunionAmendeCombo, 3, 1);
	    form.add(observationsLabel, 0, 2);
	    form.add(observationsArea, 1, 2, 3, 1);
	    
	    Button ajouterAmendeBtn = new Button("➕ Ajouter l'amende");
	    ajouterAmendeBtn.setStyle(Styles.BOUTON_PRINCIPAL);
	    ajouterAmendeBtn.setOnAction(e -> ajouterAmende(
	        membreAmendeCombo, typeInfractionCombo, montantAmendeField, 
	        observationsArea, reunionAmendeCombo, amendesTable
	    ));
	    
	    form.add(ajouterAmendeBtn, 3, 3);
	    
	    ajoutPane.setContent(form);
	    
	    // Charger les amendes quand une réunion est sélectionnée
	    chargerBtn.setOnAction(e -> {
	        Reunion selectedReunion = reunionCombo.getValue();
	        if (selectedReunion != null) {
	            chargerAmendes(selectedReunion.getId(), amendesTable);
	        } else {
	            chargerToutesAmendes(amendesTable);
	        }
	    });
	    
	    view.getChildren().addAll(title, reunionBox, ajoutPane, amendesTable);
	    VBox.setVgrow(amendesTable, Priority.ALWAYS);
	    
	    root.setCenter(view);
	}

	/**
	 * Ajoute une nouvelle amende
	 */
	private void ajouterAmende(ComboBox<Membre> membreCombo, ComboBox<TypeInfraction> typeCombo,
	                           TextField montantField, TextArea observationsArea,
	                           ComboBox<Reunion> reunionCombo, TableView<Amende> table) {
	    try {
	        Membre membre = membreCombo.getValue();
	        TypeInfraction type = typeCombo.getValue();
	        
	        if (membre == null || type == null) {
	            showAlert("Erreur", "Veuillez sélectionner un membre et un type d'infraction");
	            return;
	        }
	        
	        String montantText = montantField.getText().trim();
	        if (montantText.isEmpty()) {
	            showAlert("Erreur", "Veuillez saisir un montant");
	            return;
	        }
	        
	        BigDecimal montant = new BigDecimal(montantText);
	        Reunion reunion = reunionCombo.getValue();
	        String observations = observationsArea.getText().trim();
	        
	        AmendeService amendeService = new AmendeService();
	        Amende amende;
	        
	        if (reunion != null) {
	            amende = amendeService.creerAmende(membre, reunion, type, montant, observations);
	        } else {
	            amende = amendeService.creerAmende(membre, null, type, montant, observations);
	        }
	        
	        showInfo("Succès", "Amende ajoutée avec succès!\n" +
	                "Membre: " + membre.getNomComplet() + "\n" +
	                "Type: " + type.getLibelle() + "\n" +
	                "Montant: " + String.format("%,.0f FCFA", montant));
	        
	        // Réinitialiser le formulaire
	        membreCombo.setValue(null);
	        typeCombo.setValue(null);
	        montantField.clear();
	        observationsArea.clear();
	        reunionCombo.setValue(null);
	        
	        // Rafraîchir le tableau
	        chargerToutesAmendes(table);
	        
	    } catch (NumberFormatException e) {
	        showAlert("Erreur", "Montant invalide");
	    } catch (SQLException e) {
	        showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage());
	    }
	}

	/**
	 * Marque une amende comme payée
	 */
	private void marquerAmendePayee(Amende amende, TableView<Amende> table) {
	    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
	    confirm.setTitle("Confirmation");
	    confirm.setHeaderText("Marquer l'amende comme payée");
	    confirm.setContentText("Confirmez-vous que cette amende a été payée ?");
	    
	    confirm.showAndWait().ifPresent(response -> {
	        if (response == ButtonType.OK) {
	            try {
	                AmendeService amendeService = new AmendeService();
	                if (amendeService.marquerPayee(amende.getId())) {
	                    showInfo("Succès", "Amende marquée comme payée!");
	                    chargerToutesAmendes(table);
	                } else {
	                    showAlert("Erreur", "Échec du paiement");
	                }
	            } catch (SQLException e) {
	                showAlert("Erreur", "Erreur: " + e.getMessage());
	            }
	        }
	    });
	}

	/**
	 * Charge les amendes d'une réunion spécifique
	 */
	private void chargerAmendes(Long reunionId, TableView<Amende> table) {
	    try {
	        AmendeService amendeService = new AmendeService();
	        List<Amende> amendes = amendeService.getAmendesByReunion(reunionId);
	        table.setItems(FXCollections.observableArrayList(amendes));
	        
	        // Calculer le total
	        BigDecimal total = BigDecimal.ZERO;
	        for (Amende a : amendes) {
	            if (!a.getEstPaye()) {
	                total = total.add(a.getMontant());
	            }
	        }
	        
	        if (total.compareTo(BigDecimal.ZERO) > 0) {
	            showInfo("Information", "Total des amendes non payées: " + 
	                    String.format("%,.0f FCFA", total));
	        }
	        
	    } catch (SQLException e) {
	        showAlert("Erreur", "Impossible de charger les amendes: " + e.getMessage());
	    }
	}

	/**
	 * Charge toutes les amendes de l'AVEC
	 */
	private void chargerToutesAmendes(TableView<Amende> table) {
	    try {
	        AmendeService amendeService = new AmendeService();
	        List<Amende> amendes = new ArrayList<>();
	        
	        // Récupérer tous les membres et leurs amendes
	        List<Membre> membres = membreService.getMembresByAvecId(avec.getId());
	        for (Membre membre : membres) {
	            amendes.addAll(amendeService.getAmendesByMembre(membre.getId()));
	        }
	        
	        table.setItems(FXCollections.observableArrayList(amendes));
	        
	    } catch (SQLException e) {
	        showAlert("Erreur", "Impossible de charger les amendes: " + e.getMessage());
	    }
	}

	/**
     * ✅ Formulaire d'ajout de membre (uniquement les champs de la base)
     */
    private void showAjoutMembre() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un membre");
        dialog.setHeaderText("Ajouter un nouveau membre à l'AVEC");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);
        
        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        nomField.setStyle(Styles.CHAMP_TEXTE);
        
        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");
        prenomField.setStyle(Styles.CHAMP_TEXTE);
        

        
        TextField telephoneField = new TextField();
        telephoneField.setPromptText("Téléphone");
        telephoneField.setStyle(Styles.CHAMP_TEXTE);
        
        content.getChildren().addAll(
            new Label("Nom:"), nomField,
            new Label("Prénom:"), prenomField,
           
            new Label("Téléphone:"),telephoneField
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    String nom = nomField.getText().trim();
                    String prenom = prenomField.getText().trim();
                    
                    String telephone = telephoneField.getText().trim();
                    
                    if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()) {
                        showAlert("Erreur", "Tous les champs sont obligatoires");
                        return null;
                    }
                    
                    Membre membre = membreService.creerMembreSimple(
                        nom, prenom, avec.getId(), telephone
                    );
                    
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

	/**
	 * ✅ Modifier le rôle d'un membre
	 */
	private void modifierRoleMembre() {
		Membre selected = membresTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Sélection requise", "Veuillez sélectionner un membre à modifier.");
			return;
		}

		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Modifier le rôle");
		dialog.setHeaderText("Modifier le rôle de " + selected.getNomComplet());

		VBox content = new VBox(10);
		content.setPadding(new Insets(20));
		content.setPrefWidth(300);

		ComboBox<RoleComite> roleCombo = new ComboBox<>();
		roleCombo.setItems(FXCollections.observableArrayList(RoleComite.values()));
		roleCombo.setValue(selected.getRoleComite());
		roleCombo.setStyle(Styles.CHAMP_TEXTE);

		ComboBox<StatutMembre> statutCombo = new ComboBox<>();
		statutCombo.setItems(FXCollections.observableArrayList(StatutMembre.values()));
		statutCombo.setValue(selected.getEstActif());
		statutCombo.setStyle(Styles.CHAMP_TEXTE);

		content.getChildren().addAll(new Label("Rôle Comité:"), roleCombo, new Label("Statut:"), statutCombo);

		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				try {
					selected.setRoleComite(roleCombo.getValue());
					selected.setEstActif(statutCombo.getValue());

					if (membreService.modifierMembre(selected)) {
						showInfo("Succès", "Rôle modifié avec succès!");
						chargerMembres();
					} else {
						showAlert("Erreur", "Échec de la modification");
					}
				} catch (SQLException e) {
					showAlert("Erreur", "Erreur: " + e.getMessage());
				}
			}
			return null;
		});

		dialog.showAndWait();
	}

	/**
	 * ✅ Désactiver un membre
	 */
	private void desactiverMembre() {
		Membre selected = membresTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Sélection requise", "Veuillez sélectionner un membre à désactiver.");
			return;
		}

		if (selected.getEstActif() == StatutMembre.INACTIF) {
			showAlert("Information", "Ce membre est déjà inactif.");
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Confirmation");
		confirm.setHeaderText("Désactiver le membre");
		confirm.setContentText("Êtes-vous sûr de vouloir désactiver " + selected.getNomComplet() + " ?");

		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try {
					if (membreService.desactiverMembre(selected.getId())) {
						showInfo("Succès", "Membre désactivé avec succès!");
						chargerMembres();
					} else {
						showAlert("Erreur", "Échec de la désactivation");
					}
				} catch (SQLException | IllegalStateException e) {
					showAlert("Erreur", "Erreur: " + e.getMessage());
				}
			}
		});
	}

	/**
	 * ✅ Afficher le comité de gestion
	 */
	private void showComite() {
		VBox view = new VBox(15);
		view.setPadding(new Insets(20));

		Label title = new Label("Comité de gestion");
		title.setStyle(Styles.TITRE_PRINCIPAL);

		try {
			List<Membre> comite = membreService.getComiteGestion(avec.getId());

			GridPane grid = new GridPane();
			grid.setHgap(20);
			grid.setVgap(15);
			grid.setPadding(new Insets(20));
			grid.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-background-radius: 10;");

			Label roleHeader = new Label("Rôle");
			roleHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");
			Label nomHeader = new Label("Nom");
			nomHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");

			grid.add(roleHeader, 0, 0);
			grid.add(nomHeader, 1, 0);

			int row = 1;
			for (Membre m : comite) {
				Label roleLabel = new Label(m.getRoleComite().getDescription());
				roleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.BLEU_SECONDAIRE + ";");

				Label nomLabel = new Label(m.getNomComplet());

				grid.add(roleLabel, 0, row);
				grid.add(nomLabel, 1, row);

				row++;
			}

			if (comite.isEmpty()) {
				Label emptyLabel = new Label("Aucun comité élu pour le moment.");
				emptyLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + ";");
				grid.add(emptyLabel, 0, 1, 2, 1);
			}

			view.getChildren().addAll(title, grid);

		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de charger le comité: " + e.getMessage());
		}

		root.setCenter(view);
	}

	private void chargerMembres() {
		try {
			List<Membre> membres = membreService.getMembresByAvecId(avec.getId());
			membresTable.setItems(FXCollections.observableArrayList(membres));
		} catch (SQLException e) {
			showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
		}
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