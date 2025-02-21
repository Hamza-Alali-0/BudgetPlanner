package Graphique;

import Depenses.GestionDepenses;
import Depenses.Revenue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDate;

public class FormulaireRevenue extends Stage {

    private Revenue revenueToEdit; 
    private TextField sourceField = new TextField();
    private TextField amountField = new TextField();
    private DatePicker datePicker = new DatePicker();

    // Constructeur 
    public FormulaireRevenue() {
        this(null); 
    }

    // Constructeur pour existant 
    public FormulaireRevenue(Revenue revenueToEdit) {
        this.revenueToEdit = revenueToEdit;
        initializeForm();
    }

    private void initializeForm() {
        setTitle(revenueToEdit == null ? "Ajouter un Revenu" : "Modifier un Revenu");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

      
        grid.getStyleClass().add("grid-pane");

        // Champs du formulaire
        grid.add(new Label("Source :"), 0, 0);
        grid.add(sourceField, 1, 0);
        grid.add(new Label("Montant :"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Date :"), 0, 2);
        grid.add(datePicker, 1, 2);

     
        sourceField.getStyleClass().add("text-field");
        amountField.getStyleClass().add("text-field");
        datePicker.getStyleClass().add("date-picker");

        // Pré-remplir les champs si on modifie un revenu existant
        
        if (revenueToEdit != null) {
            sourceField.setText(revenueToEdit.getSource());
            amountField.setText(String.valueOf(revenueToEdit.getMontant()));
            datePicker.setValue(revenueToEdit.getDate());
        }

        // Boutons
        Button saveButton = new Button(revenueToEdit == null ? "Enregistrer" : "Modifier");
        Button cancelButton = new Button("Annuler");

       
        saveButton.getStyleClass().add("button");
        cancelButton.getStyleClass().add("button");
        cancelButton.getStyleClass().add("cancel");

        grid.add(saveButton, 0, 3);
        grid.add(cancelButton, 1, 3);

        // Actions des boutons
        saveButton.setOnAction(e -> {
            try {
                String source = sourceField.getText();
                double amount = Double.parseDouble(amountField.getText());
                LocalDate date = datePicker.getValue();

                if (source.isEmpty() || amount <= 0 || date == null) {
                    afficherAlerte("Erreur", "Veuillez remplir tous les champs obligatoires.");
                } else {
                    Revenue revenue = new Revenue(
                        revenueToEdit != null ? revenueToEdit.getId() : 0, 
                        source,
                        amount,
                        date
                    );

                    GestionDepenses gestionDepenses = new GestionDepenses();
                    if (revenueToEdit == null) {
                        // ajouter 
                        gestionDepenses.ajouterRevenue(revenue);
                        afficherAlerte("Succès", "Revenu enregistré avec succès !");
                    } else {
                        // mettre a jour 
                        gestionDepenses.modifierRevenue(revenueToEdit.getId(), revenue);
                        afficherAlerte("Succès", "Revenu modifié avec succès !");
                    }
                    close();
                }
            } catch (NumberFormatException ex) {
                afficherAlerte("Erreur", "Le montant doit être un nombre valide.");
            }
        });

        cancelButton.setOnAction(e -> close());

        Scene scene = new Scene(grid, 400, 300);


        scene.getStylesheets().add(getClass().getResource("/Style/form-style.css").toExternalForm());

        setScene(scene);
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}