package fr.pantheonsorbonne.dto;

import java.util.List;

public record PharmacyResponseDTO(String pharmacyId, List<String> availableMedicaments,String location, String openingHours,
                                  List<String> requestedMedicaments, String userId, String userAddress) {
}