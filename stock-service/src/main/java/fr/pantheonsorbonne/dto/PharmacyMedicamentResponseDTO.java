package fr.pantheonsorbonne.dto;

import java.util.List;

public record PharmacyMedicamentResponseDTO(String pharmacyId, List<String> availableMedicaments) { }
