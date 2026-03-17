package com.avec.utils;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.util.Optional;

/**
 * Utilitaires pour les boîtes de dialogue JavaFX
 */
public class AlertUtils {

    /**
     * Affiche une boîte de dialogue d'information
     */
    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue d'avertissement
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue d'erreur
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue de confirmation
     * @return true si l'utilisateur a cliqué sur OK, false sinon
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Affiche une boîte de dialogue de confirmation avec des boutons personnalisés
     * @return le ButtonType cliqué
     */
    public static Optional<ButtonType> showConfirmationWithButtons(String title, String header,
                                                                   String content,
                                                                   ButtonType... buttons) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getButtonTypes().setAll(buttons);

        return alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue avec un champ de saisie
     */
    public static Optional<String> showInputDialog(String title, String header, String content,
                                                   String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        return dialog.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue personnalisée
     */
    public static void showCustomDialog(Stage owner, String title, String header,
                                        javafx.scene.Node content) {
        Alert alert = new Alert(AlertType.NONE);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.getDialogPane().setContent(content);
        alert.getButtonTypes().add(ButtonType.CLOSE);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue d'exception
     */
    public static void showException(String title, String header, Exception e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());

        // Créer un texte area pour la stack trace
        TextArea textArea = new TextArea(getStackTraceAsString(e));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setExpandableContent(textArea);
        alert.showAndWait();
    }

    /**
     * Convertit une stack trace en String
     */
    private static String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Affiche une boîte de dialogue avec barre de progression (pour les opérations longues)
     */
    public static void showProgressDialog(String title, String header,
                                          javafx.concurrent.Task<?> task) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);

        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(task.progressProperty());

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(task.messageProperty());

        VBox vbox = new VBox(10, progressBar, statusLabel);
        vbox.setPadding(new Insets(10));

        alert.getDialogPane().setContent(vbox);
        alert.getButtonTypes().clear();

        new Thread(task).start();
        alert.showAndWait();
    }

    /**
     * Affiche une notification temporaire (toast)
     */
    public static void showToast(String message, int durationInSeconds) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);

        Label label = new Label(message);
        label.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 5px;");
        label.setPadding(new Insets(10));

        Scene scene = new Scene(label);
        stage.setScene(scene);

        // Positionner au centre de l'écran
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - label.getWidth()) / 2);
        stage.setY(Screen.getPrimary().getVisualBounds().getHeight() - 100);

        stage.show();

        // Fermer automatiquement après quelques secondes
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(
                javafx.util.Duration.seconds(durationInSeconds)
        );
        delay.setOnFinished(e -> stage.close());
        delay.play();
    }
}