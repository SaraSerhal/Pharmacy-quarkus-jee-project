package fr.pantheonsorbonne.dao;

import fr.pantheonsorbonne.entity.Pharmacy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class PharmacyDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<String> findAllPharmacyIds() {
        return entityManager.createQuery("SELECT p.id FROM Pharmacy p", String.class)
                .getResultList();
    }

    public Pharmacy findPharmacyById(String pharmacyId) {
        return entityManager.find(Pharmacy.class, pharmacyId);
    }
}