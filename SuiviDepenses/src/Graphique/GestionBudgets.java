package Graphique;

import Depenses.GestionDepenses;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GestionBudgets extends Stage {

	public GestionBudgets() {
	    setTitle("Définir un Budget Mensuel Global");

	    GridPane grid = new GridPane();
	    grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(20));

	  
	    grid.getStyleClass().add("grid-pane");


	    Label globalBudgetLabel = new Label("Budget Mensuel Global :");
	    TextField globalBudgetField = new TextField();

	 
	    globalBudgetField.getStyleClass().add("text-field");

	    grid.add(globalBudgetLabel, 0, 0);
	    grid.add(globalBudgetField, 1, 0);

	 
	    Button saveButton = new Button("Enregistrer");

	    
	    saveButton.getStyleClass().add("button");

	    grid.add(saveButton, 1, 1);

	    // Action du bouton
	    saveButton.setOnAction(e -> {
	        try {
	            double globalBudget = Double.parseDouble(globalBudgetField.getText());
	            if (globalBudget <= 0) {
	                afficherAlerte("Erreur", "Le budget global doit être un nombre positif.");
	                return;
	            }

	            // Enregistrer le budget global
	            GestionDepenses gestionDepenses = new GestionDepenses();
	            gestionDepenses.definirBudget("Global", globalBudget);
	            afficherAlerte("Succès", "Budget global enregistré avec succès !");
	            globalBudgetField.clear();
	        } catch (NumberFormatException ex) {
	            afficherAlerte("Erreur", "Le budget global doit être un nombre valide.");
	        }
	    });

	    Scene scene = new Scene(grid, 400, 150);

	
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