package Graphique;

import Depenses.Depense;
import Depenses.GestionDepenses;
import Exceptions.ValidationDonnees;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDate;

public class FormulaireDepense extends Stage {

    private Depense depenseToEdit; 
    private TextField amountField = new TextField();
    private DatePicker datePicker = new DatePicker();
    private ComboBox<String> categoryComboBox = new ComboBox<>();
    private TextField descriptionField = new TextField();

    // Constructeur
    public FormulaireDepense() {
        this(null); 
    }

    // constructeur  pour depense existante
    public FormulaireDepense(Depense depenseToEdit) {
        this.depenseToEdit = depenseToEdit;
        initializeForm();
    }

    private void initializeForm() {
        setTitle(depenseToEdit == null ? "Ajouter une Dépense" : "Modifier une Dépense");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // css
        grid.getStyleClass().add("grid-pane");

       
        GestionDepenses gestionDepenses = new GestionDepenses();
        categoryComboBox.getItems().addAll(gestionDepenses.listerCategories());

        // Pré-remplir les champs si on modifie une dépense existante
        
        if (depenseToEdit != null) {
            amountField.setText(String.valueOf(depenseToEdit.getMontant()));
            datePicker.setValue(depenseToEdit.getDate());
            categoryComboBox.setValue(depenseToEdit.getCategorie());
            descriptionField.setText(depenseToEdit.getDescription());
        }

        // Champs du formulaire
        
        grid.add(new Label("Montant :"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Date :"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Catégorie :"), 0, 2);
        grid.add(categoryComboBox, 1, 2);
        grid.add(new Label("Description :"), 0, 3);
        grid.add(descriptionField, 1, 3);

        // Boutons
        Button saveButton = new Button(depenseToEdit == null ? "Enregistrer" : "Modifier");
        Button cancelButton = new Button("Annuler");

        // css
        saveButton.getStyleClass().add("button");
        cancelButton.getStyleClass().add("button");
        cancelButton.getStyleClass().add("cancel");

        grid.add(saveButton, 0, 4);
        grid.add(cancelButton, 1, 4);

        // alerte de erreur 
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                afficherAlerte("Erreur", "Le montant doit être un nombre valide.", Alert.AlertType.ERROR);
                amountField.setText(oldValue); // Revert to the old value
            }
        });

        // Gestion des actions des boutons
        
        saveButton.setOnAction(e -> {
            try {
                String amountText = amountField.getText();
                if (amountText.isEmpty()) {
                    afficherAlerte("Erreur", "Le montant ne peut pas être vide.", Alert.AlertType.ERROR);
                    return;
                }

                double amount = Double.parseDouble(amountText);
                LocalDate date = datePicker.getValue();
                String category = categoryComboBox.getValue();
                String description = descriptionField.getText();

                
                Depense depense = new Depense(
                    depenseToEdit != null ? depenseToEdit.getId() : 0, // Use existing ID if editing
                    amount,
                    date,
                    category,
                    description
                );

                // Valider
                GestionDepenses gestionDepenses1 = new GestionDepenses();
                if (ValidationDonnees.validerDepense(depense, gestionDepenses1)) {
                    if (depenseToEdit == null) {
                        // ajouter
                        gestionDepenses1.ajouterDepense(depense);
                        afficherAlerte("Succès", "Dépense enregistrée avec succès !", Alert.AlertType.INFORMATION);
                    } else {
                        // mettre a jour
                        gestionDepenses1.modifierDepense(depenseToEdit.getId(), depense);
                        afficherAlerte("Succès", "Dépense modifiée avec succès !", Alert.AlertType.INFORMATION);
                    }
                    close();
                }
            } catch (NumberFormatException ex) {
                afficherAlerte("Erreur", "Le montant doit être un nombre valide.", Alert.AlertType.ERROR);
            }
        });

        cancelButton.setOnAction(e -> close());

      
        Scene scene = new Scene(grid, 400, 300);

        // css 
        scene.getStylesheets().add(getClass().getResource("/Style/form-style.css").toExternalForm());

        setScene(scene);
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/Style/alert.css").toExternalForm());

      
        alert.showAndWait();
    }
}