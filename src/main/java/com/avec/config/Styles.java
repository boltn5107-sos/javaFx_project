package com.avec.config;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

public class Styles {

	// COULEURS - Charte graphique
	public static final String VERT_PRINCIPAL = "#2E7D32"; // Vert pour la croissance
	public static final String BLEU_SECONDAIRE = "#1565C0"; // Bleu pour la confiance
	public static final String GRIS_CLAIR = "#F5F5F5"; // Fond neutre
	public static final String ACCENT_DORE = "#FFC107"; // Doré pour l'épargne/crédit

	// Couleurs supplémentaires
	public static final String BLANC = "#FFFFFF";
	public static final String NOIR = "#212121";
	public static final String GRIS_FONCE = "#757575";
	public static final String ROUGE_ERREUR = "#D32F2F";
	public static final String VERT_SUCCES = "#2E7D32";

	// Styles de fond
	public static final String BACKGROUND_GRADIENT = "linear-gradient(to bottom right, " + VERT_PRINCIPAL + ", "
			+ BLEU_SECONDAIRE + ");";

	public static final String BACKGROUND_WHITE = "-fx-background-color: " + BLANC + ";";

	public static final String BACKGROUND_LIGHT = "-fx-background-color: " + GRIS_CLAIR + ";";

	// Styles de panneaux
	public static final String PANEL_STYLE = "-fx-background-color: " + BLANC + ";" + "-fx-background-radius: 15;"
			+ "-fx-padding: 20;" + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);";

	// Styles de titres
	public static final String TITRE_PRINCIPAL = "-fx-font-family: 'System';" + "-fx-font-weight: bold;"
			+ "-fx-font-size: 24px;" + "-fx-text-fill: " + VERT_PRINCIPAL + ";";

	public static final String TITRE_SECONDAIRE = "-fx-font-family: 'System';" + "-fx-font-weight: 600;"
			+ "-fx-font-size: 18px;" + "-fx-text-fill: " + BLEU_SECONDAIRE + ";";

	// Styles de boutons
	public static final String BOUTON_PRINCIPAL = "-fx-background-color: " + VERT_PRINCIPAL + ";"
			+ "-fx-text-fill: white;" + "-fx-font-weight: bold;" + "-fx-font-size: 14px;" + "-fx-padding: 10 20;"
			+ "-fx-background-radius: 8;" + "-fx-cursor: hand;";

	public static final String BOUTON_PRINCIPAL_HOVER = "-fx-background-color: #1B5E20;" + // Vert plus foncé
			"-fx-text-fill: white;" + "-fx-font-weight: bold;" + "-fx-font-size: 14px;" + "-fx-padding: 10 20;"
			+ "-fx-background-radius: 8;" + "-fx-cursor: hand;";

	public static final String BOUTON_SECONDAIRE = "-fx-background-color: " + BLEU_SECONDAIRE + ";"
			+ "-fx-text-fill: white;" + "-fx-font-weight: bold;" + "-fx-font-size: 14px;" + "-fx-padding: 10 20;"
			+ "-fx-background-radius: 8;" + "-fx-cursor: hand;";

	public static final String BOUTON_SECONDAIRE_HOVER = "-fx-background-color: #0D47A1;" + // Bleu plus foncé
			"-fx-text-fill: white;" + "-fx-font-weight: bold;" + "-fx-font-size: 14px;" + "-fx-padding: 10 20;"
			+ "-fx-background-radius: 8;" + "-fx-cursor: hand;";

	public static final String BOUTON_ACCENT = "-fx-background-color: " + ACCENT_DORE + ";" + "-fx-text-fill: " + NOIR
			+ ";" + "-fx-font-weight: bold;" + "-fx-font-size: 14px;" + "-fx-padding: 10 20;"
			+ "-fx-background-radius: 8;" + "-fx-cursor: hand;";

	// Styles de champs de texte
	public static final String CHAMP_TEXTE = "-fx-background-color: " + GRIS_CLAIR + ";" + "-fx-border-color: "
			+ GRIS_FONCE + ";" + "-fx-border-radius: 5;" + "-fx-background-radius: 5;" + "-fx-padding: 8;";

	public static final String CHAMP_TEXTE_FOCUS = "-fx-background-color: white;" + "-fx-border-color: "
			+ VERT_PRINCIPAL + ";" + "-fx-border-width: 2;" + "-fx-border-radius: 5;" + "-fx-background-radius: 5;"
			+ "-fx-padding: 8;";

	// Styles de tableau
	public static final String TABLEAU_ENTETE = "-fx-background-color: " + VERT_PRINCIPAL + ";"
			+ "-fx-text-fill: white;" + "-fx-font-weight: bold;";

	public static final String TABLEAU_LIGNE_PAIRE = "-fx-background-color: " + GRIS_CLAIR + ";";

	public static final String TABLEAU_LIGNE_IMPAIRE = "-fx-background-color: white;";

	// Style pour les icônes
	public static final String ICONE_STYLE = "-fx-font-family: 'Segoe UI Emoji';" + "-fx-font-size: 24px;";

	// Style pour les messages
	public static final String MESSAGE_SUCCES = "-fx-text-fill: " + VERT_PRINCIPAL + ";" + "-fx-font-size: 12px;"
			+ "-fx-font-weight: bold;";

	public static final String MESSAGE_ERREUR = "-fx-text-fill: " + ROUGE_ERREUR + ";" + "-fx-font-size: 12px;"
			+ "-fx-font-weight: bold;";

	// Effets
	public static DropShadow getOmbreLegere() {
		DropShadow ombre = new DropShadow();
		ombre.setRadius(5);
		ombre.setOffsetX(2);
		ombre.setOffsetY(2);
		ombre.setColor(Color.rgb(0, 0, 0, 0.2));
		return ombre;
	}

	public static DropShadow getOmbreMoyenne() {
		DropShadow ombre = new DropShadow();
		ombre.setRadius(10);
		ombre.setOffsetX(3);
		ombre.setOffsetY(3);
		ombre.setColor(Color.rgb(0, 0, 0, 0.3));
		return ombre;
	}
}