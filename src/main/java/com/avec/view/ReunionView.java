package com.avec.view;

import com.avec.enums.StatutReunion;
import com.avec.enums.TypeReunion;
import com.avec.model.Reunion;
import com.avec.service.ReunionService;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ReunionView {

    private Long avecId;

    public ReunionView(Long avecId) {
        this.avecId = avecId;
    }

    public void afficher() {
        Stage stage = new Stage();

        DatePicker datePicker = new DatePicker(LocalDate.now());

        ComboBox<TypeReunion> typeReunion = new ComboBox<>();
        typeReunion.getItems().addAll(TypeReunion.values());
        typeReunion.setValue(TypeReunion.EPARGNE);

        ComboBox<StatutReunion> statutCombo = new ComboBox<>();
        statutCombo.getItems().addAll(StatutReunion.values());
        statutCombo.setValue(StatutReunion.PLANIFIEE);

        ReunionService reunionService = new ReunionService();

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label title = new Label("Démarrer une réunion");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeReunion, 1, 1);
        
        grid.add(new Label("Statut:"), 0, 2);
        grid.add(statutCombo, 1, 2);

        Button btnDemarrer = new Button("Démarrer la réunion");
        btnDemarrer.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-padding: 10 25; -fx-cursor: hand; -fx-background-radius: 5;");
        
        Button btnFermer = new Button("Annuler");
        btnFermer.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-padding: 10 25; -fx-cursor: hand; -fx-background-radius: 5;");
        btnFermer.setOnAction(e -> stage.close());

        HBox buttons = new HBox(10, btnDemarrer, btnFermer);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);

        btnDemarrer.setOnAction(e -> {
            if (datePicker.getValue() == null) {
                showAlert("Veuillez sélectionner une date");
                return;
            }
            if (typeReunion.getValue() == null) {
                showAlert("Veuillez sélectionner un type");
                return;
            }

            try {
                Reunion reunion = new Reunion();
                reunion.setDate(datePicker.getValue());
                reunion.setType(typeReunion.getValue());
                reunion.setStatut(statutCombo.getValue());
                
                // Valeurs par défaut pour les soldes
                reunion.setSoldeFondCreditAvant(java.math.BigDecimal.ZERO);
                reunion.setSoldesFondsCreditApres(java.math.BigDecimal.ZERO);
                reunion.setSoldeCaisseSolidaritesApres(java.math.BigDecimal.ZERO);
                
                // Si vous avez un cycle actif, vous pouvez le définir ici
                 //reunion.setCycleId(cycleId);

                reunionService.enregistrerReunion(reunion);

                showInfo("Succès", "Réunion démarrée avec succès!\n\n" +
                    "Type: " + reunion.getType() + "\n" +
                    "Date: " + reunion.getDateFormatted() + "\n" +
                    "Statut: " + reunion.getStatut().getLibelle());
                
                stage.close();
            } catch (Exception ex) {
                showAlert("Erreur: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(title, grid, buttons);

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Nouvelle Réunion");
        stage.setScene(scene);
        stage.show();
    }

   
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.show();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.show();
    }
}