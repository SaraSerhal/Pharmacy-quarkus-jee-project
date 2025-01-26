package fr.pantheonsorbonne.dto;
import java.util.List;

public record ItineraryResponseDTO(
        String userId,
        String userAddress,
        List<PharmacyVisitDTO> pharmaciesToVisit,
        List<String> requestedMedicaments,
        List<String> orderedAddresses,
        double totalDistance
) {
}