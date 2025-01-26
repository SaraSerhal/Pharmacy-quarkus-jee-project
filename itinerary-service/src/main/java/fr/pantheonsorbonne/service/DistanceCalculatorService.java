package fr.pantheonsorbonne.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.pantheonsorbonne.exception.APIException;
import fr.pantheonsorbonne.exception.DistanceException;
import fr.pantheonsorbonne.exception.GeolocationException;
import jakarta.enterprise.context.ApplicationScoped;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class DistanceCalculatorService {

    private static final String OPENROUTESERVICE_API_KEY = "5b3ce3597851110001cf6248f8e368599e8945cbac0e9b2261a3c1c1";
    private static final String OPENROUTESERVICE_MATRIX_URL = "https://api.openrouteservice.org/v2/matrix/driving-car";

    public Map<Integer, Double> calculateDistances(List<List<Map<String, Object>>> geolocatedCombinations) throws DistanceException, APIException, GeolocationException {
        Map<Integer, Double> combinationDistances = new HashMap<>();
        int combinationIndex = 1;

        List<Map<String, Object>> uniqueLocations = geolocatedCombinations.stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        double[][] distanceMatrix = fetchDistanceMatrix(uniqueLocations);
        for (List<Map<String, Object>> combination : geolocatedCombinations) {
            try {
                double totalDistance = calculateTotalDistanceForCombination(combination, uniqueLocations, distanceMatrix);
                combinationDistances.put(combinationIndex, totalDistance);
            } catch (Exception e) {
                System.out.println("Error calculating distance for combination " + combinationIndex);
                e.printStackTrace();
                combinationDistances.put(combinationIndex, -1.0);
            }

            combinationIndex++;
        }

        return combinationDistances;
    }

    private double[][] fetchDistanceMatrix(List<Map<String, Object>> locations) throws GeolocationException, DistanceException, APIException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode locationsNode = objectMapper.createArrayNode();

            for (Map<String, Object> location : locations) {
                Object longitudeObj = location.get("longitude");
                Object latitudeObj = location.get("latitude");

                if (longitudeObj == null || latitudeObj == null) {
                    throw new GeolocationException("Invalid coordinates: missing longitude or latitude.");
                }

                double longitude = Double.parseDouble(longitudeObj.toString());
                double latitude = Double.parseDouble(latitudeObj.toString());

                if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
                    throw new GeolocationException("Invalid coordinates: longitude or latitude out of bounds.");
                }

                ArrayNode coordinate = objectMapper.createArrayNode();
                coordinate.add(longitude);
                coordinate.add(latitude);
                locationsNode.add(coordinate);
            }

            requestBody.set("locations", locationsNode);
            requestBody.putArray("metrics").add("distance");

            String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

            URL url = new URL(OPENROUTESERVICE_MATRIX_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", OPENROUTESERVICE_API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            connection.getOutputStream().write(jsonRequestBody.getBytes(StandardCharsets.UTF_8));

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8);
                String responseBody = scanner.useDelimiter("\\A").next();
                scanner.close();

                JsonNode responseJson = new ObjectMapper().readTree(responseBody);

                if (responseJson.has("distances") && responseJson.get("distances").isArray()) {
                    ArrayNode distancesArray = (ArrayNode) responseJson.get("distances");

                    double[][] distanceMatrix = new double[distancesArray.size()][distancesArray.size()];
                    for (int i = 0; i < distancesArray.size(); i++) {
                        for (int j = 0; j < distancesArray.get(i).size(); j++) {
                            distanceMatrix[i][j] = distancesArray.get(i).get(j).asDouble();
                        }
                    }

                    return distanceMatrix;
                } else {
                    throw new DistanceException("Invalid response: No distances found in the response.");
                }
            } else {
                Scanner errorScanner = new Scanner(connection.getErrorStream(), StandardCharsets.UTF_8);
                errorScanner.close();
                throw new APIException("ORS API returned error code: " + responseCode);
            }
        } catch (Exception e) {
            throw new DistanceException("An error occurred while fetching the distance matrix: " + e.getMessage());
        }
    }

    private double calculateTotalDistanceForCombination(List<Map<String, Object>> combination, List<Map<String, Object>> uniqueLocations, double[][] distanceMatrix) {
        double totalDistance = 0.0;
        for (int i = 0; i < combination.size() - 1; i++) {
            int fromIndex = uniqueLocations.indexOf(combination.get(i));
            int toIndex = uniqueLocations.indexOf(combination.get(i + 1));
            totalDistance += distanceMatrix[fromIndex][toIndex];
        }
        return totalDistance / 1000.0;
    }
}