package fr.pantheonsorbonne.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pantheonsorbonne.dto.ItineraryRequestDTO;
import fr.pantheonsorbonne.exception.APIException;
import fr.pantheonsorbonne.exception.DataNotFoundException;
import fr.pantheonsorbonne.exception.GeolocationException;
import jakarta.enterprise.context.ApplicationScoped;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


@ApplicationScoped
public class GeolocationService {

    private static final String OPENROUTESERVICE_API_KEY = "5b3ce3597851110001cf6248f8e368599e8945cbac0e9b2261a3c1c1";
    private static final String OPENROUTESERVICE_BASE_URL = "https://api.openrouteservice.org/geocode/search";

    public List<List<Map<String, Object>>> getPharmacyGeolocations(List<List<ItineraryRequestDTO>> combinations) throws GeolocationException, DataNotFoundException, APIException {
        List<List<Map<String, Object>>> geolocatedCombinations = new ArrayList<>();

        Set<String> allAddresses = new HashSet<>();
        for (List<ItineraryRequestDTO> combination : combinations) {
            for (ItineraryRequestDTO pharmacy : combination) {
                allAddresses.add(pharmacy.location());
                allAddresses.add(pharmacy.userAddress());
            }
        }
        Map<String, Map<String, Object>> geolocationResults = new HashMap<>();

        for (String address : allAddresses) {
            try {
                Map<String, Object> geolocationData = fetchGeolocation(address);
                if (geolocationData != null) {
                    geolocationResults.put(address, geolocationData);
                }
            } catch (APIException e) {
                throw new APIException("API error for address: " + address + " - " + e.getMessage());
            } catch (DataNotFoundException e) {
                throw new DataNotFoundException("No data found for address: " + address + " - " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }

        for (List<ItineraryRequestDTO> combination : combinations) {
            List<Map<String, Object>> geolocatedPharmacies = new ArrayList<>();

            for (ItineraryRequestDTO pharmacy : combination) {
                Map<String, Object> geolocationData = geolocationResults.get(pharmacy.location());
                Map<String, Object> geolocationDataForUserAddress = geolocationResults.get(pharmacy.userAddress());
                if (geolocationData != null) {
                    geolocatedPharmacies.add(geolocationData);
                }
                if (geolocationDataForUserAddress != null) {
                    geolocatedPharmacies.add(geolocationDataForUserAddress);
                }
            }
            if (geolocatedPharmacies.size() < combination.size()) {
                throw new GeolocationException("Some pharmacies could not be geolocated for the current combination.");
            }

            geolocatedCombinations.add(geolocatedPharmacies);
        }

        return geolocatedCombinations;
    }



    private Map<String, Object> fetchGeolocation(String address) throws DataNotFoundException, APIException {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
            String query = String.format("%s?api_key=%s&text=%s", OPENROUTESERVICE_BASE_URL, OPENROUTESERVICE_API_KEY, encodedAddress);
            URL url = new URL(query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8);
                String responseBody = scanner.useDelimiter("\\A").next();
                scanner.close();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseJson = objectMapper.readTree(responseBody);

                if (responseJson.has("features") && responseJson.get("features").isArray() && responseJson.get("features").size() > 0) {
                    JsonNode firstFeature = responseJson.get("features").get(0);
                    JsonNode geometry = firstFeature.get("geometry");

                    Map<String, Object> geolocation = new HashMap<>();
                    geolocation.put("address", address);
                    geolocation.put("latitude", geometry.get("coordinates").get(1).asDouble());
                    geolocation.put("longitude", geometry.get("coordinates").get(0).asDouble());

                    return geolocation;
                } else {
                    throw new DataNotFoundException("No geolocation data found for address: " + address);
                }
            } else {
                throw new APIException("API returned error code " + responseCode + " for address: " + address);
            }

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        return null;
    }

}
