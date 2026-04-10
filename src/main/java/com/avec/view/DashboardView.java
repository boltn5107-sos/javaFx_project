package com.avec.view;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.SessionUtilisateur;
import com.avec.model.Utilisateur;
import com.avec.service.UtilisateurService;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardView {
	Stage primaryStage;
	private MainApp mainApp;
	private Utilisateur utilisateur;
	private BorderPane root;
	private SessionUtilisateur session;
	private UtilisateurService utilisateurService;

	// Constantes pour les icônes (émojis)
	private static final String ICONE_TABLEAU_BORD = "📊";
	private static final String ICONE_MEMBRES = "👥";
	private static final String ICONE_PRETS = "💰";
	private static final String ICONE_REUNIONS = "📅";
	private static final String ICONE_CYCLES = "🔄";
	private static final String ICONE_ACHATS = "🛒";
	private static final String ICONE_REGLES = "📋";
	private static final String ICONE_VISITES = "📍";
	private static final String ICONE_DECONNEXION = "🚪";
	public static final String ICONE_AVEC = "🏘️";

	public DashboardView(MainApp mainApp, Utilisateur utilisateur) {
		this.mainApp = mainApp;
		this.utilisateur = utilisateur;
		this.session = SessionUtilisateur.getInstance();
		createView();
	}

	private void createView() {
		root = new BorderPane();
		root.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";");

		// ✅ REDIRIGER VERS LE DASHBOARD APPROPRIÉ SELON LE RÔLE
		if (session.isAdmin()) {
			// Administrateur → AdminDashboardView
			System.out.println("Redirection vers AdminDashboardView");
			AdminDashboardView adminDashboard = new AdminDashboardView(mainApp);
			root.setCenter(adminDashboard.getRoot());

		}
		else if (session.isAgentTerrain()) {
			// Agent Terrain → AgentTerrainDashboardView
			System.out.println("Redirection vers AgentTerrainDashboardView");
			AgentTerrainDashboardView agentTerrainDashboard = new AgentTerrainDashboardView(mainApp);
			root.setCenter(agentTerrainDashboard.getRoot());

		} 
		else if (session.isAgentVillageois()) {
			// Agent Villageois → AgentVillageoisDashboardView
			System.out.println("Redirection vers AgentVillageoisDashboardView");
			AgentVillageoisDashboardView agentVillageoisDashboard = new AgentVillageoisDashboardView(mainApp);
			root.setCenter(agentVillageoisDashboard.getRoot());

		} else if (session.isMembre()) {
			// Membre du comité → Dashboard selon son rôle
			if (session.isPresident()) {
				System.out.println("Redirection vers PresidentDashboardView");
				PresidentDashboardView presidentDashboard = new PresidentDashboardView(mainApp);
				root.setCenter(presidentDashboard.getRoot());
			} else if (session.isSecretaire()) {
				System.out.println("Redirection vers SecretaireDashboardView");
				SecretaireDashboardView secretaireDashboard = new SecretaireDashboardView(mainApp);
				root.setCenter(secretaireDashboard.getRoot());
			} else if (session.isTresorier()) {
				System.out.println("Redirection vers TresorierDashboardView");
				TresorierDashboardView tresorierDashboard = new TresorierDashboardView(mainApp);
				root.setCenter(tresorierDashboard.getRoot());
			} else if (session.isCompteur()) {
				System.out.println("Redirection vers CompteurDashboardView");
				CompteurDashboardView compteurDashboard = new CompteurDashboardView(mainApp);
				root.setCenter(compteurDashboard.getRoot());
			}
//            else {
//                // Membre simple - accès limité
//                System.out.println("Redirection vers MembreSimpleDashboardView");
//                MembreSimpleDashboardView membreSimpleDashboard = new MembreSimpleDashboardView(mainApp);
//                root.setCenter(membreSimpleDashboard.getRoot());
//            }
		} else {
			// Aucun rôle reconnu - rediriger vers login
			System.err.println("Rôle non reconnu: " + session.getTypeUtilisateur());
			LoginView loginView = new LoginView(mainApp);
			mainApp.getPrimaryStage().getScene().setRoot(loginView.getRoot());
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
                if (utilisateurService.changerMotDePasse(userId, ancienMdp, nouveauMdp, confirmation)) {
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
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

	public BorderPane getRoot() {
		return root;
	}
}