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

        ReunionService reunionService = new ReunionService();

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("Démarrer une réunion");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeReunion, 1, 1);

        Button btnDemarrer = new Button("Démarrer");
        btnDemarrer.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10 25;");
        
        Button btnFermer = new Button("Annuler");
        btnFermer.setOnAction(e -> stage.close());

        HBox buttons = new HBox(10, btnDemarrer, btnFermer);

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
                reunion.setStatut(StatutReunion.EN_COURS);

                reunionService.enregistrerReunion(reunion);

                showInfo("Succès", "Réunion démarrée!\n\n" +
                    "Type: " + reunion.getType() + "\n" +
                    "Date: " + reunion.getDateFormatted());
                
                stage.close();
            } catch (Exception ex) {
                showAlert("Erreur: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(title, grid, buttons);

        Scene scene = new Scene(root, 350, 200);

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