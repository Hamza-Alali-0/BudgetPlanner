package Depenses;

import java.time.LocalDate;

public class Depense {
	 private  int Id;
    private double montant;
    private LocalDate date;
    private String categorie;
    private String description;

    // Constructeurs
    public Depense() {}

    public Depense(int id,double montant, LocalDate date, String categorie, String description) {
    	this.Id=id;
        this.montant = montant;
        this.date = date;
        this.categorie = categorie;
        this.description = description;
    }

    // Getters et Setters
    
    public double getMontant() {
        return montant;
    }

    public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Depense [Montant=" + montant + ", Date=" + date + ", Catégorie=" + categorie + ", Description=" + description + "]";
    }
}