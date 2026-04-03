package com.avec.view;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.SessionUtilisateur;
import com.avec.model.Utilisateur;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DashboardView {
	Stage primaryStage;
	private MainApp mainApp;
	private Utilisateur utilisateur;
	private BorderPane root;
	private SessionUtilisateur session;

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

	public BorderPane getRoot() {
		return root;
	}
}