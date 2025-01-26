package fr.pantheonsorbonne.dao;

import fr.pantheonsorbonne.entity.Medicament;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class MedicamentDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Medicament> findAvailableMedicaments(String pharmacyId, List<String> medicamentNames) {
        return entityManager.createQuery(
                        "SELECT m FROM Medicament m JOIN Stock s ON m.id = s.medicamentId " +
                                "WHERE s.pharmacyId = :pharmacyId " +
                                "AND s.quantity > 0 " +
                                "AND m.name IN :medicamentNames", Medicament.class)
                .setParameter("pharmacyId", pharmacyId)
                .setParameter("medicamentNames", medicamentNames)
                .getResultList();
    }
}
