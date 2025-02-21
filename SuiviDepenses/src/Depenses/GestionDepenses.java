package Depenses;

import Basedonnes.ConnexionBDD;
import Exceptions.ValidationDonnees;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.util.Duration;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestionDepenses {
	
	/**
	 * 	 Depenses
	 */

	public void ajouterDepense(Depense depense) {
	    if (ValidationDonnees.validerDepense(depense, this)) {
	        // recuperer budget et total depenses de la category
	        double totalDepenses = calculerTotalDepensesParCategorie(depense.getCategorie());
	        double budget = obtenirBudgetPourCategorie(depense.getCategorie());

	        if (budget > 0 && (totalDepenses + depense.getMontant()) > budget) {
	            // afficher un texte d'avertisssement
	            Alert warningDialog = new Alert(Alert.AlertType.CONFIRMATION);
	            warningDialog.setTitle("Avertissement de Budget");
	            warningDialog.setHeaderText("Dépassement de Budget");
	            warningDialog.setContentText("Le montant de cette dépense dépasse le budget pour la catégorie " + depense.getCategorie() + ". Êtes-vous sûr de vouloir continuer ?");

	            Optional<ButtonType> result = warningDialog.showAndWait();
	            if (result.isPresent() && result.get() != ButtonType.OK) {
	                // quitter si utilisateur ne comfirme pas 
	                return;
	            }
	        }

	        // Psinon ajouter la depense a la category 
	        String sql = "INSERT INTO depenses (montant, date, categorie, description) VALUES (?, ?, ?, ?)";
	        try (Connection conn = ConnexionBDD.getConnexion();
	             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            pstmt.setDouble(1, depense.getMontant());
	            pstmt.setDate(2, java.sql.Date.valueOf(depense.getDate()));
	            pstmt.setString(3, depense.getCategorie());
	            pstmt.setString(4, depense.getDescription());
	            pstmt.executeUpdate();

	            ResultSet generatedKeys = pstmt.getGeneratedKeys();
	            if (generatedKeys.next()) {
	                depense.setId(generatedKeys.getInt(1));
	            }

	            System.out.println("Dépense ajoutée avec succès !");

	            // Affficher les alertes de de categorie
	            if (budget > 0) {
	                if (totalDepenses + depense.getMontant() >= budget) {
	                    afficherAlerte("Budget dépassé", "Le budget pour la catégorie " + depense.getCategorie() + " a été dépassé !");
	                } else if (totalDepenses + depense.getMontant() >= budget * 0.9) {
	                    afficherAlerte("Budget proche", "Le budget pour la catégorie " + depense.getCategorie() + " est presque épuisé !");
	                }
	            }
	        } catch (SQLException e) {
	            afficherAlerte("Erreur", "Erreur lors de l'ajout de la dépense : " + e.getMessage());
	        }
	    } else {
	        afficherAlerte("Erreur", "La dépense n'a pas été ajoutée car elle est invalide.");
	    }
	}
   
	// Modifier une dépense
	
	public void modifierDepense(int id, Depense nouvelleDepense) {
	    // si depense existe pas 
	    Depense oldDepense = obtenirDepense(id);
	    if (oldDepense == null) {
	        afficherAlerte("Erreur", "La dépense avec l'ID " + id + " n'existe pas.");
	        return;
	    }

	    // si erreur de depense mnt inf a 0 ..
	    if (!ValidationDonnees.validerDepense(nouvelleDepense, this)) {
	        afficherAlerte("Erreur", "La dépense n'a pas été modifiée car elle est invalide.");
	        return;
	    }

	    // si changement de categorie de nvl depense 
	    if (!oldDepense.getCategorie().equals(nouvelleDepense.getCategorie())) {
	        afficherAlerte("Erreur", "La catégorie de la dépense ne peut pas être modifiée.");
	        return;
	    }

	    // calculer difference 
	    double difference = nouvelleDepense.getMontant() - oldDepense.getMontant();

	    // re voir si nv depense surpasse category 
	    double totalDepenses = calculerTotalDepensesParCategorie(nouvelleDepense.getCategorie());
	    double budget = obtenirBudgetPourCategorie(nouvelleDepense.getCategorie());

	    if (budget > 0 && (totalDepenses + difference) > budget) {
	        // afficher alertes 
	        Alert warningDialog = new Alert(Alert.AlertType.CONFIRMATION);
	        warningDialog.setTitle("Avertissement de Budget");
	        warningDialog.setHeaderText("Dépassement de Budget");
	        warningDialog.setContentText("Le montant de cette dépense dépasse le budget pour la catégorie " + nouvelleDepense.getCategorie() + ". Êtes-vous sûr de vouloir continuer ?");

	        Optional<ButtonType> result = warningDialog.showAndWait();
	        if (result.isPresent() && result.get() != ButtonType.OK) {
	        	// quitter si utilisateur ne comfirme pas 
	            return;
	        }
	    }

	    // sinon mettre a jour 
	    
	    String sql = "UPDATE depenses SET montant = ?, date = ?, categorie = ?, description = ? WHERE id = ?";
	    try (Connection conn = ConnexionBDD.getConnexion();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setDouble(1, nouvelleDepense.getMontant());
	        pstmt.setDate(2, java.sql.Date.valueOf(nouvelleDepense.getDate()));
	        pstmt.setString(3, nouvelleDepense.getCategorie());
	        pstmt.setString(4, nouvelleDepense.getDescription());
	        pstmt.setInt(5, id);
	        int rowsUpdated = pstmt.executeUpdate();
	        if (rowsUpdated > 0) {
	            afficherAlerte("Succès", "Dépense modifiée avec succès !");
	        } else {
	            afficherAlerte("Erreur", "La dépense avec l'ID " + id + " n'existe pas.");
	        }
	    } catch (SQLException e) {
	        afficherAlerte("Erreur", "Erreur lors de la modification de la dépense : " + e.getMessage());
	    }
	}
	
	
    // Supprimer une dépense 
	
	public void supprimerDepense(int id) {
	    // alerte de comfirmation
		
	    Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
	    confirmationDialog.setTitle("Confirmation de suppression");
	    confirmationDialog.setHeaderText(null);
	    confirmationDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette dépense ?");

	    // style
	    DialogPane dialogPane = confirmationDialog.getDialogPane();
	    dialogPane.getStylesheets().add(getClass().getResource("/Style/alert.css").toExternalForm());
	    dialogPane.getStyleClass().add("confirmation");
	    Optional<ButtonType> result = confirmationDialog.showAndWait();

	    // si utilisateur comfirme
	    
	    if (result.isPresent() && result.get() == ButtonType.OK) {
	        String sql = "DELETE FROM depenses WHERE id = ?";
	        try (Connection conn = ConnexionBDD.getConnexion();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setInt(1, id);
	            int rowsDeleted = pstmt.executeUpdate();
	            if (rowsDeleted > 0) {
	                afficherAlerte("Succès", "Dépense supprimée avec succès !");
	            } else {
	                afficherAlerte("Erreur", "La dépense avec l'ID " + id + " n'existe pas.");
	            }
	        } catch (SQLException e) {
	            afficherAlerte("Erreur", "Erreur lors de la suppression de la dépense : " + e.getMessage());
	        }
	    } else {
	        
	        afficherAlerte("Annulé", "Suppression de la dépense annulée.");
	    }
	}
	
	
    // Obtenir une dépense par ID
	
    public Depense obtenirDepense(int id) {
        String sql = "SELECT * FROM depenses WHERE id = ?";
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Depense(
                    rs.getInt("id"), 
                    rs.getDouble("montant"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("categorie"),
                    rs.getString("description")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la dépense : " + e.getMessage());
        }
        return null; 
    }

    // Lister toutes les depenses

    	public List<Depense> listerDepenses() {
    	    List<Depense> depenses = new ArrayList<>();
    	    String sql = "SELECT * FROM depenses";
    	    try (Connection conn = ConnexionBDD.getConnexion();
    	         Statement stmt = conn.createStatement();
    	         ResultSet rs = stmt.executeQuery(sql)) {
    	        while (rs.next()) {
    	            Depense depense = new Depense(
    	                rs.getInt("id"), // Include the ID
    	                rs.getDouble("montant"),
    	                rs.getDate("date").toLocalDate(),
    	                rs.getString("categorie"),
    	                rs.getString("description")
    	            );
    	            depenses.add(depense);
    	        }
    	    } catch (SQLException e) {
    	        System.out.println("Erreur lors de la récupération des dépenses : " + e.getMessage());
    	    }
    	    return depenses;
    	}
    	
    	
    	 // Calculer le total des dépenses pour un mois
    	
        public double calculerTotalDepensesMensuels(LocalDate mois) {
            double total = 0;
            String sql = "SELECT SUM(montant) AS total FROM depenses WHERE MONTH(date) = ? AND YEAR(date) = ?";
            try (Connection conn = ConnexionBDD.getConnexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, mois.getMonthValue()); // Mois (1-12)
                pstmt.setInt(2, mois.getYear());       // Annee
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors du calcul des dépenses mensuelles : " + e.getMessage());
            }
            return total;
        }
        
   	 // Calculer le total des dépenses pour pour une categorie 
    	

        public double calculerTotalDepensesParCategorie(String categorie) {
            double total = 0.0;
            String sql = "SELECT SUM(montant) AS total FROM depenses WHERE categorie = ?";
            try (Connection conn = ConnexionBDD.getConnexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, categorie);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Erreur lors du calcul des dépenses totales : " + e.getMessage());
            }
            return total;
        }

    	

   

  
/**
 * 
 *  Categories
 */
    
   
    
    
    // Ajouter une catégorie
        
    public void ajouterCategorie(String nomCategorie) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomCategorie);
            pstmt.executeUpdate();
            System.out.println("Catégorie ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la catégorie : " + e.getMessage());
        }
    }

    // Modifier une catégorie
    
    public void modifierCategorie(int id, String nouveauNom) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nouveauNom);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Catégorie modifiée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de la catégorie : " + e.getMessage());
        }
    }

    // Supprimer une catégorie 
    
    public void supprimerCategorie(String nomCategorie) {
        // alerte comfirmation
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation de suppression");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Êtes-vous sûr de vouloir supprimer la catégorie \"" + nomCategorie + "\" ?");

        //style
        DialogPane dialogPane = confirmationDialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/Style/alert.css").toExternalForm());
       dialogPane.getStyleClass().add("confirmation");
        Optional<ButtonType> result = confirmationDialog.showAndWait();

        // comfirme
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM categories WHERE name = ?";
            try (Connection conn = ConnexionBDD.getConnexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nomCategorie);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    afficherAlerte("Succès", "Catégorie supprimée avec succès !");
                } else {
                    afficherAlerte("Erreur", "La catégorie \"" + nomCategorie + "\" n'existe pas.");
                }
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Erreur lors de la suppression de la catégorie : " + e.getMessage());
            }
        } else {
            
            afficherAlerte("Annulé", "Suppression de la catégorie annulée.");
        }
    }
    
    // Lister toutes les catégories
    
    public List<String> listerCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT name FROM categories";
        try (Connection conn = ConnexionBDD.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Erreur lors de la récupération des catégories : " + e.getMessage());
        }
        return categories;
    }
    
    // Vérifier si une categorie existe
    
    public boolean categorieExiste(String nomCategorie) {
        String sql = "SELECT COUNT(*) AS count FROM categories WHERE name = ?";
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomCategorie);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de la catégorie : " + e.getMessage());
        }
        return false;
    }
    

    // Obtenir l'ID d'une catégorie 
    
    public int obtenirIdCategorie(String nomCategorie) {
        String sql = "SELECT id FROM categories WHERE name = ?";
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomCategorie);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'ID de la catégorie : " + e.getMessage());
        }
        return -1; 
    }

   



   /**
    * 
    *  revenus
    */
 
	
	
    // Ajouter un revenu
    
    public void ajouterRevenue(Revenue revenue) {
        String sql = "INSERT INTO revenus (source, montant, date) VALUES (?, ?, ?)";
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, revenue.getSource());
            pstmt.setDouble(2, revenue.getMontant());
            pstmt.setDate(3, java.sql.Date.valueOf(revenue.getDate()));
            pstmt.executeUpdate();
            System.out.println("Revenu ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du revenu : " + e.getMessage());
        }
    }
    
    //Lister revenus
    
    public List<Revenue> listerRevenus() {
        List<Revenue> revenus = new ArrayList<>();
        String sql = "SELECT * FROM revenus";
        try (Connection conn = ConnexionBDD.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Revenue revenue = new Revenue(
                    rs.getInt("id"), 
                    rs.getString("source"),
                    rs.getDouble("montant"),
                    rs.getDate("date").toLocalDate()
                );
                revenus.add(revenue);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des revenus : " + e.getMessage());
        }
        return revenus;
    }
    
    //Modifier revenu
    
    public void modifierRevenue(int id, Revenue revenue) {
        String sql = "UPDATE revenus SET source = ?, montant = ?, date = ? WHERE id = ?";
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, revenue.getSource());
            pstmt.setDouble(2, revenue.getMontant());
            pstmt.setDate(3, java.sql.Date.valueOf(revenue.getDate()));
            pstmt.setInt(4, id);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Revenu modifié avec succès !");
            } else {
                System.out.println("Erreur : Le revenu avec l'ID " + id + " n'existe pas.");
            }
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Erreur lors de la modification du revenu : " + e.getMessage());
        }
    }
    
   //suprimer revenu
    
    public void supprimerRevenue(int id) {
        // alerte 
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation de suppression");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce revenu ?");

      
        DialogPane dialogPane = confirmationDialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/Style/alert.css").toExternalForm());
        dialogPane.getStyleClass().add("confirmation");
        Optional<ButtonType> result = confirmationDialog.showAndWait();

      
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM revenus WHERE id = ?";
            try (Connection conn = ConnexionBDD.getConnexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    afficherAlerte("Succès", "Revenu supprimé avec succès !");
                } else {
                    afficherAlerte("Erreur", "Le revenu avec l'ID " + id + " n'existe pas.");
                }
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Erreur lors de la suppression du revenu : " + e.getMessage());
            }
        } else {
           
            afficherAlerte("Annulé", "Suppression du revenu annulée.");
        }
    }
    
    
    // Calculer le total des revenus pour un mois
    
    public double calculerTotalRevenusMensuels(LocalDate mois) {
        double total = 0;
        String sql = "SELECT SUM(montant) AS total FROM revenus WHERE MONTH(date) = ? AND YEAR(date) = ?";
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mois.getMonthValue());
            pstmt.setInt(2, mois.getYear());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du calcul des revenus mensuels : " + e.getMessage());
        }
        return total;
    }

    
    /**
     * 
     *  Tableau de Bord
     */
    
   

    public double getBudgetTotal() {
        double totalBudget = 0.0;
        String sql = "SELECT SUM(montant_max) AS total FROM budgets WHERE  categorie = 'Global'";
        try (Connection conn = ConnexionBDD.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                totalBudget = rs.getDouble("total");
            }
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Erreur lors de la récupération du budget total : " + e.getMessage());
        }
        return totalBudget;
    }
    
    // Calculer l'épargne nette (revenus - dépenses)
    
    public double calculerEpargneNette(LocalDate mois) {
        double revenus = calculerTotalRevenusMensuels(mois);
        double depenses = calculerTotalDepensesMensuels(mois);
        return revenus - depenses;
    }
    
    public double calculerBudgetRestant() {
        LocalDate now = LocalDate.now();
        double totalRevenus = calculerTotalRevenusMensuels(now); 
        double totalDepenses = calculerTotalDepensesMensuels(now); 
        return totalRevenus - totalDepenses; 
    }
    
    
    public void definirBudget(String categorie, double montantMax) {
        String sql;
        if (categorieExiste(categorie)) {
            // UPDATE: montant_max comes first, followed by categorie
            sql = "UPDATE budgets SET montant_max = ? WHERE categorie = ?";
        } else {
            // INSERT: categorie comes first, followed by montant_max
            sql = "INSERT INTO budgets (categorie, montant_max) VALUES (?, ?)";
        }

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (categorieExiste(categorie)) {
                // For UPDATE: Set montant_max first, then categorie
                pstmt.setDouble(1, montantMax);
                pstmt.setString(2, categorie);
            } else {
                // For INSERT: Set categorie first, then montant_max
                pstmt.setString(1, categorie);
                pstmt.setDouble(2, montantMax);
            }

            pstmt.executeUpdate();
            System.out.println("Budget défini avec succès !");
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Erreur lors de la définition du budget : " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    public double obtenirBudgetPourCategorie(String categorie) {
        double budget = 0.0;
        String sql = "SELECT montant_max FROM budgets WHERE categorie = ?";
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categorie);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                budget = rs.getDouble("montant_max");
            }
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Erreur lors de la récupération du budget : " + e.getMessage());
        }
        return budget;
    }
    public double calculerBudgetRestantGlobal() {
        double budgetGlobal = obtenirBudgetPourCategorie("Global");
        double totalDepenses = calculerTotalDepensesMensuels(LocalDate.now());
        return budgetGlobal - totalDepenses;
    }

    public void verifierBudgetGlobal() {
        double budgetRestant = calculerBudgetRestantGlobal();
        double budgetGlobal = obtenirBudgetPourCategorie("Global");

        if (budgetRestant <= 0) {
            afficherAlerte("Alerte", "Le budget global a été dépassé !");
        } else if (budgetRestant > 0 && budgetRestant < budgetGlobal * 0.1) {
            afficherAlerte("Avertissement", "Le budget global est presque épuisé !");
        }
    }
 
    /**
     * 
     *  Rappels
     */
    
    public void configurerRappels(Duration interval) {
        System.out.println("Configuring reminders with interval: " + interval.toMinutes() + " minutes");
        Timeline rappel = new Timeline(new KeyFrame(interval, e -> {
            Platform.runLater(() -> {
                System.out.println("Reminder triggered at: " + LocalTime.now());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rappel");
                alert.setHeaderText(null);
                alert.setContentText("N'oubliez pas d'enregistrer vos dépenses !");
                alert.showAndWait();
            });
        }));
        rappel.setCycleCount(Timeline.INDEFINITE);
        rappel.play();
        System.out.println("Timeline started");
    }
    
    // Afficher les alerte
    
    public void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
}