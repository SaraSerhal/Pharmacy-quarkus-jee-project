package fr.pantheonsorbonne.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Stock {

    @Id
    private String pharmacyId;

    @Id
    private String medicamentId;

    @ManyToOne
    @JoinColumn(name = "medicamentId", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    private Medicament medicament;
    private int quantity;


    public String getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(String pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Medicament getMedicament() {
        return medicament;
    }

    public void setMedicament(Medicament medicament) {
        this.medicament = medicament;
    }
}
