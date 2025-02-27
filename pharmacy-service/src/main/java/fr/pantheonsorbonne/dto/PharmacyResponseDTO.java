package fr.pantheonsorbonne.dto;

import java.util.List;

public record PharmacyResponseDTO(String pharmacyId, List<String> availableMedicaments, String location, String openingHours,
                                  String closingHours,List<String> requestedMedicaments, String userId, String userAddress) {
}
