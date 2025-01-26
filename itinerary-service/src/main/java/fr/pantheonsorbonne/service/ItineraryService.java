package fr.pantheonsorbonne.service;

import fr.pantheonsorbonne.dto.ItineraryRequestDTO;
import fr.pantheonsorbonne.dto.ItineraryResponseDTO;
import fr.pantheonsorbonne.dto.PharmacyVisitDTO;
import fr.pantheonsorbonne.exception.CombitionException;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ItineraryService {


    public List<ItineraryRequestDTO> filterOpenPharmacies(ItineraryRequestDTO[] pharmacies) {
        LocalTime currentTime = LocalTime.now();

        return Arrays.stream(pharmacies)
                .filter(pharmacy -> isPharmacyOpen(pharmacy.openingHours(), pharmacy.closingHours(), currentTime))
                .filter(pharmacy -> pharmacy.availableMedicaments().stream()
                        .anyMatch(pharmacy.requestedMedicaments()::contains))
                .collect(Collectors.toList());
    }


    public List<List<ItineraryRequestDTO>> calculatePossibleCombinations(List<ItineraryRequestDTO> pharmacies) {
        if (pharmacies.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> requestedMedicaments = new HashSet<>(pharmacies.get(0).requestedMedicaments());
        return findValidCombinations(pharmacies, requestedMedicaments);
    }


    private boolean isPharmacyOpen(String openingHours, String closingHours, LocalTime currentTime) {
        if (openingHours == null || closingHours == null) {
            return false;
        }
        LocalTime open = parseTime(openingHours);
        LocalTime close = parseTime(closingHours);
        if (open == null || close == null) {
            return false;
        }
        return !currentTime.isBefore(open) && !currentTime.isAfter(close);
    }

    private LocalTime parseTime(String time) {
        try {
            return LocalTime.parse(time);
        } catch (Exception e) {
            return null;
        }
    }

    private List<List<ItineraryRequestDTO>> findValidCombinations(
            List<ItineraryRequestDTO> pharmacies,
            Set<String> requestedMedicaments) {

        List<List<ItineraryRequestDTO>> validCombinations = new ArrayList<>();
        List<ItineraryRequestDTO> singleCoveragePharmacies = new ArrayList<>();

        Iterator<ItineraryRequestDTO> iterator = pharmacies.iterator();
        while (iterator.hasNext()) {
            ItineraryRequestDTO pharmacy = iterator.next();
            if (pharmacy.availableMedicaments().containsAll(requestedMedicaments)) {
                singleCoveragePharmacies.add(pharmacy);
                iterator.remove();
            }
        }

        for (ItineraryRequestDTO pharmacy : singleCoveragePharmacies) {
            validCombinations.add(Collections.singletonList(pharmacy));
        }

        for (int size = 1; size <= pharmacies.size(); size++) {
            List<List<ItineraryRequestDTO>> combinations = new ArrayList<>();
            generatePermutations(new ArrayList<>(), pharmacies, size, combinations);
            for (List<ItineraryRequestDTO> combination : combinations) {
                Set<String> coveredMedicaments = new HashSet<>();
                for (ItineraryRequestDTO pharmacy : combination) {
                    coveredMedicaments.addAll(pharmacy.availableMedicaments());
                }
                if (coveredMedicaments.containsAll(requestedMedicaments)) {
                    validCombinations.add(new ArrayList<>(combination));
                }
            }
        }

        return validCombinations;
    }

    private void generatePermutations(
            List<ItineraryRequestDTO> current,
            List<ItineraryRequestDTO> pharmacies,
            int size,
            List<List<ItineraryRequestDTO>> results) {

        if (current.size() == size) {
            results.add(new ArrayList<>(current));
            return;
        }

        for (ItineraryRequestDTO pharmacy : pharmacies) {
            if (current.contains(pharmacy)) {
                continue;
            }
            current.add(pharmacy);
            generatePermutations(current, pharmacies, size, results);
            current.remove(current.size() - 1);
        }
    }

    public ItineraryResponseDTO findOptimalItinerary(
            List<List<ItineraryRequestDTO>> possibleCombinations,
            Map<Integer, Double> distances,
            ItineraryRequestDTO userRequest) throws CombitionException {

        int optimalCombinationIndex = distances.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow(() -> new CombitionException("No combination found"))
                .getKey();

        List<ItineraryRequestDTO> optimalCombination = possibleCombinations.get(optimalCombinationIndex - 1);
        List<String> orderedAddresses = optimalCombination.stream()
                .map(ItineraryRequestDTO::location)
                .collect(Collectors.toList());

        double totalDistance = distances.get(optimalCombinationIndex);

        List<PharmacyVisitDTO> pharmaciesToVisit = optimalCombination.stream()
                .map(pharmacy -> new PharmacyVisitDTO(pharmacy.location(), pharmacy.availableMedicaments()))
                .collect(Collectors.toList());

        return new ItineraryResponseDTO(
                userRequest.userId(),
                userRequest.userAddress(),
                pharmaciesToVisit,
                userRequest.requestedMedicaments(),
                orderedAddresses,
                totalDistance
        );
    }
}
