package fr.pantheonsorbonne.dto;

import java.util.List;

public record PharmacyVisitDTO(

        String address,
        List<String> availableMedicaments

) {
}