package Depenses;

public class Budget {
    private String categorie;
    private double montantMax;

    // Constructeur
    public Budget(String categorie, double montantMax) {
        this.categorie = categorie;
        this.montantMax = montantMax;
    }

    // Getters et Setters
    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public double getMontantMax() {
        return montantMax;
    }

    public void setMontantMax(double montantMax) {
        this.montantMax = montantMax;
    }

    @Override
    public String toString() {
        return "Budget [Catégorie=" + categorie + ", MontantMax=" + montantMax + "]";
    }
}