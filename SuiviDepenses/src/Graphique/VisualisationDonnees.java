package Graphique;

import Depenses.GestionDepenses;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

public class VisualisationDonnees extends Stage {

    public VisualisationDonnees() {
        setTitle("Visualisation des Données");

        // Créer un diagramme circulaire
        PieChart pieChart = new PieChart();
        GestionDepenses gestionDepenses = new GestionDepenses();
        for (String categorie : gestionDepenses.listerCategories()) {
            double total = gestionDepenses.calculerTotalDepensesParCategorie(categorie);
            pieChart.getData().add(new PieChart.Data(categorie, total));
        }

        // Afficher la scène
        Scene scene = new Scene(pieChart, 600, 400);
        setScene(scene);
    }

	
}