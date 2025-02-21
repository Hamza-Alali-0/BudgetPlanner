package Exceptions;

import Depenses.Depense;
import Depenses.GestionDepenses;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;

public class ValidationDonnees {

    /**
     * Valider une depense en verifiant le montant, la date, la catégorie et le budget.
     *
     */
    public static boolean validerDepense(Depense depense, GestionDepenses gestionDepenses) {
        // Validation du montant
        if (depense.getMontant() <= 0) {
            afficherAlerte("Erreur", "Le montant doit être un nombre positif.");
            return false;
        }

        // Validation de  date
        if (depense.getDate().isAfter(LocalDate.now())) {
            afficherAlerte("Erreur", "La date ne peut pas être dans le futur.");
            return false;
        }

        // Validation de  catégorie
        if (depense.getCategorie() == null || depense.getCategorie().trim().isEmpty()) {
            afficherAlerte("Erreur", "La catégorie est obligatoire.");
            return false;
        }

        // Validation du budget de la catégorie
        double budget = gestionDepenses.obtenirBudgetPourCategorie(depense.getCategorie());
        if (budget > 0) { 
            double totalDepenses = gestionDepenses.calculerTotalDepensesParCategorie(depense.getCategorie());

            //  modifier une dépense existante
            if (depense.getId() != 0) {
                Depense oldDepense = gestionDepenses.obtenirDepense(depense.getId());
                if (oldDepense != null) {
                    totalDepenses -= oldDepense.getMontant();
                }
            }

        }

        return true;
    }

    /**
     * Affiche une alerte avec un message.
     *
     */
    private static void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}