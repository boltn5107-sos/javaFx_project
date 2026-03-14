package com.avec;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

	public void start(Stage stage) {
		
		Button btn = new Button("start");
		
		VBox vb = new VBox();
		
		vb.getChildren().add(btn);
		
		Scene scene = new Scene(vb,500,500);
		stage.setScene(scene);
		
		
		stage.show();

	}

	public static void main(String[] args) {

		launch(args);
	}
}
