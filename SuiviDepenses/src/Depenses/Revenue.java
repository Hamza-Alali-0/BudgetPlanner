package Depenses;

import java.time.LocalDate;

public class Revenue {
    private int id;
    private String source;
    private double montant;
    private LocalDate date;

    // Constructeurs
    public Revenue() {}

    public Revenue(int id,String source, double montant, LocalDate date) {
    	this.id=id;
        this.source = source;
        this.montant = montant;
        this.date = date;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getMontant() {
        return montant;
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

    @Override
    public String toString() {
        return "Revenue [Source=" + source + ", Montant=" + montant + ", Date=" + date + "]";
    }
}