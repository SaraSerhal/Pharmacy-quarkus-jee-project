package fr.pantheonsorbonne.processor;

import fr.pantheonsorbonne.dto.*;
import fr.pantheonsorbonne.exception.*;
import fr.pantheonsorbonne.service.DistanceCalculatorService;
import fr.pantheonsorbonne.service.GeolocationService;
import fr.pantheonsorbonne.service.ItineraryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;

@ApplicationScoped
public class ItineraryProcessor {

    @Inject
    ItineraryService itineraryService;

    @Inject
    GeolocationService geolocationService;

    @Inject
    DistanceCalculatorService distanceCalculatorService;

    public ItineraryResponseDTO processItinerary(ItineraryRequestDTO[] requests) throws CombitionException, DataNotFoundException, APIException, GeolocationException, DistanceException {

        List<ItineraryRequestDTO> openPharmacies = itineraryService.filterOpenPharmacies(requests);
        List<List<ItineraryRequestDTO>> combinations = itineraryService.calculatePossibleCombinations(openPharmacies);
        List<List<Map<String, Object>>> geolocatedCombinations;
        geolocatedCombinations = geolocationService.getPharmacyGeolocations(combinations);
        Map<Integer, Double> distances = distanceCalculatorService.calculateDistances(geolocatedCombinations);
        ItineraryResponseDTO response = itineraryService.findOptimalItinerary(combinations, distances, requests[0]);


        return response;
    }
}
