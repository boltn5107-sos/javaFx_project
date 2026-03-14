package com.avec;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.avec.config.DBConnection;

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
		insert();
		
		stage.show();

	}

	public static void main(String[] args) {

		launch(args);
	}
	
	
}
