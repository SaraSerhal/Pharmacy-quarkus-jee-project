package fr.pantheonsorbonne.dto;

import java.util.List;

public record MedicamentAvailabilityRequestDTO(List<String> pharmacyIds, List<String> medicamentNames) { }
