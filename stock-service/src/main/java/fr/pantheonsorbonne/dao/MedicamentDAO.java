package fr.pantheonsorbonne.dao;

import fr.pantheonsorbonne.entity.Medicament;
import fr.pantheonsorbonne.entity.Stock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class MedicamentDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Medicament> findByPharmacy(String pharmacyId) {
        return entityManager.createQuery(
                        "SELECT m FROM Medicament m JOIN Stock s ON m.id = s.medicamentId WHERE s.pharmacyId = :pharmacyId AND s.quantity > 0", Medicament.class)
                .setParameter("pharmacyId", pharmacyId)
                .getResultList();
    }

    public Integer getMedicamentQuantity(String pharmacyId, String medicamentId) {
        return entityManager.createQuery(
                        "SELECT s.quantity FROM Stock s WHERE s.pharmacyId = :pharmacyId AND s.medicamentId = :medicamentId", Integer.class)
                .setParameter("pharmacyId", pharmacyId)
                .setParameter("medicamentId", medicamentId)
                .getResultStream()
                .findFirst()
                .orElse(0);
    }
}
