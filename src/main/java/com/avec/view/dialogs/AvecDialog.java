package com.avec.view.dialogs;

import com.avec.enums.JourReunion;
import com.avec.model.Avec;
import com.avec.utils.ValidationUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.Optional;

public class AvecDialog extends Dialog<Avec> {

    private final TextField nomField;
    private final TextField prixPartField;
    private final TextField lieuField;
    private final ComboBox<JourReunion> jourCombo;
    private final TextField tauxField;
    private final TextField cotisationField;
    private final CheckBox caisseActiveCheck;

    public AvecDialog(Stage owner, String title) {
        this(owner, title, null);
    }

    public AvecDialog(Stage owner, String title, Avec avec) {
        initOwner(owner);
        setTitle(title);
        setHeaderText(avec == null ? "Création d'une nouvelle AVEC" : "Modification de l'AVEC");

        // Boutons
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        nomField = new TextField();
        nomField.setPromptText("Nom de l'AVEC");

        prixPartField = new TextField();
        prixPartField.setPromptText("Prix de la part");

        lieuField = new TextField();
        lieuField.setPromptText("Lieu de réunion");

        jourCombo = new ComboBox<>();
        jourCombo.setItems(javafx.collections.FXCollections.observableArrayList(JourReunion.values()));
        jourCombo.setPromptText("Jour de réunion");

        tauxField = new TextField();
        tauxField.setPromptText("Taux des frais (%)");
        tauxField.setText("10");

        cotisationField = new TextField();
        cotisationField.setPromptText("Cotisation caisse solidarité");
        cotisationField.setText("100");

        caisseActiveCheck = new CheckBox("Activer la caisse de solidarité");
        caisseActiveCheck.setSelected(true);

        // Ajout des champs
        grid.add(new Label("Nom*:"), 0, 0);
        grid.add(nomField, 1, 0);

        grid.add(new Label("Prix de la part*:"), 0, 1);
        grid.add(prixPartField, 1, 1);

        grid.add(new Label("Lieu de réunion*:"), 0, 2);
        grid.add(lieuField, 1, 2);

        grid.add(new Label("Jour de réunion*:"), 0, 3);
        grid.add(jourCombo, 1, 3);

        grid.add(new Label("Taux des frais (%):"), 0, 4);
        grid.add(tauxField, 1, 4);

        grid.add(new Label("Cotisation caisse:"), 0, 5);
        grid.add(cotisationField, 1, 5);

        grid.add(caisseActiveCheck, 0, 6, 2, 1);

        getDialogPane().setContent(grid);

        // Remplir si modification
        if (avec != null) {
            nomField.setText(avec.getNom());
            prixPartField.setText(avec.getPrixPart().toString());
            lieuField.setText(avec.getLieuReunion());
            jourCombo.setValue(avec.getJourReunion());
            tauxField.setText(avec.getTauxFraisServiceMensuel().toString());
            cotisationField.setText(avec.getCotisationCaisseSolidarite().toString());
            caisseActiveCheck.setSelected(avec.isCaisseSolidariteActive());
        }

        // Validation
        Node saveButton = getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Listeners pour validation
        nomField.textProperty().addListener((obs, old, newVal) -> validateForm());
        prixPartField.textProperty().addListener((obs, old, newVal) -> validateForm());
        lieuField.textProperty().addListener((obs, old, newVal) -> validateForm());
        jourCombo.valueProperty().addListener((obs, old, newVal) -> validateForm());

        // Résultat
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createAvecFromForm();
            }
            return null;
        });
    }

    private void validateForm() {
        boolean isValid = !nomField.getText().trim().isEmpty()
                && !prixPartField.getText().trim().isEmpty()
                && ValidationUtils.isValidBigDecimal(prixPartField.getText())
                && !lieuField.getText().trim().isEmpty()
                && jourCombo.getValue() != null;

        getDialogPane().lookupButton(ButtonType.OK).setDisable(!isValid);
    }

    private Avec createAvecFromForm() {
        Avec avec = new Avec();
        avec.setNom(nomField.getText().trim());
        avec.setPrixPart(new BigDecimal(prixPartField.getText().trim()));
        avec.setLieuReunion(lieuField.getText().trim());
        avec.setJourReunion(jourCombo.getValue());
        avec.setTauxFraisServiceMensuel(new BigDecimal(tauxField.getText()));
        avec.setCotisationCaisseSolidarite(new BigDecimal(cotisationField.getText()));
        avec.setCaisseSolidariteActive(caisseActiveCheck.isSelected());
        return avec;
    }
}