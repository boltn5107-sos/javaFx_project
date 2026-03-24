package com.avec.view;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.AgentTerrain;
import com.avec.model.AgentVillageois;
import com.avec.model.Membre;
import com.avec.model.SessionUtilisateur;
import com.avec.model.Utilisateur;
import com.avec.service.AgentTerrainService;
import com.avec.service.AgentVillageoisService;
import com.avec.service.MembreService;
import com.avec.service.UtilisateurService;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class LoginView {

	private MainApp mainApp;
	private UtilisateurService utilisateurService;
	private VBox root;

	private MembreService membreService;
	private AgentTerrainService agentTerrainService;
	private AgentVillageoisService agentVillageoisService;

	private ComboBox<String> roleComboBox;
	private TextField emailField;
	private PasswordField passwordField;
	private TextField numeroCarteField;
	
	private StackPane formContainer;
	private Button loginButton;

	public LoginView(MainApp mainApp) {
		this.mainApp = mainApp;
		this.utilisateurService = new UtilisateurService();
		this.membreService = new MembreService();
		this.agentTerrainService = new AgentTerrainService();
		this.agentVillageoisService = new AgentVillageoisService();
		createView();
	}

	private void createView() {
		// Conteneur principal avec dégradé Vert/Bleu
		root = new VBox(20);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(30));
		root.setStyle("-fx-background-color: " + Styles.BACKGROUND_GRADIENT);

		// Panneau de connexion
		VBox loginPanel = new VBox(20);
		loginPanel.setAlignment(Pos.CENTER);
		loginPanel.setMaxWidth(400);
		loginPanel.setStyle(Styles.PANEL_STYLE);
		loginPanel.setEffect(Styles.getOmbreMoyenne());

		// Icône (représentant la communauté)
		Label iconLabel = new Label("🤝");
		iconLabel.setStyle("-fx-font-size: 64px;");

		// Titre
		Label titleLabel = new Label("GESTION AVEC");
		titleLabel.setStyle(Styles.TITRE_PRINCIPAL);

		// Sous-titre
		Label subtitleLabel = new Label("Association Villageoise d'Épargne et de Crédit");
		subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Styles.GRIS_FONCE + ";");
		subtitleLabel.setWrapText(true);
		subtitleLabel.setTextAlignment(TextAlignment.CENTER);

		// Sélecteur de rôle
		VBox roleBox = new VBox(5);
		roleBox.setAlignment(Pos.CENTER_LEFT);
		roleBox.setMaxWidth(350);

		Label roleLabel = new Label("Connectez-vous en tant que:");
		roleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Styles.VERT_PRINCIPAL + ";");

		roleComboBox = new ComboBox<>();
		roleComboBox.getItems().addAll(

				"Administrateur", "Président", "Secrétaire", "Trésorier", "Compteur", "Agent Villageois",
				"Agent de Terrain");

		roleComboBox.setValue("Administrateur");
		roleComboBox.setMaxWidth(350);
		roleComboBox.setStyle(Styles.CHAMP_TEXTE);
		roleComboBox.setOnAction(e -> changerFormulaire());

		roleBox.getChildren().addAll(roleLabel, roleComboBox);

		// Conteneur pour les formulaires dynamiques
		formContainer = new StackPane();
		formContainer.setPrefHeight(200);

		// Formulaire par défaut (admin)
		formContainer.getChildren().add(createAdminForm());

		// Champs de saisie

		// Effet focus
		emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				emailField.setStyle(Styles.CHAMP_TEXTE_FOCUS);
			} else {
				emailField.setStyle(Styles.CHAMP_TEXTE);
			}
		});

		passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				passwordField.setStyle(Styles.CHAMP_TEXTE_FOCUS);
			} else {
				passwordField.setStyle(Styles.CHAMP_TEXTE);
			}
		});

		// Bouton de connexion
		loginButton = new Button("SE CONNECTER");
		loginButton.setMaxWidth(300);
		loginButton.setStyle(Styles.BOUTON_PRINCIPAL);

		// Effets de survol
		loginButton.setOnMouseEntered(e -> loginButton.setStyle(Styles.BOUTON_PRINCIPAL_HOVER));
		loginButton.setOnMouseExited(e -> loginButton.setStyle(Styles.BOUTON_PRINCIPAL));
		

		// Label pour les messages
		Label messageLabel = new Label();
		messageLabel.setWrapText(true);
		messageLabel.setTextAlignment(TextAlignment.CENTER);
		messageLabel.setMaxWidth(350);
		messageLabel.setStyle(Styles.MESSAGE_ERREUR);
        messageLabel.setText("");
        
        
        final Label finalMessageLabel = messageLabel;
        
        loginButton.setOnAction(e ->{ System.out.println("Bouton cliqué !");login(finalMessageLabel);});

		// Pied de page
		HBox footerBox = new HBox(20);
		footerBox.setAlignment(Pos.CENTER);

		Label contactLabel = new Label("📞 +221 77 777 77 77");
		contactLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + "; -fx-font-size: 10px;");

		Label emailLabel = new Label("✉ avec@uadb.edu.sn");
		emailLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + "; -fx-font-size: 10px;");

		footerBox.getChildren().addAll(contactLabel, emailLabel);

	

		// Assemblage
		loginPanel.getChildren().addAll(iconLabel, titleLabel, subtitleLabel, roleBox,formContainer, loginButton, messageLabel,
				footerBox);

		root.getChildren().add(loginPanel);
		
		System.out.println("messageLabel initialisé: " + (messageLabel != null));
	}

	private VBox createAdminForm() {
		VBox form = new VBox(10);
		form.setAlignment(Pos.CENTER);

		emailField = new TextField();
		emailField.setPromptText("Email");
		emailField.setMaxWidth(300);
		emailField.setStyle(Styles.CHAMP_TEXTE);

		passwordField = new PasswordField();
		passwordField.setPromptText("Mot de passe");
		passwordField.setMaxWidth(300);
		passwordField.setStyle(Styles.CHAMP_TEXTE);

		form.getChildren().addAll(emailField, passwordField);

		return form;
	}

	private VBox createMembreForm() {
		VBox form = new VBox(10);
		form.setAlignment(Pos.CENTER);

		numeroCarteField = new TextField();
		numeroCarteField.setPromptText("Numero de carte membre");
		numeroCarteField.setMaxWidth(300);
		numeroCarteField.setStyle(Styles.CHAMP_TEXTE);

		passwordField = new PasswordField();
		passwordField.setPromptText("Mot de passe");
		passwordField.setMaxWidth(300);
		passwordField.setStyle(Styles.CHAMP_TEXTE);

		form.getChildren().addAll(numeroCarteField, passwordField);

		return form;
	}

	private VBox createAgentForm() {
		VBox form = new VBox(10);
		form.setAlignment(Pos.CENTER);

		emailField = new TextField();
		emailField.setPromptText("Email");
		emailField.setMaxWidth(300);
		emailField.setStyle(Styles.CHAMP_TEXTE);

		passwordField = new PasswordField();
		passwordField.setPromptText("Mot de passe");
		passwordField.setMaxWidth(300);
		passwordField.setStyle(Styles.CHAMP_TEXTE);

		form.getChildren().addAll(emailField, passwordField);

		return form;
	}

	private void changerFormulaire() {
		String role = roleComboBox.getValue();
		formContainer.getChildren().clear();

		switch (role) {
		 case "Administrateur":
             formContainer.getChildren().add(createAdminForm());
             break;
         case "Président":
         case "Secrétaire":
         case "Trésorier":
         case "Compteur":
             formContainer.getChildren().add(createMembreForm());
             break;
         case "Agent Villageois":
         case "Agent de Terrain":
             formContainer.getChildren().add(createAgentForm());
             break;
		}
	}

	 private void login(Label messageLabel) {
	        System.out.println("login() appelé, messageLabel = " + messageLabel);
	        
	        if (messageLabel == null) {
	            System.err.println("ERREUR: messageLabel est null dans login()");
	            return;
	        }
	        
	        String role = roleComboBox.getValue();
	        System.out.println("Tentative de connexion avec rôle: " + role);
	        
	        messageLabel.setStyle(Styles.MESSAGE_ERREUR);
	        messageLabel.setText("Connexion en cours...");
	        
	        // Désactiver le bouton
	        loginButton.setDisable(true);
	        
	        // Appel direct sans délai pour éviter les problèmes de référence
	        SessionUtilisateur session = SessionUtilisateur.getInstance();
	        
	        switch (role) {
	            case "Administrateur":
	                loginAdmin(messageLabel);
	                break;
	            case "Président":
	            case "Secrétaire":
	            case "Trésorier":
	            case "Compteur":
	                //loginMembre(role);
	                break;
	            case "Agent Villageois":
	                loginAgentVillageois(messageLabel);
	                break;
	            case "Agent de Terrain":
	                loginAgentTerrain(messageLabel);
	                break;
	            default:
	                messageLabel.setText("Rôle non reconnu: " + role);
	                loginButton.setDisable(false);
	        }
	    }

	 private void loginAdmin(Label messageLabel) {
		    if (emailField == null || passwordField == null) {
		        messageLabel.setText("Erreur: Formulaire non initialisé");
		        loginButton.setDisable(false);
		        return;
		    }
		    
		    String email = emailField.getText().trim();
		    String password = passwordField.getText().trim();
		    
		    System.out.println("Tentative login admin: " + email);
		    
		    if (email.isEmpty() || password.isEmpty()) {
		        messageLabel.setText("Veuillez remplir tous les champs");
		        loginButton.setDisable(false);
		        return;
		    }
		    
		    // 🔍 RECHERCHE DANS LA TABLE UTILISATEUR
		    Utilisateur utilisateur = utilisateurService.login(email, password);
		    
		    if (utilisateur != null) {
		        System.out.println("Admin trouvé: " + utilisateur.getNom());
		        
		        // ✅ CONNEXION EN TANT QU'ADMIN
		        SessionUtilisateur.getInstance().connecterAdmin(utilisateur);
		        ouvrirDashboard(messageLabel,utilisateur);
		    } else {
		        System.out.println("Admin non trouvé");
		        messageLabel.setText("Email ou mot de passe incorrect");
		        loginButton.setDisable(false);
		    }
		}

//	private void loginMembreComite(Label messageLabel, String roleAttendu) {
//		String numeroCarte = numeroCarteField.getText().trim();
//		String password = passwordField.getText().trim();
//
//		if (numeroCarte.isEmpty() || password.isEmpty()) {
//			messageLabel.setText("Veuillez remplir tous les champs");
//			return;
//		}
//
//		try {
//			// Chercher le membre par carte
//			Membre membre = membreService.getMembreByNumeroCarte(numeroCarte);
//
//			if (membre == null) {
//				messageLabel.setText("Numéro de carte incorrect");
//				return;
//			}

//			// Vérifier que le membre est actif
//			if (membre.getStatut() != StatutMembre.ACTIF) {
//				messageLabel.setText("Ce membre est inactif");
//				return;
//			}

//			// Vérifier le rôle
//			String roleMembre = membre.getRoleComite().getDescription();
//			if (!roleMembre.equals(roleAttendu)) {
//				messageLabel.setText("Vous n'avez pas le rôle " + roleAttendu);
//				return;
//			}
//
//			// Vérifier le mot de passe (à adapter selon votre système)
//			if (verifierMotDePasseMembre(membre, password)) {
//				SessionUtilisateur.getInstance().connecterMembre(membre);
//				ouvrirDashboard();
//			} else {
//				messageLabel.setText("Mot de passe incorrect");
//			}
//
//		} catch (SQLException e) {
//			messageLabel.setText("Erreur de connexion: " + e.getMessage());
//			e.printStackTrace();
//		}
//	}
//	
	 private void loginAgentVillageois(Label messageLabel) {
	        String email = emailField.getText().trim();
	        String password = passwordField.getText().trim();
	        
	        if (email.isEmpty() || password.isEmpty()) {
	            messageLabel.setText("Veuillez remplir tous les champs");
	            return;
	        }
	        
	        AgentVillageois agent = agentVillageoisService.login(email, password);
	        
	        if (agent != null) {
	            SessionUtilisateur.getInstance().connecterAgentVillageois(agent);
	            ouvrirDashboard(messageLabel,agent);
	        } else {
	            messageLabel.setText("Email ou mot de passe incorrect");
	        }
	    }
	    
	    private void loginAgentTerrain(Label messageLabel) {
	        String email = emailField.getText().trim();
	        String password = passwordField.getText().trim();
	        
	        if (email.isEmpty() || password.isEmpty()) {
	            messageLabel.setText("Veuillez remplir tous les champs");
	            return;
	        }
	        
	        AgentTerrain agent = agentTerrainService.login(email, password);
	        
	        if (agent != null) {
	            SessionUtilisateur.getInstance().connecterAgentTerrain(agent);
	            ouvrirDashboard(messageLabel,agent);
	        } else {
	            messageLabel.setText("Email ou mot de passe incorrect");
	        }
	    }
	    
	    private void ouvrirDashboard(Label messageLabel, Utilisateur utilisateur) {
	        messageLabel.setStyle(Styles.MESSAGE_SUCCES);
	        messageLabel.setText("Connexion réussie! Chargement...");
	        
	        // Utiliser Platform.runLater pour garantir l'exécution sur le thread JavaFX
	        // ET éviter les conflits d'animation
	        Platform.runLater(() -> {
	            try {
	                DashboardView dashboard = new DashboardView(mainApp,utilisateur);
	                mainApp.getPrimaryStage().getScene().setRoot(dashboard.getRoot());
	                mainApp.getPrimaryStage().setMaximized(true);
	            } catch (Exception e) {
	                System.err.println("Erreur lors du chargement du dashboard: " + e.getMessage());
	                e.printStackTrace();
	                messageLabel.setText("Erreur: " + e.getMessage());
	                loginButton.setDisable(false);
	            }
	        });
	    }
	    

	// Méthode temporaire pour vérifier le mot de passe
	// À remplacer par votre vraie logique d'authentification
	private boolean verifierMotDePasseMembre(Membre membre, String password) {
		// TODO: Implémentez votre vérification
		// Par exemple, si vous avez un champ motDePasse dans la table Membre:
		// return password.equals(membre.getMotDePasse());

		// Pour le test, on accepte un mot de passe par défaut
		return "password".equals(password);
	}

	public VBox getRoot() {
		return root;
	}
}