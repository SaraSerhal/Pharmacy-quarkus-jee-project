package fr.pantheonsorbonne.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Stock {

    @Id
    private String pharmacyId;

    @Id
    private String medicamentId;

    private int quantity;


    public String getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(String pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public String getMedicamentId() {
        return medicamentId;
    }

    public void setMedicamentId(String medicamentId) {
        this.medicamentId = medicamentId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
