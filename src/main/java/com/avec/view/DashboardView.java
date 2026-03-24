package com.avec.view;

import com.avec.MainApp;
import com.avec.config.Styles;
import com.avec.model.SessionUtilisateur;

import javafx.scene.layout.BorderPane;

public class DashboardView {

	private MainApp mainApp;
	private BorderPane root;
	private SessionUtilisateur session;

	public DashboardView(MainApp mainApp) {
		this.mainApp = mainApp;
		this.session = SessionUtilisateur.getInstance();
		createView();
	}

	private void createView() {
		root = new BorderPane();
		root.setStyle("-fx-background-color: " + Styles.GRIS_CLAIR + ";");

		if (session.isAdmin()) {
			AdminDashboardView adminDashboard = new AdminDashboardView(mainApp);
			root.setCenter(adminDashboard.getRoot());
//        } else if (session.isAgentTerrain()) {
//            AgentTerrainDashboardView agentTerrainDashboard = new AgentTerrainDashboardView(mainApp);
//            root.setCenter(agentTerrainDashboard.getRoot());
		} else if (session.isAgentVillageois()) {
			AgentVillageoisDashboardView agentVillageoisDashboard = new AgentVillageoisDashboardView(mainApp);
			root.setCenter(agentVillageoisDashboard.getRoot());
		} else if (session.isMembre()) {
			// Rediriger vers le dashboard spécifique selon le rôle
			if (session.isPresident()) {
				PresidentDashboardView presidentDashboard = new PresidentDashboardView(mainApp);
				root.setCenter(presidentDashboard.getRoot());
			}
//            else if (session.isSecretaire()) {
//                SecretaireDashboardView secretaireDashboard = new SecretaireDashboardView(mainApp);
//                root.setCenter(secretaireDashboard.getRoot());
//            } else if (session.isTresorier()) {
//                TresorierDashboardView tresorierDashboard = new TresorierDashboardView(mainApp);
//                root.setCenter(tresorierDashboard.getRoot());
//            } else if (session.isCompteur()) {
//                CompteurDashboardView compteurDashboard = new CompteurDashboardView(mainApp);
//                root.setCenter(compteurDashboard.getRoot());
//            }
		}
	}

	public BorderPane getRoot() {
		return root;
	}
}