package fr.pantheonsorbonne.service;

import fr.pantheonsorbonne.dao.MedicamentDAO;
import fr.pantheonsorbonne.dto.MedicamentAvailabilityRequestDTO;
import fr.pantheonsorbonne.dto.PharmacyMedicamentResponseDTO;
import fr.pantheonsorbonne.entity.Medicament;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PharmacyStockService {

    @Inject
    MedicamentDAO medicamentDAO;
    @Transactional
    public List<PharmacyMedicamentResponseDTO> processRequest(MedicamentAvailabilityRequestDTO requestDTO) {
        List<PharmacyMedicamentResponseDTO> responses = new ArrayList<>();

        for (String pharmacyId : requestDTO.pharmacyIds()) {

            List<Medicament> availableMedicaments = medicamentDAO.findByPharmacy(pharmacyId);

            List<String> matchedMedicaments = requestDTO.medicamentNames().stream()
                    .filter(name -> availableMedicaments.stream()
                            .anyMatch(m -> m.getName().equalsIgnoreCase(name) &&
                                    medicamentDAO.getMedicamentQuantity(pharmacyId, m.getId()) > 0))
                    .collect(Collectors.toList());

            responses.add(new PharmacyMedicamentResponseDTO(pharmacyId, matchedMedicaments));
        }

        return responses;
    }
}
