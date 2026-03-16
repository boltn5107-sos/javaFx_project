package com.avec.view;

import com.avec.model.Pret;
import com.avec.service.PretService;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class PretView {

    public void afficher() {

        Stage stage = new Stage();

        TextField numeroPret = new TextField();
        numeroPret.setPromptText("Numero Pret");

        TextField montant = new TextField();
        montant.setPromptText("Montant");

        Button btnCreer = new Button("Créer Pret");

        PretService pretService = new PretService();

        btnCreer.setOnAction(e -> {

            Pret pret = new Pret();

            pret.setNumeroPret(numeroPret.getText());
            pret.setMontantInitial(new BigDecimal(montant.getText()));

            pretService.creerPret(pret);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Pret enregistré");
            alert.show();

        });

        VBox root = new VBox(10);

        root.setPadding(new Insets(20));

        root.getChildren().addAll(

                new Label("Numero Pret"),
                numeroPret,

                new Label("Montant"),
                montant,

                btnCreer
        );

        Scene scene = new Scene(root, 300, 250);

        stage.setTitle("Gestion des Prêts");
        stage.setScene(scene);
        stage.show();
    }
}
