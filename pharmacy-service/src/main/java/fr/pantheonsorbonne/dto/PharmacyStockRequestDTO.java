package fr.pantheonsorbonne.dto;

import java.util.List;

public record PharmacyStockRequestDTO(List<String> pharmacyIds, List<String> medicamentNames) {
}
