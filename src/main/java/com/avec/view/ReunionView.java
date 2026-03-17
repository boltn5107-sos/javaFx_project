package com.avec.view;

import com.avec.enums.TypeReunion;
import com.avec.model.Reunion;
import com.avec.service.ReunionService;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReunionView {

    public void afficher() {

        Stage stage = new Stage();

        DatePicker datePicker = new DatePicker();

        ComboBox<TypeReunion> typeReunion = new ComboBox<>();
        typeReunion.getItems().addAll(TypeReunion.values());

        Button btnEnregistrer = new Button("Enregistrer");

        ReunionService reunionService = new ReunionService();

        btnEnregistrer.setOnAction(e -> {

            Reunion reunion = new Reunion();

            reunion.setDate(datePicker.getValue());
            reunion.setType(typeReunion.getValue());

            reunionService.creerReunion(reunion);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Réunion enregistrée");
            alert.show();

        });

        VBox root = new VBox(10);

        root.setPadding(new Insets(20));
        root.getChildren().addAll(

                new Label("Date réunion"),
                datePicker,

                new Label("Type réunion"),
                typeReunion,

                btnEnregistrer
        );

        Scene scene = new Scene(root, 300, 250);

        stage.setTitle("Gestion des Réunions");
        stage.setScene(scene);
        stage.show();
    }
}
