package Graphique;

import Depenses.Depense;
import Depenses.GestionDepenses;
import Depenses.Revenue;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import java.util.Map;
import java.util.HashMap;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;


public class TableauDeBord extends Application {

    private GestionDepenses gestionDepenses = new GestionDepenses();
    private VBox contentPane = new VBox(10); 
    private ComboBox<String> selectionComboBox = new ComboBox<>();

    // Labels for financial summary
    private Label totalExpensesLabel = new Label("Dépenses totales : $0.00");
    private Label totalRevenueLabel = new Label("Revenus totaux : $0.00");
    private Label netSavingsLabel = new Label("Épargne nette : $0.00");
    private Label remainingBudgetLabel = new Label("Budget restant : $0.00");
    private Label globalBudgetLabel = new Label("Budget global : $0.00");
    private Label budgetStatusLabel = new Label("État du Budget : En attente...");

    // variables pour voir si alertes sont affiches (flags)
    private boolean budgetExceededAlertShown = false;
    private boolean budgetNearlyExhaustedAlertShown = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tableau de Bord - Gestion des Dépenses et Revenus");

       
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

      
        GridPane summaryPanel = new GridPane();
        summaryPanel.setHgap(10); 
        summaryPanel.setVgap(10); 
        summaryPanel.setPadding(new Insets(10)); 

        String backgroundColor = "white"; 
        String accentColor = "#007bff"; 

        //labels pour infos
        summaryPanel.add(createSummaryLabel("Dépenses totales", totalExpensesLabel, backgroundColor, accentColor), 0, 0);
        summaryPanel.add(createSummaryLabel("Revenus totaux", totalRevenueLabel, backgroundColor, accentColor), 1, 0);
        summaryPanel.add(createSummaryLabel("Épargne nette", netSavingsLabel, backgroundColor, accentColor), 2, 0);
        summaryPanel.add(createSummaryLabel("Budget restant", remainingBudgetLabel, backgroundColor, accentColor), 0, 1);
        summaryPanel.add(createSummaryLabel("Budget global", globalBudgetLabel, backgroundColor, accentColor), 1, 1);
       
     // Create the budget status summary card
        VBox budgetStatusCard = createSummaryCard("État du budget", budgetStatusLabel, backgroundColor,accentColor);

        // Add the card to the summary panel at column 2, row 1
        summaryPanel.add(budgetStatusCard, 2, 1);

    
   
      root.setCenter(summaryPanel);
updateFinancialSummary();
        // Navbar
        HBox navbar = new HBox(10); 
        navbar.setPadding(new Insets(10)); 
        navbar.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;"); 

        // Buttons 
        Button depensesButton = new Button("Dépenses");
        Button revenusButton = new Button("Revenus");
        Button categoriesButton = new Button("Catégories");
        Button budgetsButton = new Button("Budget Global");
        Button rapportsButton = new Button("Rapports");

        // Style pour boutons
        String buttonStyle = "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px;";
        depensesButton.setStyle(buttonStyle);
        revenusButton.setStyle(buttonStyle);
        categoriesButton.setStyle(buttonStyle);
        budgetsButton.setStyle(buttonStyle);
        rapportsButton.setStyle(buttonStyle);

        // style hover
        depensesButton.setOnMouseEntered(e -> depensesButton.setStyle(buttonStyle + "-fx-background-color: #0056b3;"));
        depensesButton.setOnMouseExited(e -> depensesButton.setStyle(buttonStyle));
        revenusButton.setOnMouseEntered(e -> revenusButton.setStyle(buttonStyle + "-fx-background-color: #0056b3;"));
        revenusButton.setOnMouseExited(e -> revenusButton.setStyle(buttonStyle));
        categoriesButton.setOnMouseEntered(e -> categoriesButton.setStyle(buttonStyle + "-fx-background-color: #0056b3;"));
        categoriesButton.setOnMouseExited(e -> categoriesButton.setStyle(buttonStyle));
        budgetsButton.setOnMouseEntered(e -> budgetsButton.setStyle(buttonStyle + "-fx-background-color: #0056b3;"));
        budgetsButton.setOnMouseExited(e -> budgetsButton.setStyle(buttonStyle));
        rapportsButton.setOnMouseEntered(e -> rapportsButton.setStyle(buttonStyle + "-fx-background-color: #0056b3;"));
        rapportsButton.setOnMouseExited(e -> rapportsButton.setStyle(buttonStyle));

        // actions 
        depensesButton.setOnAction(e -> updateContent("Dépenses"));
        revenusButton.setOnAction(e -> updateContent("Revenus"));
        categoriesButton.setOnAction(e -> updateContent("Catégories"));
        budgetsButton.setOnAction(e -> updateContent("Budgets"));
        rapportsButton.setOnAction(e -> updateContent("Rapports"));

        
        navbar.getChildren().addAll(depensesButton, revenusButton, categoriesButton, budgetsButton, rapportsButton);

      
        root.setTop(new VBox(10, summaryPanel, navbar));

        // m-a-j tout 5 secondes
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            updateFinancialSummary();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); 
        timeline.play(); 

      
        gestionDepenses.configurerRappels(Duration.minutes(4));

      
        root.setCenter(contentPane); 

        
        Scene scene = new Scene(root, 1000, 700); 
        scene.getStylesheets().add(getClass().getResource("/Style/Style.css").toExternalForm()); 
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Status card 
     */
    private VBox createSummaryCard(String title, Label valueLabel, String color,String accentColor) {
        // Create the container for the card
        VBox card = new VBox(10); // Spacing between elements
        card.setAlignment(Pos.CENTER); // Center align content
        card.setPadding(new Insets(15)); // Padding inside the card
        card.setStyle(
            "-fx-background-color: " + color + "; " + // Set background color
            		  "-fx-border-color: " + accentColor + "; " + // Border color
            "-fx-background-radius: 10; " + // Rounded corners
            "-fx-border-radius: 10; " + // Rounded border
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 2);" // Shadow effect
        );

        // Create the title label
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 14px; " + // Font size
            "-fx-font-weight: bold; " + // Bold text
            "-fx-text-fill: black;" // Text color
        );

        // Style the value label
        valueLabel.setStyle(
            "-fx-font-size: 18px; " + // Larger font size
            "-fx-font-weight: bold; " + // Bold text
            "-fx-text-fill: white;" // Text color
        );

      
        card.getChildren().addAll(titleLabel, valueLabel);

        return card;
    }
   
    /**
     * Met à jour le résumé financier (dépenses totales, revenus totaux, épargne nette).
     */
    private void updateFinancialSummary() {
        LocalDate now = LocalDate.now();
        double totalExpenses = gestionDepenses.calculerTotalDepensesMensuels(now);
        double totalRevenue = gestionDepenses.calculerTotalRevenusMensuels(now);
        double totalBudget = gestionDepenses.getBudgetTotal(); // Budget global

        // Ensure values are non-negative
        totalExpenses = Math.max(0, totalExpenses);
        totalRevenue = Math.max(0, totalRevenue);
        totalBudget = Math.max(0, totalBudget);
        double netSavings = Math.round((totalRevenue - totalExpenses) * 100) / 100.0;
        double remainingBudget = gestionDepenses.calculerBudgetRestantGlobal(); 

        // Determine the budget status
        String budgetStatus;
        if (remainingBudget <= 0) {
            budgetStatus = "--- Dépassé ---";
        } else if (remainingBudget > 0 && remainingBudget < totalBudget * 0.3) {
            budgetStatus = "-- Presque épuisé ---";
        } else {
            budgetStatus = "--- Dans les limites ---";
        }

        
        updateStringLabel(budgetStatusLabel, budgetStatus);

    
        updateNumberLabel(totalExpensesLabel, totalExpenses);
        updateNumberLabel(totalRevenueLabel, totalRevenue);
        updateNumberLabel(netSavingsLabel, netSavings);
        updateNumberLabel(remainingBudgetLabel, remainingBudget);
        updateNumberLabel(globalBudgetLabel, totalBudget);
    }
      
    /**
     couleurs pour indiquer niveau

     */
    private void updateNumberLabel(Label valueLabel, double value) {
        Label numberLabel = (Label) valueLabel.getUserData();
        numberLabel.setText(String.format("%.2f", value));

        if (value > 0) {
            numberLabel.setTextFill(Color.GREEN); 
        } else if (value < 0) {
            numberLabel.setTextFill(Color.RED); 
        } else {
            numberLabel.setTextFill(Color.BLACK);
        }
    }
    
    
    private void updateStringLabel(Label valueLabel, String value) {
        // Ensure the label is not null
        if (valueLabel == null) {
            return;
        }

        // Update the label text
        valueLabel.setText(value);

        // Set the text color based on the value
        if (value.contains("Dépassé")) {
            valueLabel.setTextFill(Color.RED); 
        } else if (value.contains("Presque épuisé")) {
            valueLabel.setTextFill(Color.ORANGE); 
        } else if (value.contains("Dans les limites")) {
            valueLabel.setTextFill(Color.GREEN); 
        } else {
            valueLabel.setTextFill(Color.BLACK); 
        }
    }
    
    
    
    /**
     * Creates a summary card with a title and value.
     */
    /**
 
     */
    private VBox createSummaryLabel(String title, Label valueLabel, String backgroundColor, String accentColor) {
        VBox container = new VBox(5); 
        container.setAlignment(Pos.CENTER); 
        container.setPadding(new Insets(10)); 
        container.setStyle(
            "-fx-background-color: " + backgroundColor + "; " + 
            "-fx-background-radius: 10; " + // Rounded corners
            "-fx-border-color: " + accentColor + "; " + // Border color
            "-fx-border-radius: 10; " + // Rounded border
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);" // Subtle shadow
        );

        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 12px; " + 
            "-fx-font-weight: bold; " +
            "-fx-text-fill: black;" 
        );

        // Value label (text + number)
        HBox valueContainer = new HBox(5);
        valueContainer.setAlignment(Pos.CENTER);

        // Change the textLabel's color from white to black (to match the dark theme):
        Label textLabel = new Label(title + " : ");
        textLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: white;" 
        );

        Label numberLabel = new Label(); 
        numberLabel.setStyle(
            "-fx-font-size: 18px; " + // Larger font size for numbers
            "-fx-font-weight: bold; " + // Bold numbers
            "-fx-text-fill: black;" 
        );

        valueContainer.getChildren().addAll(textLabel, numberLabel);
       
      
        valueLabel.setUserData(numberLabel);

        
        container.getChildren().addAll(titleLabel, valueContainer);
     
        return container;
    }   
    /**
     * Met à jour le contenu dynamique en fonction de la sélection.
     *
     * 
     */
    private void updateContent(String selectedItem) {
        contentPane.getChildren().clear(); 

        if (selectedItem == null) {
            return; 
        }

        switch (selectedItem) {
            case "Dépenses":
                displayDepenses();
                break;
            case "Revenus":
                displayRevenus();
                break;
            case "Catégories":
                displayCategories();
                break;
            case "Budgets":
                displayBudgets();
                break;
            case "Rapports":
                displayRapports();
                break;
            default:
                break;
        }
    }

    /**
     * Affiche la gestion des dépenses avec des options de filtrage.
     */
    private void displayDepenses() {
        AtomicBoolean isFiltering = new AtomicBoolean(false);

     
        HBox filterControls = new HBox(10);
        filterControls.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher par description...");

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Filtrer par catégorie...");
        categoryFilter.getItems().addAll(gestionDepenses.listerCategories());

        DatePicker dateFilter = new DatePicker();
        dateFilter.setPromptText("Filtrer par date...");

        TextField amountFilter = new TextField();
        amountFilter.setPromptText("Filtrer par montant...");

        Button filterButton = new Button("Filtrer");

       
        Button clearFiltersButton = new Button("Réinitialiser les filtres");

        filterControls.getChildren().addAll(
                new Label("Rechercher :"), searchField,
                new Label("Catégorie :"), categoryFilter,
                new Label("Date :"), dateFilter,
                new Label("Montant :"), amountFilter,
                filterButton, clearFiltersButton
        );

  
        Button addButton = new Button("+ Ajouter une Dépense");
        addButton.setOnAction(e -> new FormulaireDepense().show());

        //liste de depenses 
        VBox depensesList = new VBox(10);
        List<Depense> depenses = gestionDepenses.listerDepenses();
        updateDepensesList(depensesList, depenses);

    
        filterButton.setOnAction(e -> {
            isFiltering.set(true);
            String searchText = searchField.getText().toLowerCase();
            String selectedCategory = categoryFilter.getValue();
            LocalDate selectedDate = dateFilter.getValue();
            String amountText = amountFilter.getText();

            List<Depense> filteredDepenses = depenses.stream()
                    .filter(depense -> searchText.isEmpty() || depense.getDescription().toLowerCase().contains(searchText))
                    .filter(depense -> selectedCategory == null || depense.getCategorie().equals(selectedCategory))
                    .filter(depense -> selectedDate == null || depense.getDate().equals(selectedDate))
                    .filter(depense -> amountText.isEmpty() || depense.getMontant() == Double.parseDouble(amountText))
                    .collect(Collectors.toList());

            updateDepensesList(depensesList, filteredDepenses);
        });

      
        clearFiltersButton.setOnAction(e -> {
            isFiltering.set(false);
            searchField.clear();
            categoryFilter.setValue(null);
            dateFilter.setValue(null);
            amountFilter.clear();
            updateDepensesList(depensesList, depenses);
        });

       
        contentPane.getChildren().addAll(filterControls, addButton, depensesList);

     
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            if (!isFiltering.get()) {
                // refresh
                List<Depense> updatedDepenses = gestionDepenses.listerDepenses();
                updateDepensesList(depensesList, updatedDepenses);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Met à jour la liste des dépenses affichées.
     *
     */
    private void updateDepensesList(VBox depensesList, List<Depense> depenses) {
        depensesList.getChildren().clear();

        if (depenses.isEmpty()) {
            Label noExpensesLabel = new Label("Aucune dépense trouvée.");
            noExpensesLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #777777;");
            depensesList.getChildren().add(noExpensesLabel);
            return;
        }

        Image modifyIcon = new Image(getClass().getResourceAsStream("/Ressources/modify.png"));
        Image deleteIcon = new Image(getClass().getResourceAsStream("/Ressources/delete.png"));
        Image expenseImage = new Image(getClass().getResourceAsStream("/Ressources/depense.png"));

        TilePane expenseGrid = new TilePane();
        expenseGrid.setPadding(new Insets(20));
        expenseGrid.setHgap(20);
        expenseGrid.setVgap(20);
        expenseGrid.setPrefColumns(3);
        expenseGrid.setStyle("-fx-background-color: #f5f5f5;");

        for (Depense depense : depenses) {
            VBox expenseCard = new VBox(10);
            expenseCard.setPadding(new Insets(15));
            expenseCard.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f9f9f9); " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-radius: 10px; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);"
            );

            expenseCard.setOnMouseEntered(e -> {
                expenseCard.setStyle(
                        "-fx-background-color: linear-gradient(to bottom right, #f0f0f0, #e0e0e0); " +
                        "-fx-border-color: #d0d0d0; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);"
                );
            });
            expenseCard.setOnMouseExited(e -> {
                expenseCard.setStyle(
                        "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f9f9f9); " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);"
                );
            });

            HBox topBox = new HBox(10);
            topBox.setAlignment(Pos.CENTER_LEFT);

            ImageView expenseImageView = new ImageView(expenseImage);
            expenseImageView.setFitWidth(40);
            expenseImageView.setFitHeight(40);
            expenseImageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");

            Label categoryLabel = new Label(depense.getCategorie());
            categoryLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            ImageView modifyIconView = new ImageView(modifyIcon);
            modifyIconView.setFitWidth(20);
            modifyIconView.setFitHeight(20);

            Button modifyButton = new Button("", modifyIconView);
            modifyButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            modifyButton.setOnAction(e -> {
                new FormulaireDepense(depense).show();
                updateContent("Dépenses");
            });

            ImageView deleteIconView = new ImageView(deleteIcon);
            deleteIconView.setFitWidth(20);
            deleteIconView.setFitHeight(20);

            Button deleteButton = new Button("", deleteIconView);
            deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            deleteButton.setOnAction(e -> {
                gestionDepenses.supprimerDepense(depense.getId());
                updateContent("Dépenses");
            });

            topBox.getChildren().addAll(expenseImageView, categoryLabel, spacer, modifyButton, deleteButton);

            Label amountLabel = new Label("$" + String.format("%.2f", depense.getMontant()));
            amountLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #F44336;");

            Label dateLabel = new Label("Date: " + depense.getDate());
            dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777777;");

            Label descriptionLabel = new Label("Description: " + depense.getDescription());
            descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777777;");

            VBox detailsBox = new VBox(5, amountLabel, dateLabel, descriptionLabel);
            detailsBox.setPadding(new Insets(10, 0, 0, 0));

            expenseCard.getChildren().addAll(topBox, detailsBox);
            expenseGrid.getChildren().add(expenseCard);
        }

        depensesList.getChildren().add(expenseGrid);
    }

    /**
     * Affiche la gestion des revenus.
     */
    private void displayRevenus() {
        AtomicBoolean isFiltering = new AtomicBoolean(false);

      
        HBox filterControls = new HBox(10);
        filterControls.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher par source...");

        DatePicker dateFilter = new DatePicker();
        dateFilter.setPromptText("Filtrer par date...");

        TextField amountFilter = new TextField();
        amountFilter.setPromptText("Filtrer par montant...");

        Button filterButton = new Button("Filtrer");

    
        Button clearFiltersButton = new Button("Réinitialiser les filtres");

        filterControls.getChildren().addAll(
                new Label("Rechercher :"), searchField,
                new Label("Date :"), dateFilter,
                new Label("Montant :"), amountFilter,
                filterButton, clearFiltersButton
        );

    
        Button addButton = new Button("+ Ajouter un Revenu");
        addButton.setOnAction(e -> new FormulaireRevenue().show());

   
        VBox revenusList = new VBox(10);
        List<Revenue> revenus = gestionDepenses.listerRevenus();
        updateRevenusList(revenusList, revenus);

   
        filterButton.setOnAction(e -> {
            isFiltering.set(true);
            String searchText = searchField.getText().toLowerCase();
            LocalDate selectedDate = dateFilter.getValue();
            String amountText = amountFilter.getText();

            List<Revenue> filteredRevenus = revenus.stream()
                    .filter(revenue -> searchText.isEmpty() || revenue.getSource().toLowerCase().contains(searchText))
                    .filter(revenue -> selectedDate == null || revenue.getDate().equals(selectedDate))
                    .filter(revenue -> amountText.isEmpty() || revenue.getMontant() == Double.parseDouble(amountText))
                    .collect(Collectors.toList());

            updateRevenusList(revenusList, filteredRevenus);
        });

      
        clearFiltersButton.setOnAction(e -> {
            isFiltering.set(false);
            searchField.clear();
            dateFilter.setValue(null);
            amountFilter.clear();
            updateRevenusList(revenusList, revenus);
        });

      
        contentPane.getChildren().addAll(filterControls, addButton, revenusList);

   
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            if (!isFiltering.get()) {
                
                List<Revenue> updatedRevenus = gestionDepenses.listerRevenus();
                updateRevenusList(revenusList, updatedRevenus);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateRevenusList(VBox revenusList, List<Revenue> revenus) {
        revenusList.getChildren().clear();

        Image modifyIcon = new Image(getClass().getResourceAsStream("/Ressources/modify.png"));
        Image deleteIcon = new Image(getClass().getResourceAsStream("/Ressources/delete.png"));
        Image revenueImage = new Image(getClass().getResourceAsStream("/Ressources/Money.png"));

        TilePane revenueGrid = new TilePane();
        revenueGrid.setPadding(new Insets(20));
        revenueGrid.setHgap(20);
        revenueGrid.setVgap(20);
        revenueGrid.setPrefColumns(3);
        revenueGrid.setStyle("-fx-background-color: #f5f5f5;");

        for (Revenue revenue : revenus) {
            VBox revenueCard = new VBox(10);
            revenueCard.setPadding(new Insets(15));
            revenueCard.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f9f9f9); " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-radius: 10px; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);"
            );

            revenueCard.setOnMouseEntered(e -> {
                revenueCard.setStyle(
                        "-fx-background-color: linear-gradient(to bottom right, #f0f0f0, #e0e0e0); " +
                        "-fx-border-color: #d0d0d0; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);"
                );
            });
            revenueCard.setOnMouseExited(e -> {
                revenueCard.setStyle(
                        "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f9f9f9); " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);"
                );
            });

            HBox topBox = new HBox(10);
            topBox.setAlignment(Pos.CENTER_LEFT);

            ImageView revenueImageView = new ImageView(revenueImage);
            revenueImageView.setFitWidth(40);
            revenueImageView.setFitHeight(40);
            revenueImageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");

            Label sourceLabel = new Label(revenue.getSource());
            sourceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            ImageView modifyIconView = new ImageView(modifyIcon);
            modifyIconView.setFitWidth(20);
            modifyIconView.setFitHeight(20);

            Button modifyButton = new Button("", modifyIconView);
            modifyButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            modifyButton.setOnAction(e -> {
                new FormulaireRevenue(revenue).show();
                updateContent("Revenus");
            });

            ImageView deleteIconView = new ImageView(deleteIcon);
            deleteIconView.setFitWidth(20);
            deleteIconView.setFitHeight(20);

            Button deleteButton = new Button("", deleteIconView);
            deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            deleteButton.setOnAction(e -> {
                gestionDepenses.supprimerRevenue(revenue.getId());
                updateContent("Revenus");
            });

            topBox.getChildren().addAll(revenueImageView, sourceLabel, spacer, modifyButton, deleteButton);

            Label amountLabel = new Label("$" + String.format("%.2f", revenue.getMontant()));
            amountLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

            Label dateLabel = new Label("Date: " + revenue.getDate());
            dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777777;");

            VBox detailsBox = new VBox(5, amountLabel, dateLabel);
            detailsBox.setPadding(new Insets(10, 0, 0, 0));

            revenueCard.getChildren().addAll(topBox, detailsBox);
            revenueGrid.getChildren().add(revenueCard);
        }

        revenusList.getChildren().add(revenueGrid);
    }
    /**
     * Affiche la gestion des catégories avec les budgets.
     */
    private void displayCategories() {
        contentPane.getChildren().clear();
        contentPane.getChildren().clear();

        HBox legendBox = new HBox(10);
        legendBox.setPadding(new Insets(10));
        legendBox.setAlignment(Pos.CENTER_LEFT);

        Circle greenCircle = new Circle(5, Color.BLACK);
        Circle yellowCircle = new Circle(5, Color.ORANGE);
        Circle redCircle = new Circle(5, Color.RED);

        legendBox.getChildren().addAll(
                greenCircle, new Label(": Budget dans les limites"),
                yellowCircle, new Label(": Budget presque épuisé"),
                redCircle, new Label(": Budget dépassé")
        );

        contentPane.getChildren().add(legendBox);

        AtomicBoolean isFiltering = new AtomicBoolean(false);

        Button addButton = new Button("+ Ajouter une Catégorie avec Budget");
        addButton.setOnAction(e -> new GestionCategories().show());

        HBox filterControls = new HBox(10);
        filterControls.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher par nom...");

        Button filterButton = new Button("Filtrer");
        Button clearFilterButton = new Button("Réinitialiser");

        filterControls.getChildren().addAll(new Label("Rechercher :"), searchField, filterButton, clearFilterButton);

        List<String> categories = gestionDepenses.listerCategories();
        VBox categoriesList = new VBox(10);

        updateCategoriesList(categoriesList, categories);

        filterButton.setOnAction(e -> {
            isFiltering.set(true);
            String searchText = searchField.getText().trim().toLowerCase();
            List<String> filteredCategories = categories.stream()
                    .filter(category -> category.toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

            updateCategoriesList(categoriesList, filteredCategories);
        });

        clearFilterButton.setOnAction(e -> {
            isFiltering.set(false);
            searchField.clear();
            updateCategoriesList(categoriesList, categories);
        });

        contentPane.getChildren().addAll(filterControls, addButton, categoriesList);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            if (!isFiltering.get()) {
                List<String> updatedCategories = gestionDepenses.listerCategories();
                updateCategoriesList(categoriesList, updatedCategories);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Met à jour la liste des catégories affichées.
     *
     */
    private void updateCategoriesList(VBox categoriesList, List<String> categories) {
        categoriesList.getChildren().clear();

        HBox categoriesContainer = new HBox(20);
        categoriesContainer.setPadding(new Insets(10));
        categoriesContainer.setAlignment(Pos.CENTER);

        for (String category : categories) {
            double budget = gestionDepenses.obtenirBudgetPourCategorie(category);
            double totalExpenses = gestionDepenses.calculerTotalDepensesParCategorie(category);
            double remainingBudget = budget - totalExpenses;
            double budgetUsagePercentage = (totalExpenses / budget) * 100;

            HBox wifiBars = new HBox(5);
            wifiBars.setAlignment(Pos.CENTER);

            int numberOfBars = 4;
            double barWidth = 10;
            double[] barHeights = {10, 20, 30, 40};

            for (int i = 0; i < numberOfBars; i++) {
                Rectangle bar = new Rectangle(barWidth, barHeights[i]);
                bar.setFill(Color.TRANSPARENT);
                bar.setStroke(Color.LIGHTGRAY);
                bar.setStrokeWidth(1);

                if (budgetUsagePercentage >= (100 / numberOfBars) * (i + 1)) {
                    if (i == numberOfBars - 1 && budgetUsagePercentage >= 100) {
                        bar.setFill(Color.RED);
                    } else if (i == numberOfBars - 2 && budgetUsagePercentage >= 80) {
                        bar.setFill(Color.ORANGE);
                    } else {
                        bar.setFill(Color.BLACK);
                    }
                }

                wifiBars.getChildren().add(bar);
            }

            Label categoryLabel = new Label(category);
            categoryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Label budgetLabel = new Label("Budget: $" + String.format("%.2f", budget));
            Label expensesLabel = new Label("Dépenses: $" + String.format("%.2f", totalExpenses));
            Label remainingBudgetLabel = new Label("Restant: $" + String.format("%.2f", remainingBudget));

            Image editIcon = new Image(getClass().getResourceAsStream("/Ressources/modify.png"));
            ImageView editIconView = new ImageView(editIcon);
            editIconView.setFitWidth(16);
            editIconView.setFitHeight(16);

            Button modifyButton = new Button("Modifier", editIconView);
            modifyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px;");
            modifyButton.setOnAction(e -> {
                TextInputDialog dialog = new TextInputDialog(String.valueOf(budget));
                dialog.setTitle("Modifier la catégorie");
                dialog.setHeaderText("Modifier le budget pour " + category);
                dialog.setContentText("Nouveau budget:");

                dialog.showAndWait().ifPresent(newBudget -> {
                    try {
                        double updatedBudget = Double.parseDouble(newBudget);
                        gestionDepenses.definirBudget(category, updatedBudget);
                        updateContent("Catégories");
                    } catch (NumberFormatException ex) {
                        afficherAlerte("Erreur", "Le budget doit être un nombre valide.", Alert.AlertType.ERROR);
                    }
                });
            });

            Image deleteIcon = new Image(getClass().getResourceAsStream("/Ressources/delete.png"));
            ImageView deleteIconView = new ImageView(deleteIcon);
            deleteIconView.setFitWidth(16);
            deleteIconView.setFitHeight(16);

            Button deleteButton = new Button("Supprimer", deleteIconView);
            deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 12px;");
            deleteButton.setOnAction(e -> {
                gestionDepenses.supprimerCategorie(category);
                updateContent("Catégories");
            });

            VBox categoryBox = new VBox(10, wifiBars, categoryLabel, budgetLabel, expensesLabel, remainingBudgetLabel, modifyButton, deleteButton);
            categoryBox.setAlignment(Pos.CENTER);
            categoryBox.setPadding(new Insets(10));
            categoryBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px; -fx-background-color: #f9f9f9;");

            categoriesContainer.getChildren().add(categoryBox);
        }

        categoriesList.getChildren().add(categoriesContainer);
    }
    /**
     * Affiche la gestion des budgets.
     */
    private void displayBudgets() {
        contentPane.getChildren().clear();

        HBox topPanel = new HBox(10);
        topPanel.setPadding(new Insets(10));
        topPanel.setAlignment(Pos.CENTER_LEFT);

        Button addButton = new Button("Définir un Budget Mensuel Global");
        addButton.setOnAction(e -> new GestionBudgets().show());

        Circle greenCircle = new Circle(5, Color.GREEN);
        Circle yellowCircle = new Circle(5, Color.ORANGE);
        Circle redCircle = new Circle(5, Color.RED);

        topPanel.getChildren().addAll(
                addButton,
                greenCircle, new Label(": Budget dans les limites"),
                yellowCircle, new Label(": Budget presque épuisé"),
                redCircle, new Label(": Budget dépassé")
        );

        double budgetGlobal = gestionDepenses.obtenirBudgetPourCategorie("Global");
        double totalDepenses = gestionDepenses.calculerTotalDepensesMensuels(LocalDate.now());
        double remainingBudget = budgetGlobal - totalDepenses;

        double budgetUsagePercentage = (totalDepenses / budgetGlobal) * 100;

        VBox budgetCard = new VBox(10);
        budgetCard.setPadding(new Insets(15));
        budgetCard.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-radius: 5px;");
        budgetCard.setAlignment(Pos.CENTER);

        ImageView budgetIcon = new ImageView(new Image(getClass().getResourceAsStream("/Ressources/budget.png")));
        budgetIcon.setFitWidth(400);
        budgetIcon.setFitHeight(400);
        budgetIcon.setSmooth(true);
        budgetIcon.setPreserveRatio(true);

        Label cardTitle = new Label("Budget Mensuel Global");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label budgetDetails = new Label(
                " Budget: $" + String.format("%.2f", budgetGlobal) + "\n" +
                        "Dépenses: $" + String.format("%.2f", totalDepenses) + "\n" +
                        "Restant: $" + String.format("%.2f", remainingBudget)
        );
        budgetDetails.setStyle("-fx-font-size: 14px;");

        ProgressBar progressBar = new ProgressBar(budgetUsagePercentage / 100);
        progressBar.setPrefWidth(300);

        if (remainingBudget <= 0) {
            progressBar.setStyle("-fx-accent: red;");
        } else if (budgetUsagePercentage >= 80) {
            progressBar.setStyle("-fx-accent: orange;");
        } else {
            progressBar.setStyle("-fx-accent: green;");
        }

        budgetCard.getChildren().addAll(budgetIcon, cardTitle, budgetDetails, progressBar);

        if (remainingBudget <= 0) {
            if (!budgetExceededAlertShown) {
                afficherAlerte("Alerte", "Le budget global a été dépassé !", Alert.AlertType.WARNING);
                budgetExceededAlertShown = true;
            }
        } else {
            budgetExceededAlertShown = false;
        }

        if (remainingBudget > 0 && remainingBudget < budgetGlobal * 0.1) {
            if (!budgetNearlyExhaustedAlertShown) {
                afficherAlerte("Avertissement", "Le budget global est presque épuisé !", Alert.AlertType.WARNING);
                budgetNearlyExhaustedAlertShown = true;
            }
        } else {
            budgetNearlyExhaustedAlertShown = false;
        }

        StackPane centerContainer = new StackPane();
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.getChildren().add(budgetCard);

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(10));
        mainContainer.getChildren().addAll(topPanel, centerContainer);

        contentPane.getChildren().add(mainContainer);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            double updatedBudgetGlobal = gestionDepenses.obtenirBudgetPourCategorie("Global");
            double updatedTotalDepenses = gestionDepenses.calculerTotalDepensesMensuels(LocalDate.now());
            double updatedRemainingBudget = updatedBudgetGlobal - updatedTotalDepenses;

            budgetDetails.setText(
                    " Budget: $" + String.format("%.2f", updatedBudgetGlobal) + "\n" +
                            "Dépenses: $" + String.format("%.2f", updatedTotalDepenses) + "\n" +
                            "Restant: $" + String.format("%.2f", updatedRemainingBudget)
            );

            double updatedBudgetUsagePercentage = (updatedTotalDepenses / updatedBudgetGlobal) * 100;
            progressBar.setProgress(updatedBudgetUsagePercentage / 100);

            if (updatedRemainingBudget <= 0) {
                progressBar.setStyle("-fx-accent: red;");
            } else if (updatedBudgetUsagePercentage >= 80) {
                progressBar.setStyle("-fx-accent: orange;");
            } else {
                progressBar.setStyle("-fx-accent: green;");
            }

            if (updatedRemainingBudget <= 0) {
                if (!budgetExceededAlertShown) {
                    afficherAlerte("Alerte", "Le budget global a été dépassé !", Alert.AlertType.ERROR);
                    budgetExceededAlertShown = true;
                }
            } else {
                budgetExceededAlertShown = false;
            }

            if (updatedRemainingBudget > 0 && updatedRemainingBudget < updatedBudgetGlobal * 0.1) {
                if (!budgetNearlyExhaustedAlertShown) {
                    afficherAlerte("Avertissement", "Le budget global est presque épuisé !", Alert.AlertType.WARNING);
                    budgetNearlyExhaustedAlertShown = true;
                }
            } else {
                budgetNearlyExhaustedAlertShown = false;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    /**
     * Affiche les rapports.
     */
    private void displayRapports() {
        contentPane.getChildren().clear();

        Label titleLabel = new Label("Rapports et Statistiques");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label pieChartLabel = new Label("Répartition des Dépenses par Catégorie");
        pieChartLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Dépenses par Catégorie");
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
        pieChart.setStyle("-fx-font-size: 14px;");

        Map<String, Double> expensesByCategory = new HashMap<>();
        for (Depense depense : gestionDepenses.listerDepenses()) {
            expensesByCategory.put(
                depense.getCategorie(),
                expensesByCategory.getOrDefault(depense.getCategorie(), 0.0) + depense.getMontant()
            );
        }

        double totalExpenses = expensesByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            double percentage = (entry.getValue() / totalExpenses) * 100;
            pieChart.getData().add(new PieChart.Data(entry.getKey() + " (" + String.format("%.2f", percentage) + "%)", entry.getValue()));
        }

        Label barChartLabel = new Label("Statistiques Mensuelles des Dépenses");
        barChartLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mois");
        xAxis.setStyle("-fx-font-size: 14px;");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Montant ($)");
        yAxis.setStyle("-fx-font-size: 14px;");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Dépenses Mensuelles");
        barChart.setLegendVisible(false);
        barChart.setStyle("-fx-font-size: 14px;");

        XYChart.Series<String, Number> monthlySeries = new XYChart.Series<>();
        monthlySeries.setName("Dépenses Mensuelles");

        Map<YearMonth, Double> monthlyExpenses = new HashMap<>();
        for (Depense depense : gestionDepenses.listerDepenses()) {
            YearMonth month = YearMonth.from(depense.getDate());
            monthlyExpenses.put(month, monthlyExpenses.getOrDefault(month, 0.0) + depense.getMontant());
        }

        for (Map.Entry<YearMonth, Double> entry : monthlyExpenses.entrySet()) {
            monthlySeries.getData().add(new XYChart.Data<>(entry.getKey().format(DateTimeFormatter.ofPattern("MMM yyyy")), entry.getValue()));
        }

        barChart.getData().add(monthlySeries);

        Label categoryDetailsLabel = new Label("Détails des Dépenses par Catégorie");
        categoryDetailsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        VBox categoryDetailsBox = new VBox(10);
        categoryDetailsBox.setPadding(new Insets(20));
        categoryDetailsBox.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            String category = entry.getKey();
            double total = entry.getValue();
            double percentage = (total / totalExpenses) * 100;

            Label categoryLabel = new Label(category + ": $" + String.format("%.2f", total) + " (" + String.format("%.2f", percentage) + "%)");
            categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

            categoryDetailsBox.getChildren().add(categoryLabel);
        }

        Label monthlyBreakdownLabel = new Label("Détails Mensuels des Dépenses");
        monthlyBreakdownLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        VBox monthlyBreakdownBox = new VBox(10);
        monthlyBreakdownBox.setPadding(new Insets(20));
        monthlyBreakdownBox.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        for (Map.Entry<YearMonth, Double> entry : monthlyExpenses.entrySet()) {
            String month = entry.getKey().format(DateTimeFormatter.ofPattern("MMM yyyy"));
            double total = entry.getValue();
            double percentage = (total / totalExpenses) * 100;

            Label monthLabel = new Label(month + ": $" + String.format("%.2f", total) + " (" + String.format("%.2f", percentage) + "%)");
            monthLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

            monthlyBreakdownBox.getChildren().add(monthLabel);
        }

        Label summaryLabel = new Label("Résumé des Dépenses");
        summaryLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        double totalBudget = gestionDepenses.getBudgetTotal();
        double remainingBudget = totalBudget - totalExpenses;

        Label totalExpensesLabel = new Label("Dépenses Totales: $" + String.format("%.2f", totalExpenses));
        totalExpensesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        Label totalBudgetLabel = new Label("Budget Total: $" + String.format("%.2f", totalBudget));
        totalBudgetLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        Label remainingBudgetLabel = new Label("Budget Restant: $" + String.format("%.2f", remainingBudget));
        remainingBudgetLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (remainingBudget < 0 ? "#e74c3c" : "#2ecc71") + ";");

        VBox summaryBox = new VBox(10, summaryLabel, totalExpensesLabel, totalBudgetLabel, remainingBudgetLabel);
        summaryBox.setPadding(new Insets(20));
        summaryBox.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        VBox chartsBox = new VBox(20, titleLabel, pieChartLabel, pieChart, barChartLabel, barChart, categoryDetailsLabel, categoryDetailsBox, monthlyBreakdownLabel, monthlyBreakdownBox, summaryBox);
        chartsBox.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(chartsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #ffffff; -fx-border-color: #ffffff;");

        contentPane.getChildren().add(scrollPane);
    }
    /**
     * Affiche une alerte avec un message.
     *
     */
    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(titre);
            alert.setHeaderText(null);
            alert.setContentText(message);

          
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/Style/alert.css").toExternalForm());

          
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
} 