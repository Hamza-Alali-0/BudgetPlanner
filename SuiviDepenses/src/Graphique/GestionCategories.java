package Graphique;

import Depenses.GestionDepenses;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class GestionCategories extends Stage {

    private GestionDepenses gestionDepenses = new GestionDepenses();
    private ListView<String> categoriesList = new ListView<>(); 

    public GestionCategories() {
        setTitle("Gestion des Catégories");

       
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

      
        root.getStyleClass().add("root");

        // Filter 
        HBox filterControls = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher par nom...");
        Button filterButton = new Button("Filtrer");

     
        searchField.getStyleClass().add("text-field");
        filterButton.getStyleClass().add("button");

        filterControls.getChildren().addAll(new Label("Rechercher :"), searchField, filterButton);

        
        GridPane addCategoryPane = new GridPane();
        addCategoryPane.setHgap(10);
        addCategoryPane.setVgap(10);

  
        addCategoryPane.getStyleClass().add("grid-pane");

        TextField categoryField = new TextField();
        TextField budgetField = new TextField();
        Button addButton = new Button("Ajouter");


        categoryField.getStyleClass().add("text-field");
        budgetField.getStyleClass().add("text-field");
        addButton.getStyleClass().add("button");

        addCategoryPane.add(new Label("Nom de la catégorie :"), 0, 0);
        addCategoryPane.add(categoryField, 1, 0);
        addCategoryPane.add(new Label("Budget :"), 0, 1);
        addCategoryPane.add(budgetField, 1, 1);
        addCategoryPane.add(addButton, 0, 2);

        //appeler filters 
        root.getChildren().addAll(filterControls, addCategoryPane, new Label("Catégories existantes :"), categoriesList);

     
        categoriesList.getStyleClass().add("list-view");

      //appel des categories
        updateCategoriesList();

        // Action du bouton "Filtrer"
        filterButton.setOnAction(e -> {
            String searchText = searchField.getText().trim().toLowerCase();
            List<String> categories = gestionDepenses.listerCategories();

            // Filter par texte
            List<String> filteredCategories = categories.stream()
                    .filter(category -> category.toLowerCase().contains(searchText))
                    .toList();

            // m-a-j
            categoriesList.getItems().setAll(filteredCategories);
        });

        // Action du bouton "Ajouter"
        addButton.setOnAction(e -> {
            String categoryName = categoryField.getText().trim();
            String budgetText = budgetField.getText().trim();

            if (!categoryName.isEmpty() && !budgetText.isEmpty()) {
                try {
                    double budget = Double.parseDouble(budgetText);
                    // Define the budget for the category
                    gestionDepenses.definirBudget(categoryName, budget);
                    // Add the category to the database
                    gestionDepenses.ajouterCategorie(categoryName);

                 

                    afficherAlerte("Succès", "Catégorie et budget ajoutés avec succès !");
                    categoryField.clear();
                    budgetField.clear();

                    // Update the display
                    updateCategoriesList();
                } catch (NumberFormatException ex) {
                    afficherAlerte("Erreur", "Le budget doit être un nombre valide.");
                }
            } else {
                afficherAlerte("Erreur", "Veuillez entrer un nom de catégorie et un budget valides.");
            }
        });
      
        Scene scene = new Scene(root, 500, 400);

      
        scene.getStylesheets().add(getClass().getResource("/Style/form-style.css").toExternalForm());

        setScene(scene);
    }
    
    /**
     * Met à jour la liste des catégories affichées.
     */
    private void updateCategoriesList() {
        List<String> categories = gestionDepenses.listerCategories();
        categoriesList.getItems().setAll(categories);
    }

  
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}