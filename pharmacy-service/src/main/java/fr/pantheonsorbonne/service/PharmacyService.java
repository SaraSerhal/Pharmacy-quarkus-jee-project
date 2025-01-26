package fr.pantheonsorbonne.service;

import fr.pantheonsorbonne.dao.PharmacyDAO;
import fr.pantheonsorbonne.dto.PharmacyRequestDTO;
import fr.pantheonsorbonne.dto.PharmacyStockRequestDTO;
import fr.pantheonsorbonne.dto.PharmacyResponseDTO;
import fr.pantheonsorbonne.entity.Pharmacy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PharmacyService{

    @Inject
    PharmacyDAO pharmacyDAO;

    @Transactional
    public PharmacyStockRequestDTO prepareStockRequest(PharmacyRequestDTO requestDTO) {
        List<String> pharmacyIds = pharmacyDAO.findAllPharmacyIds();

        return new PharmacyStockRequestDTO(pharmacyIds, requestDTO.medications());
    }

    @Transactional

    public List<PharmacyResponseDTO> processStockResponse(List<PharmacyResponseDTO> stockResponses,
                                                          String userId, String userAddress) {
        return stockResponses.stream()
                .filter(response -> !response.availableMedicaments().isEmpty())
                .map(response -> {
                    Pharmacy pharmacy = pharmacyDAO.findPharmacyById(response.pharmacyId());
                    return new PharmacyResponseDTO(
                            response.pharmacyId(),
                            response.availableMedicaments(),
                            pharmacy.getLocation(),
                            pharmacy.getOpeningTime(),
                            pharmacy.getClosingTime(),
                            response.requestedMedicaments(),
                            userId,
                            userAddress
                    );
                })
                .collect(Collectors.toList());
    }
}
