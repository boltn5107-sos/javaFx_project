package com.avec;

import com.avec.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Système de Gestion AVEC");
        this.primaryStage.setResizable(true);
        
        // Afficher la vue de connexion
        showLoginView();
        
        primaryStage.show();
    }
    
    public void showLoginView() {
        LoginView loginView = new LoginView(this);
        Scene scene = new Scene(loginView.getRoot(), 450, 350);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}