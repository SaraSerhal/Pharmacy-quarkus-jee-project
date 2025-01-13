package fr.pantheonsorbonne.dto;

import java.util.List;

public record PharmacyRequestDTO(String userId,List<String> medications,String address) {
}
