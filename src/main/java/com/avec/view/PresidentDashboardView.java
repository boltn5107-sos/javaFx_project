package com.avec.view;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
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
	private TableView<Membre> amendeTable;

	private static final String ICONE_MEMBRES = "👥";
	private static final String ICONE_COMITE = "👤";
	private static final String ICONE_DECONNEXION = "🚪";
	private static final String ICONE_AMENDES = "💰";

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
		sidebar.setStyle("-fx-background-color: " + Styles.BLANC + ";" + "-fx-border-color: " + Styles.GRIS_CLAIR + ";"
				+ "-fx-border-width: 0 2 0 0;");

		VBox profileBox = new VBox(10);
		profileBox.setAlignment(Pos.CENTER);
		profileBox.setPadding(new Insets(0, 0, 20, 0));
		profileBox.setStyle("-fx-border-color: " + Styles.GRIS_CLAIR + ";" + "-fx-border-width: 0 0 2 0;");

		Label avatarLabel = new Label("👑");
		avatarLabel.setStyle("-fx-font-size: 48px;");

		Label nameLabel = new Label(president.getNomComplet());
		nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

		profileBox.getChildren().addAll(avatarLabel, nameLabel);

		VBox menuBox = new VBox(5);
		menuBox.setPadding(new Insets(20, 0, 0, 0));

		menuBox.getChildren().addAll(createMenuButton(ICONE_MEMBRES, "Gestion des membres", this::showMembres),
				createMenuButton(ICONE_COMITE, "Comité de gestion", this::showComite),
				createMenuButton(ICONE_AMENDES, "Gestion des amendes", this::showAmende),
				createMenuButton(ICONE_AMENDES, "Liste des amendes", this::showAmendes),
	            createMenuButton(ICONE_MEMBRES, "Liste des membres", this::showMembre));
		
		// Dans le header de chaque dashboard
		Button btnChangerMdp = new Button("🔒 Changer mot de passe");
		btnChangerMdp.setStyle(Styles.BOUTON_ACCENT);
		btnChangerMdp.setOnAction(e -> showChangerMotDePasse());

		

		sidebar.getChildren().addAll(profileBox, menuBox, btnChangerMdp);

		return sidebar;
	}

	private Button createMenuButton(String icon, String text, Runnable action) {
		Button button = new Button(icon + "  " + text);
		button.setStyle(
				"-fx-background-color: transparent; " + "-fx-text-fill: " + Styles.NOIR + "; " + "-fx-font-size: 14px; "
						+ "-fx-padding: 10 15; " + "-fx-alignment: CENTER_LEFT; " + "-fx-cursor: hand;");
		button.setMaxWidth(Double.MAX_VALUE);
		button.setOnAction(e -> action.run());
		return button;
	}
	
	private void showAmendes() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Gestion des amendes");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        // Tableau des amendes
        TableView<Object> amendesTable = new TableView<>();
        
        TableColumn<Object, String> colDate = new TableColumn<>("Date");
        colDate.setPrefWidth(100);
        TableColumn<Object, String> colMembre = new TableColumn<>("Membre");
        colMembre.setPrefWidth(150);
        TableColumn<Object, String> colMotif = new TableColumn<>("Motif");
        colMotif.setPrefWidth(200);
        TableColumn<Object, String> colMontant = new TableColumn<>("Montant");
        colMontant.setPrefWidth(100);
        TableColumn<Object, String> colStatut = new TableColumn<>("Statut");
        colStatut.setPrefWidth(80);
        
        amendesTable.getColumns().addAll(colDate, colMembre, colMotif, colMontant, colStatut);
        
        view.getChildren().addAll(title, amendesTable);
        root.setCenter(view);
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

	/**
	 * ✅ Gestion des membres - UNIQUEMENT les champs de la base
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
	
	private void showAmende() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label title = new Label("Gestion des Amendes");
        title.setStyle(Styles.TITRE_PRINCIPAL);
        
        // Sélection de la date
        HBox dateBox = new HBox(10);
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(Styles.CHAMP_TEXTE);
        
        Button chargerButton = new Button("Charger");
        chargerButton.setStyle(Styles.BOUTON_PRINCIPAL);
        
        dateBox.getChildren().addAll(new Label("Date de la réunion:"), datePicker, chargerButton);
        
        // Tableau des Amendes
        amendeTable = new TableView<>();
        amendeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Membre, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(150);
        
        TableColumn<Membre, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(150);
        
        TableColumn<Membre, Boolean> colRetard = new TableColumn<>("Retard");
        colRetard.setCellFactory(col -> new TableCell<Membre, Boolean>() {
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
        colRetard.setPrefWidth(80);
        
        
        TableColumn<Membre, String> colAmende = new TableColumn<>("Amende");
        colAmende.setCellFactory(col -> new TableCell<Membre, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TextField textField = new TextField(item);
                    textField.setPromptText("Montant");
                    setGraphic(textField);
                }
            }
        });
        colAmende.setPrefWidth(100);
        
        amendeTable.getColumns().addAll(colNom, colPrenom, colRetard, colAmende);
        
        // Charger les membres
        try {
            List<Membre> membres = membreService.getMembresByAvecId(president.getAvecId());
            amendeTable.setItems(FXCollections.observableArrayList(membres));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les membres: " + e.getMessage());
        }
        
     // Bouton enregistrer
        Button saveButton = new Button("💾 Enregistrer les amendes");
        saveButton.setStyle(Styles.BOUTON_PRINCIPAL);
        saveButton.setPrefWidth(300);
        
        saveButton.setOnAction(e -> {
            // TODO: Sauvegarder les présences dans la base
            showInfo("Succès", "Amendes enregistrées avec succès!");
        });
        
        view.getChildren().addAll(title, dateBox, amendeTable,saveButton);
        root.setCenter(view);
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