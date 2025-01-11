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

    /**
     * Prépare la requête pour le StockService.
     */
    @Transactional
    public PharmacyStockRequestDTO prepareStockRequest(PharmacyRequestDTO requestDTO) {
        // Récupérer les IDs des pharmacies
        List<String> pharmacyIds = pharmacyDAO.findAllPharmacyIds();

        return new PharmacyStockRequestDTO(pharmacyIds, requestDTO.medications());
    }

    /**
     * Filtre et enrichit la réponse du StockService.
     */
    @Transactional

    public List<PharmacyResponseDTO> processStockResponse(List<PharmacyResponseDTO> stockResponses) {
        return stockResponses.stream()
                .filter(response -> !response.availableMedicaments().isEmpty()) // Filtrer les pharmacies sans médicaments
                .map(response -> {
                    Pharmacy pharmacy = pharmacyDAO.findPharmacyById(response.pharmacyId());
                    return new PharmacyResponseDTO(
                            response.pharmacyId(),
                            response.availableMedicaments(),
                            pharmacy.getLocation(),
                            pharmacy.getOpeningHours()
                    );
                })
                .collect(Collectors.toList());
    }
}
