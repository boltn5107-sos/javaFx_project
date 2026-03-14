package com.avec.view;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.Utilisateur;
import com.avec.service.UtilisateurService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class LoginView {

	private MainApp mainApp;
	private UtilisateurService utilisateurService;
	private VBox root;

	public LoginView(MainApp mainApp) {
		this.mainApp = mainApp;
		this.utilisateurService = new UtilisateurService();
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

		// Champs de saisie
		VBox fieldsBox = new VBox(15);
		fieldsBox.setAlignment(Pos.CENTER);

		TextField emailField = new TextField();
		emailField.setPromptText("Email");
		emailField.setMaxWidth(300);
		emailField.setStyle(Styles.CHAMP_TEXTE);

		// Effet focus
		emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				emailField.setStyle(Styles.CHAMP_TEXTE_FOCUS);
			} else {
				emailField.setStyle(Styles.CHAMP_TEXTE);
			}
		});

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Mot de passe");
		passwordField.setMaxWidth(300);
		passwordField.setStyle(Styles.CHAMP_TEXTE);

		passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				passwordField.setStyle(Styles.CHAMP_TEXTE_FOCUS);
			} else {
				passwordField.setStyle(Styles.CHAMP_TEXTE);
			}
		});

		fieldsBox.getChildren().addAll(emailField, passwordField);

		// Bouton de connexion
		Button loginButton = new Button("SE CONNECTER");
		loginButton.setMaxWidth(300);
		loginButton.setStyle(Styles.BOUTON_PRINCIPAL);

		// Effets de survol
		loginButton.setOnMouseEntered(e -> loginButton.setStyle(Styles.BOUTON_PRINCIPAL_HOVER));
		loginButton.setOnMouseExited(e -> loginButton.setStyle(Styles.BOUTON_PRINCIPAL));

		// Label pour les messages
		Label messageLabel = new Label();
		messageLabel.setWrapText(true);
		messageLabel.setTextAlignment(TextAlignment.CENTER);

		// Pied de page
		HBox footerBox = new HBox(20);
		footerBox.setAlignment(Pos.CENTER);

		Label contactLabel = new Label("📞 +221 77 777 77 77");
		contactLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + "; -fx-font-size: 10px;");

		Label emailLabel = new Label("✉ avec@uadb.edu.sn");
		emailLabel.setStyle("-fx-text-fill: " + Styles.GRIS_FONCE + "; -fx-font-size: 10px;");

		footerBox.getChildren().addAll(contactLabel, emailLabel);

		// Action du bouton
		loginButton.setOnAction(e -> {
			String email = emailField.getText().trim();
			String password = passwordField.getText().trim();

			if (email.isEmpty() || password.isEmpty()) {
				messageLabel.setStyle(Styles.MESSAGE_ERREUR);
				messageLabel.setText("Veuillez remplir tous les champs");
				return;
			}

			Utilisateur utilisateur = utilisateurService.login(email, password);

			if (utilisateur != null) {
				messageLabel.setStyle(Styles.MESSAGE_SUCCES);
				messageLabel.setText("Connexion réussie! Chargement...");

				javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(
						javafx.util.Duration.seconds(1));
				delay.setOnFinished(event -> {
					DashboardView dashboardView = new DashboardView(mainApp, utilisateur);
					mainApp.getPrimaryStage().getScene().setRoot(dashboardView.getRoot());
					mainApp.getPrimaryStage().setMaximized(true);
				});
				delay.play();
			} else {
				messageLabel.setStyle(Styles.MESSAGE_ERREUR);
				messageLabel.setText("Email ou mot de passe incorrect");
			}
		});

		// Assemblage
		loginPanel.getChildren().addAll(iconLabel, titleLabel, subtitleLabel, fieldsBox, loginButton, messageLabel,
				footerBox);

		root.getChildren().add(loginPanel);
	}

	public VBox getRoot() {
		return root;
	}
}