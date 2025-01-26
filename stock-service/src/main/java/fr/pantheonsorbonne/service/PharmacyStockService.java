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

            List<Medicament> availableMedicaments = medicamentDAO.findAvailableMedicaments(pharmacyId, requestDTO.medicamentNames());

            List<String> matchedMedicaments = availableMedicaments.stream()
                    .map(Medicament::getName)
                    .collect(Collectors.toList());

            responses.add(new PharmacyMedicamentResponseDTO(pharmacyId, matchedMedicaments,requestDTO.medicamentNames()));
        }

        return responses;
    }
}
