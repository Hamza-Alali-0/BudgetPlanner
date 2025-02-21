package Graphique;

import Depenses.Depense;
import Depenses.GestionDepenses;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GestionDepensesPage extends Stage {

    private TextField searchField = new TextField();
    private ComboBox<String> categoryFilter = new ComboBox<>();
    private DatePicker dateFilter = new DatePicker();
    private TextField amountFilter = new TextField();
    private Button filterButton = new Button("Filtrer");

    public GestionDepensesPage() {
        setTitle("Gestion des Dépenses");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Ajouter les contrôles de filtrage
        HBox filterControls = new HBox(10);
        filterControls.getChildren().addAll(
                new Label("Rechercher :"), searchField,
                new Label("Catégorie :"), categoryFilter,
                new Label("Date :"), dateFilter,
                new Label("Montant :"), amountFilter,
                filterButton
        );

        grid.add(filterControls, 0, 0, 4, 1);

       
        GestionDepenses gestionDepenses = new GestionDepenses();
        categoryFilter.getItems().addAll(gestionDepenses.listerCategories());

       
        grid.add(new Label("Montant"), 0, 1);
        grid.add(new Label("Date"), 1, 1);
        grid.add(new Label("Catégorie"), 2, 1);
        grid.add(new Label("Description"), 3, 1);
        grid.add(new Label("Actions"), 4, 1);

        // Récupérer les dépenses
        List<Depense> depenses = gestionDepenses.listerDepenses();

        // Afficher les dépenses initiales
        displayDepenses(grid, depenses, 2);

        // Gestion des actions de filtrage
        
        filterButton.setOnAction(e -> {
            List<Depense> filteredDepenses = filterDepenses(depenses);
            displayDepenses(grid, filteredDepenses, 2);
        });

       
        Scene scene = new Scene(grid, 800, 600);
        setScene(scene);
    }

    private List<Depense> filterDepenses(List<Depense> depenses) {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = categoryFilter.getValue();
        LocalDate selectedDate = dateFilter.getValue();
        String amountText = amountFilter.getText();

        return depenses.stream()
                .filter(depense -> searchText.isEmpty() || depense.getDescription().toLowerCase().contains(searchText))
                .filter(depense -> selectedCategory == null || depense.getCategorie().equals(selectedCategory))
                .filter(depense -> selectedDate == null || depense.getDate().equals(selectedDate))
                .filter(depense -> amountText.isEmpty() || depense.getMontant() == Double.parseDouble(amountText))
                .collect(Collectors.toList());
    }

    private void displayDepenses(GridPane grid, List<Depense> depenses, int startRow) {
      
        grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) >= startRow);

        int row = startRow;
        for (Depense depense : depenses) {
            grid.add(new Label("$" + String.format("%.2f", depense.getMontant())), 0, row);
            grid.add(new Label(depense.getDate().toString()), 1, row);
            grid.add(new Label(depense.getCategorie()), 2, row);
            grid.add(new Label(depense.getDescription()), 3, row);

            // Boutons Modifier et Supprimer
            Button modifyButton = new Button("Modifier");
            modifyButton.setOnAction(e -> {
                new FormulaireDepense(depense).show();
                this.close();
                new GestionDepensesPage().show();
            });

            Button deleteButton = new Button("Supprimer");
            deleteButton.setOnAction(e -> {
                GestionDepenses gestionDepenses = new GestionDepenses();
                gestionDepenses.supprimerDepense(depense.getId());
                afficherAlerte("Succès", "Dépense supprimée avec succès !");
                this.close();
                new GestionDepensesPage().show();
            });

            grid.add(modifyButton, 4, row);
            grid.add(deleteButton, 5, row);

            row++;
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}