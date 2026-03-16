package com.avec.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AchatPartView {

    public void afficher(){

        Stage stage = new Stage();

        TextField nombreParts = new TextField();
        nombreParts.setPromptText("Nombre de parts");

        Button btn = new Button("Acheter");

        VBox root = new VBox(10,nombreParts,btn);

        Scene scene = new Scene(root,300,200);

        stage.setScene(scene);
        stage.setTitle("Achat de parts");

        stage.show();

    }

}